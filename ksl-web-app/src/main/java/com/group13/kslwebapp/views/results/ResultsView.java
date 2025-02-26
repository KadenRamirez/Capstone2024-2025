package com.group13.kslwebapp.views.results;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.net.*;
import java.io.*;


@PageTitle("Simulation Results")
@Route("results")
@Menu(order = 3, icon = "line-awesome/svg/database-solid.svg")
public class ResultsView extends Composite<VerticalLayout> {
    private final Div resultsContainer;
    public ResultsView() {
        resultsContainer = new Div();
        resultsContainer.getStyle().set("border", "1px solid var(--lumo-contrast-30pct)");
        resultsContainer.getStyle().set("padding", "10px");
        resultsContainer.getStyle().set("background-color", "var(--lumo-shade-5pct)");
        resultsContainer.getStyle().set("font-style", "Arial, sans-serif");
        resultsContainer.setWidthFull();
        resultsContainer.setHeight("100px");
        getContent().add(resultsContainer);
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");

        listener();
    }

    public void listener() {
        String serverAddress = "localhost";
        int serverPort = 1600;

        // Create a separate thread for the socket connection to avoid blocking the UI thread
        new Thread(() -> {
            try (Socket socket = new Socket(serverAddress, serverPort);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                System.out.println("Connected");

                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("Received: " + message);
                    // Update the UI thread with the received message
                    String finalMessage = message;
                    getUI().ifPresent(ui -> ui.access(() -> {
                        // Update the results container with the received message
                        resultsContainer.setText("Model Name: " + finalMessage); // Example message
                    }));
                }

            } catch (IOException e) {
                // Handle exceptions
                return;
            }
        }).start();
    }
}

