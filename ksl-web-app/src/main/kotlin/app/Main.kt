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

fun invokeRunSimulation(jarPath: String, controlValues: MutableMap<String, String>): String {
    var args = mutableListOf("java", "-jar", jarPath, "runSimulation")
    for ((key, value) in controlValues) {
        val cliKey = key.replace(" ", "@")
        args.add("$cliKey=$value")
        
    }
    println("Control Values: $controlValues")
    println("args: $args")
    val process = ProcessBuilder(args)
        .redirectErrorStream(true)
        .start()

    val output = process.inputStream.bufferedReader().readText()
    process.waitFor()

    return markdownTableToHtml(output)
}

fun invokeGetControls(jarPath: String): MutableMap<String, Double> {
    val process = ProcessBuilder("java", "-jar", jarPath, "getControls")
        .redirectErrorStream(true)
        .start()

    val output = process.inputStream.bufferedReader().readText()
    process.waitFor()


    val result = mutableMapOf<String, Double>()
    val regex = Regex("\"(.*?)\"\\s*:\\s*([\\d.]+)")
    regex.findAll(output).forEach {
        val key = it.groupValues[1]
        val value = it.groupValues[2].toDouble()
        result[key] = value
    }

    return result
}


fun markdownTableToHtml(markdown: String): String {
    // Grab non‑blank lines and their indices
    val rawLines = markdown.trim().lines().filter { it.isNotBlank() }

    // Find the first row that actually looks like a table row (starts & ends with |)
    val headerIdx = rawLines.indexOfFirst { it.trim().startsWith("|") && it.trim().endsWith("|") }
    if (headerIdx == -1 || headerIdx + 1 >= rawLines.size) return ""  // no table found

    // Check for a caption immediately above the header row
    val possibleCaption = if (headerIdx > 0) rawLines[headerIdx - 1].trim() else ""
    val captionText = possibleCaption.takeIf { it.startsWith("Table:", ignoreCase = true) }
        ?.removePrefix("Table:")
        ?.trim()

    val header = parseRow(rawLines[headerIdx])
    val bodyRows = rawLines.drop(headerIdx + 2).map(::parseRow) // skip alignment row

    return buildString {
        appendLine("<table>")
        if (captionText != null) appendLine("  <caption>$captionText</caption>")
        appendLine("  <thead>")
        appendLine("    <tr>")
        header.forEach { cell -> appendLine("      <th>${cell.trim()}</th>") }
        appendLine("    </tr>")
        appendLine("  </thead>")
        appendLine("  <tbody>")
        bodyRows.forEach { row ->
            appendLine("    <tr>")
            row.forEach { cell -> appendLine("      <td>${cell.trim()}</td>") }
            appendLine("    </tr>")
        }
        appendLine("  </tbody>")
        append("</table>")
    }
}

/** Helper to split a pipe‑delimited row into its cells. */
private fun parseRow(line: String): List<String> =
    line.trim().trim('|').split('|')


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

        val jarPath = "src/main/kotlin/simulation/$filename"
        var vars = mutableMapOf<String, Double>()
        vars = invokeGetControls(jarPath)

        var keys = mutableListOf<String>()
        var values = mutableListOf<Double>()
        for ((key, value) in vars) {
            keys.add(key) // Add the key to the keys list
            values.add(value) // Add the value to the values list
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
        var submittedValues = mutableMapOf<String, String>()

        for (key in keys) {
            var paramName = key.replace("@", " ") // Match the `th:name` in the form
            var value = ctx.queryParam(paramName) ?: "0" // Default to "0" if no value is provided
            submittedValues[key] = value
        }
        //println("Submitted Values: $submittedValues")

        val filename = ctx.sessionAttribute<String>("filename")
        if (filename != null) {
            println("This is the filename: " + filename)
        } else {
            ctx.status(400).result("No file uploaded.")
            return@get
        }
        val jarPath = "src/main/kotlin/simulation/$filename"
        val results = invokeRunSimulation(jarPath, submittedValues)
    // Display Markdown as raw text in a preformatted block
    ctx.html(results)
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

    println("Server running on http://localhost:7070")
    println("Press ENTER to stop the server...")

    // Wait for ENTER key press to stop the server
    Scanner(System.`in`).nextLine()
    app.stop()

    println("Server stopped.")
}
