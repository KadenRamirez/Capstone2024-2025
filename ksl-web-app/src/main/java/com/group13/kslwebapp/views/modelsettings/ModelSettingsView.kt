package com.group13.kslwebapp.views.modelsettings

import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Menu
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@PageTitle("Model Settings")
@Route("model-settings")
@Menu(order = 2.0, icon = "line-awesome/svg/database-solid.svg")
class ModelSettingsView : Composite<VerticalLayout?>() {
    init {
        content!!.width = "100%"
        content!!.style["flex-grow"] = "1"
        content!!.add(H3("Advanced Model Settings"))
    }
}
