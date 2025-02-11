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

        Span street = new Span("4027 Amber Lake Canyon");
        Span zipCode = new Span("72333-5884 Cozy Nook");
        Span city = new Span("Arkansas");
        VerticalLayout billingAddressLayout = new VerticalLayout();
        billingAddressLayout.setSpacing(false);
        billingAddressLayout.setPadding(false);
        billingAddressLayout.add(street, zipCode, city);
        accordion.add("Billing address", billingAddressLayout);

        Span cardBrand = new Span("Mastercard");
        Span cardNumber = new Span("1234 5678 9012 3456");
        Span expiryDate = new Span("Expires 06/21");
        VerticalLayout paymentLayout = new VerticalLayout();
        paymentLayout.setSpacing(false);
        paymentLayout.setPadding(false);
        paymentLayout.add(cardBrand, cardNumber, expiryDate);
        accordion.add("Payment", paymentLayout);
    }
}