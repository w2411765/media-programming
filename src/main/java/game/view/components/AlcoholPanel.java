package game.view.components;

import java.awt.*;
import javax.swing.*;
import game.model.alcohol.AlcoholType;

public class AlcoholPanel extends JPanel {
    private Image alcoholImage;
    private int count = 0;
    private AlcoholType alcoholType;
    
    /**
     * 空のスロット用コンストラクタ
     */
    public AlcoholPanel() {
        this(null, 0);
    }
    
    /**
     * 番号表示用コンストラクタ（後方互換性のため）
     */
    public AlcoholPanel(int num) {
        this(null, 0);
    }
    
    /**
     * お酒の種類と数量を指定するコンストラクタ
     */
    public AlcoholPanel(AlcoholType type, int count) {
        this.alcoholType = type;
        this.count = count;
        this.setOpaque(false);
        
        Dimension size = new Dimension(50, 50);
        this.setPreferredSize(size);
        this.setMaximumSize(size);
        this.setMinimumSize(size);
        
        // お酒の種類に応じてアイコンを読み込む
        if (type != null) {
            String imagePath = "/images/ui/gameplay/alcohol/" + type.name().toLowerCase() + ".png";
            java.net.URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                alcoholImage = icon.getImage();
            }
        }
    }
    
    /**
     * 表示するお酒の種類と数量を更新する
     */
    public void setAlcohol(AlcoholType type, int count) {
        this.alcoholType = type;
        this.count = count;
        
        if (type != null) {
            String imagePath = "/images/ui/gameplay/alcohol/" + type.name().toLowerCase() + ".png";
            java.net.URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                alcoholImage = icon.getImage();
            } else {
                alcoholImage = null;
            }
        } else {
            alcoholImage = null;
        }
        repaint();
    }
    
    public AlcoholType getAlcoholType() {
        return alcoholType;
    }
    
    public int getCount() {
        return count;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        int width = getWidth();
        int height = getHeight();
        
        // お酒のアイコンを描画
        if (alcoholImage != null && count > 0) {
            // アイコンを中央に描画（少し余白を持たせる）
            int padding = 2;
            int iconSize = Math.min(width, height) - padding * 2;
            int x = (width - iconSize) / 2;
            int y = (height - iconSize) / 2;
            
            g2d.drawImage(alcoholImage, x, y, iconSize, iconSize, this);
            
            // 数量を右下に表示
            if (count > 1) {
                String countStr = String.valueOf(count);
                g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(countStr);
                int textHeight = fm.getHeight();
                
                // 背景（半透明の黒）
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.fillRoundRect(width - textWidth - 6, height - textHeight, 
                                  textWidth + 4, textHeight - 2, 3, 3);
                
                // 数字（白）
                g2d.setColor(Color.WHITE);
                g2d.drawString(countStr, width - textWidth - 4, height - 4);
            }
        }
        
        g2d.dispose();
    }
}

