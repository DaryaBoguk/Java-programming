package lab1;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Frame  extends JFrame {

    public static void main(String[] args) {
        Frame frame = new Frame("List");
    }
    JTextArea window1;
    Map<String, String> vocabulary;
    boolean checkOfSave = false;
    File selectedFile;
    Frame(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 430);
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

        JButton inputFile = new JButton("Take data from file");
        inputFile.setBounds(20, 340, 200, 20);
        add(inputFile);

        JButton inputRule = new JButton("Choose rules for translate");
        inputRule.setBounds(230, 340, 200, 20);
        add(inputRule);

        JButton translate = new JButton("Translate");
        translate.setBounds(110, 365, 200, 20);
        add(translate);

        setVisible(true);
        window1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                checkOfSave = true;
            }
        });
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
             JFileChooser fileChooser = new JFileChooser("D:\\java\\Java-Programming\\src\\lab1");
             int result = fileChooser.showOpenDialog(null);
             String str = "";
             if (result == JFileChooser.APPROVE_OPTION) {
                 selectedFile = fileChooser.getSelectedFile();
                 try {
                     FileReader fr = new FileReader(selectedFile.getAbsolutePath());
                     BufferedReader br = new BufferedReader(fr);
                     while (br.ready()) {
                         str = br.readLine();
                         window1.append(str);
                     }
                     br.close();
                     fr.close();
                 } catch (IOException ex) {
                     throw new RuntimeException(ex);
                 }
             }
         }
        }
        );

        inputRule.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser("D:\\java\\Java-Programming\\src\\lab1");
                int result = fileChooser.showOpenDialog(null);
                String str = "";
                vocabulary = new TreeMap<>(Comparator.comparing(String::length).thenComparing(String::compareTo).reversed());
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile1 = fileChooser.getSelectedFile();
                    try {
                        FileReader fr = new FileReader(selectedFile1.getAbsolutePath());
                        BufferedReader br = new BufferedReader(fr);
                        while (br.ready()) {
                            str = br.readLine();
                            String[] splitStr = str.trim().split(" - ");
                            vocabulary.put(splitStr[0], splitStr[1]);
                        }
                        br.close();
                        fr.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        translate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (vocabulary.isEmpty()) {
                    JOptionPane.showMessageDialog(window1, "Rules are not set. Choose rules file", "Error", JOptionPane.ERROR_MESSAGE);
                } else if ("".equals(window1.getText())) {
                    JOptionPane.showMessageDialog(window1, "Enter text!", "Error", JOptionPane.ERROR_MESSAGE);
                }else {
                    String text = window1.getText();
                    for (Map.Entry<String, String> entry : vocabulary.entrySet()) {
                        text = text.replace(entry.getKey(), entry.getValue());
                    }
                    window2.setText(text);
                }
            }
        });
    }
        private boolean check() {
            if (checkOfSave) {
                int result = JOptionPane.showConfirmDialog(this,
                        "Do you want to save changes?",
                        "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    boolean m = saveFile();
                    return m;
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
                } else if (result == JFileChooser.CANCEL_OPTION) {
                    return false;
                }
            }
            if (selectedFile != null) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                    writer.write(window1.getText());
                    checkOfSave = false;
                    setTitle("Text Translator - " + selectedFile.getName());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            return true;
        }

}

