package lab5;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;

public class Client extends JFrame {
    private static final String IP_ADRESSS = "localhost";
    private static final int PORT = 21035;

    public static void main(String[] args) throws IOException {
        lab5.Client frame = new lab5.Client("Client");
    }

    Client(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 230);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JLabel label = new JLabel("Choose file.");
        panel.add(label, BorderLayout.NORTH);
        JButton chooseButton = new JButton("Choose file");
        JButton sendButton = new JButton("Send file");
        sendButton.setEnabled(false);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(chooseButton);
        buttonPanel.add(sendButton);
        panel.add(buttonPanel, BorderLayout.CENTER);

        JFileChooser fileChooser = new JFileChooser("D:\\java\\Java-Programming");
        final File[] selectedFile = {null};

        chooseButton.addActionListener(e -> {
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile[0] = fileChooser.getSelectedFile();
                label.setText("We are choosing file: " + selectedFile[0].getName());
                sendButton.setEnabled(true);
            }
        });

        sendButton.addActionListener(e -> {
            if (selectedFile[0] != null) {
                try {
                    Socket socket = new Socket(IP_ADRESSS, PORT);
                    FileInputStream fileInputStream = new FileInputStream(selectedFile[0]);
                    OutputStream out = socket.getOutputStream();
                    InputStream in = socket.getInputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    out.flush();
                    socket.shutdownOutput();
                    File decodedFile = new File(selectedFile[0].getParent(), "decoded_" + selectedFile[0].getName());
                    try (FileOutputStream fileOutputStream = new FileOutputStream(decodedFile)) {
                        while ((bytesRead = in.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    label.setText("File saved " + decodedFile.getName());
                }catch(ConnectException er){

                    label.setText("Error: " + er.getMessage());
                } catch(IOException ex) {
                    label.setText("Error: " + ex.getMessage());
                }
            }
        });
        add(panel);
        setVisible(true);
    }
}
