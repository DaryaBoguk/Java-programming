package lab2;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Frame  extends JFrame{

    public static void main(String[] args) {
        Frame frame = new Frame("List");
    }
    ArrayList<Person> residents = new ArrayList<>();
    JTextArea window1;
    boolean checkOfSave = false;
    File selectedFile;

    Frame(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(465, 470);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel input = new JLabel("Input data");
        input.setBounds(15, 15, 100, 10);
        add(input);

        JLabel output = new JLabel("Analysed data");
        output.setBounds(230, 15, 100, 10);
        add(output);


        window1 = new JTextArea("", 10, 20);
        window1.setEditable(false);
        window1.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(window1);
        scroll.setBounds(20, 30, 200, 300);
        add(scroll);

        JTextArea window2 = new JTextArea("", 10, 20);
        window2.setEditable(false);
        window2.setLineWrap(true);
        JScrollPane scroll2 = new JScrollPane(window2);
        scroll2.setBounds(230, 30, 200, 300);
        add(scroll2);


        JButton sorted = new JButton("Save");
        sorted.setBounds(230, 340, 200, 20);
        add(sorted);


        JButton inputFile = new JButton("Take data from file");
        inputFile.setBounds(10, 340, 200, 20);
        add(inputFile);

        JButton added = new JButton("Add");
        added.setBounds(10, 370, 200, 20);
        add(added);

        JButton analyse = new JButton("Analyse");
        analyse.setBounds(230, 370, 200, 20);
        add(analyse);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (check()) {
                    dispose();
                }
            }
        });


        inputFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window1.setText("");
                JFileChooser fileChooser = new JFileChooser("D:\\java\\Java-Programming");
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                     selectedFile = fileChooser.getSelectedFile();
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(selectedFile))) {
                        residents = (ArrayList<Person>)ois.readObject();
                        residents.forEach(resident -> window1.append(resident.toString()));
                    } catch (IOException | ClassNotFoundException ex) {
                        JOptionPane.showMessageDialog(null, "Something went wrong during reading");
                    }
                }
            }
        }
        );

        sorted.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });

        analyse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                window2.setText("");
                Map<String, Long> cityCounts = residents.stream()
                        .collect(Collectors.groupingBy(Person::getCity, Collectors.counting()));
                long totalResidents = residents.size();
                StringBuilder result = new StringBuilder("City Analysis:\n");
                cityCounts.forEach((city, count) -> result.append(city).append(": ").append(count).append("\n"));
                result.append("Total residents: ").append(totalResidents);
                window2.setText(result.toString());
            }
        });

        added.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Dialog adds = new Dialog(Frame.this);
                }
            }
        );


    }
    public void update() {
        window1.setText("");
        for (Person student : residents) {
            window1.append(student.toString());
        }
    }
    private boolean check() {
        if (checkOfSave) {
            int result = JOptionPane.showConfirmDialog(this,
                    "Do you want to save changes?",
                    "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                return saveFile();

            } else if (result == JOptionPane.CANCEL_OPTION) {
                return false;
            }
        }
        return true;
    }
    private boolean saveFile() {
        if (selectedFile == null) {
            JFileChooser fileChooser = new JFileChooser("D:\\java\\Java-Programming\\src\\lab1");
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(selectedFile))) {
                    oos.writeObject(residents);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Something went wrong during reading");
                }
            } else if (result == JFileChooser.CANCEL_OPTION) {
                return false;
            }
        }
        if (selectedFile != null) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(selectedFile))) {
                oos.writeObject(residents);
                checkOfSave = false;
                setTitle("Text Translator - " + selectedFile.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return true;
    }
}

