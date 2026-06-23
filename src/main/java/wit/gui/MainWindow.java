package wit.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class MainWindow extends BaseScreen {

    //rzeczy w których jest zmienny tekst
    private JLabel greeting;
    private JButton btnRental, btnRentee, btnSkiTypes, btnSkis;


    public MainWindow(Locale locale){
        super("mainwindow" ,locale);
    }


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

    @Override
    protected void updateTexts(){
        window.setTitle(bundle.getString("window.title"));
        greeting.setText(bundle.getString("title.greeting"));
        btnRental.setText(bundle.getString("btn.rental"));
        btnRentee.setText(bundle.getString("btn.client"));
        btnSkiTypes.setText(bundle.getString("btn.skiType"));
        btnSkis.setText(bundle.getString("btn.skis"));
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            MainWindow app = new MainWindow(new Locale("pl","PL"));
            app.show();
        });
    }
}
