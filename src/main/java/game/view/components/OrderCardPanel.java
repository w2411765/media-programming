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
    private boolean serveEnabled = false;  // 提供フェーズ中かどうか
    private boolean served = false;        // 提供済みかどうか
    private boolean canServe = false;      // 提供可能かどうか（インベントリで提供可能）
    private boolean showServeButton = false; // 提供ボタンを表示するかどうか
    
    // 提供ボタン
    private JButton serveButton;
    
    // お酒のアイコン画像キャッシュ
    private static Map<AlcoholType, Image> alcoholImages = new HashMap<>();
    
    // カード描画用パネル
    private JPanel cardDrawPanel;
    
    public OrderCardPanel(OrderCard orderCard) {
        this.orderCard = orderCard;
        this.setLayout(new BorderLayout());
        this.setOpaque(false);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // カード描画用パネル（CENTER）
        cardDrawPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintCard(g);
            }
        };
        cardDrawPanel.setOpaque(false);
        cardDrawPanel.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        cardDrawPanel.setMinimumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        cardDrawPanel.setMaximumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        this.add(cardDrawPanel, BorderLayout.CENTER);
        
        // 提供ボタン（初期状態では非表示）
        serveButton = new JButton("提供");
        serveButton.setFont(new Font(Font.SERIF, Font.BOLD, 14));
        serveButton.setBackground(new Color(50, 200, 50));
        serveButton.setForeground(Color.WHITE);
        serveButton.setFocusPainted(false);
        serveButton.setPreferredSize(new Dimension(CARD_WIDTH, 30));
        serveButton.setVisible(false);
        this.add(serveButton, BorderLayout.SOUTH);
        
        // 初期サイズ設定
        this.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        this.setMinimumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        this.setMaximumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        
        // クリックで選択状態を切り替え（デフォルト動作）
        // CustomerPanel側で提供フェーズ用の動作を上書きする
    }
    
    /**
     * カードの描画処理（元のpaintComponentから移動）
     */
    private void paintCard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int w = cardDrawPanel.getWidth();
        int h = cardDrawPanel.getHeight();
        
        // サイズが0の場合はデフォルトサイズを使用
        if (w <= 0) w = CARD_WIDTH;
        if (h <= 0) h = CARD_HEIGHT;
    
        
        // カード背景（クリーム色のカード）
        Color cardBg;
        if (served) {
            // 提供済み：暗くする
            cardBg = new Color(150, 145, 130);
        } else {
            cardBg = new Color(255, 248, 220); // cornsilk
        }
        g2d.setColor(cardBg);
        g2d.fillRoundRect(2, 2, w - 4, h - 4, CORNER_RADIUS, CORNER_RADIUS);
        
        // 選択時のハイライト
        if (selected && !served) {
            g2d.setColor(new Color(255, 215, 0, 100)); // 半透明の金色
            g2d.fillRoundRect(2, 2, w - 4, h - 4, CORNER_RADIUS, CORNER_RADIUS);
        }
        
        // 提供可能時のハイライト（canServeがtrueの場合のみ）
        if (canServe && !served) {
            g2d.setColor(new Color(100, 255, 100, 80)); // 半透明の緑（少し濃く）
            g2d.fillRoundRect(2, 2, w - 4, h - 4, CORNER_RADIUS, CORNER_RADIUS);
        }
        
        // カード枠
        Color borderColor;
        if (served) {
            borderColor = new Color(100, 90, 70);
        } else if (canServe) {
            borderColor = new Color(50, 200, 50); // 緑色の枠
        } else if (selected) {
            borderColor = new Color(255, 165, 0);
        } else {
            borderColor = new Color(139, 90, 43);
        }
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke((selected || canServe) && !served ? 3.0f : 2.0f));
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
    
    /**
     * 提供可能状態を設定
     */
    public void setServeEnabled(boolean enabled) {
        this.serveEnabled = enabled;
        this.setCursor(enabled ? new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));
        repaint();
    }
    
    /**
     * 提供可能状態を取得
     */
    public boolean isServeEnabled() {
        return serveEnabled;
    }
    
    /**
     * 提供済み状態を設定
     */
    public void setServed(boolean served) {
        this.served = served;
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        repaint();
    }
    
    /**
     * 提供済み状態を取得
     */
    public boolean isServed() {
        return served;
    }
    
    /**
     * 提供可能状態を設定
     */
    public void setCanServe(boolean canServe) {
        this.canServe = canServe;
        repaint();
    }
    
    /**
     * 提供可能状態を取得
     */
    public boolean isCanServe() {
        return canServe;
    }
    
    /**
     * 提供ボタンの表示/非表示を設定
     */
    public void setShowServeButton(boolean show) {
        this.showServeButton = show;
        if (serveButton != null) {
            serveButton.setVisible(show);
            // サイズを調整（ボタン表示時は高さを増やす）
            if (show) {
                this.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT + 35));
                this.setMaximumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT + 35));
            } else {
                this.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
                this.setMaximumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
            }
            revalidate();
            repaint();
        }
    }
    
    /**
     * 提供ボタンの表示状態を取得
     */
    public boolean isShowServeButton() {
        return showServeButton;
    }
    
    /**
     * 提供ボタンのクリックイベントリスナーを設定
     */
    public void setServeButtonListener(java.awt.event.ActionListener listener) {
        if (serveButton != null) {
            // 既存のリスナーを削除
            for (java.awt.event.ActionListener existing : serveButton.getActionListeners()) {
                serveButton.removeActionListener(existing);
            }
            // 新しいリスナーを追加
            if (listener != null) {
                serveButton.addActionListener(listener);
            }
        }
    }
}
