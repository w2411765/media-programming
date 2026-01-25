package game.view.components;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.util.ArrayList;
import java.util.List;
import game.model.order.OrderCard;

/**
 * CustomerPanel
 * ------------------------------
 * プレイヤーの手持ち注文カードを表示するパネル。
 */
public class CustomerPanel extends JPanel {
    private Image backgroundImage;
    private static final int CORNER_RADIUS = 10;
    
    private JPanel cardsContainer;
    private List<OrderCardPanel> cardPanels = new ArrayList<>();
    private JLabel titleLabel;
    private JScrollPane scrollPane;
    
    public CustomerPanel() {
        this.setOpaque(false);
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(520, 0));
        
        // 背景画像の読み込み
        java.net.URL imageUrl = getClass().getResource("/images/ui/gameplay/speakeasy.png");
        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            backgroundImage = icon.getImage();
        } else {
            backgroundImage = null;
        }
        
        // タイトルラベル
        titleLabel = new JLabel("-Customers-", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SERIF, Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        this.add(titleLabel, BorderLayout.NORTH);
        
        // カード表示用のコンテナ（3列左上詰め、ビューポート幅に合わせて折り返し）
        cardsContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5)) {
            @Override
            public Dimension getPreferredSize() {
                // ビューポートの幅に合わせてPreferredSizeを計算
                if (getParent() != null && getParent().getWidth() > 0) {
                    int width = getParent().getWidth();
                    FlowLayout layout = (FlowLayout) getLayout();
                    int hgap = layout.getHgap();
                    int vgap = layout.getVgap();
                    Insets insets = getInsets();
                    
                    int x = insets.left + hgap;
                    int y = insets.top + vgap;
                    int rowHeight = 0;
                    
                    for (Component comp : getComponents()) {
                        if (comp.isVisible()) {
                            Dimension d = comp.getPreferredSize();
                            if (x + d.width + hgap + insets.right > width) {
                                x = insets.left + hgap;
                                y += rowHeight + vgap;
                                rowHeight = 0;
                            }
                            x += d.width + hgap;
                            rowHeight = Math.max(rowHeight, d.height);
                        }
                    }
                    y += rowHeight + vgap + insets.bottom;
                    return new Dimension(width, y);
                }
                return super.getPreferredSize();
            }
        };
        cardsContainer.setOpaque(false);
        
        // スクロール可能なパネルでラップ
        scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // ビューポートのサイズ変更時にコンテナを再レイアウト
        scrollPane.getViewport().addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                cardsContainer.revalidate();
            }
        });
        
        // スクロールバーを目立たないスタイルに
        setupMinimalScrollBar(scrollPane);
        
        this.add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * スクロールバーを目立たないスタイルに設定
     */
    private void setupMinimalScrollBar(JScrollPane scrollPane) {
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setPreferredSize(new Dimension(6, 0));
        verticalBar.setOpaque(false);
        verticalBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(200, 200, 200, 150);
                this.trackColor = new Color(0, 0, 0, 0);
            }
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                // 透明なトラック
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x + 1, thumbBounds.y, thumbBounds.width - 2, thumbBounds.height, 4, 4);
                g2.dispose();
            }
        });
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
            double scale = Math.max(scaleX, scaleY);
            
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
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(3.0f));
        g2d.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, CORNER_RADIUS, CORNER_RADIUS);
        
        g2d.dispose();
    }
    
    /**
     * 注文カードを設定（プレイヤーの手札を表示）
     * 3列左上詰めで表示
     */
    public void setOrderCards(List<OrderCard> orders) {
        cardsContainer.removeAll();
        cardPanels.clear();
        
        if (orders == null || orders.isEmpty()) {
            // カードがない場合のメッセージ
            JLabel emptyLabel = new JLabel("カードがありません");
            emptyLabel.setForeground(Color.WHITE);
            emptyLabel.setFont(new Font(Font.SERIF, Font.ITALIC, 14));
            cardsContainer.add(emptyLabel);
        } else {
            for (OrderCard order : orders) {
                OrderCardPanel cardPanel = new OrderCardPanel(order);
                cardPanels.add(cardPanel);
                cardsContainer.add(cardPanel);
            }
        }
        
        cardsContainer.revalidate();
        cardsContainer.repaint();
    }
    
    /**
     * カードを追加
     */
    public void addOrderCard(OrderCard order) {
        OrderCardPanel cardPanel = new OrderCardPanel(order);
        cardPanels.add(cardPanel);
        cardsContainer.add(cardPanel);
        
        cardsContainer.revalidate();
        cardsContainer.repaint();
    }
    
    /**
     * カードを削除
     */
    public void removeOrderCard(OrderCard order) {
        OrderCardPanel toRemove = null;
        for (OrderCardPanel panel : cardPanels) {
            if (panel.getOrderCard() == order) {
                toRemove = panel;
                break;
            }
        }
        
        if (toRemove != null) {
            cardPanels.remove(toRemove);
            cardsContainer.remove(toRemove);
            cardsContainer.revalidate();
            cardsContainer.repaint();
        }
    }
    
    /**
     * 選択されているカードを取得
     */
    public List<OrderCard> getSelectedCards() {
        List<OrderCard> selected = new ArrayList<>();
        for (OrderCardPanel panel : cardPanels) {
            if (panel.isSelected()) {
                selected.add(panel.getOrderCard());
            }
        }
        return selected;
    }
    
    /**
     * すべてのカードの選択を解除
     */
    public void clearSelection() {
        for (OrderCardPanel panel : cardPanels) {
            panel.setSelected(false);
        }
    }
    
    /**
     * カードをすべてクリア
     */
    public void clearCards() {
        cardsContainer.removeAll();
        cardPanels.clear();
        cardsContainer.revalidate();
        cardsContainer.repaint();
    }
    
    /**
     * カードパネルのリストを取得
     */
    public List<OrderCardPanel> getCardPanels() {
        return cardPanels;
    }
}
