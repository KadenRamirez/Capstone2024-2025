package com.group13.kslwebapp.views.results;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Simulation Results")
@Route("results")
@Menu(order = 3, icon = "line-awesome/svg/database-solid.svg")
public class ResultsView extends Composite<VerticalLayout> {

    public ResultsView() {
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
    }
}

