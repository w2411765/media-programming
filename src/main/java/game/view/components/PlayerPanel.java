package game.view.components;

import java.awt.*;
import javax.swing.*;

// 各プレイヤーの情報パネル
public class PlayerPanel {
    
    public static class South extends JPanel {
        public South() {
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

    public static class East extends JPanel {
        public East() {
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

    public static class North extends JPanel {
        public North() {
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

    public static class West extends JPanel {
        public West() {
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
}
