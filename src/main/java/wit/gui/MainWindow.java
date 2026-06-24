package wit.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;


/**
 * Entry point and navigation menu.
 * Allows users to access different management screens.
 */
public class MainWindow extends BaseScreen {

    /**
     * Initializes main window with specified locale.
     *
     * @param locale Initial language setting for UI.
     */
    private JLabel greeting;
    private JButton btnRental, btnRentee, btnSkiTypes, btnSkis;

    public MainWindow(Locale locale){
        super("mainwindow" ,locale);
    }

    /**
     * Constructs main menu layout.
     * Initializes grid layout, creates buttons via UIFactory,
     * sets up action listeners for navigation.
     */
    @Override
    protected void buildUI(){
        mainPanel.setLayout(new GridLayout(5,1,0,10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        greeting = UIFactory.createLabel("");
        btnRental = UIFactory.createButton("");
        btnRentee = UIFactory.createButton("");
        btnSkiTypes = UIFactory.createButton("");
        btnSkis = UIFactory.createButton("");

        btnRental.addActionListener(e -> {
            dispose();
            new RentalManagementScreen(bundle.getLocale()).show();
        });
        btnRentee.addActionListener(e -> {
            dispose();
            new RenteeManagementScreen(bundle.getLocale()).show();
        });
        btnSkiTypes.addActionListener(e -> {
            dispose();
            new SkisTypeManagementScreen(bundle.getLocale()).show();
        });
        btnSkis.addActionListener(e -> {
            dispose();
            new SkisManagementScreen(bundle.getLocale()).show();
        });

        mainPanel.add(greeting);
        mainPanel.add(btnRental);
        mainPanel.add(btnRentee);
        mainPanel.add(btnSkiTypes);
        mainPanel.add(btnSkis);

    }

    /**
     * Refreshes text components with values from current resource bundle.
     * Triggered during initialization and whenever the language is switched.
     */
    @Override
    protected void updateTexts(){
        window.setTitle(bundle.getString("window.title"));
        greeting.setText(bundle.getString("title.greeting"));
        btnRental.setText(bundle.getString("btn.rental"));
        btnRentee.setText(bundle.getString("btn.client"));
        btnSkiTypes.setText(bundle.getString("btn.skiType"));
        btnSkis.setText(bundle.getString("btn.skis"));
    }

    /**
     * Entry point.
     * Launches GUI with default Polish locale.
     *
     * @param args CLI arguments (ignored).
     */
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            MainWindow app = new MainWindow(new Locale("pl","PL"));
            app.show();
        });
    }
}
