package Backned;

import java.io.Serializable;

public class Wallet implements Serializable {

    private double balance = 100;

    public double getBalance(){return balance;}
    void Pay(double price)
    {
        if (price>balance)throw new IllegalArgumentException("No poor people allowed!");
        balance-=price;
    }
    public void getMoney(double price) {
        balance += price;
    }
}
