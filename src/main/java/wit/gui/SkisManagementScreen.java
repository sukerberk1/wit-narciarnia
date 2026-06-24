package wit.gui;


import wit.domain.SkiTiesType;
import wit.domain.Skis;
import wit.domain.SkisType;
import wit.handlers.SkisHandler;
import wit.handlers.SkisTypeHandler;
import wit.persistence.SkisTypePersistence;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Screen for managing ski inventory (CRUD operations)
 * Allows users to add, edit, delete, and view individual ski units.
 */
public class SkisManagementScreen extends BaseScreen {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete, btnBack;

    private final SkisHandler skisHandler;
    private final SkisTypeHandler typeHandler;

    /**
     * Initializes ski management screen and necessary data handlers.
     *
     * @param loc Initial language locale for UI.
     */
    public SkisManagementScreen(Locale loc) {
        super("SkisManagement", loc);
        this.skisHandler = new SkisHandler();
        this.typeHandler = new SkisTypeHandler();
    }

    /**
     * Constructs primary UI, including data table and control panel.
     */
    @Override
    protected void buildUI() {
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int Column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

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

    /**
     * Updates all UI text elements using current localization bundle.
     */
    @Override
    protected void updateTexts() {
        window.setTitle(bundle.getString("window.title"));
        btnAdd.setText(bundle.getString("btn.add"));
        btnEdit.setText(bundle.getString("btn.edit"));
        btnDelete.setText(bundle.getString("btn.delete"));
        btnBack.setText(bundle.getString("btn.back"));

        tableModel.setColumnIdentifiers(new String[]{
                bundle.getString("table.column.id"),
                bundle.getString("table.column.type"),
                bundle.getString("table.column.brand"),
                bundle.getString("table.column.model"),
                bundle.getString("table.column.ties"),
                bundle.getString("table.column.length")
        });
        loadData();
    }

    /**
     * Fetches all ski records from handler and populates the table.
     */
    private void loadData() {
        tableModel.setRowCount(0);
        List<Skis> skisList = skisHandler.getAll();
        for (Skis skis : skisList) {
            tableModel.addRow(new Object[]{
                    skis.getId().toString(),
                    skis.getType().getName(),
                    skis.getBrand(),
                    skis.getModel(),
                    skis.getTies().toString(),
                    skis.getLength().toString()
            });
        }
    }

    /**
     * Displays a dialog allowing the user to add a new pair of skis to the inventory.
     */
    private void showAddDialog() {
        JDialog dialog = new JDialog(window, bundle.getString("dialog.add.title"), true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(500, 350);

        JComboBox<SkisType> cbType = UIFactory.createComboBox(typeHandler.getAll().toArray(new SkisType[0]));
        JComboBox<SkiTiesType> cbTies = UIFactory.createComboBox(SkiTiesType.values());

        cbType.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SkisType) {
                    setText(((SkisType) value).getName());
                }
                return this;
            }
        });

        JTextField txtBrand = UIFactory.createTextField();
        JTextField txtModel = UIFactory.createTextField();
        JTextField txtLength = UIFactory.createTextField();

        dialog.add(UIFactory.createSubLabel(bundle.getString("label.type")));
        dialog.add(cbType);
        dialog.add(UIFactory.createSubLabel(bundle.getString("label.brand")));
        dialog.add(txtBrand);
        dialog.add(UIFactory.createSubLabel(bundle.getString("label.model")));
        dialog.add(txtModel);
        dialog.add(UIFactory.createSubLabel(bundle.getString("label.ties")));
        dialog.add(cbTies);
        dialog.add(UIFactory.createSubLabel(bundle.getString("label.length")));
        dialog.add(txtLength);

        JButton btnSave = UIFactory.createButton(bundle.getString("btn.save"));
        JButton btnCancel = UIFactory.createButton(bundle.getString("btn.cancel"));

        btnSave.addActionListener(e -> {
            try {
                SkisType selectedType = (SkisType) cbType.getSelectedItem();
                SkiTiesType selectedTies = (SkiTiesType) cbTies.getSelectedItem();
                String brand = txtBrand.getText().trim();
                String model = txtModel.getText().trim();

                Double length = Double.parseDouble(txtLength.getText().trim());

                if (selectedType == null || selectedTies == null || brand.isEmpty() || model.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, bundle.getString("error.empty_fields"));
                    return;
                }

                skisHandler.create(selectedType, brand, model, selectedTies, length);

                loadData();
                JOptionPane.showMessageDialog(dialog, bundle.getString("success.saved"));
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, bundle.getString("error.invalid_number"));
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.add(btnSave);
        dialog.add(btnCancel);

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
     * Displays a dialog allowing the user to edit details of selected skis.
     */
    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(window, bundle.getString("error.no_selection"));
            return;
        }

        String id = tableModel.getValueAt(selectedRow, 0).toString();

        Skis skis = skisHandler.getById(UUID.fromString(id)).orElse(null);
        if (skis == null) return;

        JDialog dialog = new JDialog(window, bundle.getString("dialog.edit.title"), true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(500, 350);

        JComboBox<SkisType> cbType = UIFactory.createComboBox(typeHandler.getAll().toArray(new SkisType[0]));
        JComboBox<SkiTiesType> cbTies = UIFactory.createComboBox(SkiTiesType.values());

        cbType.setSelectedItem(skis.getType());
        cbTies.setSelectedItem(skis.getTies());

        cbType.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SkisType) {
                    setText(((SkisType) value).getName());
                }
                return this;
            }
        });

        JTextField txtBrand = UIFactory.createTextField(skis.getBrand());
        JTextField txtModel = UIFactory.createTextField(skis.getModel());
        JTextField txtLength = UIFactory.createTextField(skis.getLength().toString());

        dialog.add(UIFactory.createSubLabel(bundle.getString("label.type")));
        dialog.add(cbType);
        dialog.add(UIFactory.createSubLabel(bundle.getString("label.brand")));
        dialog.add(txtBrand);
        dialog.add(UIFactory.createSubLabel(bundle.getString("label.model")));
        dialog.add(txtModel);
        dialog.add(UIFactory.createSubLabel(bundle.getString("label.ties")));
        dialog.add(cbTies);
        dialog.add(UIFactory.createSubLabel(bundle.getString("label.length")));
        dialog.add(txtLength);

        JButton btnSave = UIFactory.createButton(bundle.getString("btn.save"));
        JButton btnCancel = UIFactory.createButton(bundle.getString("btn.cancel"));

        btnSave.addActionListener(e -> {
            try {
                SkisType selectedType = (SkisType) cbType.getSelectedItem();
                SkiTiesType selectedTies = (SkiTiesType) cbTies.getSelectedItem();
                String brand = txtBrand.getText().trim();
                String model = txtModel.getText().trim();
                Double length = Double.parseDouble(txtLength.getText().trim());

                if (selectedType == null || selectedTies == null || brand.isEmpty() || model.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, bundle.getString("error.empty_fields"));
                    return;
                }

                skisHandler.update(skis.getId(), selectedType, brand, model, selectedTies, length);


                loadData();
                JOptionPane.showMessageDialog(dialog, bundle.getString("success.saved"));
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, bundle.getString("error.invalid_number"));
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.add(btnSave);
        dialog.add(btnCancel);

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
     * Deletes currently selected ski record after user confirmation.
     * Displays a success or failure message based on the handler's response.
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

            boolean success = skisHandler.delete(UUID.fromString(id));

            if (success) {
                loadData();
                JOptionPane.showMessageDialog(window, bundle.getString("success.deleted"));
            } else {
                JOptionPane.showMessageDialog(window, bundle.getString("failed.deleted"));
            }
        }
    }
}


