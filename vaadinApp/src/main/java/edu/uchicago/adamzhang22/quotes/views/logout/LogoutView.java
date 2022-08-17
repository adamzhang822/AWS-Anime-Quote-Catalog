package edu.uchicago.adamzhang22.quotes.views.logout;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import edu.uchicago.adamzhang22.quotes.views.MainLayout;

@Route(value = "logout", layout = MainLayout.class)
@PageTitle("Log Out")
public class LogoutView extends Div implements AfterNavigationObserver {

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        UI.getCurrent().getPage().executeJs("window.location.href='logout'");
        //UI.getCurrent().getPage().reload();
    }
}
