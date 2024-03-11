package com.diana.controller;

import com.diana.model.MyTableModel;
import com.diana.model.dao.EquipmentDAO;
import com.diana.model.dao.ManufacturerDAO;
import com.diana.model.dao.SupplierDAO;
import com.diana.model.dao.WarehouseDAO;
import com.diana.model.entities.*;
import com.diana.view.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class MainController {
    private MainView view;
    private ManufacturerDAO manufacturerDAO;
    private SupplierDAO supplierDAO;
    private EquipmentDAO equipmentDAO;
    private WarehouseDAO warehouseDAO;

    private JTable table;

    private String TABLE;
    private Set<Integer> updatedRows;
    private Set<Integer> deletedRows;


    public MainController(MainView view) {
        manufacturerDAO = ManufacturerDAO.createInstance();
        supplierDAO = SupplierDAO.createInstance(manufacturerDAO);
        equipmentDAO = EquipmentDAO.createInstance(manufacturerDAO, supplierDAO);
        warehouseDAO = WarehouseDAO.createInstance(equipmentDAO);

        this.view = view;
        initListeners();
    }

    private void initListeners() {

        JMenu tables = view.getViewMenuBar().getMenu(0);

        JMenu manufacturers = view.getViewMenuBar().getMenu(0);
        JMenu suppliers = view.getViewMenuBar().getMenu(1);
        JMenu equipment = view.getViewMenuBar().getMenu(2);
        JMenu warehouse = view.getViewMenuBar().getMenu(3);

        table = view.getTable();

        table.addPropertyChangeListener(evt -> {
            if(evt.getPropertyName().equals("tableCellEditor") && !table.isEditing()){

                int editingRow = ((JTable)evt.getSource()).getEditingRow();
                updatedRows.add(editingRow);

                view.getSaveButton().setVisible(true);
            }
        });


        JButton save = view.getSaveButton();

        save.addActionListener((e)-> {
            System.out.println("SAVING!!!");
            table = view.getTable();

            switch (TABLE) {
                case "manufacturer":
                    updatedRows.removeAll(deletedRows);
                    updateManufacturers(updatedRows);
                    deleteManufacturers(deletedRows);
                    buildTableForManufacturer(table);
                    break;
                case "supplier":
                    updatedRows.removeAll(deletedRows);
                    updateSupplier(updatedRows);
                    deleteSupplier(deletedRows);
                    buildTableForSupplier(table);
                    break;
                case "equipment":
                    updatedRows.removeAll(deletedRows);
                    updateEquipment(updatedRows);
                    deleteEquipment(deletedRows);
                    buildTableForEquipmentWithEquipmentList(table, equipmentDAO.findAll());
                    break;
                case "warehouse":
                    updatedRows.removeAll(deletedRows);
                    updateWarehouses(updatedRows);
                    deleteWarehouses(deletedRows);
                    buildTableForWarehouses(table);
                    break;
            }
            updatedRows=new TreeSet<>();
            deletedRows=new TreeSet<>();
        });


        manufacturers.getItem(0).addActionListener((e) -> {
            new ManufacturerAddDialog(view, "Add Manufacturer");
        });
        manufacturers.getItem(1).addActionListener((e) -> {
            view.getError().setVisible(false);
            TABLE = "manufacturer";
            deletedRows = new TreeSet<>();
            updatedRows = new TreeSet<>();

            table = view.getTable();
            buildTableForManufacturer(table);
        });


        suppliers.getItem(0).addActionListener((e) -> {
            new SupplierAddDialog(view, "Add supplier");
        });
        suppliers.getItem(1).addActionListener((e) -> {
            view.getError().setVisible(false);
            TABLE = "supplier";
            deletedRows = new TreeSet<>();
            updatedRows = new TreeSet<>();

            table = view.getTable();
            buildTableForSupplier(table);
        });


        equipment.getItem(0).addActionListener((e) -> {
            new EquipmentAddDialog(view, "Add new Equipment");
        });
        equipment.getItem(1).addActionListener((e) -> {
            view.getError().setVisible(false);
            TABLE = "equipment";
            deletedRows = new TreeSet<>();
            updatedRows = new TreeSet<>();

            table = view.getTable();
            buildTableForEquipmentWithEquipmentList(table, equipmentDAO.findAll());
            addSearchAndFilterBarForEquipment();
        });


        warehouse.getItem(0).addActionListener((e) -> {
            new WarehouseAddDialog(view, "Add warehouse");
        });
        warehouse.getItem(1).addActionListener((e) -> {
            view.getError().setVisible(false);
            TABLE = "warehouse";
            deletedRows = new TreeSet<>();
            updatedRows = new TreeSet<>();

            table = view.getTable();
            buildTableForWarehouses(table);
        });
    }

    private void buildTableForManufacturer(JTable table) {
        view.getSaveButton().setVisible(false);
        view.getSearchAndFilter().setVisible(false);
        view.getSearchAndFilter().removeAll();
        table.setRowSorter(null);

        List<Manufacturer> manufacturerList = manufacturerDAO.findAll();
        Object[][] manufacturersArray = new Object[manufacturerList.size()][4];
        for (int i = 0; i < manufacturerList.size(); i++) {
            Manufacturer m = manufacturerList.get(i);
            manufacturersArray[i] = new Object[]{m.getId(), m.getName(), m.getAddress(), m.getPhone()};
        }

        Object[] headers = {"id", "Name", "Address", "Phone"};

        MyTableModel tableModel = new MyTableModel();
        tableModel.setDataVector(manufacturersArray, headers);

        TableCellEditor editor = table.getCellEditor();
        if (editor != null){
            editor.stopCellEditing();
        }

        table.setModel(tableModel);

        addDeleteColumn(table);
        table.getColumn("id").setMaxWidth(25);
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumn("id").setCellRenderer(cellRenderer);

        table.getColumnModel().removeColumn(table.getColumn("id"));
    }

    private void buildTableForSupplier(JTable table) {
        view.getSaveButton().setVisible(false);
        view.getSearchAndFilter().setVisible(false);
        view.getSearchAndFilter().removeAll();
        table.setRowSorter(null);

        List<Supplier> supplierList = supplierDAO.findAll();
        Object[][] suppliersArray = new Object[supplierList.size()][3];
        JComboBox<Object> allManufacturers = new JComboBox<>(manufacturerDAO.findAll().stream().map(m -> m.getName()).toArray());

        for (int i = 0; i < supplierList.size(); i++) {
            Supplier s = supplierList.get(i);
            suppliersArray[i] = new Object[]{s.getId(), s.getName(), s.getManufacturer().getName()};
        }

        Object[] headers = {"id", "Name", "Manufacturer"};
        MyTableModel tableModel = new MyTableModel();
        tableModel.setDataVector(suppliersArray, headers);

        TableCellEditor editor = table.getCellEditor();
        if (editor != null){
            editor.stopCellEditing();
        }

        table.setModel(tableModel);

        addDeleteColumn(table);

        TableColumn col = table.getColumnModel().getColumn(2);
        col.setCellEditor(new DefaultCellEditor(allManufacturers));

        table.getColumn("id").setMaxWidth(25);
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumn("id").setCellRenderer(cellRenderer);

        table.getColumnModel().removeColumn(table.getColumn("id"));
    }

    private void buildTableForEquipmentWithEquipmentList(JTable table, List<Equipment> equipmentList){
        view.getSaveButton().setVisible(false);


        Object[] headers = {"id", "Name", "Type", "Price", "Size", "Weight", "Manufacturer", "Supplier", "Warehouse"};

        Object[] allManufacturers = manufacturerDAO.findAll().stream().map(m -> m.getName()).toArray();
        Object[] allSuppliers = supplierDAO.findAll().stream().map(s -> s.getName()).toArray();
        Object[] allWarehouses = warehouseDAO.findAll().stream().map(w -> w.getName()).toArray();
        Object[] allTypes = {"HELMET", "JACKET", "PANTS", "GLOVES", "SHOES", "BODY_ARMOR", "GLASSES", "ACCESSORY"};
        Object[] allSizes = {"M", "L", "S", "X", "XL", "XS", "XXL"};

        Object[][] data = new Object[equipmentList.size()][9];
        for(int i=0; i<equipmentList.size(); i++){
            Equipment e = equipmentList.get(i);
            data[i] = new Object[]{e.getId(), e.getName(), e.getType(), e.getPrice(), e.getSize(), e.getWeight(), e.getManufacturer().getName(), e.getSupplier().getName(), warehouseDAO.getWarehouseForEquipment(e).getName()};
        }

        MyTableModel tableModel = new MyTableModel();
        tableModel.setDataVector(data, headers);

        TableCellEditor editor = table.getCellEditor();
        if (editor != null){
            editor.stopCellEditing();
        }

        table.setModel(tableModel);

        addDeleteColumn(table);

        TableColumn types = table.getColumn("Type");
        types.setCellEditor(new DefaultCellEditor(new JComboBox<>(allTypes)));

        TableColumn size = table.getColumn("Size");
        size.setCellEditor(new DefaultCellEditor(new JComboBox<>(allSizes)));

        TableColumn manufacturer = table.getColumn("Manufacturer");
        manufacturer.setCellEditor(new DefaultCellEditor(new JComboBox<>(allManufacturers)));

        TableColumn supplier = table.getColumn("Supplier");
        supplier.setCellEditor(new DefaultCellEditor(new JComboBox<>(allSuppliers)));

        TableColumn warehouse = table.getColumn("Warehouse");
        warehouse.setCellEditor(new DefaultCellEditor(new JComboBox<>(allWarehouses)));

        table.getColumn("id").setMaxWidth(25);
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumn("id").setCellRenderer(cellRenderer);

        table.getColumnModel().removeColumn(table.getColumn("id"));

        table.getColumn("Type").setMaxWidth(120);
        table.getColumn("Price").setMaxWidth(70);
        table.getColumn("Size").setMaxWidth(50);
        table.getColumn("Weight").setMaxWidth(70);
        table.getColumn("Manufacturer").setMaxWidth(100);

    }

    private void buildTableForWarehouses(JTable table){
        view.getSaveButton().setVisible(false);
        view.getSearchAndFilter().setVisible(false);
        view.getSearchAndFilter().removeAll();
        table.setRowSorter(null);

        List<Warehouse> warehouseList = warehouseDAO.findAll();
        Object[][] warehouseArray = new Object[warehouseList.size()][3];

        for (int i=0; i<warehouseList.size(); i++){
            Warehouse w = warehouseList.get(i);
            warehouseArray[i] = new Object[]{w.getId(), w.getName(), w.getAddress()};
        }

        Object[] headers = {"id", "Name", "Address"};

        MyTableModel tableModel = new MyTableModel();
        tableModel.setDataVector(warehouseArray, headers);

        TableCellEditor editor = table.getCellEditor();
        if (editor != null){
            editor.stopCellEditing();
        }

        table.setModel(tableModel);

        addDeleteColumn(table);

        table.getColumn("id").setMaxWidth(25);
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumn("id").setCellRenderer(cellRenderer);

        table.getColumnModel().removeColumn(table.getColumn("id"));
    }

    private void addDeleteColumn(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        Object[] buttons = Stream.generate(()->false).limit(model.getRowCount()).toArray();
        model.addColumn("Delete", buttons);

        TableColumn column = table.getColumn("Delete");

        JCheckBox checkBox = new JCheckBox();
        checkBox.addChangeListener(e -> {
            int deletingRow = table.getEditingRow();

            if(((JCheckBox)e.getSource()).isSelected() == true){
                if (deletingRow>=0)
                    deletedRows.add(deletingRow);
                view.getSaveButton().setVisible(true);
            } else {
                if (deletingRow>=0)
                    deletedRows.remove(deletingRow);

            }
        });
        checkBox.setHorizontalAlignment(SwingConstants.CENTER);

        column.setMaxWidth(50);

        column.setCellEditor(new DefaultCellEditor(checkBox));
        column.setCellRenderer(new CheckboxRenderer(view.getSaveButton()));
    }

    private void updateManufacturers(Set<Integer> id){
        view.getError().setVisible(false);
        System.out.println("UPDATING MANUFACTURERS: " + id);
        id.forEach(i -> {
            Integer m_id = Integer.parseInt(table.getModel().getValueAt(i, 0).toString());
            String name= table.getModel().getValueAt(i, 1).toString();
            String address= table.getModel().getValueAt(i, 2).toString();
            String phone= table.getModel().getValueAt(i, 3).toString();

            Manufacturer manufacturer = new Manufacturer(m_id, name, address, phone);

            manufacturerDAO.update(m_id, manufacturer);
        });
    }
    private void updateSupplier(Set<Integer> id){
        view.getError().setVisible(false);
        System.out.println("UPDATING SUPPLIERS: " + id);
        id.forEach(i -> {
            Integer s_id = Integer.parseInt(table.getModel().getValueAt(i, 0).toString());
            String name= table.getModel().getValueAt(i, 1).toString();
            String manufacturer_name = table.getModel().getValueAt(i, 2).toString();
            Supplier supplier = new Supplier(s_id, name);
            try {
                supplier.setManufacturer(manufacturerDAO.findByName(manufacturer_name));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            supplierDAO.update(s_id, supplier);
        });
    }
    private void updateEquipment(Set<Integer> id){
        view.getError().setVisible(false);
        System.out.println("UPDATING EQUIPMENTS: " + id);
        id.forEach(i -> {
            Integer e_id = Integer.parseInt(table.getModel().getValueAt(i, 0).toString());
            String name = table.getModel().getValueAt(i, 1).toString();
            String type = table.getModel().getValueAt(i, 2).toString();
            Integer price = Integer.parseInt(table.getModel().getValueAt(i, 3).toString());
            String size = table.getModel().getValueAt(i, 4).toString();
            Integer weight = Integer.parseInt(table.getModel().getValueAt(i, 5).toString());
            String manufacturer_name = table.getModel().getValueAt(i, 6).toString();
            String supplier_name = table.getModel().getValueAt(i, 7).toString();
            String warehouse_name="";
            if (table.getModel().getValueAt(i, 8) != null)
                warehouse_name =  table.getModel().getValueAt(i, 8).toString();

            Equipment equipment = new Equipment(e_id, name, EquipmentType.valueOf(type), price, size, weight);
            Warehouse warehouse = new Warehouse();

            try {
                equipment.setManufacturer(manufacturerDAO.findByName(manufacturer_name));
                equipment.setSupplier(supplierDAO.findByName(supplier_name));
                warehouse = warehouseDAO.findByName(warehouse_name);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            warehouseDAO.updateForEquipment(equipment, warehouse);
            equipmentDAO.update(e_id, equipment);

        });
    }
    private void updateWarehouses(Set<Integer> id){
        view.getError().setVisible(false);
        System.out.println("UPDATING WAREHOUSES: " + id);
        id.forEach(i -> {
            Integer w_id = Integer.parseInt(table.getModel().getValueAt(i, 0).toString());
            String name= table.getModel().getValueAt(i, 1).toString();
            String address= table.getModel().getValueAt(i, 2).toString();

            Warehouse warehouse = new Warehouse(w_id, name, address);

            warehouseDAO.update(w_id, warehouse);
        });
    }


    private void deleteManufacturers(Set<Integer> id){
        System.out.println("DELETING MANUFACTURERS: " + id);
        id.forEach(i -> {
            Integer m_id = Integer.parseInt(table.getModel().getValueAt(i, 0).toString());
            try {
                manufacturerDAO.delete(m_id);
            } catch (Exception e) {
                if (e.getMessage().contains("foreign key constraint fails")){
                    view.getError().setText("This manufacturer is used!");
                    view.getError().setVisible(true);
                }
            }
        });
    }
    private void deleteSupplier(Set<Integer> id){
        System.out.println("DELETING SUPPLIERS: " + id);
        id.forEach(i -> {
            Integer s_id = Integer.parseInt(table.getModel().getValueAt(i, 0).toString());
            try {
                supplierDAO.delete(s_id);
        } catch (Exception e) {
            if (e.getMessage().contains("foreign key constraint fails")){
                view.getError().setText("This supplier is used!");
                view.getError().setVisible(true);
            }
        }
        });
    }
    private void deleteEquipment(Set<Integer> id){
        System.out.println("DELETING EQUIPMENTS: " + id);
        id.forEach(i -> {
            Integer e_id = Integer.parseInt(table.getModel().getValueAt(i, 0).toString());
            try {
                equipmentDAO.delete(e_id);
        } catch (Exception e) {
            if (e.getMessage().contains("foreign key constraint fails")){
                view.getError().setText("This equipment is used!");
                view.getError().setVisible(true);
            }
        }
        });
    }
    private void deleteWarehouses(Set<Integer> id){
        System.out.println("DELETING WAREHOUSES: " + id);
        id.forEach(i -> {
            Integer w_id = Integer.parseInt(table.getModel().getValueAt(i, 0).toString());
            try {
                warehouseDAO.delete(w_id);
        } catch (Exception e) {
            if (e.getMessage().contains("foreign key constraint fails")){
                view.getError().setText("This warehouse is used");
                view.getError().setVisible(true);
            }
        }
        });
    }

    private void addSearchAndFilterBarForEquipment(){
        JPanel saf = view.getSearchAndFilter();
        view.getSearchAndFilter().removeAll();
        view.getSearchAndFilter().updateUI();
        saf.setVisible(true);

        GridLayout layout = new GridLayout(4, 1);
        FlowLayout operationLayout = new FlowLayout(FlowLayout.LEFT);

        saf.setLayout(layout);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(operationLayout);

        JTextField name = new JTextField(30);
        name.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                JPanel filterPanel = (JPanel)view.getSearchAndFilter().getComponent(2);
                JTextField from = (JTextField) filterPanel.getComponent(1);
                JTextField to = (JTextField) filterPanel.getComponent(3);
                updateEquipmentWithConditionForNameWithPriceFiltering(name.getText()+keyEvent.getKeyChar(), from.getText(), to.getText());
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
            }
        });


        searchPanel.add(new JLabel("SEARCH BY NAME: "));
        searchPanel.add(name);

        saf.add(searchPanel);

        JPanel sortPanel = new JPanel();
        sortPanel.setLayout(operationLayout);

        String[] options = {"", "WEIGHT"};
        JComboBox<String> sortingOptions = new JComboBox<>(options);
        sortingOptions.setPreferredSize(new Dimension(100, 20));

        sortingOptions.addActionListener(itemEvent -> {
            JComboBox<String> combobox = (JComboBox<String>) itemEvent.getSource();
            buildTableForEquipmentWithEquipmentList(table, sortEquipmentBy(combobox.getSelectedItem().toString()));
        });

        sortPanel.add(new JLabel("SORT BY: "));
        sortPanel.add(sortingOptions);

        saf.add(sortPanel);


        JPanel priceFilterPanel = new JPanel();
        priceFilterPanel.setLayout(operationLayout);
        priceFilterPanel.add(new JLabel("PRICE FROM: "));

        JTextField from = new JTextField(5);
        from.addKeyListener(new OnlyNumbersListener());
        priceFilterPanel.add(from);

        priceFilterPanel.add(new JLabel(" TO: "));

        JTextField to = new JTextField(5);
        to.addKeyListener(new OnlyNumbersListener());
        priceFilterPanel.add(to);

        JButton filter = new JButton("FILTER");
        priceFilterPanel.add(filter);

        filter.addActionListener(actionEvent -> {

            List<Equipment> equipmentList = equipmentDAO.findAllByName(name.getText());

            buildTableForEquipmentWithEquipmentList(table, filterEquipment(from.getText(), to.getText(), equipmentList));
        });


        saf.add(priceFilterPanel);

        JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        Integer total = equipmentDAO.findAll().stream().map(e -> e.getPrice()).reduce((n1, n2) -> n1+n2).orElse(0);
        totalPanel.add(new JLabel("SUM OF PRICES: " + total));

        saf.add(totalPanel);
    }

    private void updateEquipmentWithConditionForName(String name){
        name = name.replaceAll("\b", "");
        List<Equipment> equipmentList = equipmentDAO.findAllByName(name);
        buildTableForEquipmentWithEquipmentList(table, equipmentList);
    }

    private void updateEquipmentWithConditionForNameWithPriceFiltering(String name, String from, String to){
        name = name.replaceAll("\b", "");
        List<Equipment> equipmentList = equipmentDAO.findAllByName(name);
        buildTableForEquipmentWithEquipmentList(table, filterEquipment(from, to, equipmentList));
    }

    private List<Equipment> getSortedEquipmentByWeight(){
        return getCurrentEquipmentList().stream().sorted((e1, e2) -> e1.getWeight().compareTo(e2.getWeight())).toList();
    }

    private List<Equipment> getCurrentEquipmentList(){
        TableModel model = table.getModel();
        List<Equipment> list = new ArrayList<>();
        for (int i=0; i<model.getRowCount(); i++) {
            list.add(equipmentDAO.findByKey(Integer.parseInt(model.getValueAt(i, 0).toString())));
        }
        if(list.isEmpty()){
            return Collections.emptyList();
        }

        return list;
    }

    private List<Equipment> sortEquipmentBy(String criteria) {
        switch(criteria){
            case "WEIGHT":
                return getSortedEquipmentByWeight();
            default:
                return getCurrentEquipmentList();
        }
    }

    private List<Equipment> filterEquipment(String from, String to, List<Equipment> equipmentList){
        Integer min_price;
        Integer max_price;

        if(!to.isBlank())
            max_price = Integer.parseInt(to);
        else
            max_price = equipmentList.stream().map(e -> e.getPrice()).max((p1, p2) -> p1.compareTo(p2)).orElse(0);

        if(!from.isBlank())
            min_price = Integer.parseInt(from);
        else
            min_price = 0;

        return equipmentList.stream().filter(equipment -> (equipment.getPrice() >= min_price && equipment.getPrice() <= max_price)).toList();
    }
}