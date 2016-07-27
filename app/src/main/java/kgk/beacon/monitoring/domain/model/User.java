package kgk.beacon.monitoring.domain.model;

public class User {

    public final String login;

    public String contacts;
    public double balance;

    ////

    public User(String login) {
        this.login = login;
    }

    ////

    public String getLogin() {
        return login;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "User - " + login;
    }
}
