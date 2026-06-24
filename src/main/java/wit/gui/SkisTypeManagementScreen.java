package wit.gui;

import wit.domain.SkisType;
import wit.handlers.SkisTypeHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Screen for managing ski types (CRUD operations).
 * Allows users to add, edit, delete and view ski types.
 */
public class SkisTypeManagementScreen extends BaseScreen {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete, btnBack;
    private final SkisTypeHandler handler;

    /**
     * Constructor for SkisTypeManagementScreen.
     * @param locale Initial locale for the screen
     */
    public SkisTypeManagementScreen(Locale locale) {
        super("SkiTypeManagement", locale);
        this.handler = new SkisTypeHandler();
    }

    /**
     * Constructs primary UI, including data table and control panel.
     */
    @Override
    protected void buildUI() {
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Table setup
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdd = UIFactory.createButton("");
        btnEdit = UIFactory.createButton("");
        btnDelete = UIFactory.createButton("");
        btnBack = UIFactory.createButton("");

        // Add action listeners
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

    /**
     * Updates all UI text elements using the current localization bundle.
     */
    @Override
    protected void updateTexts() {
        window.setTitle(bundle.getString("window.title"));
        btnAdd.setText(bundle.getString("btn.add"));
        btnEdit.setText(bundle.getString("btn.edit"));
        btnDelete.setText(bundle.getString("btn.delete"));
        btnBack.setText(bundle.getString("btn.back"));

        // Update table column headers
        tableModel.setColumnIdentifiers(new String[]{
                bundle.getString("table.column.id"),
                bundle.getString("table.column.name"),
                bundle.getString("table.column.description")
        });
        loadData();
    }

    /**
     * Loads all ski types from persistence and displays them in the table.
     */
    private void loadData() {
        tableModel.setRowCount(0);
        List<SkisType> types = handler.getAll();
        for (SkisType type : types) {
            tableModel.addRow(new Object[]{
                    type.getId().toString(),
                    type.getName(),
                    type.getDescription()
            });
        }
    }

    /**
     * Shows dialog for adding a new ski type.
     */
    private void showAddDialog() {
        JDialog dialog = new JDialog(window, bundle.getString("dialog.add.title"), true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setSize(400, 200);

        JTextField txtName = UIFactory.createTextField();
        JTextField txtDescription = UIFactory.createTextField();

        dialog.add(UIFactory.createSubLabel(bundle.getString("label.name")));
        dialog.add(txtName);
        dialog.add(UIFactory.createSubLabel(bundle.getString("label.description")));
        dialog.add(txtDescription);

        JButton btnSave = UIFactory.createButton(bundle.getString("btn.save"));
        JButton btnCancel = UIFactory.createButton(bundle.getString("btn.cancel"));

        btnSave.addActionListener(e -> {
            String name = txtName.getText().trim();
            String description = txtDescription.getText().trim();

            if (name.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, bundle.getString("error.empty_fields"));
                return;
            }

            handler.create(name, description);

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

    /**
     * Shows dialog for editing the selected ski type.
     */
    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(window, bundle.getString("error.no_selection"));
            return;
        }

        String id = tableModel.getValueAt(selectedRow, 0).toString();
        SkisType type = handler.getById(UUID.fromString(id)).orElse(null);
        if (type == null) return;

        JDialog dialog = new JDialog(window, bundle.getString("dialog.edit.title"), true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setSize(400, 200);

        JTextField txtName = UIFactory.createTextField(type.getName());
        JTextField txtDescription = UIFactory.createTextField(type.getDescription());

        dialog.add(UIFactory.createSubLabel(bundle.getString("label.name")));
        dialog.add(txtName);
        dialog.add(UIFactory.createSubLabel(bundle.getString("label.description")));
        dialog.add(txtDescription);

        JButton btnSave = UIFactory.createButton(bundle.getString("btn.save"));
        JButton btnCancel = UIFactory.createButton(bundle.getString("btn.cancel"));

        btnSave.addActionListener(e -> {
            String name = txtName.getText().trim();
            String description = txtDescription.getText().trim();

            if (name.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, bundle.getString("error.empty_fields"));
                return;
            }

            handler.update(type.getId(), name, description);

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

    /**
     * Deletes the selected ski type after confirmation.
     */
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

            boolean success = handler.delete(UUID.fromString(id));

            if (success) {
                loadData();
                JOptionPane.showMessageDialog(window, bundle.getString("success.deleted"));
            }else{
                JOptionPane.showMessageDialog(window, bundle.getString("failed.deleted"));
            }
        }
    }
}
