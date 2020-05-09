package ubc.pavlab.ndb.beans;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpSession;

@ManagedBean(name="idleMonitorView")
@SessionScoped
public class IdleMonitorView {

    //static boolean expired = false;
    private static int maxInterval;

    public void onIdleWarning() {
        final int TEN_MINUTES = 60 * 10;

        FacesContext facesContext = FacesContext.getCurrentInstance();
        // facesContext.addMessage("messagesWarn", new FacesMessage(FacesMessage.SEVERITY_WARN,
                //         "No activity.", "Your session will expire soon due to inactivity."));
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
        maxInterval = session.getMaxInactiveInterval();
        session.setMaxInactiveInterval(TEN_MINUTES);
    }

    public void onActiveWarning() {
        FacesContext.getCurrentInstance().addMessage("messagesWarn", new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Welcome Back", "Your session has been preserved."));

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
        session.setMaxInactiveInterval(maxInterval);
    }
}