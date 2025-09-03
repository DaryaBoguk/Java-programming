package lab2;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CreateBinFile {
    public static void main(String[] args) {
        List<Person> residents = new ArrayList<>();
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("inp.txt"))) {
            residents.add(new Person("Galkevich", "Victor", "Berlin"));
            residents.add(new Person("Trofimovich", "Timofey", "Warsaw"));
            residents.add(new Person("Boguk", "Darya", "Canberra"));
            residents.add(new Person("Kozlovsky", "Anton", "Beijing"));
            residents.add(new Person("Dziga", "Anna", "Minsk"));
            residents.add(new Person("Volkov", "Pavel", "Vitebsk"));
            residents.add(new Person("Bezmen", "Nick", "Grodno"));
            residents.add(new Person("Staver", "Alex", "Brest"));
            residents.add(new Person("Konovalova", "Alina", "Mogilev"));
            residents.add(new Person("Filon", "Vanya", "Gates"));
            residents.add(new Person("Brown", "Denis", "Krakow"));
            oos.writeObject(residents);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
