import java.io.FileInputStream
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

private const val TABLE_NAME = "tbl"
private const val DB_PROPERTIES_PATH = "db.properties"

class DB {
    private lateinit var connection: Connection
    private val path : String
    private val name : String
    private val port : String
    private val username : String
    private val password : String

    init {
        val properties = Properties()
        properties.load(FileInputStream(DB_PROPERTIES_PATH))
        path = properties.getProperty("db.path")
        port = properties.getProperty("db.port")
        username = properties.getProperty("db.username")
        password = properties.getProperty("db.password")
        name = properties.getProperty("db.name")
        try {
            connection = DriverManager.getConnection("jdbc:mysql://$path:$port/$name", username, password)
            createTable(connection)
        } catch (e: SQLException) {
            println(e.message)
            e.printStackTrace()
        }
    }

    private fun checkConnection() {
        if (connection.isClosed) {
            println("Connection to MySQL is closed.")
            try {
                connection = DriverManager.getConnection("jdbc:mysql://$path:$port/$name", username, password)
            } catch (e: SQLException) {
                println(e.message)
                e.printStackTrace()
            }
        }
    }

    private fun createTable(connection: Connection) {
        val stmt = connection.createStatement()
        stmt.execute("""
        CREATE TABLE IF NOT EXISTS $TABLE_NAME (
            code TEXT NOT NULL,
            `key` TEXT NOT NULL
        )
    """.trimIndent())
    }

    fun getCode(key: String): String? {
        checkConnection()
        val stmt = connection.createStatement()
        val rs = stmt.executeQuery("SELECT code FROM $TABLE_NAME WHERE `key`=\"$key\"")
        if (rs.next()) {
            return String(Base64.getDecoder().decode(rs.getString("code")))
        }
        return null
    }

    fun putCode(code: String, generateKey: () -> String): String {
        checkConnection()
        val stmt = connection.createStatement()
        while (true) {
            val key = generateKey()
            val rs = stmt.executeQuery("SELECT `key` FROM $TABLE_NAME WHERE `key`=\"$key\"")
            if (!rs.next()) {
                val escapedCode = String(Base64.getEncoder().encode(code.toByteArray()))
                stmt.execute("INSERT INTO $TABLE_NAME (code, `key`) VALUES (\'$escapedCode\', \"$key\")")
                return key
            }
        }
    }
}