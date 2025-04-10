package app
import io.javalin.Javalin
import java.io.File
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.util.Scanner
import org.apache.commons.io.FileUtils
import simulation.StemFairMixerEnhancedWithMovement
import ksl.simulation.Model
import java.io.StringWriter
import java.io.PrintWriter
import ksl.utilities.io.KSL
import ksl.utilities.io.MarkDown

fun main() {
    val app = Javalin.create().start(7070)

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
    app.post("/upload") { ctx ->
        val uploadDir = File("upload")
        if (!uploadDir.exists()) uploadDir.mkdirs() // Ensure upload directory exists

        ctx.uploadedFiles("files").forEach { uploadedFile ->
            val destFile = File(uploadDir, uploadedFile.filename())
            FileUtils.copyInputStreamToFile(uploadedFile.content(), destFile)
        }
        val modelDescription = ctx.formParam("description") ?: "No description provided"
        val context = Context().apply {
            setVariable("modelDescription", modelDescription)
        }
        ctx.sessionAttribute("modelDescription", modelDescription)
        ctx.redirect("/model")
    }

    // Render the model page with the description
    app.get("/model") { ctx ->
        val modelDescription = ctx.sessionAttribute<String>("modelDescription") ?: "No description provided"
        val context = Context().apply {
            setVariable("modelDescription", modelDescription)
        }
        val html = templateEngine.process("model", context)
        ctx.html(html)
    }
    // Run the new `TandemQueueWithBlocking` simulation
    app.get("/run-simulation") { ctx ->
        val m = Model()
        StemFairMixerEnhancedWithMovement(m, "Stem Fair Base Case")
        m.numberOfReplications = 400
        m.simulate()
        m.print()
        
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)

        m.simulationReporter.writeHalfWidthSummaryReportAsMarkDown(printWriter, df = MarkDown.D3FORMAT)

        val markdownOutput = stringWriter.toString()

    // Display Markdown as raw text in a preformatted block
    ctx.html("<pre>$markdownOutput</pre>")
    }
    
    // route to upload model page 
    app.get("/upload-model") { ctx ->
        val context = Context()
        val html = templateEngine.process("upload-model", context)
        ctx.html(html)
    }

    //route to example setup page
    app.get("/setup-guide") { ctx ->
        val context = Context()
        val html = templateEngine.process("setup-guide", context)
        ctx.html(html)
    }
    
    //route to example model page
    app.get("/kslmodel-examples") { ctx ->
        val context = Context()
        val html = templateEngine.process("kslmodel-examples", context)
        ctx.html(html)
    }

    //route to faq  page
    app.get("/faq") { ctx ->
        val context = Context()
        val html = templateEngine.process("faq", context)
        ctx.html(html)
    }


    println("Server running on http://localhost:7000")
    println("Press ENTER to stop the server...")

    // Wait for ENTER key press to stop the server
    Scanner(System.`in`).nextLine()
    app.stop()

    println("Server stopped.")
}
