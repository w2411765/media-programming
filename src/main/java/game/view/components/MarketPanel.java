package game.view.components;

import java.awt.*;
import javax.swing.*;
import game.model.Player;

public class MarketPanel extends JPanel {
    private Image backgroundImage;
    private static final int CORNER_RADIUS = 10; // 角の丸み
    private CenterPanel centerPanel;
    
    // プレイヤーパネルの参照（インベントリ更新用）
    private PlayerPanel.South southPlayerPanel;
    private PlayerPanel.North northPlayerPanel;
    private PlayerPanel.East eastPlayerPanel;
    private PlayerPanel.West westPlayerPanel;
    
    public MarketPanel() {
        this.setLayout(new BorderLayout());
        this.setOpaque(false);
        
        java.net.URL imageUrl = getClass().getResource("/images/ui/gameplay/brickwall.png");
        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            backgroundImage = icon.getImage();
        } else {
            backgroundImage = null;
        }

        int playerPanelWidth = 160; // 小さくしてCenterPanelを大きく
        int playerPanelHeight = 420; // 小さくしてCenterPanelを大きく
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
        southPlayerPanel = new PlayerPanel.South();
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
        northPlayerPanel = new PlayerPanel.North();
        northPlayerPanel.setPreferredSize(new Dimension(0, playerPanelWidth));
        northContainer.add(northPlayerPanel, BorderLayout.CENTER);
        this.add(northContainer, BorderLayout.NORTH);

        eastPlayerPanel = new PlayerPanel.East();
        eastPlayerPanel.setPreferredSize(new Dimension(playerPanelWidth, playerPanelHeight));
        this.add(eastPlayerPanel, BorderLayout.EAST);

        westPlayerPanel = new PlayerPanel.West();
        westPlayerPanel.setPreferredSize(new Dimension(playerPanelWidth, playerPanelHeight));
        this.add(westPlayerPanel, BorderLayout.WEST);

        centerPanel = new CenterPanel();
        this.add(centerPanel, BorderLayout.CENTER);
    }
    
    public CenterPanel getCenterPanel() {
        return centerPanel;
    }
    
    /**
     * プレイヤーのインベントリを更新
     * @param player 更新するプレイヤー
     */
    public void updatePlayerInventory(Player player) {
        int playerId = player.getId();
        
        // プレイヤーIDに応じて対応するパネルを更新
        switch (playerId) {
            case 0:
                southPlayerPanel.updateInventory(player);
                break;
            case 1:
                eastPlayerPanel.updateInventory(player);
                break;
            case 2:
                northPlayerPanel.updateInventory(player);
                break;
            case 3:
                westPlayerPanel.updateInventory(player);
                break;
        }
    }
    
    /**
     * 全プレイヤーのインベントリを更新
     */
    public void updateAllInventories(java.util.List<Player> players) {
        for (Player player : players) {
            updatePlayerInventory(player);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 背景を黒で塗りつぶし
        g2d.setColor(Color.BLACK);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
        
        // 背景画像を描画（縦横比を保ったまま、角丸にクリップ）
        if (backgroundImage != null) {
            int imgWidth = backgroundImage.getWidth(this);
            int imgHeight = backgroundImage.getHeight(this);
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            
            // 縦横比を保ったままスケールを計算（パネルからはみ出すように）
            double scaleX = (double) panelWidth / imgWidth;
            double scaleY = (double) panelHeight / imgHeight;
            double scale = Math.max(scaleX, scaleY); // 大きい方を使用してパネル全体を覆う
            
            int scaledWidth = (int) (imgWidth * scale);
            int scaledHeight = (int) (imgHeight * scale);
            
            // 中央に配置
            int x = (panelWidth - scaledWidth) / 2;
            int y = (panelHeight - scaledHeight) / 2;
            
            Shape clip = new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
            g2d.setClip(clip);
            g2d.drawImage(backgroundImage, x, y, scaledWidth, scaledHeight, this);
            g2d.setClip(null);
        }
        
        // 角丸の枠を描画
        g2d.setColor(Color.GRAY); // 灰色の枠
        g2d.setStroke(new BasicStroke(3.0f)); // 太い枠
        g2d.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, CORNER_RADIUS, CORNER_RADIUS);
        
        g2d.dispose();
    }
}
