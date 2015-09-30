package kgk.beacon.util;

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
