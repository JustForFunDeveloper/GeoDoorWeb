package tapsi;

import com.vaadin.annotations.*;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import tapsi.com.MainScreen;
import tapsi.com.authentication.AccessControl;
import tapsi.com.authentication.BasicAccessControl;
import tapsi.com.authentication.LoginScreen;
import tapsi.com.authentication.LoginScreen.LoginListener;
import tapsi.com.data.DataHandler;
import tapsi.com.data.LogHandler;
import tapsi.com.socket.SocketThread;
import tapsi.com.status.Status;

import javax.servlet.annotation.WebServlet;

/**
 * Main UI class of the application that shows either the login screen or the
 * main view of the application depending on whether a user is signed in.
 *
 * The @Viewport annotation configures the viewport meta tags appropriately on
 * mobile devices. Instead of device based scaling (default), using responsive
 * layouts.
 */
@Viewport("user-scalable=no,initial-scale=1.0")
@Theme("mytheme")
@Widgetset("tapsi.MyAppWidgetset")
@PreserveOnRefresh
public class MyUI extends UI {

    private AccessControl accessControl = new BasicAccessControl();

    /**
     * Every time the browser refreshes i.e. F5 is pressed the "enter" event is invoked.
     *
     * @param request don't need the parameter in this case
     */
    @Override
    protected void refresh(VaadinRequest request) {
        try {
            getNavigator().getCurrentView().enter(null);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        super.refresh(request);
    }

    /**
     * Initialize all handlers, register with the server and show the login screen.
     * After a successful login the main view will be shown.
     *
     * @param vaadinRequest VaadinRequest
     */
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        new SocketThread();
        new DataHandler();
        new LogHandler();
        SocketThread.register("visuRegister:GeoDoorVisu");
        setPollInterval(2000);

        Responsive.makeResponsive(this);
        setLocale(vaadinRequest.getLocale());
        getPage().setTitle("Geo Door Server");
        if (!accessControl.isUserSignedIn()) {
            setContent(new LoginScreen(accessControl, new LoginListener() {
                @Override
                public void loginSuccessful() {
                    showMainView();
                }
            }));
        } else {
            showMainView();
        }
    }

    /**
     * Navigates to the main view.
     *
     */
    protected void showMainView() {
        addStyleName(ValoTheme.UI_WITH_MENU);
        setContent(new MainScreen(MyUI.this));
        getNavigator().navigateTo(Status.VIEW_NAME);
    }

    /**
     * returns the
     *
     * @return returns the main UI
     */
    public static MyUI get() {
        return (MyUI) UI.getCurrent();
    }

    /**
     *  A getter to the AccessControl interface
     *
     * @return returns the interface to the BasicAccessControl
     */
    public AccessControl getAccessControl() {
        return accessControl;
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = true)
    public static class MyUIServlet extends VaadinServlet {
    }
}
