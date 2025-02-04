package com.group13.kslwebapp.views.model;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PageTitle("Run Model")
@Route("")
@Menu(order = 0, icon = "line-awesome/svg/project-diagram-solid.svg")
public class ModelView extends Composite<VerticalLayout> {

    public ModelView() {
        VerticalLayout layoutColumn2 = new VerticalLayout();
        FormLayout formLayout2Col = new FormLayout();
        H3 titleHeader = new H3();
        FormLayout formLayout2Col2 = new FormLayout();
        TextField numOfReplications = new TextField();
        TextField lenOfReplication = new TextField();
        TextField lenWarmUp = new TextField();
        HorizontalLayout layoutRow = new HorizontalLayout();
        Button runButton = new Button();
        Paragraph statusText = new Paragraph();
        ProgressBar progressBar = new ProgressBar();

        H3 modelName = new H3("Model Name");

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);

        layoutColumn2.setWidth("100%");
        layoutColumn2.setMaxWidth("800px");
        layoutColumn2.setHeight("min-content");
        formLayout2Col.setWidth("100%");

        titleHeader.setText("Parameters");
        titleHeader.setWidth("max-content");
        formLayout2Col2.setWidth("100%");
        numOfReplications.setLabel("Number of Replications");
        numOfReplications.setWidth("min-content");
        lenOfReplication.setLabel("Length of Replication");
        lenOfReplication.setWidth("min-content");
        lenWarmUp.setLabel("Length of Warm-Up Replication");
        lenWarmUp.setWidth("min-content");
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        runButton.setText("Run");
        layoutRow.setAlignSelf(FlexComponent.Alignment.START, runButton);
        runButton.setWidth("min-content");
        runButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        statusText.setText("Ready for Simulation");
        statusText.setWidth("100%");
        statusText.getStyle().set("font-size", "var(--lumo-font-size-m)");
        progressBar.setValue(0);

        getContent().add(layoutColumn2);

        layoutColumn2.add(modelName);

        layoutColumn2.add(formLayout2Col);
        layoutColumn2.add(titleHeader);
        layoutColumn2.add(formLayout2Col2);
        formLayout2Col2.add(numOfReplications);
        formLayout2Col2.add(lenOfReplication);
        formLayout2Col2.add(lenWarmUp);
        layoutColumn2.add(layoutRow);
        layoutRow.add(runButton);
        layoutColumn2.add(statusText);
        layoutColumn2.add(progressBar);

        runButton.addClickListener(event -> {
            String numOfReplicationsValue = numOfReplications.getValue();
            String lenOfReplicationValue = lenOfReplication.getValue();
            String lenWarmUpValue = lenWarmUp.getValue();
            System.out.println(numOfReplicationsValue);
            System.out.println(lenOfReplicationValue);
            System.out.println(lenWarmUpValue);

            statusText.setText("Running simulation...");
            progressBar.setIndeterminate(true);

            // (In a real application, you'd replace this with the actual simulation logic)
            new Thread(() -> {
                try {
                    Thread.sleep(2000);

                    getUI().ifPresent(ui -> ui.access(() -> {
                        if (ui.isAttached()) {
                            statusText.setText("Simulation complete!");
                            progressBar.setIndeterminate(false);
                            progressBar.setValue(1.0); // Set progress to 100%
                        }
                    }));
                } catch (InterruptedException e) {
                    System.err.println("An error occurred: " + e.getMessage());
                }
            }).start();
        });
    }

    record SampleItem(String value, String label, Boolean disabled) {
    }


}
