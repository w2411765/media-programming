package com.speakeasy.view.components;

import java.awt.*;
import javax.swing.*;

public class MarketPanel extends JPanel {
    public MarketPanel() {
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(240, 240, 200));
        this.setBorder(BorderFactory.createTitledBorder("Market Panel"));
        
        int playerPanelWidth = 250;
        int playerPanelHeight = 600;
        int horizontalMargin = playerPanelWidth;
        
        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.setOpaque(false);
        JPanel southLeftMargin = new JPanel();
        southLeftMargin.setPreferredSize(new Dimension(horizontalMargin, 0));
        southLeftMargin.setOpaque(false);
        JPanel southRightMargin = new JPanel();
        southRightMargin.setPreferredSize(new Dimension(horizontalMargin, 0));
        southRightMargin.setOpaque(false);
        southContainer.add(southLeftMargin, BorderLayout.WEST);
        southContainer.add(southRightMargin, BorderLayout.EAST);
        PlayerPanel.South southPlayerPanel = new PlayerPanel.South();
        southPlayerPanel.setPreferredSize(new Dimension(0, playerPanelWidth));
        southContainer.add(southPlayerPanel, BorderLayout.CENTER);
        this.add(southContainer, BorderLayout.SOUTH);

        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.setOpaque(false);
        JPanel northLeftMargin = new JPanel();
        northLeftMargin.setPreferredSize(new Dimension(horizontalMargin, 0));
        northLeftMargin.setOpaque(false);
        JPanel northRightMargin = new JPanel();
        northRightMargin.setPreferredSize(new Dimension(horizontalMargin, 0));
        northRightMargin.setOpaque(false);
        northContainer.add(northLeftMargin, BorderLayout.WEST);
        northContainer.add(northRightMargin, BorderLayout.EAST);
        PlayerPanel.North northPlayerPanel = new PlayerPanel.North();
        northPlayerPanel.setPreferredSize(new Dimension(0, playerPanelWidth));
        northContainer.add(northPlayerPanel, BorderLayout.CENTER);
        this.add(northContainer, BorderLayout.NORTH);

        PlayerPanel.East eastPlayerPanel = new PlayerPanel.East();
        eastPlayerPanel.setPreferredSize(new Dimension(playerPanelWidth, playerPanelHeight));
        this.add(eastPlayerPanel, BorderLayout.EAST);

        PlayerPanel.West westPlayerPanel = new PlayerPanel.West();
        westPlayerPanel.setPreferredSize(new Dimension(playerPanelWidth, playerPanelHeight));
        this.add(westPlayerPanel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(255, 200, 200));
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        centerPanel.add(new JLabel("Center Market"));
        this.add(centerPanel, BorderLayout.CENTER);
    }
}
