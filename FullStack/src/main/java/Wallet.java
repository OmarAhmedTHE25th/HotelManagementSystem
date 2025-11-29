public class Wallet {

    private double balance = 100;
   void setBalance(int balance)
   {
       this.balance = balance;
   }
   double getBalance(){return balance;}
    void Pay(double price)
    {
        if (price>balance)throw new IllegalArgumentException("Insufficient Funds");
        balance-=price;
    }
    void getMoney(double price) {
        balance += price;
    }
}
