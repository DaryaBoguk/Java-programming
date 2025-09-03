package lab4;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Frame extends JFrame {
    public static void main(String[] args) throws IOException {
        lab4.Frame frame = new lab4.Frame("List");
    }

    JTextArea window1, window2;
    boolean checkOfSave = false;
    File selectedFile;
    final static Pattern PATTERN = Pattern.compile("\"[^\"]*\"|//.*?(?=\\r?$|\\n)");



    Frame(String title) throws IOException {
        super(title);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(650, 430);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel input = new JLabel("Input data");
        input.setBounds(15, 15, 100, 10);
        add(input);

        window1 = new JTextArea("", 10, 20);
        window1.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(window1);
        scroll.setBounds(20, 30, 300, 300);
        add(scroll);
        window2 = new JTextArea("", 10, 20);
        window2.setEditable(false);
        window2.setLineWrap(true);
        JScrollPane scroll2 = new JScrollPane(window2);
        scroll2.setBounds(330, 30, 300, 300);
        add(scroll2);
        JButton sorted = new JButton("Save");
        sorted.setBounds(20, 370, 300, 20);
        add(sorted);
        JButton inputFile = new JButton("Take data from file");
        inputFile.setBounds(20, 340, 300, 20);
        add(inputFile);

        JButton translate = new JButton("Remove comments");
        translate.setBounds(330, 340, 300, 20);
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
                JFileChooser fileChooser = new JFileChooser("D:\\java\\Java-Programming\\src\\lab4");
                int result = fileChooser.showOpenDialog(null);
                String str = "";
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    try {
                        FileReader fr = new FileReader(selectedFile.getAbsolutePath());
                        BufferedReader br = new BufferedReader(fr);
                        while (br.ready()) {
                            str = br.readLine();
                            window1.append(str + "\n");
                        }
                        br.close();
                        fr.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        sorted.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });

        translate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputText = window1.getText();
                String modifiedText = removeSingleLineComments(inputText);
                window2.setText(modifiedText);
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
    private String removeSingleLineComments(String content) {
        Matcher matcher = PATTERN.matcher(content);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            if (matcher.group().startsWith("\"")) {
                matcher.appendReplacement(result, matcher.group());
            } else {
                matcher.appendReplacement(result, "");
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }



}
