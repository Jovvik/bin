import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.xhr.XMLHttpRequest

fun main() {
    window.onload = {
        val saveFun = {
            val input = (document.getElementById("text-input") as HTMLTextAreaElement)
            val http = XMLHttpRequest()
            http.open("POST", "/save")
            http.setRequestHeader("Content-Type", "text/plain")
            http.onreadystatechange = {
                println(http.responseText)
                document.location!!.href += http.responseText.drop(1)
                Unit // why are you retarded?
            }
            http.send(input.value)
        }
        val saveButton = document.getElementById("save-button")!!
        document.onkeydown = {
            if (it.ctrlKey && it.key == "s") {
                saveFun()
                it.preventDefault()
            }
        }
        saveButton.addEventListener("click", { saveFun() })
    }
}