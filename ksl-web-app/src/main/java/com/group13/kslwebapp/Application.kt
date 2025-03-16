package com.group13.kslwebapp

import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
@Theme(value = "ksl-web-app", variant = Lumo.DARK)
open class Application : AppShellConfigurator {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }
    }
}
