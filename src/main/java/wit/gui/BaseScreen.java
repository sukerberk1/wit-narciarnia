package wit.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

public abstract class BaseScreen {
    protected JFrame window;
    protected JPanel mainPanel;
    protected ResourceBundle bundle;
    protected String bundleName;

    public BaseScreen(String bundleName,Locale initialLocale) {
        this.bundle = ResourceBundle.getBundle(bundleName, initialLocale);
        this.bundleName = bundleName;

        window = new JFrame();
        window.setSize(600, 500);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new BorderLayout());

        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnPL = UIFactory.createIconButton("/icons/pl.png", 60, 30);
        JButton btnEN = UIFactory.createIconButton("/icons/en.png", 60, 30);

        btnPL.addActionListener(e -> switchLanguage(new Locale("pl", "PL")));
        btnEN.addActionListener(e -> switchLanguage(new Locale("en", "GB")));

        langPanel.add(btnPL);
        langPanel.add(btnEN);
        window.add(langPanel, BorderLayout.SOUTH);


        //pusty panel na content okien
        mainPanel = new JPanel();
        window.add(mainPanel, BorderLayout.CENTER);

        buildUI();
        updateTexts();
    }

    //tworzenie layoutu w mainPanel
    protected abstract void buildUI();

    //ustawienie tekstów
    protected abstract void updateTexts();

    private void switchLanguage(Locale newLocale) {
        this.bundle = ResourceBundle.getBundle(bundleName, newLocale);
        updateTexts();
    }

    public void show() {
        window.setVisible(true);
    }

    public void dispose() {
        window.dispose();
    }
}