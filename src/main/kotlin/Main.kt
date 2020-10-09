import java.io.PrintWriter
import kotlinx.html.*
import kotlinx.html.stream.appendHTML

fun main() {
    PrintWriter("build/index.html").use {
        it.appendHTML().html {
            head {
                title("Kbin")
            }
            body {
                h1 {
                    +"Kbin"
                }
                p {
                    +"This is a pastebin with:"
                    ul {
                        li { +"Code syntax highlighting" }
                        li { +"Custom URLs" }
                        li { +"Intuitive shortcuts" }
                    }
                }
            }
        }
    }
}
