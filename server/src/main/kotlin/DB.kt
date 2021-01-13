import java.sql.*

private const val TABLE_NAME = "tbl"
private const val DB_PATH = "build/resources/main/code.db"

class DB {
    private lateinit var connection: Connection

    init {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:$DB_PATH")
            createTable(connection)
        } catch (e: SQLException) {
            println(e.message)
            e.printStackTrace()
        }
    }

    private fun createTable(connection: Connection) {
        val stmt = connection.createStatement()
        stmt.execute("""
        CREATE TABLE IF NOT EXISTS $TABLE_NAME (
            code TEXT NOT NULL,
            key TEXT NOT NULL PRIMARY KEY
        )
    """.trimIndent())
    }

    fun getCode(key: String): String? {
        val stmt = connection.createStatement()
        val rs = stmt.executeQuery("SELECT code FROM $TABLE_NAME WHERE key=\"$key\"")
        while (rs.next()) {
            return rs.getString("code")
        }
        return null
    }

    fun putCode(code: String, generateKey: () -> String): String {
        val stmt = connection.createStatement()
        while (true) {
            val key = generateKey()
            val rs = stmt.executeQuery("SELECT key FROM $TABLE_NAME WHERE key=\"$key\"")
            if (!rs.next()) {
                stmt.execute("INSERT INTO $TABLE_NAME (code, key) VALUES (\'$code\', \"$key\")")
                return key
            }
        }
    }
}