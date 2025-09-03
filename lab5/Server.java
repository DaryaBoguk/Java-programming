package lab5;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 21035;
    private static final String XKEY = "daryabohuk";

    public static void main(String[] args) {
        int i = 0;
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try  {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("connected " + (i++));
                    Thread thread = new Thread(() -> {
                        try {
                            handleClient(clientSocket);
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    thread.start();
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) throws IOException, InterruptedException {
        try (InputStream in = clientSocket.getInputStream();
             OutputStream out = clientSocket.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                byte[] decodedData = xor(buffer, bytesRead);
                out.write(decodedData, 0, bytesRead);
            }
            out.flush();
        }finally {
            try{
                clientSocket.close();
            }catch(IOException er){
                System.out.println("ERRORRRRR");
            }
        }
    }

    private static byte[] xor(byte[] data, int length) {
        byte[] keyBytes = XKEY.getBytes();
        for (int i = 0; i < length; i++) {
                for (int j = 0; j < keyBytes.length; j++) {
                    data[i] ^= keyBytes[j];
                }
        }
        return data;
    }
}
