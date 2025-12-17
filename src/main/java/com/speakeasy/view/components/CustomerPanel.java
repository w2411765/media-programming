package com.speakeasy.view.components;

import java.awt.*;
import javax.swing.*;

public class CustomerPanel extends JPanel {
    public CustomerPanel() {
        this.setBackground(new Color(200, 255, 200));
        this.setBorder(BorderFactory.createTitledBorder("Customer Panel"));
        this.add(new JLabel("Customers"));
        this.setPreferredSize(new Dimension(440, 0));
    }
}

