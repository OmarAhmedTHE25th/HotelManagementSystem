package Backned;

import java.io.Serializable;
import java.time.LocalDate;
abstract public class User implements Serializable {
    public String username;
    LocalDate birthday;
    public String ID;
User(){}
}
