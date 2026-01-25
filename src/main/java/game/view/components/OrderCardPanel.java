package game.view.components;

import java.awt.*;
import javax.swing.*;
import java.util.Map;
import java.util.HashMap;
import game.model.order.OrderCard;
import game.model.alcohol.AlcoholType;

/**
 * OrderCardPanel
 * ------------------------------
 * 注文カード1枚を表示するパネル。
 * 
 * 構成：
 * - 名前（一番上の中央）
 * - 注文のお酒（縦に並べる）
 * - 報酬金（一番下）
 */
public class OrderCardPanel extends JPanel {
    private static final int CARD_WIDTH = 120;
    private static final int CARD_HEIGHT = 180;
    private static final int CORNER_RADIUS = 12;
    
    private OrderCard orderCard;
    private boolean selected = false;
    
    // お酒のアイコン画像キャッシュ
    private static Map<AlcoholType, Image> alcoholImages = new HashMap<>();
    
    public OrderCardPanel(OrderCard orderCard) {
        this.orderCard = orderCard;
        this.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        this.setMinimumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        this.setMaximumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        this.setOpaque(false);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // クリックで選択状態を切り替え
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                setSelected(!selected);
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        
        // カード背景（クリーム色のカード）
        Color cardBg = new Color(255, 248, 220); // cornsilk
        g2d.setColor(cardBg);
        g2d.fillRoundRect(2, 2, w - 4, h - 4, CORNER_RADIUS, CORNER_RADIUS);
        
        // 選択時のハイライト
        if (selected) {
            g2d.setColor(new Color(255, 215, 0, 100)); // 半透明の金色
            g2d.fillRoundRect(2, 2, w - 4, h - 4, CORNER_RADIUS, CORNER_RADIUS);
        }
        
        // カード枠
        g2d.setColor(selected ? new Color(255, 165, 0) : new Color(139, 90, 43)); // 選択時はオレンジ
        g2d.setStroke(new BasicStroke(selected ? 3.0f : 2.0f));
        g2d.drawRoundRect(2, 2, w - 5, h - 5, CORNER_RADIUS, CORNER_RADIUS);
        
        if (orderCard == null) {
            g2d.dispose();
            return;
        }
        
        // --- 名前（一番上） ---
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font(Font.SERIF, Font.BOLD, 14));
        String name = orderCard.getCustomerName();
        FontMetrics fm = g2d.getFontMetrics();
        int nameWidth = fm.stringWidth(name);
        int nameX = (w - nameWidth) / 2;
        g2d.drawString(name, nameX, 22);
        
        // 名前の下に線
        g2d.setColor(new Color(139, 90, 43));
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.drawLine(10, 30, w - 10, 30);
        
        // --- 注文のお酒（中央） ---
        Map<AlcoholType, Integer> required = orderCard.getRequired();
        int startY = 45;
        int itemHeight = 32;
        int iconSize = 26;
        
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        int i = 0;
        for (Map.Entry<AlcoholType, Integer> entry : required.entrySet()) {
            AlcoholType type = entry.getKey();
            int count = entry.getValue();
            int y = startY + (i * itemHeight);
            
            // お酒アイコン（実際の画像）
            Image alcoholImg = getAlcoholImage(type);
            if (alcoholImg != null) {
                g2d.drawImage(alcoholImg, 12, y - 1, iconSize, iconSize, this);
            }
            
            // お酒の種類と数量
            g2d.setColor(Color.BLACK);
            String alcoholText = getAlcoholDisplayName(type) + " x" + count;
            g2d.drawString(alcoholText, 43, y + 17);
            
            i++;
        }
        
        // --- 報酬金（一番下） ---
        // 報酬エリアの背景
        int rewardY = h - 40;
        g2d.setColor(new Color(34, 139, 34, 50)); // 半透明の緑
        g2d.fillRoundRect(10, rewardY, w - 20, 30, 8, 8);
        
        // 報酬金額
        g2d.setColor(new Color(0, 100, 0)); // ダークグリーン
        g2d.setFont(new Font(Font.SERIF, Font.BOLD, 18));
        String rewardText = orderCard.getReward() + "円";
        fm = g2d.getFontMetrics();
        int rewardWidth = fm.stringWidth(rewardText);
        g2d.drawString(rewardText, (w - rewardWidth) / 2, rewardY + 22);
        
        g2d.dispose();
    }
    
    /**
     * お酒の種類に対応するアイコン画像を返す（キャッシュ付き）
     */
    private Image getAlcoholImage(AlcoholType type) {
        if (type == null) return null;
        
        if (!alcoholImages.containsKey(type)) {
            String imagePath = "/images/ui/gameplay/alcohol/" + type.name().toLowerCase() + ".png";
            java.net.URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                alcoholImages.put(type, icon.getImage());
            } else {
                alcoholImages.put(type, null);
            }
        }
        return alcoholImages.get(type);
    }
    
    /**
     * お酒の種類の日本語表示名を返す
     */
    private String getAlcoholDisplayName(AlcoholType type) {
        switch (type) {
            case BEER: return "ビール";
            case RUM: return "ラム";
            case VODKA: return "ウォッカ";
            case GIN: return "ジン";
            default: return type.name();
        }
    }
    
    /**
     * 選択状態を設定
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }
    
    /**
     * 選択状態を取得
     */
    public boolean isSelected() {
        return selected;
    }
    
    /**
     * このパネルが表示しているOrderCardを取得
     */
    public OrderCard getOrderCard() {
        return orderCard;
    }
}
