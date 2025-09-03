package lab2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Dialog extends JDialog {
    Frame parent;
    JTextField surname;
    JTextField name;
    JTextField city;
    Dialog(Frame parent) {
        super();
        this.parent = parent;
        setLocation(parent.getX() + parent.getWidth(), parent.getY());
        setSize(250, 400);
        setResizable(false);
        setLayout(null);

        JLabel numLabel = new JLabel("Surname:");
        numLabel.setBounds(40, 20, 100, 50);
        JLabel surLabel = new JLabel("Name:");
        surLabel.setBounds(40, 80, 100, 50);
        JLabel courseLabel = new JLabel("City:");
        courseLabel.setBounds(40, 140, 100, 50);

        surname = new JTextField();
        surname.setBounds(40, 60, 100, 30);
        name = new JTextField();
        name.setBounds(40, 120, 100, 30);
        city = new JTextField();
        city.setBounds(40, 180, 100, 30);

        JButton added = new JButton("Add");
        added.setBounds(40, 300, 100, 30);

        add(numLabel);
        add(surname);
        add(surLabel);
        add(name);
        add(courseLabel);
        add(city);
        add(added);
        setVisible(true);

        added.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String surn = surname.getText();
                    String nam = name.getText();
                    String cit = city.getText();
                    if(!isAlpha(surn)){
                        throw new IllegalArgumentException("Wrong data of surname, eou can input only letters");}
                    if(!isAlpha(nam)){
                        throw new IllegalArgumentException("Wrong data of surname, eou can input only letters");}
                    if(!isAlpha(cit)){
                        throw new IllegalArgumentException("Wrong data of surname, eou can input only letters");}
                    Person stud = new Person(surn, nam, cit);
                    parent.residents.add(stud);
                    parent.checkOfSave = true;
                    parent.update();
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Amount must be a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    public static boolean isAlpha(String s) {
        return s != null && s.matches("[a-zA-Z]+$");
    }
}