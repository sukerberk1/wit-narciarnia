package wit.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class MainWindow extends BaseScreen {

    //rzeczy w których jest zmienny tekst
    private JLabel greeting;
    private JButton btnRental;
    private JButton btnRentee;
    private JButton btnSkiis;

    public MainWindow(Locale locale){
        super("mainwindow" ,locale);
    }


    @Override
    protected void buildUI(){
        mainPanel.setLayout(new GridLayout(4,1,0,10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        greeting = UIFactory.createLabel("");
        btnRental = UIFactory.createButton("");
        btnRentee = UIFactory.createButton("");
        btnSkiis = UIFactory.createButton("");


        btnRentee.addActionListener(e -> {
            dispose();
            new RenteeManagementScreen(bundle.getLocale()).show();
        });
        btnSkiis.addActionListener(e -> {
            dispose();
            new SkisTypeManagementScreen(bundle.getLocale()).show();
        });

        mainPanel.add(greeting);
        mainPanel.add(btnRental);
        mainPanel.add(btnRentee);
        mainPanel.add(btnSkiis);

    }

    @Override
    protected void updateTexts(){
        window.setTitle(bundle.getString("window.title"));
        greeting.setText(bundle.getString("title.greeting"));
        btnRental.setText(bundle.getString("btn.rental"));
        btnRentee.setText(bundle.getString("btn.client"));
        btnSkiis.setText(bundle.getString("btn.skiis"));
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            MainWindow app = new MainWindow(new Locale("pl","PL"));
            app.show();
        });
    }
}
