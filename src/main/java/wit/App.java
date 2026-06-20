package wit;

import wit.gui.MainWindow;

import javax.swing.SwingUtilities;
import java.util.Locale;

/**
 * Główna klasa uruchamiająca aplikację wypożyczalni nart.
 */
public class App {

    /**
     * Uruchamia interfejs graficzny aplikacji.
     *
     * @param args argumenty uruchomieniowe
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow =
                    new MainWindow(new Locale("pl", "PL"));

            mainWindow.show();
        });
    }
}