package com.group13.kslwebapp.views.configurations

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Menu
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@PageTitle("Configurations")
@Route("configurations")
@Menu(order = 4.0, icon = "line-awesome/svg/cog-solid.svg")
class ConfigView : Composite<VerticalLayout?>() {
    init {
        content!!.width = "100%"
        content!!.style["flex-grow"] = "1"
        content!!.add(H3("Configurations"))

        val titleHeader = H3("Configuration Options")

        val saveasCSV = Checkbox("Save as CSV")
        val saveasSpreadSheet = Checkbox("Save as SpreadSheet")

        val saveButton = Button("Save")
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY)

        saveButton.addClickListener { e: ClickEvent<Button?>? ->
            if (saveasCSV.value) {
                println("Save as CSV file selected")
            }
            if (saveasSpreadSheet.value) {
                println("Save as SpreadSheet  selected")
            }
        }

        val formLayout = FormLayout()
        formLayout.addFormItem(saveasCSV, saveasSpreadSheet)

        content!!.add(titleHeader, formLayout, saveButton)
    }
}
