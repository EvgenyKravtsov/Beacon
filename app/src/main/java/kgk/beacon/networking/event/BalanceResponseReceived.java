package kgk.beacon.networking.event;

public class BalanceResponseReceived {

    private int resultCode; // 0 - success, 1 - error
    private String balance;

    ////

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
