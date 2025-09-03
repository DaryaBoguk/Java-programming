package lab5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    private static final String XOR_KEY = "daryabohuk";
    public static void main(String[] args) {
        File inputFile = new File("D:\\java\\Java-Programming\\inp.txt");
        File encodedFile = new File("encodedinp.txt");
        File decodedFile = new File("decoded.txt");
        try {
            xor(inputFile, encodedFile, XOR_KEY);
            System.out.println("Encode: " + encodedFile.getAbsolutePath());
            xor(encodedFile, decodedFile, XOR_KEY);
            System.out.println("Decode: " + decodedFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
        private static void xor(File inputFile, File outputFile, String key) throws IOException {
            try (FileInputStream fis = new FileInputStream(inputFile);
                 FileOutputStream fos = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[1024];
                byte[] keyBytes = key.getBytes();
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    for (int i = 0; i < bytesRead; i++) {
                        for (int j = 0; j < keyBytes.length; j++) {
                            buffer[i] ^= keyBytes[j];
                        }
                    }
                    fos.write(buffer, 0, bytesRead);
                }
            }
        }
        }
