package lab2;

import java.io.Serializable;

public class Person implements Serializable {
    String surname;
    String name;
    String city;

    public Person(String surname, String name, String city) {
        this.surname = surname;
        this.name = name;
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    @Override
    public String toString() {
        return surname + " " + name + " " + city + "\n";
    }
}

