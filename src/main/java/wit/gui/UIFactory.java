package wit.gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/*
* Tutaj są takie helper metody -
*
* np createButton od razu styluje przyciski
* narazie tylko ustawia kursor ale potem możemy
* wiecej rzeczy ustawiać z jednego miejsca
*
* */

public final class UIFactory {

    private UIFactory(){}

    public static JLabel createLabel(String text){
        JLabel label = new JLabel(text);

        label.setFont(new Font("Arial", Font.BOLD, 30));

        return label;
    }

    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false); // Removes the dotted line when clicked
        button.setBorderPainted(false); // Removes the 3D border
        button.setOpaque(true); // Forces the background color to actually render (crucial on Mac)

        Color defaultColor = new Color(100, 161, 200, 255);
        button.setBackground(defaultColor);
        button.setForeground(Color.WHITE); // White text
        button.setFont(new Font("Arial", Font.BOLD, 20)); // Overrides global font if needed

        // 3. Add internal padding (Top, Left, Bottom, Right) - like CSS padding
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Changes mouse to a hand on hover
        return button;
    }

    public static JButton createIconButton(String path, int width, int height) {
        // Find the image file in the resources folder safely
        URL imgUrl = MainWindow.class.getResource(path);

        if (imgUrl == null) {
            System.err.println("Could not find image: " + path);
            return new JButton("Error"); // Fallback if image is missing
        }

        // Load the image
        ImageIcon originalIcon = new ImageIcon(imgUrl);

        // Resize the image smoothly
        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JButton button = new JButton(scaledIcon);

        // PRO TIP: Make the button look just like a clickable image (no ugly borders)
        button.setFocusPainted(false); // Removes the square focus outline
        button.setContentAreaFilled(false); // Removes the gray background
        button.setBorderPainted(false); // Removes the 3D border
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Changes mouse to a hand on hover

        return button;
    }
}
