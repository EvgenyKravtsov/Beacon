package kgk.beacon.view.general.event;

/** Событие, сигнализирующее о необходимости загрузить определенный экран */
public class StartActivityEvent {

    private Class activityClass;
    private boolean loginSuccessful;

    public StartActivityEvent(Class activityClass) {
        this.activityClass = activityClass;
    }

    public Class getActivityClass() {
        return activityClass;
    }

    public void setActivityClass(Class activityClass) {
        this.activityClass = activityClass;
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    public void setLoginSuccessful(boolean loginSuccessful) {
        this.loginSuccessful = loginSuccessful;
    }
}
