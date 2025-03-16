package com.group13.kslwebapp.views.model

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.progressbar.ProgressBar
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Menu
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.VaadinSession
import com.vaadin.flow.theme.lumo.LumoUtility
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

@PageTitle("Run Model")
@Route("")
@Menu(order = 1.0, icon = "line-awesome/svg/project-diagram-solid.svg")
class ModelView : Composite<VerticalLayout?>() {
    init {
        val layoutColumn2 = VerticalLayout()
        val formLayout2Col = FormLayout()
        val titleHeader = H3()
        val formLayout2Col2 = FormLayout()
        val numOfReplications = TextField()
        val lenOfReplication = TextField()
        val lenWarmUp = TextField()
        val layoutRow = HorizontalLayout()
        val runButton = Button()
        val statusText = Paragraph()
        val progressBar = ProgressBar()


        //
        val modelInfo = H3("Model Information")
        val modelDescriptionDisplay = Div()

        modelDescriptionDisplay.style["border"] = "1px solid var(--lumo-contrast-30pct)"
        modelDescriptionDisplay.style["padding"] = "10px"
        modelDescriptionDisplay.style["background-color"] = "var(--lumo-shade-5pct)"
        modelDescriptionDisplay.style["font-style"] = "Arial, sans-serif"
        modelDescriptionDisplay.setWidthFull()
        modelDescriptionDisplay.height = "100px"

        val modelName = "*Insert Model Name*"
        val modelDescription = "*Insert Model Description*"
        modelDescriptionDisplay.text = """
            $modelName
            
            $modelDescription
            """.trimIndent()


        //


        //
        val advancedButton = Button("Advanced")
        val configurationButton = Button("Configurations")

        advancedButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY)
        configurationButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY)

        val buttonRow = HorizontalLayout(advancedButton, configurationButton)
        buttonRow.setWidthFull()
        buttonRow.justifyContentMode = JustifyContentMode.START


        //
        content!!.width = "100%"
        content!!.style["flex-grow"] = "1"
        content!!.justifyContentMode = JustifyContentMode.START
        content!!.alignItems = FlexComponent.Alignment.CENTER

        layoutColumn2.width = "100%"
        layoutColumn2.maxWidth = "800px"
        layoutColumn2.height = "min-content"
        formLayout2Col.width = "100%"

        titleHeader.text = "Parameters"
        titleHeader.width = "max-content"
        formLayout2Col2.width = "100%"
        numOfReplications.label = "Number of Replications"
        numOfReplications.width = "min-content"
        lenOfReplication.label = "Length of Replication"
        lenOfReplication.width = "min-content"
        lenWarmUp.label = "Length of Warm-Up Replication"
        lenWarmUp.width = "min-content"
        layoutRow.addClassName(LumoUtility.Gap.MEDIUM)
        layoutRow.width = "100%"
        layoutRow.style["flex-grow"] = "1"
        runButton.text = "Run"
        layoutRow.setAlignSelf(FlexComponent.Alignment.START, runButton)
        runButton.width = "min-content"
        runButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        statusText.text = "Ready for Simulation"
        statusText.width = "100%"
        statusText.style["font-size"] = "var(--lumo-font-size-m)"
        progressBar.value = 0.0

        content!!.add(layoutColumn2)

        //
        layoutColumn2.add(modelInfo, modelDescriptionDisplay)


        //
        layoutColumn2.add(formLayout2Col)
        layoutColumn2.add(titleHeader)
        layoutColumn2.add(formLayout2Col2)
        formLayout2Col2.add(numOfReplications)
        formLayout2Col2.add(lenOfReplication)
        formLayout2Col2.add(lenWarmUp)

        //
        layoutColumn2.add(buttonRow)

        //
        layoutColumn2.add(layoutRow)
        layoutRow.add(runButton)
        layoutColumn2.add(statusText)
        layoutColumn2.add(progressBar)


        //
        configurationButton.addClickListener { event: ClickEvent<Button?>? ->
            ui.ifPresent { ui: UI ->
                ui.navigate(
                    "configurations"
                )
            }
        }

        advancedButton.addClickListener { event: ClickEvent<Button?>? ->
            ui.ifPresent { ui: UI ->
                ui.navigate(
                    "model-settings"
                )
            }
        }
        //
        runButton.addClickListener { event: ClickEvent<Button?>? ->
            val numOfReplicationsValue = numOfReplications.value
            val lenOfReplicationValue = lenOfReplication.value
            val lenWarmUpValue = lenWarmUp.value
            println(numOfReplicationsValue)
            println(lenOfReplicationValue)
            println(lenWarmUpValue)

            progressBar.isIndeterminate = true
            sendDataToServer(numOfReplicationsValue, lenOfReplicationValue, lenWarmUpValue)
            progressBar.isIndeterminate = false
            statusText.text = "Simulation complete!"
        }
    }

    private fun sendDataToServer(numOfReplications: String, lenOfReplication: String, lenWarmUp: String) {
        val session = VaadinSession.getCurrent() // Store session reference
        val ui = UI.getCurrent() // Store UI reference
        Thread {
            val client = ClientTest()
            client.sendMessage("02597b2228e6")

            val response = client.listenForMessages() // Now waits and returns message
            if (response != null && !response.isEmpty() && session != null && ui != null) {
                session.lock() // Lock session before modification
                try {
                    session.setAttribute("simulationResult", response)
                } finally {
                    session.unlock() // Always unlock after modification
                }

                ui.access { ui.navigate("results") } // UI updates must be inside `ui.access()`
            }
        }.start()
    }
}

internal class ClientTest {
    private val host = "localhost"
    private val port = 21000
    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null

    init {
        try {
            socket = Socket(host, port)
            writer = PrintWriter(socket!!.getOutputStream(), true)
            reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendMessage(message: String) {
        if (writer != null) {
            writer!!.println(message)
            println("Message sent: $message")
        }
    }

    fun listenForMessages(): String {
        val responseBuilder = StringBuilder()
        var response: String

        try {
            while ((reader!!.readLine().also { response = it }) != null) {
                println("Received: $response")
                responseBuilder.append(response).append("\n") // Append with a newline
            }
        } catch (e: IOException) {
            println("Connection closed.")
        }

        return responseBuilder.toString().trim { it <= ' ' }  // Return concatenated response
    }
}


