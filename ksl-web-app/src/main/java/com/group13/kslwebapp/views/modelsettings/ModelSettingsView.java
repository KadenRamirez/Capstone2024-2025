package com.group13.kslwebapp.views.modelsettings;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Model Settings")
@Route("model-settings")
@Menu(order = 2, icon = "line-awesome/svg/database-solid.svg")
public class ModelSettingsView extends Composite<VerticalLayout> {

    public ModelSettingsView() {
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().add(new H3("Advanced Model Settings"));
    }
}
