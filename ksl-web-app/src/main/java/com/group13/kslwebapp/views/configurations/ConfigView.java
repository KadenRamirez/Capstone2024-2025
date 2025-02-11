package com.group13.kslwebapp.views.configurations;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Configurations")
@Route("configurations")
@Menu(order = 4, icon = "line-awesome/svg/cog-solid.svg")
public class ConfigView extends Composite<VerticalLayout> {

    public ConfigView() {
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().add(new H3("Configurations"));

        H3 titleHeader = new H3("Configuration Options");

        Checkbox saveasCSV = new Checkbox("Save as CSV");
        Checkbox saveasSpreadSheet = new Checkbox("Save as SpreadSheet");

        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        saveButton.addClickListener(e -> {
            if(saveasCSV.getValue())
            {
                System.out.println("Save as CSV file selected");
            }
            if(saveasSpreadSheet.getValue())
            {
                System.out.println("Save as SpreadSheet  selected");
            }
        });

        FormLayout formLayout = new FormLayout();
        formLayout.addFormItem(saveasCSV, saveasSpreadSheet);

        getContent().add(titleHeader, formLayout, saveButton);

    }
}
