package kr;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Time extends JFrame {
    public static void main(String[] args) {
        Time frame = new Time("Current time");
        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        timeT = new TimeThread();
        threadPool.execute(timeT);
    }
    static TimeThread timeT;
    static JLabel time = new JLabel("Time");
    static JButton pause = new JButton("Pause");
    static JButton resume = new JButton("Resume");

    Time(String title){
        setTitle(title);
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(time, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(pause);
        buttonPanel.add(resume);
        add(buttonPanel, BorderLayout.SOUTH);
        pause.addActionListener(e ->
        {
            timeT.pauseThread();
        });
        resume.addActionListener(e -> {
            timeT.resumeThread();
        });
        setVisible(true);
    }

    static class TimeThread implements Runnable {
        private static final Lock lock = new ReentrantLock();

        public void pauseThread(){
            lock.lock();
        }

        public void resumeThread(){
            lock.unlock();
        }

        public void run() {
            while (true) {
                    lock.lock();
                    try {
                        time.setText(LocalTime.now().toString());
                    } finally {
                        lock.unlock();
                    }
            }
        }
    }
}
