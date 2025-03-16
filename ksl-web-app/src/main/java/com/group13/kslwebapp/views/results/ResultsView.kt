package com.group13.kslwebapp.views.results

import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Menu
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.VaadinSession

@PageTitle("Simulation Results")
@Route("results")
@Menu(order = 3.0, icon = "line-awesome/svg/database-solid.svg")
class ResultsView : Composite<VerticalLayout?>() {
    private val resultsContainer: Div

    init {
        var resultMessage = ""
        try {
            resultMessage = VaadinSession.getCurrent().getAttribute("simulationResult") as String
        } catch (e: ArithmeticException) {
            println("Error: There are no simulation results.")
        }
        resultsContainer = Div()
        resultsContainer.style["border"] = "1px solid var(--lumo-contrast-30pct)"
        resultsContainer.style["padding"] = "10px"
        resultsContainer.style["background-color"] = "var(--lumo-shade-5pct)"
        resultsContainer.style["font-style"] = "Arial, sans-serif"
        resultsContainer.setWidthFull()
        resultsContainer.height = "100px"
        // Set the text content
        if (!resultMessage.isEmpty()) {
            resultsContainer.text = resultMessage
        } else {
            resultsContainer.text = "No results available."
        }
        content!!.add(resultsContainer)
        content!!.width = "100%"
        content!!.style["flex-grow"] = "1"
    }
}

