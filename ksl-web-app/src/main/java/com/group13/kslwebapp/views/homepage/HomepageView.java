package com.group13.kslwebapp.views.homepage;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Home")
@Route("homepage")
@Menu(order = 0, icon = "line-awesome/svg/database-solid.svg")
public class HomepageView extends Composite<VerticalLayout> {
    public HomepageView() {
        Accordion accordion = new Accordion();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        accordion.setWidth("100%");
        setAccordionSampleData(accordion);
        getContent().add(accordion);
    }

    private void setAccordionSampleData(Accordion accordion) {
        Anchor ch5Model1 = new Anchor("#", "Model 1");
        Anchor ch5Model2 = new Anchor("#", "Model 2");
        Anchor ch5Model3 = new Anchor("#", "Model 3");
        VerticalLayout ch5modelLayout = new VerticalLayout(ch5Model1, ch5Model2, ch5Model3);
        ch5modelLayout.setSpacing(false);
        ch5modelLayout.setPadding(false);
        accordion.add("Chapter 5", ch5modelLayout);

        Anchor ch6Model1 = new Anchor("#", "Model 1");
        Anchor ch6Model2 = new Anchor("#", "Model 2");
        Anchor ch6Model3 = new Anchor("#", "Model 3");
        VerticalLayout ch6modelLayout = new VerticalLayout(ch6Model1, ch6Model2, ch6Model3);
        ch6modelLayout.setSpacing(false);
        ch6modelLayout.setPadding(false);
        accordion.add("Chapter 6", ch6modelLayout);

        Anchor ch7Model1 = new Anchor("#", "Model 1");
        Anchor ch7Model2 = new Anchor("#", "Model 2");
        Anchor ch7Model3 = new Anchor("#", "Model 3");
        VerticalLayout ch7modelLayout = new VerticalLayout(ch7Model1, ch7Model2, ch7Model3);
        ch7modelLayout.setSpacing(false);
        ch7modelLayout.setPadding(false);
        accordion.add("Chapter 7", ch7modelLayout);
    }
}