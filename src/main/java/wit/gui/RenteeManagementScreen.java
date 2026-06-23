package wit.gui;

import wit.domain.Rentee;
import wit.handlers.RenteeHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Locale;

/**
 * Screen for managing rentess/clients (CRUD operations).
 * Allows users to add, edit, delete and view clients.
 */
public class RenteeManagementScreen extends BaseScreen {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete, btnBack;
    private final RenteeHandler handler;

    public RenteeManagementScreen(Locale locale) {
        super("RenteeManagement", locale);
        this.handler = new RenteeHandler();
    }

    @Override
    protected void buildUI() {
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Konfiguracja Tabeli
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

        // Panel Przycisków
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdd = UIFactory.createButton("");
        btnEdit = UIFactory.createButton("");
        btnDelete = UIFactory.createButton("");
        btnBack = UIFactory.createButton("");

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnDelete.addActionListener(e -> deleteSelected());
        btnBack.addActionListener(e -> {
            dispose();
            new MainWindow(bundle.getLocale()).show();
        });

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnBack);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        loadData();
    }

    @Override
    protected void updateTexts() {
        window.setTitle(bundle.getString("window.title"));
        btnAdd.setText(bundle.getString("btn.add"));
        btnEdit.setText(bundle.getString("btn.edit"));
        btnDelete.setText(bundle.getString("btn.delete"));
        btnBack.setText(bundle.getString("btn.back"));

        tableModel.setColumnIdentifiers(new String[]{
                bundle.getString("table.column.document"),
                bundle.getString("table.column.firstname"),
                bundle.getString("table.column.lastname"),
                bundle.getString("table.column.description")
        });
        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Rentee> clients = handler.getAll();
        for (Rentee client : clients) {
            tableModel.addRow(new Object[]{
                    client.getId(),
                    client.getFirstName(),
                    client.getLastName(),
                    client.getDescription()
            });
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog(window, bundle.getString("dialog.add.title"), true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setSize(400, 250);

        JTextField txtDocument = UIFactory.createTextField(); // ID to nr dokumentu
        JTextField txtFirstName = UIFactory.createTextField();
        JTextField txtLastName = UIFactory.createTextField();
        JTextField txtDescription = UIFactory.createTextField();

        dialog.add(UIFactory.createSubLabel(bundle.getString("label.document")));
        dialog.add(txtDocument);
        dialog.add(UIFactory.createSubLabel(bundle.getString("label.firstname")));
        dialog.add(txtFirstName);
        dialog.add(UIFactory.createSubLabel(bundle.getString("label.lastname")));
        dialog.add(txtLastName);
        dialog.add(UIFactory.createSubLabel(bundle.getString("label.description")));
        dialog.add(txtDescription);

        JButton btnSave = UIFactory.createButton(bundle.getString("btn.save"));
        JButton btnCancel = UIFactory.createButton(bundle.getString("btn.cancel"));

        btnSave.addActionListener(e -> {
            String documentId = txtDocument.getText().trim();
            String firstName = txtFirstName.getText().trim();
            String lastName = txtLastName.getText().trim();
            String description = txtDescription.getText().trim();

            if (documentId.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, bundle.getString("error.empty_fields"));
                return;
            }
            try {
                handler.create(documentId, firstName, lastName, description);

                loadData();
                JOptionPane.showMessageDialog(dialog, bundle.getString("success.saved"));
                dialog.dispose();
            }catch (Exception IllegalArgumentException){
                JOptionPane.showMessageDialog(dialog, bundle.getString("dialog.id.exists"));
            }


        });

        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.add(btnSave);
        dialog.add(btnCancel);

        // focus na cancel, zeby nie zaznaczal pierwszego jcombobox
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                btnCancel.requestFocusInWindow();
            }
        });

        dialog.setLocationRelativeTo(window);
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(window, bundle.getString("error.no_selection"));
            return;
        }
        String id = tableModel.getValueAt(selectedRow, 0).toString();
        Rentee client = handler.getById(id).orElse(null); // Brak konwersji na UUID
        if (client == null) return;

        JDialog dialog = new JDialog(window, bundle.getString("dialog.edit.title"), true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setSize(400, 250);

        JTextField txtDocument = UIFactory.createTextField(client.getId()); // Edycja ID (dokumentu) może być zablokowana
        txtDocument.setEditable(false); // ID z reguły nie edytujemy, żeby nie popsuć bazy

        JTextField txtFirstName = UIFactory.createTextField(client.getFirstName());
        JTextField txtLastName = UIFactory.createTextField(client.getLastName());
        JTextField txtDescription = UIFactory.createTextField(client.getDescription());

        dialog.add(UIFactory.createSubLabel(bundle.getString("label.document")));
        dialog.add(txtDocument);
        dialog.add(UIFactory.createSubLabel(bundle.getString("label.firstname")));
        dialog.add(txtFirstName);
        dialog.add(UIFactory.createSubLabel(bundle.getString("label.lastname")));
        dialog.add(txtLastName);
        dialog.add(UIFactory.createSubLabel(bundle.getString("label.description")));
        dialog.add(txtDescription);

        JButton btnSave = UIFactory.createButton(bundle.getString("btn.save"));
        JButton btnCancel = UIFactory.createButton(bundle.getString("btn.cancel"));

        btnSave.addActionListener(e -> {
            String firstName = txtFirstName.getText().trim();
            String lastName = txtLastName.getText().trim();
            String description = txtDescription.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, bundle.getString("error.empty_fields"));
                return;
            }

            handler.update(client.getId(), firstName, lastName, description);

            loadData();
            JOptionPane.showMessageDialog(dialog, bundle.getString("success.saved"));
            dialog.dispose();
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.add(btnSave);
        dialog.add(btnCancel);

        // focus na cancel, zeby nie zaznaczal pierwszego jcombobox
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                btnCancel.requestFocusInWindow();
            }
        });

        dialog.setLocationRelativeTo(window);
        dialog.setVisible(true);
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
            String id = tableModel.getValueAt(selectedRow, 0).toString(); // ID jako String
            handler.delete(id);
            loadData();
            JOptionPane.showMessageDialog(window, bundle.getString("success.deleted"));
        }
    }
}