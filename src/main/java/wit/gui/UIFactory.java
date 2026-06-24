package wit.gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * A utility factory class delivering
 * helper methods for creating consistently
 * styled Swing UI components.
 */
public final class UIFactory {


    /**
     * Private constructor to prevent
     * instantiation of this utility class.
     */
    private UIFactory(){}

    /**
     * Creates a large, bold label for main window titles and greetings.
     *
     * @param text The text to be displayed on the label.
     * @return A styled {@link JLabel} with a large bold font (size 30).
     */
    public static JLabel createLabel(String text){
        JLabel label = new JLabel(text);

        label.setFont(new Font("Arial", Font.BOLD, 30));

        return label;
    }

    /**
     * Creates a smaller, bold label used for form inputs.
     *
     * @param text The text to be displayed on the label.
     * @return A styled {@link JLabel} with a medium bold font (size 15).
     */
    public static JLabel createSubLabel(String text){
        JLabel label = new JLabel(text);

        label.setFont(new Font("Arial", Font.BOLD, 15));

        return label;
    }

    /**
     * Creates a simple button with consistent colors.
     * The method controls internal padding and a hand cursor on hover.
     *
     * @param text The text to be displayed on the button.
     * @return Styled {@link JButton}.
     */
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

    /**
     * Creates a button represented by an image icon.
     * Meant for elements like language selection flags.
     *
     * @param path   The relative resource path to the image (e.g., "/icons/en.png").
     * @param width  The desired width to scale the image to.
     * @param height The desired height to scale the image to.
     * @return A {@link JButton} displaying the image, or an error text button if the image is missing.
     */
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

    /**
     * Creates a customized dropdown box with dynamic border styling.
     * The border changes when in focus.
     *
     * @param items An array of items to be displayed in the list.
     * @param <T>   The type of elements in the combo box.
     * @return A styled {@link JComboBox} with hover and focus.
     */
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
    /**
     * Creates a customized text field.
     * Border changes when in focus.
     *
     * @param defaultText
     * @return A styled {@link JTextField}.
     */
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

    /**
     * Creates an empty, customized text field.
     *
     * @return A styled, empty {@link JTextField}.
     */
    public static JTextField createTextField() {
        return createTextField("");
    }

}


