package game.view.components;

import java.awt.*;
import javax.swing.*;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import game.model.Player;
import game.model.alcohol.AlcoholType;

// 各プレイヤーの情報パネル
public class PlayerPanel {
    
    // 背景画像付きShelfPanel
    private static class ShelfPanel extends JPanel {
        private Image backgroundImage;
        private boolean rotate90;
        private List<AlcoholPanel> alcoholPanels = new ArrayList<>();
        
        public ShelfPanel(int rows, int cols, int hgap, int vgap, boolean rotate90) {
            this.setLayout(new GridLayout(rows, cols, hgap, vgap));
            this.setOpaque(false); 
            this.rotate90 = rotate90;
            
            java.net.URL imageUrl = getClass().getResource("/images/ui/gameplay/inventory.png");
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                backgroundImage = icon.getImage();
            } else {
                backgroundImage = null;
            }
            
            // 空のスロットを作成
            int totalSlots = rows * cols;
            for (int i = 0; i < totalSlots; i++) {
                AlcoholPanel panel = new AlcoholPanel();
                alcoholPanels.add(panel);
                this.add(panel);
            }
        }
        
        /**
         * インベントリを更新
         */
        public void updateInventory(Map<AlcoholType, Integer> inventory) {
            // 全スロットをクリア
            for (AlcoholPanel panel : alcoholPanels) {
                panel.setAlcohol(null, 0);
            }
            
            // インベントリの内容を配置
            int slotIndex = 0;
            if (inventory != null) {
                for (AlcoholType type : AlcoholType.values()) {
                    int count = inventory.getOrDefault(type, 0);
                    // 各お酒を1本ずつスロットに配置
                    for (int i = 0; i < count && slotIndex < alcoholPanels.size(); i++) {
                        alcoholPanels.get(slotIndex).setAlcohol(type, 1);
                        slotIndex++;
                    }
                }
            }
            
            revalidate();
            repaint();
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
        private ShelfPanel shelfPanel;
        
        public South() {
            this.setLayout(new BorderLayout());
            this.setOpaque(false);
            
            // グリッドのギャップを0にして、背景画像のセルと合わせる
            shelfPanel = new ShelfPanel(3, 10, 0, 0, false);
            shelfPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            shelfPanel.setPreferredSize(new Dimension(520, 156));
            shelfPanel.setMaximumSize(new Dimension(520, 156));
            shelfPanel.setMinimumSize(new Dimension(520, 156));
            
            this.add(shelfPanel, BorderLayout.CENTER);
        }
        
        public void updateInventory(Player player) {
            if (player != null) {
                shelfPanel.updateInventory(player.getInventory());
            }
        }
    }

    public static class East extends JPanel {
        private ShelfPanel shelfPanel;
        
        public East() {
            this.setLayout(new BorderLayout());
            this.setOpaque(false);

            shelfPanel = new ShelfPanel(10, 3, 0, 0, true);
            shelfPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            shelfPanel.setPreferredSize(new Dimension(156, 520));
            shelfPanel.setMaximumSize(new Dimension(156, 520));
            shelfPanel.setMinimumSize(new Dimension(156, 520));
            
            this.add(shelfPanel, BorderLayout.CENTER);
        }
        
        public void updateInventory(Player player) {
            if (player != null) {
                shelfPanel.updateInventory(player.getInventory());
            }
        }
    }

    public static class North extends JPanel {
        private ShelfPanel shelfPanel;
        
        public North() {
            this.setLayout(new BorderLayout());
            this.setOpaque(false);
            
            shelfPanel = new ShelfPanel(3, 10, 0, 0, false);
            shelfPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            shelfPanel.setPreferredSize(new Dimension(520, 156));
            shelfPanel.setMaximumSize(new Dimension(520, 156));
            shelfPanel.setMinimumSize(new Dimension(520, 156));
            
            this.add(shelfPanel, BorderLayout.CENTER);
        }
        
        public void updateInventory(Player player) {
            if (player != null) {
                shelfPanel.updateInventory(player.getInventory());
            }
        }
    }

    public static class West extends JPanel {
        private ShelfPanel shelfPanel;
        
        public West() {
            this.setLayout(new BorderLayout());
            this.setOpaque(false);

            shelfPanel = new ShelfPanel(10, 3, 0, 0, true);
            shelfPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            shelfPanel.setPreferredSize(new Dimension(156, 520));
            shelfPanel.setMaximumSize(new Dimension(156, 520));
            shelfPanel.setMinimumSize(new Dimension(156, 520));
            
            this.add(shelfPanel, BorderLayout.CENTER);
        }
        
        public void updateInventory(Player player) {
            if (player != null) {
                shelfPanel.updateInventory(player.getInventory());
            }
        }
    }
}
