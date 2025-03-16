package com.group13.kslwebapp.views

import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.html.Footer
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Header
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.SvgIcon
import com.vaadin.flow.component.orderedlayout.Scroller
import com.vaadin.flow.component.sidenav.SideNav
import com.vaadin.flow.component.sidenav.SideNavItem
import com.vaadin.flow.router.Layout
import com.vaadin.flow.server.auth.AnonymousAllowed
import com.vaadin.flow.server.menu.MenuConfiguration
import com.vaadin.flow.server.menu.MenuEntry
import com.vaadin.flow.theme.lumo.LumoUtility
import java.util.function.Consumer

/**
 * The main view is a top-level placeholder for other views.
 */
@Layout
@AnonymousAllowed
class MainLayout : AppLayout() {
    private var viewTitle: H1? = null

    init {
        primarySection = Section.DRAWER
        addDrawerContent()
        addHeaderContent()
    }

    private fun addHeaderContent() {
        val toggle = DrawerToggle()
        toggle.setAriaLabel("Menu toggle")

        viewTitle = H1()
        viewTitle!!.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE)

        addToNavbar(true, toggle, viewTitle)
    }

    private fun addDrawerContent() {
        val appName = Span("KSL Web App")
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE)
        val header = Header(appName)

        val scroller = Scroller(createNavigation())

        addToDrawer(header, scroller, createFooter())
    }

    private fun createNavigation(): SideNav {
        val nav = SideNav()

        val menuEntries = MenuConfiguration.getMenuEntries()
        menuEntries.forEach(Consumer { entry: MenuEntry ->
            if (entry.icon() != null) {
                nav.addItem(SideNavItem(entry.title(), entry.path(), SvgIcon(entry.icon())))
            } else {
                nav.addItem(SideNavItem(entry.title(), entry.path()))
            }
        })

        return nav
    }

    private fun createFooter(): Footer {
        val layout = Footer()

        return layout
    }

    override fun afterNavigation() {
        super.afterNavigation()
        viewTitle!!.text = currentPageTitle
    }

    private val currentPageTitle: String
        get() = MenuConfiguration.getPageHeader(content).orElse("")
}
