package app
import io.javalin.Javalin
import java.io.File
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.util.Scanner
import org.apache.commons.io.FileUtils
import ksl.simulation.Model
import java.io.StringWriter
import java.io.PrintWriter
import ksl.utilities.io.KSL
import ksl.utilities.io.MarkDown
import javax.script.ScriptEngineManager
import javax.script.Invocable

fun invokeRunSimulation(scriptPath: String, controlValues: MutableMap<String, String>): String {
    val engine = ScriptEngineManager().getEngineByExtension("kts")

    engine.eval(File(scriptPath).readText())

    val invocable = engine as? Invocable
    val result = invocable?.invokeFunction("runSimulation", controlValues) as? String
    return result ?: "No output from runSimulation"
}

fun invokeGetControls(scriptPath: String, inputList: List<String>): List<String> {
    val engine = ScriptEngineManager().getEngineByExtension("kts")
    engine.eval(File(scriptPath).readText())

    val invocable = engine as? Invocable
    val result = invocable?.invokeFunction("getControls") as? List<String>
    return result ?: emptyList()
}

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
        val uploadDir = File("src/main/kotlin/simulation")
        if (!uploadDir.exists()) uploadDir.mkdirs() // Ensure upload directory exists
        
        var filename = ""
        val uploadedFile = ctx.uploadedFile("files")
        if (uploadedFile != null) {
            val destFile = File(uploadDir, uploadedFile.filename())
            filename = uploadedFile.filename()
            FileUtils.copyInputStreamToFile(uploadedFile.content(), destFile)
        } else {
            ctx.status(400).result("No file uploaded.")
        }

        val scriptPath = "src/main/kotlin/simulation/$filename"
        var vars = emptyList<String>()

        vars = invokeGetControls(scriptPath, vars)
        println(vars)
        val keys = mutableListOf<String>()
        val values = mutableListOf<Double>()
        for (i in vars) {
            val key = i
            keys.add(key)
            val value = 0.0
            values.add(value)
        }

        val modelDescription = ctx.formParam("description") ?: "No description provided"
        val context = Context().apply {
            setVariable("modelDescription", modelDescription)
            setVariable("keys", keys)
            setVariable("values", values)
            setVariable("filename", filename)
        }
        ctx.sessionAttribute("modelDescription", modelDescription)
        ctx.sessionAttribute("keys", keys)
        ctx.sessionAttribute("values", values)
        ctx.sessionAttribute("filename", filename)
        ctx.redirect("/model")
    }

    // Render the model page with the description
    app.get("/model") { ctx ->
        val modelDescription = ctx.sessionAttribute<String>("modelDescription") ?: "No description provided"
        val keys = ctx.sessionAttribute<List<String>>("keys") ?: emptyList()
        val values = ctx.sessionAttribute<List<String>>("values") ?: emptyList()
        val context = Context().apply {
            setVariable("modelDescription", modelDescription)
            setVariable("keys", keys)
            setVariable("values", values)
        }
        val html = templateEngine.process("model", context)
        ctx.html(html)
    }
    // Run the new `TandemQueueWithBlocking` simulation
    app.get("/run-simulation") { ctx ->
        val keys = ctx.sessionAttribute<List<String>>("keys") ?: emptyList()
        val submittedValues = mutableMapOf<String, String>()
        for (key in keys) {
            val paramName = key.replace("@", " ") // Match the `th:name` in the form
            val value = ctx.queryParam(paramName) ?: "0" // Default to "0" if no value is provided
            submittedValues[key] = value
        }
        println("Submitted Values: $submittedValues")

        val filename = ctx.sessionAttribute<String>("filename")
        if (filename != null) {
            println("This is the filename: " + filename)
        } else {
            ctx.status(400).result("No file uploaded.")
            return@get
        }
        val scriptPath = "src/main/kotlin/simulation/$filename"
        val results = invokeRunSimulation(scriptPath, submittedValues)
    // Display Markdown as raw text in a preformatted block
    ctx.html("<pre>$results</pre>")
    }
    
    app.get("/get-controls") { ctx ->
        val filename = ctx.sessionAttribute<String>("filename")
        val scriptPath = "src/main/kotlin/simulation/$filename"
        var vars = emptyList<String>()
        vars = invokeGetControls(scriptPath, vars)
    }
    
    // route to upload model page 
    app.get("/upload-model") { ctx ->
        val context = Context()
        val html = templateEngine.process("upload-model", context)
        ctx.html(html)
    }
    app.get("/setup-guide") { ctx ->
        val context = Context()
        val html = templateEngine.process("setup-guide", context)
        ctx.html(html)
    }

    println("Server running on http://localhost:7070")
    println("Press ENTER to stop the server...")

    // Wait for ENTER key press to stop the server
    Scanner(System.`in`).nextLine()
    app.stop()

    println("Server stopped.")
}
