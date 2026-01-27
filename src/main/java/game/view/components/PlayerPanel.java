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
         * @param inventory インベントリ
         * @param direction プレイヤーの方向（"SOUTH", "NORTH", "EAST", "WEST"）
         */
        public void updateInventory(Map<AlcoholType, Integer> inventory, String direction) {
            // 全スロットをクリア
            for (AlcoholPanel panel : alcoholPanels) {
                panel.setAlcohol(null, 0);
            }
            
            if (inventory == null || inventory.isEmpty()) {
                revalidate();
                repaint();
                return;
            }
            
            // インベントリの内容をリストに変換
            List<AlcoholType> alcoholList = new ArrayList<>();
            for (AlcoholType type : AlcoholType.values()) {
                int count = inventory.getOrDefault(type, 0);
                for (int i = 0; i < count; i++) {
                    alcoholList.add(type);
                }
            }
            
            int totalSlots = alcoholPanels.size();
            int alcoholCount = alcoholList.size();
            
            // 方向に応じて配置を変更
            if ("SOUTH".equals(direction)) {
                // 南：上詰め左詰め（現在のまま）
                for (int i = 0; i < alcoholCount && i < totalSlots; i++) {
                    alcoholPanels.get(i).setAlcohol(alcoholList.get(i), 1);
                }
            } else if ("WEST".equals(direction)) {
                // 西：右詰め上詰め
                // GridLayoutは10行3列なので、右から左に埋める
                int cols = 3;
                int rows = 10;
                int index = 0;
                for (int col = cols - 1; col >= 0 && index < alcoholCount; col--) {
                    for (int row = 0; row < rows && index < alcoholCount; row++) {
                        int slotIndex = row * cols + col;
                        if (slotIndex < totalSlots) {
                            alcoholPanels.get(slotIndex).setAlcohol(alcoholList.get(index), 1);
                            index++;
                        }
                    }
                }
            } else if ("NORTH".equals(direction)) {
                // 北：下詰め右詰め
                // GridLayoutは3行10列なので、下から上、右から左に埋める
                int cols = 10;
                int rows = 3;
                int index = 0;
                for (int row = rows - 1; row >= 0 && index < alcoholCount; row--) {
                    for (int col = cols - 1; col >= 0 && index < alcoholCount; col--) {
                        int slotIndex = row * cols + col;
                        if (slotIndex < totalSlots) {
                            alcoholPanels.get(slotIndex).setAlcohol(alcoholList.get(index), 1);
                            index++;
                        }
                    }
                }
            } else if ("EAST".equals(direction)) {
                // 東：左詰め下詰め
                // GridLayoutは10行3列なので、下から上、左から右に埋める
                int cols = 3;
                int rows = 10;
                int index = 0;
                for (int row = rows - 1; row >= 0 && index < alcoholCount; row--) {
                    for (int col = 0; col < cols && index < alcoholCount; col++) {
                        int slotIndex = row * cols + col;
                        if (slotIndex < totalSlots) {
                            alcoholPanels.get(slotIndex).setAlcohol(alcoholList.get(index), 1);
                            index++;
                        }
                    }
                }
            } else {
                // デフォルト：上詰め左詰め
                for (int i = 0; i < alcoholCount && i < totalSlots; i++) {
                    alcoholPanels.get(i).setAlcohol(alcoholList.get(i), 1);
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
                shelfPanel.updateInventory(player.getInventory(), "SOUTH");
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
                shelfPanel.updateInventory(player.getInventory(), "SOUTH");
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
                shelfPanel.updateInventory(player.getInventory(), "NORTH");
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
                shelfPanel.updateInventory(player.getInventory(), "WEST");
            }
        }
    }
}
