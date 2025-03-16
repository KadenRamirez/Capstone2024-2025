import io.javalin.Javalin
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.util.Scanner

fun main() {
    val app = Javalin.create().start(7000)

    // Configure Thymeleaf
    val templateEngine = TemplateEngine().apply {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/"
            suffix = ".html"
            characterEncoding = "UTF-8"
        })
    }

    // Render the form (GET request)
    app.get("/") { ctx ->
        val context = Context()
        val html = templateEngine.process("index", context)
        ctx.html(html)
    }

    // Handle form submission (POST request)
    app.post("/submit") { ctx ->
        val modelDescription = ctx.formParam("description") ?: "No description provided"
        val context = Context().apply {
            setVariable("modelDescription", modelDescription)
        }
        val html = templateEngine.process("index", context)
        ctx.html(html)
    }

    println("Server running on http://localhost:7000")
    println("Press ENTER to stop the server...")

    // Wait for ENTER key press to stop the server
    Scanner(System.`in`).nextLine()
    app.stop()

    println("Server stopped.")
}
