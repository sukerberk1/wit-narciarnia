package wit.gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Klasa factory zapewniająca komponenty UI
 */
public final class UIFactory {

    private UIFactory(){}

    public static JLabel createLabel(String text){
        JLabel label = new JLabel(text);

        label.setFont(new Font("Arial", Font.BOLD, 30));

        return label;
    }

    public static JLabel createSubLabel(String text){
        JLabel label = new JLabel(text);

        label.setFont(new Font("Arial", Font.BOLD, 15));

        return label;
    }

    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        Color defaultColor = new Color(100, 161, 200, 255);
        button.setBackground(defaultColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 20));

        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JButton createIconButton(String path, int width, int height) {
        URL imgUrl = MainWindow.class.getResource(path);

        if (imgUrl == null) {
            System.err.println("Could not find image: " + path);
            return new JButton("Error");
        }

        ImageIcon originalIcon = new ImageIcon(imgUrl);

        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JButton button = new JButton(scaledIcon);

        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);

        comboBox.setFont(new Font("Arial", Font.PLAIN, 18));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(Color.DARK_GRAY);

        comboBox.setFocusable(true);
        comboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color themeColor = new Color(100, 161, 200, 255);
        javax.swing.border.Border defaultBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1);
        javax.swing.border.Border focusedBorder = BorderFactory.createLineBorder(themeColor, 2);

        comboBox.setBorder(defaultBorder);

        comboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                comboBox.setBorder(focusedBorder);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                comboBox.setBorder(defaultBorder);
            }
        });

        return comboBox;
    }

    public static JTextField createTextField(String defaultText) {
        JTextField textField = new JTextField(defaultText);

        textField.setFont(new Font("Arial", Font.PLAIN, 18));
        textField.setBackground(Color.WHITE);
        textField.setForeground(Color.DARK_GRAY);
        textField.setCaretColor(Color.DARK_GRAY); // Keeps the blinking text cursor visible

        Color themeColor = new Color(100, 161, 200, 255);
        Color defaultBorderColor = Color.LIGHT_GRAY;

        javax.swing.border.Border padding = BorderFactory.createEmptyBorder(8, 10, 8, 10);

        javax.swing.border.Border defaultBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(defaultBorderColor, 1),
                padding
        );
        javax.swing.border.Border focusedBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeColor, 2),
                padding
        );

        textField.setBorder(defaultBorder);

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (textField.isEditable()) {
                    textField.setBorder(focusedBorder);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                textField.setBorder(defaultBorder);
            }
        });

        return textField;
    }

    //dla pustych
    public static JTextField createTextField() {
        return createTextField("");
    }

}


