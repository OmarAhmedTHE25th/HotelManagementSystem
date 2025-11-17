public class Wallet {

    private double balance;
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
    void refund(double price)
    {
        balance+=price;
    }
}
