package game.view.components;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import game.model.order.OrderCard;
import game.model.Player;

/**
 * CustomerPanel
 * ------------------------------
 * プレイヤーの手持ち注文カードを表示するパネル。
 * 上部：未提供カード、下部：提供済みカード
 */
public class CustomerPanel extends JPanel {
    private Image backgroundImage;
    private static final int CORNER_RADIUS = 10;
    
    // 上部：未提供カード
    private JPanel pendingCardsContainer;
    private List<OrderCardPanel> pendingCardPanels = new ArrayList<>();
    private JScrollPane pendingScrollPane;
    
    // 下部：提供済みカード
    private JPanel servedCardsContainer;
    private List<OrderCardPanel> servedCardPanels = new ArrayList<>();
    private JScrollPane servedScrollPane;
    
    private JLabel titleLabel;
    private JLabel servedTitleLabel;
    
    // 提供フェーズ用コールバック
    private Consumer<OrderCard> onServeCallback;
    private boolean servePhaseActive = false;
    private Player currentPlayer; // 提供可能判定用のプレイヤー情報
    private OrderCardPanel selectedCardPanel; // 現在選択されているカード（提供ボタン表示用）
    
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
        
        // メインコンテナ（上下分割）
        JPanel mainContainer = new JPanel();
        mainContainer.setOpaque(false);
        mainContainer.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        
        // ===== 上部：未提供カード =====
        JPanel pendingSection = new JPanel(new BorderLayout());
        pendingSection.setOpaque(false);
        
        titleLabel = new JLabel("-Customers-", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SERIF, Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 3, 0));
        pendingSection.add(titleLabel, BorderLayout.NORTH);
        
        pendingCardsContainer = createFlowContainer();
        pendingScrollPane = createScrollPane(pendingCardsContainer);
        pendingSection.add(pendingScrollPane, BorderLayout.CENTER);
        
        gbc.gridy = 0;
        gbc.weighty = 0.6;  // 60%
        mainContainer.add(pendingSection, gbc);
        
        // ===== セパレータ =====
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setForeground(new Color(100, 100, 100));
        separator.setBackground(new Color(60, 60, 60));
        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(3, 10, 3, 10);
        mainContainer.add(separator, gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
        
        // ===== 下部：提供済みカード =====
        JPanel servedSection = new JPanel(new BorderLayout());
        servedSection.setOpaque(false);
        
        servedTitleLabel = new JLabel("-Served-", SwingConstants.CENTER);
        servedTitleLabel.setFont(new Font(Font.SERIF, Font.BOLD, 14));
        servedTitleLabel.setForeground(new Color(150, 150, 150));
        servedTitleLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        servedSection.add(servedTitleLabel, BorderLayout.NORTH);
        
        servedCardsContainer = createFlowContainer();
        servedScrollPane = createScrollPane(servedCardsContainer);
        servedSection.add(servedScrollPane, BorderLayout.CENTER);
        
        gbc.gridy = 2;
        gbc.weighty = 0.4;  // 40%
        mainContainer.add(servedSection, gbc);
        
        this.add(mainContainer, BorderLayout.CENTER);
    }
    
    /**
     * FlowLayoutのカードコンテナを作成（常に4列表示）
     */
    private JPanel createFlowContainer() {
        // 4列表示のための固定幅計算: カード幅(120) * 4 + ギャップ(5) * 3 + パディング(16)
        final int FIXED_WIDTH = 120 * 4 + 5 * 3 + 16; // 511px
        
        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5)) {
            @Override
            public Dimension getPreferredSize() {
                // 常に4列表示するための固定幅
                int width = FIXED_WIDTH;
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
                return new Dimension(width, Math.max(y, 50));
            }
        };
        container.setOpaque(false);
        return container;
    }
    
    /**
     * スクロールペインを作成
     */
    private JScrollPane createScrollPane(JPanel container) {
        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(3, 8, 5, 8));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        scrollPane.getViewport().addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                container.revalidate();
            }
        });
        
        setupMinimalScrollBar(scrollPane);
        return scrollPane;
    }
    
    /**
     * スクロールバーを目立たないスタイルに設定
     */
    private void setupMinimalScrollBar(JScrollPane scrollPane) {
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setPreferredSize(new Dimension(6, 0));
        verticalBar.setOpaque(false);
        verticalBar.setUI(new BasicScrollBarUI() {
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
        
        g2d.setColor(Color.BLACK);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
        
        if (backgroundImage != null) {
            int imgWidth = backgroundImage.getWidth(this);
            int imgHeight = backgroundImage.getHeight(this);
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            
            double scaleX = (double) panelWidth / imgWidth;
            double scaleY = (double) panelHeight / imgHeight;
            double scale = Math.max(scaleX, scaleY);
            
            int scaledWidth = (int) (imgWidth * scale);
            int scaledHeight = (int) (imgHeight * scale);
            
            int x = (panelWidth - scaledWidth) / 2;
            int y = (panelHeight - scaledHeight) / 2;
            
            Shape clip = new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
            g2d.setClip(clip);
            g2d.drawImage(backgroundImage, x, y, scaledWidth, scaledHeight, this);
            g2d.setClip(null);
        }
        
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(3.0f));
        g2d.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, CORNER_RADIUS, CORNER_RADIUS);
        
        g2d.dispose();
    }
    
    /**
     * 注文カードを設定（プレイヤーの手札を表示）
     */
    public void setOrderCards(List<OrderCard> orders) {
        pendingCardsContainer.removeAll();
        pendingCardPanels.clear();
        selectedCardPanel = null;
        
        if (orders == null || orders.isEmpty()) {
            JLabel emptyLabel = new JLabel("カードがありません");
            emptyLabel.setForeground(Color.WHITE);
            emptyLabel.setFont(new Font(Font.SERIF, Font.ITALIC, 14));
            pendingCardsContainer.add(emptyLabel);
        } else {
            for (OrderCard order : orders) {
                OrderCardPanel cardPanel = new OrderCardPanel(order);
                setupCardClickHandler(cardPanel);
                // 提供フェーズ中の場合、提供可能かどうかを判定
                if (servePhaseActive && currentPlayer != null) {
                    boolean canServe = currentPlayer.canComplete(order);
                    cardPanel.setServeEnabled(canServe);
                    cardPanel.setCanServe(canServe);
                }
                pendingCardPanels.add(cardPanel);
                pendingCardsContainer.add(cardPanel);
            }
        }
        
        pendingCardsContainer.revalidate();
        pendingCardsContainer.repaint();
    }
    
    /**
     * カードのクリックハンドラーを設定
     */
    private void setupCardClickHandler(OrderCardPanel cardPanel) {
        cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (servePhaseActive) {
                    // 提供可能なカードをクリックした場合、提供ボタンを表示
                    if (cardPanel.isCanServe()) {
                        // 以前選択されていたカードの提供ボタンを非表示
                        if (selectedCardPanel != null && selectedCardPanel != cardPanel) {
                            hideServeButton(selectedCardPanel);
                        }
                        // クリックされたカードの提供ボタンを表示/非表示を切り替え
                        boolean showButton = !cardPanel.isShowServeButton();
                        if (showButton) {
                            showServeButton(cardPanel);
                            selectedCardPanel = cardPanel;
                        } else {
                            hideServeButton(cardPanel);
                            selectedCardPanel = null;
                        }
                    }
                }
            }
        });
    }
    
    /**
     * カードの下に提供ボタンを表示
     */
    private void showServeButton(OrderCardPanel cardPanel) {
        cardPanel.setShowServeButton(true);
        
        // 提供ボタンのクリックイベントを設定
        cardPanel.setServeButtonListener(e -> {
            if (onServeCallback != null) {
                onServeCallback.accept(cardPanel.getOrderCard());
                // 提供後、ボタンを非表示
                hideServeButton(cardPanel);
                selectedCardPanel = null;
            }
        });
        
        pendingCardsContainer.revalidate();
        pendingCardsContainer.repaint();
    }
    
    /**
     * カードの提供ボタンを非表示
     */
    private void hideServeButton(OrderCardPanel cardPanel) {
        cardPanel.setShowServeButton(false);
        cardPanel.setServeButtonListener(null);
        
        pendingCardsContainer.revalidate();
        pendingCardsContainer.repaint();
    }
    
    /**
     * 提供フェーズを開始
     * @param player プレイヤー情報（提供可能判定用）
     * @param onServe 提供コールバック
     */
    public void startServePhase(Player player, Consumer<OrderCard> onServe) {
        this.servePhaseActive = true;
        this.onServeCallback = onServe;
        this.currentPlayer = player;
        this.selectedCardPanel = null;
        titleLabel.setText("-Customers- (提供可能)");
        titleLabel.setForeground(new Color(150, 255, 150));
        
        // 提供可能なカードのみを緑色で強調表示
        updateCanServeStatus();
        
        revalidate();
        repaint();
    }
    
    /**
     * 提供可能なカードの状態を更新（インベントリ変更後に呼び出す）
     */
    public void updateCanServeStatus() {
        if (currentPlayer != null && servePhaseActive) {
            for (OrderCardPanel panel : pendingCardPanels) {
                boolean canServe = currentPlayer.canComplete(panel.getOrderCard());
                panel.setServeEnabled(canServe);
                panel.setCanServe(canServe);
                // 提供不可能になったカードの提供ボタンを非表示
                if (!canServe && panel.isShowServeButton()) {
                    hideServeButton(panel);
                    if (selectedCardPanel == panel) {
                        selectedCardPanel = null;
                    }
                }
            }
            revalidate();
            repaint();
        }
    }
    
    /**
     * プレイヤー情報を更新（提供後にインベントリが変更された場合に呼び出す）
     */
    public void updatePlayer(Player player) {
        this.currentPlayer = player;
        if (servePhaseActive) {
            updateCanServeStatus();
        }
    }
    
    /**
     * 提供フェーズを終了
     */
    public void endServePhase() {
        this.servePhaseActive = false;
        this.onServeCallback = null;
        this.currentPlayer = null;
        
        // すべての提供ボタンを非表示
        for (OrderCardPanel panel : pendingCardPanels) {
            hideServeButton(panel);
            panel.setServeEnabled(false);
            panel.setCanServe(false);
        }
        
        selectedCardPanel = null;
        
        titleLabel.setText("-Customers-");
        titleLabel.setForeground(Color.WHITE);
        
        revalidate();
        repaint();
    }
    
    /**
     * カードを提供済みに移動
     */
    public void moveToServed(OrderCard order) {
        OrderCardPanel toMove = null;
        for (OrderCardPanel panel : pendingCardPanels) {
            if (panel.getOrderCard() == order) {
                toMove = panel;
                break;
            }
        }
        
        if (toMove != null) {
            // 提供ボタンを非表示
            hideServeButton(toMove);
            if (selectedCardPanel == toMove) {
                selectedCardPanel = null;
            }
            
            pendingCardPanels.remove(toMove);
            pendingCardsContainer.remove(toMove);
            
            // 暗くして提供済みエリアに移動
            toMove.setServed(true);
            toMove.setServeEnabled(false);
            toMove.setCanServe(false);
            toMove.setShowServeButton(false);
            servedCardPanels.add(toMove);
            servedCardsContainer.add(toMove);
            
            pendingCardsContainer.revalidate();
            pendingCardsContainer.repaint();
            servedCardsContainer.revalidate();
            servedCardsContainer.repaint();
        }
    }
    
    /**
     * カードを追加（未提供エリア）
     */
    public void addOrderCard(OrderCard order) {
        OrderCardPanel cardPanel = new OrderCardPanel(order);
        setupCardClickHandler(cardPanel);
        if (servePhaseActive && currentPlayer != null) {
            boolean canServe = currentPlayer.canComplete(order);
            cardPanel.setServeEnabled(canServe);
            cardPanel.setCanServe(canServe);
        }
        pendingCardPanels.add(cardPanel);
        pendingCardsContainer.add(cardPanel);
        
        pendingCardsContainer.revalidate();
        pendingCardsContainer.repaint();
    }
    
    /**
     * カードを削除（未提供エリアから）
     */
    public void removeOrderCard(OrderCard order) {
        OrderCardPanel toRemove = null;
        for (OrderCardPanel panel : pendingCardPanels) {
            if (panel.getOrderCard() == order) {
                toRemove = panel;
                break;
            }
        }
        
        if (toRemove != null) {
            pendingCardPanels.remove(toRemove);
            pendingCardsContainer.remove(toRemove);
            pendingCardsContainer.revalidate();
            pendingCardsContainer.repaint();
        }
    }
    
    /**
     * 選択されているカードを取得
     */
    public List<OrderCard> getSelectedCards() {
        List<OrderCard> selected = new ArrayList<>();
        for (OrderCardPanel panel : pendingCardPanels) {
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
        for (OrderCardPanel panel : pendingCardPanels) {
            panel.setSelected(false);
        }
    }
    
    /**
     * カードをすべてクリア
     */
    public void clearCards() {
        pendingCardsContainer.removeAll();
        pendingCardPanels.clear();
        servedCardsContainer.removeAll();
        servedCardPanels.clear();
        
        pendingCardsContainer.revalidate();
        pendingCardsContainer.repaint();
        servedCardsContainer.revalidate();
        servedCardsContainer.repaint();
    }
    
    /**
     * カードパネルのリストを取得（未提供のみ）
     */
    public List<OrderCardPanel> getCardPanels() {
        return pendingCardPanels;
    }
    
    /**
     * 提供済みカードパネルのリストを取得
     */
    public List<OrderCardPanel> getServedCardPanels() {
        return servedCardPanels;
    }
}
