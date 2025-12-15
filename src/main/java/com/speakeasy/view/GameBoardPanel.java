// メイン画面の親パネル
package com.speakeasy.view;

import java.awt.*;
import javax.swing.*;

class AlcoholPanel extends JPanel {
    public AlcoholPanel(int num) {
        this.setBackground(new Color(255, 255, 255));

        this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        Dimension size = new Dimension(50, 50);
        this.setPreferredSize(size);
        this.setMaximumSize(size);
        this.setMinimumSize(size);
        this.setLayout(new BorderLayout());
        
        JLabel label = new JLabel(String.valueOf(num), SwingConstants.CENTER);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        this.add(label, BorderLayout.CENTER);
    }
}

class SouthPlayerPanel extends JPanel {
    public SouthPlayerPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(new Color(220, 220, 220));
        this.setBorder(BorderFactory.createTitledBorder("South Player Panel"));
        
        JPanel shelfPanel = new JPanel();
        shelfPanel.setLayout(new GridLayout(3, 10, 1, 1));
        shelfPanel.setBackground(new Color(200, 180, 150));
        shelfPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        shelfPanel.setPreferredSize(new Dimension(509, 152));
        shelfPanel.setMaximumSize(new Dimension(509, 152));
        shelfPanel.setMinimumSize(new Dimension(509, 152));
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 10; j++) {
                shelfPanel.add(new AlcoholPanel(i * 10 + j + 1));
            }
        }
        this.add(shelfPanel);

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(180, 200, 220));
        infoPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
        infoPanel.add(new JLabel("Player Info"));
        this.add(infoPanel);
    }
}

class EastPlayerPanel extends JPanel {
    public EastPlayerPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setBackground(new Color(220, 220, 220));
        this.setBorder(BorderFactory.createTitledBorder("East Player Panel"));

        JPanel shelfPanel = new JPanel();
        shelfPanel.setLayout(new GridLayout(10, 3, 1, 1));
        shelfPanel.setBackground(new Color(200, 180, 150));
        shelfPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        shelfPanel.setPreferredSize(new Dimension(152, 509));
        shelfPanel.setMaximumSize(new Dimension(152, 509));
        shelfPanel.setMinimumSize(new Dimension(152, 509));
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 3; j++) {
                shelfPanel.add(new AlcoholPanel(10 * (j + 1) - i));
            }
        }
        this.add(shelfPanel);

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(180, 200, 220));
        infoPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
        infoPanel.add(new JLabel("Player Info"));
        this.add(infoPanel);
    }
}

class NorthPlayerPanel extends JPanel {
    public NorthPlayerPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(new Color(220, 220, 220));
        this.setBorder(BorderFactory.createTitledBorder("North Player Panel"));

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(180, 200, 220));
        infoPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
        infoPanel.add(new JLabel("Player Info"));
        this.add(infoPanel);
        
        JPanel shelfPanel = new JPanel();
        shelfPanel.setLayout(new GridLayout(3, 10, 1, 1));
        shelfPanel.setBackground(new Color(200, 180, 150));
        shelfPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        shelfPanel.setPreferredSize(new Dimension(509, 152));
        shelfPanel.setMaximumSize(new Dimension(509, 152));
        shelfPanel.setMinimumSize(new Dimension(509, 152));
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 10; j++) {
                shelfPanel.add(new AlcoholPanel(30 - i * 10 - j));
            }
        }
        this.add(shelfPanel);
    }
}

class WestPlayerPanel extends JPanel {
    public WestPlayerPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setBackground(new Color(220, 220, 220));
        this.setBorder(BorderFactory.createTitledBorder("West Player Panel"));

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(180, 200, 220));
        infoPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
        infoPanel.add(new JLabel("Player Info"));
        this.add(infoPanel);

        JPanel shelfPanel = new JPanel();
        shelfPanel.setLayout(new GridLayout(10, 3, 1, 1));
        shelfPanel.setBackground(new Color(200, 180, 150));
        shelfPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        shelfPanel.setPreferredSize(new Dimension(152, 509));
        shelfPanel.setMaximumSize(new Dimension(152, 509));
        shelfPanel.setMinimumSize(new Dimension(152, 509));
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 3; j++) {
                shelfPanel.add(new AlcoholPanel(30 - 10 * (j + 1) + i + 1));
            }
        }
        this.add(shelfPanel);
    }
}

class MarketPanel extends JPanel {
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
        SouthPlayerPanel southPlayerPanel = new SouthPlayerPanel();
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
        NorthPlayerPanel northPlayerPanel = new NorthPlayerPanel();
        northPlayerPanel.setPreferredSize(new Dimension(0, playerPanelWidth));
        northContainer.add(northPlayerPanel, BorderLayout.CENTER);
        this.add(northContainer, BorderLayout.NORTH);

        EastPlayerPanel eastPlayerPanel = new EastPlayerPanel();
        eastPlayerPanel.setPreferredSize(new Dimension(playerPanelWidth, playerPanelHeight));
        this.add(eastPlayerPanel, BorderLayout.EAST);

        WestPlayerPanel westPlayerPanel = new WestPlayerPanel();
        westPlayerPanel.setPreferredSize(new Dimension(playerPanelWidth, playerPanelHeight));
        this.add(westPlayerPanel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(255, 200, 200));
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        centerPanel.add(new JLabel("Center Market"));
        this.add(centerPanel, BorderLayout.CENTER);
    }
}
class LogPanel extends JPanel {
    public LogPanel() {
        this.setBackground(new Color(255, 255, 200));
        this.setBorder(BorderFactory.createTitledBorder("Log Panel"));
        this.add(new JLabel("Game Log"));
        this.setPreferredSize(new Dimension(440, 0));
    }
}

class CustomerPanel extends JPanel {
    public CustomerPanel() {
        this.setBackground(new Color(200, 255, 200));
        this.setBorder(BorderFactory.createTitledBorder("Customer Panel"));
        this.add(new JLabel("Customers"));
        this.setPreferredSize(new Dimension(440, 0));
    }
}

class InfoPanel extends JPanel {
    public InfoPanel() {
        this.setBackground(new Color(255, 200, 255));
        this.setBorder(BorderFactory.createTitledBorder("Info Panel"));
        this.add(new JLabel("Game Info"));
    }
}

public class GameBoardPanel extends JPanel {
    public GameBoardPanel() {
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(250, 250, 250));
        
        MarketPanel marketPanel = new MarketPanel();
        LogPanel logPanel = new LogPanel();
        CustomerPanel customerPanel = new CustomerPanel();
        InfoPanel infoPanel = new InfoPanel();
        
        this.add(marketPanel, BorderLayout.CENTER);
        this.add(logPanel, BorderLayout.EAST);
        this.add(customerPanel, BorderLayout.WEST);
        this.add(infoPanel, BorderLayout.SOUTH);
    }
}