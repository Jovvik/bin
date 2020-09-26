import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.io.PrintWriter

fun main() {
    PrintWriter("build/index.html").use {
        it.appendHTML().html {
            head {
                title("Client-server and Kotlin")
            }
            body {
                h1 {
                    +"John Doe"
                }
                p {
                    +"I am going to build a Death Star!"
                }
            }
        }
    }
}