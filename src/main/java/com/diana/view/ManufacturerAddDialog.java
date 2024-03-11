package com.diana.view;

import com.diana.model.dao.ManufacturerDAO;
import com.diana.model.entities.Manufacturer;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ManufacturerAddDialog extends AbstractAddDialogue {
    private ManufacturerDAO manufacturerDAO;
    private JLabel nameLabel;
    private JTextField name;
    private JLabel addressLabel;
    private JTextField address;
    private JLabel phoneLabel;
    private JTextField phone;

    public ManufacturerAddDialog(Frame owner, String title){
        super(owner, title);

        manufacturerDAO = ManufacturerDAO.createInstance();

        JPanel input = getInput();
        input.setLayout(new GridLayout(6, 1));
        nameLabel = new JLabel("Enter name:");
        name = new JTextField(20);
        input.add(nameLabel);
        input.add(name);
        addressLabel = new JLabel("Enter address:");
        address = new JTextField(20);
        input.add(addressLabel);
        input.add(address);
        phoneLabel = new JLabel("Enter phone:");
        phone = new JTextField(20);
        input.add(phoneLabel);
        input.add(phone);

        getSave().addActionListener((e)->{
            getError().setVisible(false);
            Manufacturer manufacturer = new Manufacturer(null, name.getText(), address.getText(), phone.getText());
            try {
                manufacturerDAO.save(manufacturer);
            } catch (SQLException ex) {
                getError().setText("Such manufacturer already exists!");
                getError().setVisible(true);
                throw new RuntimeException(ex);
            }
        });

        showDialogue(true);
    }
}
