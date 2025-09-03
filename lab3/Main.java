package lab3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main {
    private static ResourceBundle messages;
    private static JFrame frame;
    private static JButton calculateButton;
    private static JLabel resultLabel;
    private static JLabel labNameFunc;
    private static JLabel labParam;
    private static JLabel date;
    private static JLabel money;
    private static DateFormat dateFormat;
   private static NumberFormat currencyFormatter;
    private static JComboBox<String> methodParamsComboBox;
    private static ArrayList<Method> availableMethods;
    private static JButton b;
    private static JTextField methodName;

    public static void main(String[] args) {
        Locale local = new Locale("ru", "RU");
        messages = ResourceBundle.getBundle("lab3.MessagesBundle", local);
        createFrmae();
    }


    private static String getMethodSignature(Method method) {
        StringBuilder signature = new StringBuilder(method.getName() + "(");
        Class<?>[] paramTypes = method.getParameterTypes();

        for (int i = 0; i < paramTypes.length; i++) {
            signature.append(paramTypes[i].getSimpleName());
            if (i < paramTypes.length - 1) {
                signature.append(", ");
            }
        }
        signature.append(")");
        return signature.toString();
    }
    private static Object[] convertParams(Method method, String[] params) throws NumberFormatException {
        Class<?>[] paramTypes = method.getParameterTypes();
        Object[] paramValues = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            String param = params[i].trim();
            if (paramTypes[i] == int.class) {
                paramValues[i] = Integer.parseInt(param);
            } else if (paramTypes[i] == double.class) {
                paramValues[i] = Double.parseDouble(param);
            } else if (paramTypes[i] == long.class) {
                paramValues[i] = Long.parseLong(param);
            }else if (paramTypes[i] == float.class) {
                paramValues[i] = Float.parseFloat(param);
            }
        }
        return paramValues;
    }
    private static Object invokeMathMethod(int methodIndex, String[] params) throws InvocationTargetException, IllegalAccessException {
            Method method = availableMethods.get(methodIndex);
        Object[] paramValues = new Object[0];
        try {
            paramValues = convertParams(method, params);
            return method.invoke(null, paramValues);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Wrong data!!!!!!!!");
            return "";
        }
    }
 public static void update() {
     labNameFunc.setText(messages.getString("method.name"));
     labParam.setText(messages.getString("method.params"));
     calculateButton.setText(messages.getString("button.calculate"));
     resultLabel.setText(messages.getString("result.label"));
     frame.setTitle(messages.getString("app.title"));
     date.setText(messages.getString("date.label"));
     money.setText(messages.getString("money.label"));
     b.setText(messages.getString("search.name"));

     dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, messages.getLocale());
     String date1 = dateFormat.format(new Date());
     date.setText(messages.getString("date.label") + " " + date1);

     currencyFormatter = NumberFormat.getCurrencyInstance(messages.getLocale());
     Double currency = 525600.10;
     money.setText(messages.getString("money.label") + " " + currencyFormatter.format(currency));
 }
    public static void createFrmae(){
        frame = new JFrame(messages.getString("app.title"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        String[] items = {
                "ru",
                "en",
                "fr",
        };
        JComboBox editComboBox = new JComboBox(items);
        editComboBox.setBounds(10, 10, 40, 20);
        editComboBox.setEditable(true);
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox)e.getSource();
                String item = (String)box.getSelectedItem();
                if (item.equals("ru")) {
                    messages = ResourceBundle.getBundle("lab3.MessagesBundle", new Locale("ru", "RU"));
                    update();
                } else if (item.equals("en")){
                    messages = ResourceBundle.getBundle("lab3.MessagesBundle", new Locale("en", "GB"));
                    update();
                }else{
                    messages = ResourceBundle.getBundle("lab3.MessagesBundle", new Locale("fr", "FR"));
                    update();
                }
            }
        };
        editComboBox.addActionListener(actionListener);
        labNameFunc = new JLabel(messages.getString("method.name"));
        labNameFunc.setBounds(10, 35, 200, 10);
        b = new JButton(messages.getString("search.name"));
        b.setBounds(200, 25, 200, 20);
        frame.add(b);
        methodName = new JTextField();
        methodName.setBounds(10, 50, 370, 20);
        labParam = new JLabel(messages.getString("method.params"));
        labParam.setBounds(10, 75, 370, 20);
        JTextField methodParams = new JTextField();
        methodParams.setBounds(10, 105, 370, 20);
        calculateButton = new JButton(messages.getString("button.calculate"));
        calculateButton.setBounds(10, 130, 370, 20);
        resultLabel = new JLabel(messages.getString("result.label"));
        resultLabel.setBounds(10, 155, 370, 20);
        date = new JLabel(messages.getString("date.label"));
        date.setBounds(10, 180, 370, 20);
        dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, messages.getLocale());
        String date1 = dateFormat.format(new Date());
        date.setText(messages.getString("date.label") + " " + date1);
        money = new JLabel(messages.getString("money.label"));
        money.setBounds(10, 205, 370, 20);
        currencyFormatter = NumberFormat.getCurrencyInstance(messages.getLocale());
        Double currency = 1545.20;
        money.setText(messages.getString("money.label") + " " + currencyFormatter.format(currency));


        methodParamsComboBox = new JComboBox<>();
        methodParamsComboBox.setBounds(10, 230, 370, 20);
        frame.add(methodParamsComboBox);
        frame.add(date);
        frame.add(money);
        frame.add(editComboBox);
        frame.add(labNameFunc);
        frame.add(methodName);
        frame.add(labParam);
        frame.add(methodParams);
        frame.add(calculateButton);
        frame.add(resultLabel);
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int methodIndex = methodParamsComboBox.getSelectedIndex();
                String[] params = null;
                if (!methodParams.getText().isBlank()) {
                    params = methodParams.getText().split(", ");
                }
                Object result = null;
                try {
                    if (params != null) {
                        result = invokeMathMethod(methodIndex, params);
                    } else {
                        result = Math.random();
                    }
                } catch (InvocationTargetException | IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
                resultLabel.setText(messages.getString("result.label") + " " + result);

            }
        });
b.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        updateMethodParams();
    }
});
        frame.setVisible(true);
    }

    private static void updateMethodParams() {
        String methodName1 = methodName.getText().trim();
        availableMethods = new ArrayList<>();

        methodParamsComboBox.removeAllItems();

        for (Method method : Math.class.getMethods()) {
            if (method.getName().equals(methodName1)) {
                availableMethods.add(method);
                methodParamsComboBox.addItem(getMethodSignature(method));
            }
        }

        if (availableMethods.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Метод не найден!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}