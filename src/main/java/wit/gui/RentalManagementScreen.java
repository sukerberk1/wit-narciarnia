package wit.gui;

import wit.domain.Rental;
import wit.domain.Rentee;
import wit.domain.Skis;
import wit.persistence.RentalPersistence;
import wit.persistence.RenteePersistence;
import wit.persistence.SkisPersistence;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class RentalManagementScreen extends BaseScreen {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnReturn, btnDelete, btnBack;

    private final RentalPersistence rentalPersistence;
    private final RenteePersistence renteePersistence;
    private final SkisPersistence skisPersistence;

    private final DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public RentalManagementScreen(Locale locale) {
        super("RentalManagement", locale);
        this.rentalPersistence = new RentalPersistence();
        this.renteePersistence = new RenteePersistence();
        this.skisPersistence = new SkisPersistence();
    }

    @Override
    protected void buildUI() {
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdd = UIFactory.createButton("");
        btnReturn = UIFactory.createButton(""); // Przycisk do zwrotu nart
        btnDelete = UIFactory.createButton("");
        btnBack = UIFactory.createButton("");

        btnAdd.addActionListener(e -> showAddDialog());
        btnReturn.addActionListener(e -> processReturn());
        btnDelete.addActionListener(e -> deleteSelected());
        btnBack.addActionListener(e -> {
            dispose();
            new MainWindow(bundle.getLocale()).show();
        });

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnReturn);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnBack);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        loadData();
    }

    @Override
    protected void updateTexts() {
        window.setTitle(bundle.getString("window.title"));
        btnAdd.setText(bundle.getString("btn.add"));
        btnReturn.setText(bundle.getString("btn.return"));
        btnDelete.setText(bundle.getString("btn.delete"));
        btnBack.setText(bundle.getString("btn.back"));

        tableModel.setColumnIdentifiers(new String[]{
                bundle.getString("table.column.id"),
                bundle.getString("table.column.client"),
                bundle.getString("table.column.skis"),
                bundle.getString("table.column.start"),
                bundle.getString("table.column.end"),
                bundle.getString("table.column.status")
        });
        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Rental> rentals = rentalPersistence.findAll();
        for (Rental rental : rentals) {
            String clientInfo = rental.getRentee().getFirstName() + " " + rental.getRentee().getLastName();
            String skisInfo = rental.getSkis().getBrand() + " " + rental.getSkis().getModel();
            String status = rental.isEnded() ? bundle.getString("status.returned") : bundle.getString("status.active");

            tableModel.addRow(new Object[]{
                    rental.getId().toString(),
                    clientInfo,
                    skisInfo,
                    rental.getBeginDate().format(displayFormatter),
                    rental.getPlannedEndDate().format(displayFormatter),
                    status
            });
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog(window, bundle.getString("dialog.add.title"), true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setSize(500, 300);

        // Rozwijane listy dla klientów i nart
        JComboBox<Rentee> cbRentee = new JComboBox<>(renteePersistence.findAll().toArray(new Rentee[0]));
        JComboBox<Skis> cbSkis = new JComboBox<>(skisPersistence.findAll().toArray(new Skis[0]));

        // Renderery, żeby w liście wyświetlał się ładny tekst, a nie hash obiektu
        cbRentee.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Rentee) {
                    Rentee r = (Rentee) value;
                    setText(r.getFirstName() + " " + r.getLastName() + " (" + r.getId() + ")");
                }
                return this;
            }
        });

        cbSkis.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Skis) {
                    Skis s = (Skis) value;
                    setText(s.getBrand() + " " + s.getModel() + " (" + s.getLength() + "cm)");
                }
                return this;
            }
        });

        // Pola na daty (domyślnie teraz i za 2 godziny)
        JTextField txtStartDate = new JTextField(LocalDateTime.now().format(displayFormatter));
        JTextField txtEndDate = new JTextField(LocalDateTime.now().plusHours(2).format(displayFormatter));

        dialog.add(new JLabel(bundle.getString("label.client")));
        dialog.add(cbRentee);
        dialog.add(new JLabel(bundle.getString("label.skis")));
        dialog.add(cbSkis);
        dialog.add(new JLabel(bundle.getString("label.start_date") + " (yyyy-MM-dd HH:mm):"));
        dialog.add(txtStartDate);
        dialog.add(new JLabel(bundle.getString("label.end_date") + " (yyyy-MM-dd HH:mm):"));
        dialog.add(txtEndDate);

        JButton btnSave = UIFactory.createButton(bundle.getString("btn.save"));
        JButton btnCancel = UIFactory.createButton(bundle.getString("btn.cancel"));

        btnSave.addActionListener(e -> {
            try {
                Rentee selectedRentee = (Rentee) cbRentee.getSelectedItem();
                Skis selectedSkis = (Skis) cbSkis.getSelectedItem();
                LocalDateTime start = LocalDateTime.parse(txtStartDate.getText().trim(), displayFormatter);
                LocalDateTime end = LocalDateTime.parse(txtEndDate.getText().trim(), displayFormatter);

                if (selectedRentee == null || selectedSkis == null) {
                    JOptionPane.showMessageDialog(dialog, bundle.getString("error.no_selection"));
                    return;
                }

                // Zakładam konstruktor Rental z podanymi parametrami (dostosuj jeśli masz inny!)
                Rental newRental = new Rental(
                        UUID.randomUUID(),
                        selectedRentee,
                        selectedSkis,
                        start,
                        end,
                        end, // Secondary Planned
                        end, // Actual End
                        false // isEnded
                );

                rentalPersistence.save(newRental);
                loadData();
                JOptionPane.showMessageDialog(dialog, bundle.getString("success.saved"));
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, bundle.getString("error.date_format"));
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.add(btnSave);
        dialog.add(btnCancel);
        dialog.setLocationRelativeTo(window);
        dialog.setVisible(true);
    }

    private void processReturn() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(window, bundle.getString("error.no_selection"));
            return;
        }

        String id = tableModel.getValueAt(selectedRow, 0).toString();
        Rental rental = rentalPersistence.findById(UUID.fromString(id)).orElse(null);

        if (rental == null || rental.isEnded()) {
            JOptionPane.showMessageDialog(window, bundle.getString("error.already_returned"));
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(window,
                bundle.getString("dialog.return.confirm"),
                bundle.getString("btn.return"),
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Rental updatedRental = new Rental(
                    rental.getId(),
                    rental.getRentee(),
                    rental.getSkis(),
                    rental.getBeginDate(),
                    rental.getPlannedEndDate(),
                    rental.getSecondaryPlannedEndDate(),
                    LocalDateTime.now(),
                    true
            );

            rentalPersistence.update(updatedRental);
            loadData();
            JOptionPane.showMessageDialog(window, bundle.getString("success.returned"));
        }
    }

    private void deleteSelected() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(window, bundle.getString("error.no_selection"));
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(window,
                bundle.getString("dialog.delete.confirm"),
                bundle.getString("btn.delete"),
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String id = tableModel.getValueAt(selectedRow, 0).toString();
            rentalPersistence.delete(UUID.fromString(id));
            loadData();
        }
    }
}