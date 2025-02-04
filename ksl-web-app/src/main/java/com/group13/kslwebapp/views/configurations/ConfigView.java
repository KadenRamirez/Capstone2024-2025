package com.group13.kslwebapp.views.configurations;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Configurations")
@Route("configurations")
@Menu(order = 2, icon = "line-awesome/svg/cog-solid.svg")
public class ConfigView extends Composite<VerticalLayout> {

    public ConfigView() {
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
    }
}
