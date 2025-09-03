package lab6.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
  private static final int amountOfClients = 2;
  private final ArrayList<String> messages = new ArrayList<>();
  private final StringBuilder clientsMsgSequence = new StringBuilder();
  private final ReentrantLock lock = new ReentrantLock();
  private final ServerSocket[] serverSocketsForReceiving = new ServerSocket[2];
  private final ServerSocket[] serverSocketsForSending = new ServerSocket[2];
  private final RunnableForReceiving[] runnablesForReceiving =
          new RunnableForReceiving[]{new RunnableForReceiving(1),
                  new RunnableForReceiving(2)};
  private final RunnableForSending[] runnablesForSending =
          new RunnableForSending[]{new RunnableForSending(1),
                  new RunnableForSending(2)};
  private final RunnableForChecking runnableForChecking =
          new RunnableForChecking();
  private final Thread threadForChecking = new Thread(runnableForChecking);
  private final ClientResources[] clientResources =
          new ClientResources[amountOfClients];
  private final AtomicInteger amountOfMessagesToCheck = new AtomicInteger(0);
  private final AtomicBoolean[] isClientActive = new AtomicBoolean[]{
          new AtomicBoolean(false), new AtomicBoolean(false)};

  public static void main(String[] args) {
    Server server = new Server();
    try {
      server.serverSocketsForReceiving[0] = new ServerSocket(10);
      server.serverSocketsForReceiving[1] = new ServerSocket(20);
      server.serverSocketsForSending[0] = new ServerSocket(11);
      server.serverSocketsForSending[1] = new ServerSocket(21);
    } catch (IOException e) {
      System.err.println("Server creation failed");
      System.exit(0);
    }
    for (int i = 0; i < amountOfClients; ++i) {
      server.clientResources[i] = new ClientResources();
      server.clientResources[i].threadForReceiving =
              new Thread(server.runnablesForReceiving[i]);
      server.clientResources[i].threadForSending =
              new Thread(server.runnablesForSending[i]);
      server.clientResources[i].threadForReceiving.start();
      server.clientResources[i].threadForSending.start();
    }
    server.threadForChecking.start();
  }

  public Server() {
    runnableForChecking.addWordFoundEventListener(e -> {
      String msg = "WRestricted word " + e.getWord() + " was found in line "
              + (e.getLineNum() + 1);
      synchronized (runnablesForReceiving[0]) {
        if (isClientActive[0].get()) {
          clientResources[0].queue.add(msg);
        }
      }
      synchronized (runnablesForReceiving[1]) {
        if (isClientActive[1].get()) {
          clientResources[1].queue.add(msg);
        }
      }
    });
  }

  static class ClientResources {
    Socket socketForReceiving, socketForSending;
    Thread threadForReceiving, threadForSending;
    ObjectInputStream objInStreamForSending;
    ObjectOutputStream objOutStreamForSending, objOutStreamForReceiving;
    Queue<String> queue = new LinkedList<>();
  }

  class RunnableForReceiving implements Runnable {
    private final int clientId;
    private RunnableForReceiving(int clientId) {
      this.clientId = clientId;
    }

    @Override
    public void run() {
      Server server = Server.this;
      while (true) {
        ClientResources clientResources = server.clientResources[clientId - 1];
        try {
          clientResources.socketForReceiving = server.serverSocketsForReceiving[clientId - 1].accept();
          clientResources.objOutStreamForReceiving = new ObjectOutputStream(clientResources.socketForReceiving.getOutputStream());
        } catch (IOException e) {
          continue;
        }
        while (true) {
          isClientActive[clientId - 1].set(true);
          try {
            synchronized (this) {
              while (clientResources.queue.isEmpty()) {
                if (!isClientActive[clientId - 1].get()) {
                  continue;
                }
                wait(100);
              }
            }
          } catch (InterruptedException e) {
            continue;
          }
          String msg;
          synchronized (this) {
            msg = clientResources.queue.poll();
          }
          try {
            if (isClientActive[clientId - 1].get()) {
              clientResources.objOutStreamForReceiving.writeObject(msg);
            }
          } catch (IOException e) {
            continue;
          }
        }
      }
    }
  }

  class RunnableForSending implements Runnable {
    private final int clientId;

    private RunnableForSending(int clientId) {
      this.clientId = clientId;
    }

    @Override
    public void run() {
      Server server = Server.this;
      while (true) {
        ClientResources clientResources = server.clientResources[clientId - 1];
        try {
          clientResources.socketForSending = server.serverSocketsForSending[clientId - 1].accept();
        } catch (IOException e) {
          continue;
        }
        try {
          clientResources.objOutStreamForSending = new ObjectOutputStream(clientResources.socketForSending.getOutputStream());
          clientResources.objInStreamForSending = new ObjectInputStream(clientResources.socketForSending.getInputStream());
        } catch (IOException e) {
          continue;
        }
        while (true) {
          String command;
          try {
            command = (String) clientResources.objInStreamForSending.readObject();
            if (command.equals("INIT")) {
              lock.lock();
              clientResources.objOutStreamForSending.writeObject(clientsMsgSequence.toString());
              for (int i = 0; i < clientsMsgSequence.length(); ++i) {
                clientResources.objOutStreamForSending.writeObject(messages.get(i));
              }
              clientResources.objOutStreamForSending.flush();
              lock.unlock();
            } else {
              String msg = (String) clientResources.objInStreamForSending.readObject();
              lock.lock();
              messages.add(msg);
              clientsMsgSequence.append(clientId);
              lock.unlock();
              amountOfMessagesToCheck.getAndIncrement();
              synchronized (runnableForChecking) {
                runnableForChecking.notify();
              }
              int anotherClientId = (clientId == 1) ? 2 : 1;
              synchronized (runnablesForReceiving[anotherClientId - 1]) {
                if (isClientActive[anotherClientId - 1].get()) {
                  server.clientResources[anotherClientId - 1].queue.add("M" + msg);
                  runnablesForReceiving[anotherClientId - 1].notify();
                }
              }
            }
          } catch (IOException | ClassNotFoundException e) {
            continue;
          }
        }
      }
    }
  }

  class RunnableForChecking implements Runnable {
    private final ArrayList<WordFoundEventListener> listeners = new ArrayList<>();
    private final ArrayList<String> restrictedWords = new ArrayList<>();
    private int amountOfCheckedLines = 0;

    private RunnableForChecking() {
      try {
        File file = new File("words.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        while (line != null) {
          restrictedWords.add(line);
          line = reader.readLine();
        }
      } catch (IOException e) {
        System.err.println("File read failed");
        System.exit(1);
      }
    }

    public void addWordFoundEventListener(WordFoundEventListener listener) {
      listeners.add(listener);
    }
    protected void fireWordFoundEvent(String word, int strNum) {
      WordFoundEvent event = new WordFoundEvent(this, word, strNum);
      for (WordFoundEventListener listener : listeners) {
        listener.wordFound(event);
      }
    }

    void checkLine(String line, int lineNum) {
      for (String word : restrictedWords) {
        if (line.contains(word)) {
          fireWordFoundEvent(word, lineNum);
        }
      }
    }

    @Override
    public void run() {
      while (true) {
        try {
          synchronized (this) {
            while (amountOfMessagesToCheck.get() == 0) {
              wait();
            }
          }
        } catch (InterruptedException e) {
          continue;
        }
        lock.lock();
        int willBeChecked = clientsMsgSequence.length() - amountOfCheckedLines;
        lock.unlock();
        for (int i = amountOfCheckedLines; i < amountOfCheckedLines
                + willBeChecked; ++i) {
          checkLine(messages.get(i), i);
        }
        amountOfCheckedLines += willBeChecked;
        amountOfMessagesToCheck.addAndGet(-willBeChecked);
      }
    }
  }
}
