package lab6.client;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

public class Client extends JFrame {
  private final JButton sendButton = new JButton("Send");
  private final JTextField textField = new JTextField();
  private final JTable tableOfMessages;
  private final DefaultTableModel tableOfMessagesModel;
  private final JScrollPane scrollPane;
  private final ReentrantLock tableLock = new ReentrantLock();
  private Socket socketForSending, socketForReceiving;
  private final RunnableForSending runnableForSending = new RunnableForSending();
  private final Thread threadForSending = new Thread(runnableForSending);
  private final RunnableForReceiving runnableForReceiving =
          new RunnableForReceiving();
  private final Thread threadForReceiving = new Thread(runnableForReceiving);
  private ObjectInputStream objInStreamForSending, objInStreamForReceiving;
  private ObjectOutputStream objOutStreamForSending;
  private final StringBuilder clientsMsgSequence = new StringBuilder();
  private int clientId;
  private int anotherClientId;
  private boolean sending = false;

  {
    Vector<Vector<String>> rowData = new Vector<>();
    Vector<String> columnNames = new Vector<>(List.of("Spy"));
    tableOfMessages = new JTable(rowData, columnNames);
    tableOfMessagesModel = (DefaultTableModel) tableOfMessages.getModel();
    scrollPane = new JScrollPane(tableOfMessages);
  }

  public static void main(String[] args) {
    Client client = new Client();
    client.setVisible(true);
    client.clientId = Integer.parseInt("2");
    client.anotherClientId = (client.clientId == 1) ? 2 : 1;
    try {
      client.socketForReceiving = new Socket("localhost",
              Integer.parseInt(client.clientId + "0"));
      client.socketForSending = new Socket("localhost",
              Integer.parseInt(client.clientId + "1"));
      client.socketForSending.setSoTimeout(5000);
      client.objInStreamForSending = new ObjectInputStream(
              client.socketForSending.getInputStream());
      client.objOutStreamForSending = new ObjectOutputStream(
              client.socketForSending.getOutputStream());
      client.socketForSending.setSoTimeout(0);
      client.objInStreamForReceiving = new ObjectInputStream(
              client.socketForReceiving.getInputStream());
    } catch (IOException e) {
      JOptionPane.showMessageDialog(client, "Can not connect to server");
      System.exit(0);
    }
    client.threadForSending.start();
    client.threadForReceiving.start();
  }

  public Client() {
    super("Client");
    setSize(350, 400);
    setBackground(Color.black);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    Container container = getContentPane();
    container.setBackground(Color.black);
    container.setLayout(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.BOTH;
    constraints.insets = new Insets(5, 5, 2, 5);
    setConstraints(constraints, 1, 1, 5, 6, 0, 0);
    initTable(tableOfMessages, scrollPane);
    container.add(scrollPane, constraints);
    setConstraints(constraints, 1, 0.001, 3, 1, 0, 6);
    container.add(textField, constraints);
    setConstraints(constraints, 0.1, 0.1, 1, 1, 4, 6);
    initButton(sendButton, new SendEventListener());
    container.add(sendButton, constraints);
  }

  void setConstraints(GridBagConstraints constraints, double weightx, double weighty, int gridwidth, int gridheight, int gridx, int gridy) {
    constraints.weightx = weightx;
    constraints.weighty = weighty;
    constraints.gridwidth = gridwidth;
    constraints.gridheight = gridheight;
    constraints.gridx = gridx;
    constraints.gridy = gridy;
  }

  void initButton(JButton button, ActionListener actionListener) {
    button.setForeground(Color.BLUE);
    button.addActionListener(actionListener);
  }

  void initTable(JTable table, JScrollPane scrollPane) {
    table.setBackground(Color.WHITE);
    table.setDefaultEditor(Object.class, null);
    MessageTableCellRenderer msgTableCellRenderer =
            new MessageTableCellRenderer();
    msgTableCellRenderer.setHorizontalAlignment(JLabel.LEFT);
    table.setDefaultRenderer(Object.class, msgTableCellRenderer);
    table.setRowHeight(22);
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
  }

  class MessageTableCellRenderer extends DefaultTableCellRenderer {
    private final Color client1Color = Color.BLACK;
    private final Color client2Color = Color.RED;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
      JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
      if (clientsMsgSequence.charAt(row) == '1') {
        label.setForeground(client1Color);
      } else {
        label.setForeground(client2Color);
      }
      return label;
    }
  }

  class RunnableForSending implements Runnable {
    @Override
    public void run() {
      Thread me = Thread.currentThread();
      if (threadForSending == me) {
        try {
          objOutStreamForSending.writeObject("INIT");
          objOutStreamForSending.flush();
          tableLock.lock();
          clientsMsgSequence.append((String)objInStreamForSending.readObject());
          for (int i = 0; i < clientsMsgSequence.length(); ++i) {
            String msg = (String) objInStreamForSending.readObject();
            tableOfMessagesModel.addRow(new String[]{msg});
          }
          tableLock.unlock();
        } catch (IOException | ClassNotFoundException e) {
          JOptionPane.showMessageDialog(Client.this, "Connection error");
          System.exit(0);
        }
      }
      while (threadForSending == me) {
        sendButton.setEnabled(true);
        try {
          synchronized (this) {
            sending = false;
            while (!sending) {
              wait();
            }
          }
        } catch (InterruptedException e) {
          continue;
        }
        String msg;
        try {
          objOutStreamForSending.writeObject("SEND");
          msg = textField.getText();
          objOutStreamForSending.writeObject(msg);
          objOutStreamForSending.flush();
        } catch (IOException e) {
          JOptionPane.showMessageDialog(Client.this, "Connection error. " +
                  "Message wasn't sent");
          System.exit(0);
          continue;
        }
        textField.setText("");
        tableLock.lock();
        clientsMsgSequence.append(clientId);
        tableOfMessagesModel.addRow(new String[]{msg});
        tableLock.unlock();
      }
    }
  }

  class RunnableForReceiving implements Runnable {
    @Override
    public void run() {
      Thread me = Thread.currentThread();
      while (threadForReceiving == me) {
        String msg;
        try {
          msg = (String) objInStreamForReceiving.readObject();
        } catch (IOException | ClassNotFoundException e) {
          JOptionPane.showMessageDialog(Client.this, "Connection error. " + "Message wasn't sent");
          System.exit(0);
          continue;
        }
        String text = msg.substring(1);
        if (msg.startsWith("M")) {
          tableLock.lock();
          clientsMsgSequence.append(anotherClientId);
          tableOfMessagesModel.addRow(new String[]{text});
          tableLock.unlock();
        } else {
          JOptionPane.showMessageDialog(Client.this, text);
        }
      }
    }
  }
  class SendEventListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      sendButton.setEnabled(false);
      synchronized (runnableForSending) {
        sending = true;
        runnableForSending.notify();
      }
    }
  }
}

