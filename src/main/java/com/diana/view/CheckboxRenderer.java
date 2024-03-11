package com.diana.view;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class CheckboxRenderer extends JCheckBox implements TableCellRenderer {
    private JButton saveButton;
    public CheckboxRenderer(){}
    public CheckboxRenderer(JButton saveButton){
        this.saveButton = saveButton;
    }
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setSelected((Boolean) value);

        setHorizontalAlignment(CENTER);
        return this;
    }
}
