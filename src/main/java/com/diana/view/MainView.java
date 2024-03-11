package com.diana.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.MalformedURLException;

public class MainView extends JFrame {
    private final JPanel panel;
    private JTable table;
    private final JMenuBar menuBar;

    private final Toolkit toolkit;
    private final Dimension screen;

    private final Dimension frame_d;

    private JMenu manufacturers;
    private JMenu supplier;
    private JMenu equipment;
    private JMenu warehouse;
    private JScrollPane scrollPane;
    private JButton save;

    private JLabel error;
    private JPanel actionPanel;

    private JPanel searchAndFilter;

    public MainView() throws MalformedURLException {
        setIconImage(new ImageIcon("src/main/resources/images/icon.jpg").getImage());
        setTitle("MotoMotoStore");
        setCursor(Cursor.HAND_CURSOR);
        setResizable(false);


        toolkit = Toolkit.getDefaultToolkit();
        screen = toolkit.getScreenSize();
        frame_d = new Dimension(1000, 500);


        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        searchAndFilter = new JPanel();
        searchAndFilter.setVisible(false);

        panel.add(searchAndFilter);


        scrollPane = new JScrollPane();
        table = new JTable();
        table.setRowHeight(20);

        scrollPane.setViewportView(table);
        scrollPane.setBorder(new EmptyBorder(0, 10, 10, 10));
        scrollPane.setPreferredSize(new Dimension(frame_d.width-20, frame_d.height-110));

        panel.add(scrollPane);

        actionPanel = new JPanel(new GridLayout(2, 1));
        actionPanel.setMaximumSize(new Dimension(200, 100));
        actionPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        error = new JLabel();
        error.setForeground(Color.red);
        error.setVisible(false);
        error.setHorizontalAlignment(SwingConstants.CENTER);
        actionPanel.add(error);


        save = new JButton("SAVE CHANGES");
        save.setVisible(false);

        save.addActionListener((e)->{
            save.setVisible(false);
        });
        actionPanel.add(save);

        panel.add(actionPanel);

        menuBar = new JMenuBar();


        manufacturers = new JMenu("MANUFACTURERS");
        manufacturers.add(new JMenuItem("ADD"));
        manufacturers.add(new JMenuItem("OPEN"));

        supplier = new JMenu("SUPPLIER");
        supplier.add(new JMenuItem("ADD"));
        supplier.add(new JMenuItem("OPEN"));

        equipment = new JMenu("EQUIPMENT");
        equipment.add(new JMenuItem("ADD"));
        equipment.add(new JMenuItem("OPEN"));

        warehouse = new JMenu("WAREHOUSE");
        warehouse.add(new JMenuItem("ADD"));
        warehouse.add(new JMenuItem("OPEN"));

        menuBar.add(manufacturers);
        menuBar.add(supplier);
        menuBar.add(equipment);
        menuBar.add(warehouse);

        setJMenuBar(menuBar);
        setContentPane(panel);

        setBounds((screen.width/2) - (frame_d.width/2), (screen.height/2)-(frame_d.height/2), frame_d.width, frame_d.height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public JPanel getPanel(){
        return panel;
    }

    public JMenuBar getViewMenuBar(){
        return menuBar;
    }

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public JButton getSaveButton() {
        return save;
    }

    public JLabel getError() {
        return error;
    }

    public void setError(JLabel error) {
        this.error = error;
    }

    public JPanel getSearchAndFilter() {
        return searchAndFilter;
    }

    public void setSearchAndFilter(JPanel searchAndFilter) {
        this.searchAndFilter = searchAndFilter;
    }
}
