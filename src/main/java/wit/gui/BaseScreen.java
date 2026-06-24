package wit.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

/*
* Abstract base class for all application screens.
* */

public abstract class BaseScreen {
    protected JFrame window;
    protected JPanel mainPanel;
    protected ResourceBundle bundle;
    protected String bundleName;

    private boolean isInitialized = false;

    /**
     * Initializes the window and sets up the language selection panel.
     *
     * @param bundleName Base name of resource bundle.
     * @param initialLocale Starting language locale.
     */
    public BaseScreen(String bundleName,Locale initialLocale) {
        this.bundle = ResourceBundle.getBundle(bundleName, initialLocale);
        this.bundleName = bundleName;

        window = new JFrame();
        window.setSize(800, 600);
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
    }

    /**
     * Implemented by subclasses.
     * Populates panel with UI components.
     */
    protected abstract void buildUI();

    /**
     * Updates component texts.
     * Used for switching languages.
     * Implemented by subclasses.
     */
    protected abstract void updateTexts();

    /**
     * Switches language and refreshes UI.
     *
     * @param newLocale The target locale to switch to.
     */
    private void switchLanguage(Locale newLocale) {
        this.bundle = ResourceBundle.getBundle(bundleName, newLocale);
        updateTexts();
    }

    /**
     * Builds the UI (if not initialized) and displays the window.
     */
    public void show() {
        if (!isInitialized) {
            buildUI();
            updateTexts();
            isInitialized = true;
        }
        window.setVisible(true);
    }

    /**
     * Close and dispose of window.
     */
    public void dispose() {
        window.dispose();
    }
}