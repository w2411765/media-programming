package game.view.components;

import java.awt.*;
import javax.swing.*;

// 各プレイヤーの情報パネル
public class PlayerPanel {
    
// 背景画像付きShelfPanel
    private static class ShelfPanel extends JPanel {
        private Image backgroundImage;
        private boolean rotate90; 
        
        public ShelfPanel(int rows, int cols, int hgap, int vgap, boolean rotate90) {
            this.setLayout(new GridLayout(rows, cols, hgap, vgap));
            this.setOpaque(false); 
            this.rotate90 = rotate90;
            
            java.net.URL imageUrl = getClass().getResource("/images/ui/shelf.png");
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                backgroundImage = icon.getImage();
            } else {
                backgroundImage = null;
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            if (backgroundImage != null) {
                Graphics2D g2d = (Graphics2D) g.create();
                
                if (rotate90) {
                    int centerX = getWidth() / 2;
                    int centerY = getHeight() / 2;
                    g2d.translate(centerX, centerY);
                    g2d.rotate(Math.PI / 2); 
                    g2d.translate(-centerY, -centerX);
                    g2d.drawImage(backgroundImage, 0, 0, getHeight(), getWidth(), this);
                } else {
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
                
                g2d.dispose();
            }
        }
    }
    
    public static class South extends JPanel {
        public South() {
            this.setLayout(new BorderLayout());
            this.setOpaque(false);
            
            ShelfPanel shelfPanel = new ShelfPanel(3, 10, 1, 1, false);
            shelfPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            shelfPanel.setPreferredSize(new Dimension(509, 152));
            shelfPanel.setMaximumSize(new Dimension(509, 152));
            shelfPanel.setMinimumSize(new Dimension(509, 152));
            
            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 10; j++) {
                    shelfPanel.add(new AlcoholPanel(i * 10 + j + 1));
                }
            }
            this.add(shelfPanel, BorderLayout.CENTER);
        }
    }

    public static class East extends JPanel {
        public East() {
            this.setLayout(new BorderLayout());
            this.setOpaque(false);

            ShelfPanel shelfPanel = new ShelfPanel(10, 3, 1, 1, true);
            shelfPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            shelfPanel.setPreferredSize(new Dimension(152, 509));
            shelfPanel.setMaximumSize(new Dimension(152, 509));
            shelfPanel.setMinimumSize(new Dimension(152, 509));
            
            for(int i = 0; i < 10; i++) {
                for(int j = 0; j < 3; j++) {
                    shelfPanel.add(new AlcoholPanel(10 * (j + 1) - i));
                }
            }
            this.add(shelfPanel, BorderLayout.CENTER);
        }
    }

    public static class North extends JPanel {
        public North() {
            this.setLayout(new BorderLayout());
            this.setOpaque(false);
            
            ShelfPanel shelfPanel = new ShelfPanel(3, 10, 1, 1, false);
            shelfPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            shelfPanel.setPreferredSize(new Dimension(509, 152));
            shelfPanel.setMaximumSize(new Dimension(509, 152));
            shelfPanel.setMinimumSize(new Dimension(509, 152));
            
            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 10; j++) {
                    shelfPanel.add(new AlcoholPanel(30 - i * 10 - j));
                }
            }
            this.add(shelfPanel, BorderLayout.CENTER);
        }
    }

    public static class West extends JPanel {
        public West() {
            this.setLayout(new BorderLayout());
            this.setOpaque(false);

            ShelfPanel shelfPanel = new ShelfPanel(10, 3, 1, 1, true);
            shelfPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            shelfPanel.setPreferredSize(new Dimension(152, 509));
            shelfPanel.setMaximumSize(new Dimension(152, 509));
            shelfPanel.setMinimumSize(new Dimension(152, 509));
            
            for(int i = 0; i < 10; i++) {
                for(int j = 0; j < 3; j++) {
                    shelfPanel.add(new AlcoholPanel(30 - 10 * (j + 1) + i + 1));
                }
            }
            this.add(shelfPanel, BorderLayout.CENTER);
        }
    }
}
