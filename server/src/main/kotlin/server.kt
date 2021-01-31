import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.html.*

fun Application.main() {
    routing {
        class MainTemplate : Template<HTML> {
            val script = Placeholder<SCRIPT>()
            val main = Placeholder<FlowContent>()
            val button = Placeholder<BUTTON>()
            override fun HTML.apply() {
                head {
                    title { +"bin" }

                    // TODO: favicon

                    link {
                        href = "/css/bin.css"
                        rel = "stylesheet"
                    }

                    link {
                        href = "https://use.fontawesome.com/releases/v5.0.13/css/all.css"
                        rel = "stylesheet"
                    }

                    link {
                        href =
                                "//cdnjs.cloudflare.com/ajax/libs/highlight.js/10.5.0/styles/atom-one-dark.min.css"
                        rel = "stylesheet"
                    }

                    script {
                        src =
                                "//cdnjs.cloudflare.com/ajax/libs/highlight.js/10.5.0/highlight.min.js"
                    }

                    script { insert(script) }
                }

                body {
                    div("main-block") {
                        div("controls-block") { button { insert(button) } }
                        insert(main)
                    }
                }
            }
        }

        get("/") {
            call.respondHtmlTemplate(MainTemplate()) {
                script { src = "/client.js" }
                button {
                    id = "save-button"
                    span(classes = "far fa-save") {}
                }
                main {
                    textArea {
                        id = "text-input"
                        autoFocus = true
                        placeholder = "Type your code here"
                    }
                }
            }
        }
        static("/") { resources("/") }

        val db = DB()

        fun randomKey(): String {
            val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
            return (1..16).map { allowedChars.random() }.joinToString("")
        }

        post("/save") {
            val code = call.receiveText()
            val newKey = db.putCode(code, ::randomKey)
            call.respondText("/code/$newKey")
        }

        get("/code/{url}") {
            val code = db.getCode(call.parameters["url"]!!)
            if (code == null) {
                call.respond(HttpStatusCode.NotFound, "Unknown code")
            } else {
                call.respondHtmlTemplate(MainTemplate()) {
                    script { +"hljs.initHighlightingOnLoad()" }
                    button {
                        id = "new-button"
                        onClick = "window.location='/'"
                        span(classes = "fas fa-plus") {}
                    }
                    main { pre { code { +code } } }
                }
            }
        }
    }
}
