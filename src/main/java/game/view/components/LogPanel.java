package game.view.components;

import java.awt.*;
import javax.swing.*;

public class LogPanel extends JPanel {
    private Image backgroundImage;
    private static final int CORNER_RADIUS = 10; // 角の丸み

    public LogPanel() {
        this.setOpaque(false);
        this.add(new JLabel("Game Log"));
        this.setPreferredSize(new Dimension(520, 0)); // 横幅を少し小さく調整
        
        java.net.URL imageUrl = getClass().getResource("/images/ui/log.png");
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
