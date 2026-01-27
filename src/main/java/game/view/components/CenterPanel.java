package game.view.components;

import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import game.model.Player;
import game.model.alcohol.AlcoholType;
import game.model.alcohol.TruckCard;
import game.controller.TradeProposal;

public class CenterPanel extends JPanel {
    private Image backgroundImage;
    private Image truckCardImage;
    private static final int CORNER_RADIUS = 10;
    
    // アイコン画像
    private Map<AlcoholType, Image> alcoholIcons = new HashMap<>();
    private Image moneyIcon;
    
    // フェーズの種類
    public enum PhaseType {
        AUCTION,    // bootlegging.png
        TRADE,      // trade.png
        PENALTY,    // penalty.png
        SERVE,      // serve.png
        DEFAULT     // bootlegging.png
    }
    
    private PhaseType currentPhase = PhaseType.DEFAULT;
    
    // オークション用のコンテンツパネル
    private JPanel contentPanel;
    private TruckCardPanel truckCardPanel;
    private JPanel bidPanel; // BidPanel または 入札完了パネル
    
    // 入札コールバック
    private Consumer<Integer> bidCallback;
    
    // プレイヤー名（北・南・東・西）
    private String northPlayerName = "";
    private String southPlayerName = "";
    private String eastPlayerName = "";
    private String westPlayerName = "";
    
    public CenterPanel() {
        this.setOpaque(false);
        this.setLayout(new BorderLayout());
        
        loadBackgroundImage(PhaseType.DEFAULT);
        loadTruckCardImage();
        loadAlcoholIcons();
        loadMoneyIcon();
        
        // コンテンツパネル（中央に配置、外側に余白を設けて名前表示スペースを確保）
        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        this.add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * プレイヤー名を設定
     */
    public void setPlayerNames(String north, String east, String south, String west) {
        this.northPlayerName = north != null ? north : "";
        this.eastPlayerName = east != null ? east : "";
        this.southPlayerName = south != null ? south : "";
        this.westPlayerName = west != null ? west : "";
        repaint();
    }
    
    private void loadBackgroundImage(PhaseType phase) {
        String imagePath;
        switch (phase) {
            case AUCTION:
                imagePath = "/images/ui/gameplay/bootlegging.png";
                break;
            case TRADE:
                imagePath = "/images/ui/gameplay/trade.png";
                break;
            case PENALTY:
                imagePath = "/images/ui/gameplay/penalty.png";
                break;
            case SERVE:
                imagePath = "/images/ui/gameplay/serve.png";
                break;
            default:
                imagePath = "/images/ui/gameplay/bootlegging.png";
                break;
        }
        
        java.net.URL imageUrl = getClass().getResource(imagePath);
        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            backgroundImage = icon.getImage();
        } else {
            backgroundImage = null;
        }
    }
    
    private void loadTruckCardImage() {
        java.net.URL imageUrl = getClass().getResource("/images/ui/gameplay/truckcard.png");
        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            truckCardImage = icon.getImage();
        } else {
            truckCardImage = null;
        }
    }
    
    private void loadAlcoholIcons() {
        for (AlcoholType type : AlcoholType.values()) {
            String fileName = type.name().toLowerCase() + ".png";
            java.net.URL imageUrl = getClass().getResource("/images/ui/gameplay/alcohol/" + fileName);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                alcoholIcons.put(type, icon.getImage());
            }
        }
    }
    
    private void loadMoneyIcon() {
        java.net.URL imageUrl = getClass().getResource("/images/ui/gameplay/bills.png");
        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            moneyIcon = icon.getImage();
        }
    }
    
    /**
     * フェーズを切り替える
     */
    public void setPhase(PhaseType phase) {
        this.currentPhase = phase;
        loadBackgroundImage(phase);
        repaint();
    }
    
    /**
     * 半透明背景付きのラベルパネルを作成
     */
    private JPanel createLabelWithBackground(String text, Font font, Color textColor) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // 半透明の灰色背景
                g2d.setColor(new Color(40, 40, 40, 180));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(font);
        label.setForeground(textColor);
        panel.add(label, BorderLayout.CENTER);
        
        return panel;
    }
    
    private Player currentBiddingPlayer; // 現在入札中のプレイヤー
    private TruckCard currentTruck; // 現在のトラックカード
    
    /**
     * オークション表示を開始（フェーズ紹介を表示後、トラックカードを表示）
     */
    public void showAuction(TruckCard truck, Player currentPlayer, Consumer<Integer> onBidSubmit) {
        this.bidCallback = onBidSubmit;
        this.currentBiddingPlayer = currentPlayer;
        this.currentTruck = truck;
        setPhase(PhaseType.AUCTION);
        
        // まずフェーズ紹介を表示
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        JPanel titlePanel = createLabelWithBackground("オークションフェーズ", 
            new Font(Font.SERIF, Font.BOLD, 28), Color.WHITE);
        contentPanel.add(titlePanel, gbc);
        
        gbc.gridy = 1;
        JPanel infoPanel = createLabelWithBackground("トラックから届いた酒を競り落とそう", 
            new Font(Font.SERIF, Font.PLAIN, 18), new Color(220, 220, 220));
        contentPanel.add(infoPanel, gbc);
        
        contentPanel.revalidate();
        contentPanel.repaint();
        
        // 3.5秒後にトラックカードと入札UIを表示
        Timer timer = new Timer(3500, e -> {
            showAuctionContent(truck, currentPlayer);
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * オークションのトラックカードと入札UIを表示
     */
    private void showAuctionContent(TruckCard truck, Player currentPlayer) {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.NONE;
        
        // トラックカードパネル
        truckCardPanel = new TruckCardPanel(truck);
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(truckCardPanel, gbc);
        
        // 入札パネル（bidCallbackがnullなら待機中表示）
        boolean isMyTurn = (bidCallback != null);
        bidPanel = new BidPanel(currentPlayer, isMyTurn, bid -> {
            if (bidCallback != null) {
                bidCallback.accept(bid);
            }
        });
        gbc.gridy = 1;
        contentPanel.add(bidPanel, gbc);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * 次のプレイヤーの入札に切り替え
     * @param player 入札するプレイヤー
     * @param onBidSubmit コールバック（nullの場合は待機中表示）
     */
    public void showBidForPlayer(Player player, Consumer<Integer> onBidSubmit) {
        this.bidCallback = onBidSubmit;
        
        if (bidPanel != null) {
            contentPanel.remove(bidPanel);
        }
        
        // 自分の番かどうかでUI表示を切り替え
        boolean isMyTurn = (onBidSubmit != null);
        bidPanel = new BidPanel(player, isMyTurn, bid -> {
            if (bidCallback != null) {
                bidCallback.accept(bid);
            }
        });
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(bidPanel, gbc);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * 入札完了を表示（自分が入札した後）
     */
    public void showBidComplete(Player player) {
        if (bidPanel != null) {
            contentPanel.remove(bidPanel);
        }
        
        // 入札完了パネル
        JPanel completePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(40, 80, 40, 180));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        completePanel.setOpaque(false);
        completePanel.setLayout(new BoxLayout(completePanel, BoxLayout.Y_AXIS));
        completePanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        JLabel completeLabel = new JLabel("✓ 入札完了");
        completeLabel.setFont(new Font(Font.SERIF, Font.BOLD, 20));
        completeLabel.setForeground(new Color(150, 255, 150));
        completeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        completePanel.add(completeLabel);
        
        completePanel.add(Box.createVerticalStrut(10));
        
        JLabel waitLabel = new JLabel("他のプレイヤーを待っています...");
        waitLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 14));
        waitLabel.setForeground(Color.WHITE);
        waitLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        completePanel.add(waitLabel);
        
        bidPanel = completePanel;
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(bidPanel, gbc);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * 取引終了待機表示
     */
    public void showTradeEndWaiting(Player player, int completedCount, int totalCount) {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // 取引終了完了パネル
        JPanel completePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(60, 60, 100, 180));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        completePanel.setOpaque(false);
        completePanel.setLayout(new BoxLayout(completePanel, BoxLayout.Y_AXIS));
        completePanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        JLabel completeLabel = new JLabel("✓ 取引終了");
        completeLabel.setFont(new Font(Font.SERIF, Font.BOLD, 24));
        completeLabel.setForeground(new Color(150, 200, 255));
        completeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        completePanel.add(completeLabel);
        
        completePanel.add(Box.createVerticalStrut(15));
        
        JLabel waitLabel = new JLabel("他のプレイヤーを待っています...");
        waitLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 16));
        waitLabel.setForeground(Color.WHITE);
        waitLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        completePanel.add(waitLabel);
        
        completePanel.add(Box.createVerticalStrut(10));
        
        JLabel countLabel = new JLabel(completedCount + " / " + totalCount + " 人完了");
        countLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 14));
        countLabel.setForeground(new Color(200, 200, 200));
        countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        completePanel.add(countLabel);
        
        contentPanel.add(completePanel, gbc);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * ゲーム開始を表示
     */
    public void showGameStart() {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // ゲーム開始パネル
        JPanel startPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(80, 50, 20, 220));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.setColor(new Color(255, 200, 100));
                g2d.setStroke(new BasicStroke(3f));
                g2d.drawRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        startPanel.setOpaque(false);
        startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.Y_AXIS));
        startPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
        
        JLabel titleLabel = new JLabel("GAME START");
        titleLabel.setFont(new Font(Font.SERIF, Font.BOLD, 36));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        startPanel.add(titleLabel);
        
        contentPanel.add(startPanel, gbc);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * ラウンド開始を表示
     */
    public void showRoundStart(int roundNumber) {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // ラウンド開始パネル
        JPanel roundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(40, 40, 80, 220));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.setColor(new Color(150, 150, 255));
                g2d.setStroke(new BasicStroke(3f));
                g2d.drawRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        roundPanel.setOpaque(false);
        roundPanel.setLayout(new BoxLayout(roundPanel, BoxLayout.Y_AXIS));
        roundPanel.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));
        
        JLabel roundLabel = new JLabel("ROUND " + roundNumber);
        roundLabel.setFont(new Font(Font.SERIF, Font.BOLD, 32));
        roundLabel.setForeground(Color.WHITE);
        roundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        roundPanel.add(roundLabel);
        
        contentPanel.add(roundPanel, gbc);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * 取引フェーズ終了を表示
     */
    public void showTradePhaseComplete() {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // 取引終了パネル
        JPanel endPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(50, 80, 50, 200));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.setColor(new Color(100, 200, 100));
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        endPanel.setOpaque(false);
        endPanel.setLayout(new BoxLayout(endPanel, BoxLayout.Y_AXIS));
        endPanel.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));
        
        JLabel titleLabel = new JLabel("取引終了");
        titleLabel.setFont(new Font(Font.SERIF, Font.BOLD, 28));
        titleLabel.setForeground(new Color(150, 255, 150));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        endPanel.add(titleLabel);
        
        endPanel.add(Box.createVerticalStrut(10));
        
        JLabel subLabel = new JLabel("取引フェーズが終了しました");
        subLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 16));
        subLabel.setForeground(Color.WHITE);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        endPanel.add(subLabel);
        
        contentPanel.add(endPanel, gbc);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    // ============ 提供フェーズ用コールバック ============
    private Runnable serveEndCallback;
    
    /**
     * 提供フェーズ開始を表示
     */
    public void showServePhaseStart() {
        setPhase(PhaseType.SERVE);  // serve.pngを使用
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // 提供フェーズ開始パネル
        JPanel startPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(80, 60, 40, 220));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.setColor(new Color(200, 150, 100));
                g2d.setStroke(new BasicStroke(3f));
                g2d.drawRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        startPanel.setOpaque(false);
        startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.Y_AXIS));
        startPanel.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));
        
        JLabel titleLabel = new JLabel("SERVE PHASE");
        titleLabel.setFont(new Font(Font.SERIF, Font.BOLD, 28));
        titleLabel.setForeground(new Color(255, 220, 150));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        startPanel.add(titleLabel);
        
        startPanel.add(Box.createVerticalStrut(10));
        
        JLabel subLabel = new JLabel("お客様にお酒を提供しましょう");
        subLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 16));
        subLabel.setForeground(Color.WHITE);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        startPanel.add(subLabel);
        
        contentPanel.add(startPanel, gbc);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * 提供フェーズのメインUI（提供終了ボタン付き）
     */
    public void showServePhaseUI(Runnable onEndServe) {
        this.serveEndCallback = onEndServe;
        
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // 説明パネル
        JPanel infoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(60, 50, 40, 200));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel titleLabel = new JLabel("提供フェーズ");
        titleLabel.setFont(new Font(Font.SERIF, Font.BOLD, 22));
        titleLabel.setForeground(new Color(255, 220, 150));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(titleLabel);
        
        infoPanel.add(Box.createVerticalStrut(10));
        
        JLabel infoLabel1 = new JLabel("右側のカードをクリックして");
        infoLabel1.setFont(new Font(Font.SERIF, Font.PLAIN, 14));
        infoLabel1.setForeground(Color.WHITE);
        infoLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(infoLabel1);
        
        JLabel infoLabel2 = new JLabel("お客様にお酒を提供しましょう");
        infoLabel2.setFont(new Font(Font.SERIF, Font.PLAIN, 14));
        infoLabel2.setForeground(Color.WHITE);
        infoLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(infoLabel2);
        
        contentPanel.add(infoPanel, gbc);
        
        // 提供終了ボタン
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 10, 10, 10);
        
        JButton endButton = new JButton("提供終了");
        endButton.setFont(new Font(Font.SERIF, Font.BOLD, 18));
        endButton.setBackground(new Color(139, 90, 43));
        endButton.setForeground(Color.WHITE);
        endButton.setFocusPainted(false);
        endButton.setPreferredSize(new Dimension(180, 50));
        endButton.addActionListener(e -> {
            if (serveEndCallback != null) {
                serveEndCallback.run();
            }
        });
        contentPanel.add(endButton, gbc);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * 提供終了待ち表示
     */
    public void showServeEndWaiting(int completedCount, int totalCount) {
        setPhase(PhaseType.SERVE);  // serve.pngを使用
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // 待機パネル
        JPanel waitPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(60, 80, 60, 200));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        waitPanel.setOpaque(false);
        waitPanel.setLayout(new BoxLayout(waitPanel, BoxLayout.Y_AXIS));
        waitPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        JLabel titleLabel = new JLabel("✓ 提供終了");
        titleLabel.setFont(new Font(Font.SERIF, Font.BOLD, 24));
        titleLabel.setForeground(new Color(150, 255, 150));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        waitPanel.add(titleLabel);
        
        waitPanel.add(Box.createVerticalStrut(15));
        
        JLabel waitLabel = new JLabel("他のプレイヤーを待っています...");
        waitLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 16));
        waitLabel.setForeground(Color.WHITE);
        waitLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        waitPanel.add(waitLabel);
        
        waitPanel.add(Box.createVerticalStrut(10));
        
        JLabel countLabel = new JLabel(completedCount + " / " + totalCount + " 人完了");
        countLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 14));
        countLabel.setForeground(new Color(200, 200, 200));
        countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        waitPanel.add(countLabel);
        
        contentPanel.add(waitPanel, gbc);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * 提供フェーズ終了を表示
     */
    public void showServePhaseComplete() {
        setPhase(PhaseType.SERVE);  // serve.pngを使用
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // 終了パネル
        JPanel endPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(50, 80, 50, 200));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.setColor(new Color(100, 200, 100));
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        endPanel.setOpaque(false);
        endPanel.setLayout(new BoxLayout(endPanel, BoxLayout.Y_AXIS));
        endPanel.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));
        
        JLabel titleLabel = new JLabel("提供終了");
        titleLabel.setFont(new Font(Font.SERIF, Font.BOLD, 28));
        titleLabel.setForeground(new Color(150, 255, 150));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        endPanel.add(titleLabel);
        
        endPanel.add(Box.createVerticalStrut(10));
        
        JLabel subLabel = new JLabel("提供フェーズが終了しました");
        subLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 16));
        subLabel.setForeground(Color.WHITE);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        endPanel.add(subLabel);
        
        contentPanel.add(endPanel, gbc);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * 全員の入札額を開示
     * @param bidResults プレイヤー名と入札額のマップ
     */
    public void showAllBids(java.util.Map<String, Integer> bidResults) {
        if (bidPanel != null) {
            contentPanel.remove(bidPanel);
        }
        
        // 入札結果パネル
        JPanel allBidsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(50, 50, 80, 200));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.setColor(new Color(100, 100, 200));
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        allBidsPanel.setOpaque(false);
        allBidsPanel.setLayout(new BoxLayout(allBidsPanel, BoxLayout.Y_AXIS));
        allBidsPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        
        // タイトル
        JLabel titleLabel = new JLabel("入札結果発表");
        titleLabel.setFont(new Font(Font.SERIF, Font.BOLD, 22));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        allBidsPanel.add(titleLabel);
        allBidsPanel.add(Box.createVerticalStrut(15));
        
        // 各プレイヤーの入札額
        for (java.util.Map.Entry<String, Integer> entry : bidResults.entrySet()) {
            JLabel bidLabel = new JLabel(entry.getKey() + ": " + entry.getValue() + "円");
            bidLabel.setFont(new Font(Font.SERIF, Font.BOLD, 18));
            bidLabel.setForeground(Color.WHITE);
            bidLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            allBidsPanel.add(bidLabel);
            allBidsPanel.add(Box.createVerticalStrut(5));
        }
        
        bidPanel = allBidsPanel;
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(allBidsPanel, gbc);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * オークション結果を表示
     * @param winner 勝者（nullの場合は再入札メッセージを表示）
     * @param winningBid 落札額
     */
    public void showAuctionResult(Player winner, int winningBid) {
        if (bidPanel != null) {
            contentPanel.remove(bidPanel);
        }
        
        // 半透明背景付きの結果パネル
        JPanel resultPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(40, 40, 40, 180));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        resultPanel.setOpaque(false);
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        if (winner == null) {
            // 再入札メッセージ
            JLabel retryLabel = new JLabel("全員同額！再入札！");
            retryLabel.setFont(new Font(Font.SERIF, Font.BOLD, 24));
            retryLabel.setForeground(new Color(255, 150, 100)); // オレンジ
            retryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            resultPanel.add(retryLabel);
        } else {
            JLabel winnerLabel = new JLabel(winner.getName() + " が落札！");
            winnerLabel.setFont(new Font(Font.SERIF, Font.BOLD, 24));
            winnerLabel.setForeground(new Color(255, 215, 0)); // ゴールド
            winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            resultPanel.add(winnerLabel);
            
            resultPanel.add(Box.createVerticalStrut(5));
            
            JLabel bidLabel = new JLabel("落札額: " + winningBid + "円");
            bidLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
            bidLabel.setForeground(Color.WHITE);
            bidLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            resultPanel.add(bidLabel);
        }
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(resultPanel, gbc);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * コンテンツをクリア
     */
    public void clearContent() {
        contentPanel.removeAll();
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    // 取引フェーズ関連
    private Consumer<Boolean> tradeResponseCallback;
    private Consumer<String> tradeActionCallback; // "edit" or "cancel"
    private TradeProposal currentTradeProposal;
    private boolean isTradeProposer; // 自分が送り主か
    
    /**
     * 取引フェーズの表示を開始
     */
    public void showTradePhase() {
        setPhase(PhaseType.TRADE);
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // 取引フェーズのタイトル（半透明背景付き）
        JPanel titlePanel = createLabelWithBackground("取引フェーズ", 
            new Font(Font.SERIF, Font.BOLD, 28), Color.WHITE);
        contentPanel.add(titlePanel, gbc);
        
        gbc.gridy = 1;
        JPanel infoPanel = createLabelWithBackground("他プレイヤーと酒の交換ができます", 
            new Font(Font.SERIF, Font.PLAIN, 18), new Color(220, 220, 220));
        contentPanel.add(infoPanel, gbc);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * 取引提案を受信した際の表示（相手側）
     */
    public void showReceivedTrade(TradeProposal proposal, Consumer<Boolean> onResponse) {
        this.currentTradeProposal = proposal;
        this.tradeResponseCallback = onResponse;
        this.isTradeProposer = false;
        
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // タイトル
        JPanel titlePanel = createLabelWithBackground(proposal.getFrom().getName() + " からの取引提案", 
            new Font(Font.SERIF, Font.BOLD, 22), new Color(255, 215, 0));
        contentPanel.add(titlePanel, gbc);
        
        // 取引内容パネル
        gbc.gridy = 1;
        JPanel tradeContentPanel = createTradeContentPanel(proposal);
        contentPanel.add(tradeContentPanel, gbc);
        
        // ボタンパネル
        gbc.gridy = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        
        JButton acceptButton = createStyledButton("交渉成立", new Color(60, 140, 60));
        acceptButton.addActionListener(e -> {
            if (tradeResponseCallback != null) {
                tradeResponseCallback.accept(true);
            }
            showTradePhase();
        });
        buttonPanel.add(acceptButton);
        
        JButton rejectButton = createStyledButton("破談", new Color(180, 60, 60));
        rejectButton.addActionListener(e -> {
            if (tradeResponseCallback != null) {
                tradeResponseCallback.accept(false);
            }
            showTradePhase();
        });
        buttonPanel.add(rejectButton);
        
        contentPanel.add(buttonPanel, gbc);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * 送った取引の表示（送り主側）
     */
    public void showSentTrade(TradeProposal proposal, Consumer<String> onAction) {
        this.currentTradeProposal = proposal;
        this.tradeActionCallback = onAction;
        this.isTradeProposer = true;
        
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // タイトル
        JPanel titlePanel = createLabelWithBackground(proposal.getTo().getName() + " への取引提案中...", 
            new Font(Font.SERIF, Font.BOLD, 22), new Color(100, 180, 255));
        contentPanel.add(titlePanel, gbc);
        
        // 取引内容パネル
        gbc.gridy = 1;
        JPanel tradeContentPanel = createTradeContentPanel(proposal);
        contentPanel.add(tradeContentPanel, gbc);
        
        // ボタンパネル
        gbc.gridy = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        
        JButton editButton = createStyledButton("編集", new Color(100, 100, 180));
        editButton.addActionListener(e -> {
            if (tradeActionCallback != null) {
                tradeActionCallback.accept("edit");
            }
        });
        buttonPanel.add(editButton);
        
        JButton cancelButton = createStyledButton("破談", new Color(180, 60, 60));
        cancelButton.addActionListener(e -> {
            if (tradeActionCallback != null) {
                tradeActionCallback.accept("cancel");
            }
            showTradePhase();
        });
        buttonPanel.add(cancelButton);
        
        contentPanel.add(buttonPanel, gbc);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * 取引内容を表示するパネルを作成
     */
    private JPanel createTradeContentPanel(TradeProposal proposal) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(40, 40, 40, 200));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // 渡すもの
        if (!proposal.getAlcoholOffered().isEmpty() || proposal.getMoneyOffered() > 0) {
            JLabel offerLabel = new JLabel("【渡すもの】");
            offerLabel.setForeground(new Color(255, 150, 150));
            offerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            offerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(offerLabel);
            
            for (Map.Entry<AlcoholType, Integer> entry : proposal.getAlcoholOffered().entrySet()) {
                JLabel itemLabel = new JLabel("  " + getAlcoholDisplayName(entry.getKey()) + " x" + entry.getValue());
                itemLabel.setForeground(Color.WHITE);
                itemLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
                itemLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(itemLabel);
            }
            if (proposal.getMoneyOffered() > 0) {
                JLabel moneyLabel = new JLabel("  お金 " + proposal.getMoneyOffered() + "円");
                moneyLabel.setForeground(Color.WHITE);
                moneyLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
                moneyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(moneyLabel);
            }
            panel.add(Box.createVerticalStrut(10));
        }
        
        // 受け取るもの
        if (!proposal.getAlcoholRequested().isEmpty() || proposal.getMoneyRequested() > 0) {
            JLabel requestLabel = new JLabel("【受け取るもの】");
            requestLabel.setForeground(new Color(150, 200, 255));
            requestLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            requestLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(requestLabel);
            
            for (Map.Entry<AlcoholType, Integer> entry : proposal.getAlcoholRequested().entrySet()) {
                JLabel itemLabel = new JLabel("  " + getAlcoholDisplayName(entry.getKey()) + " x" + entry.getValue());
                itemLabel.setForeground(Color.WHITE);
                itemLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
                itemLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(itemLabel);
            }
            if (proposal.getMoneyRequested() > 0) {
                JLabel moneyLabel = new JLabel("  お金 " + proposal.getMoneyRequested() + "円");
                moneyLabel.setForeground(Color.WHITE);
                moneyLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
                moneyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(moneyLabel);
            }
        }
        
        return panel;
    }
    
    /**
     * スタイル付きボタンを作成
     */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        button.setPreferredSize(new Dimension(120, 40));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
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
    
    // お酒の色
    private static final Color BEER_COLOR = new Color(255, 200, 50);
    private static final Color RUM_COLOR = new Color(139, 69, 19);
    private static final Color VODKA_COLOR = new Color(200, 200, 255);
    private static final Color GIN_COLOR = new Color(150, 255, 150);
    private static final Color MONEY_COLOR = new Color(85, 170, 85);
    
    private Color getAlcoholColor(AlcoholType type) {
        switch (type) {
            case BEER: return BEER_COLOR;
            case RUM: return RUM_COLOR;
            case VODKA: return VODKA_COLOR;
            case GIN: return GIN_COLOR;
            default: return Color.GRAY;
        }
    }
    
    // 取引作成UI用のフィールド
    private Consumer<TradeProposal> tradeSubmitCallback;
    private Runnable tradeEndCallback;
    private JComboBox<PlayerComboItem> playerComboBox;
    private JToggleButton sellButton;
    private JToggleButton buyButton;
    private Map<AlcoholType, JSpinner> alcoholSpinners;
    private JSpinner moneySpinner;
    
    /**
     * 取引作成UIを表示（CenterPanel内で）
     */
    public void showTradeCreationUI(Player currentPlayer, List<Player> otherPlayers,
                                    Consumer<TradeProposal> onSubmit, Runnable onEndTrade) {
        this.tradeSubmitCallback = onSubmit;
        this.tradeEndCallback = onEndTrade;
        this.alcoholSpinners = new HashMap<>();
        
        setPhase(PhaseType.TRADE);
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        
        // タイトル
        JPanel titlePanel = createLabelWithBackground("取引を作成", 
            new Font(Font.SERIF, Font.BOLD, 22), Color.WHITE);
        contentPanel.add(titlePanel, gbc);
        
        // 取引相手選択
        gbc.gridy = 1;
        JPanel targetPanel = createTransparentPanel();
        targetPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        JLabel targetLabel = new JLabel("取引相手:");
        targetLabel.setForeground(Color.WHITE);
        targetLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        targetPanel.add(targetLabel);
        
        playerComboBox = new JComboBox<>();
        for (Player p : otherPlayers) {
            if (p.getId() != currentPlayer.getId()) {
                playerComboBox.addItem(new PlayerComboItem(p));
            }
        }
        playerComboBox.setPreferredSize(new Dimension(150, 28));
        targetPanel.add(playerComboBox);
        
        contentPanel.add(targetPanel, gbc);
        
        // 売る/買うボタン
        gbc.gridy = 2;
        JPanel modePanel = createTransparentPanel();
        modePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 5));
        
        sellButton = createModeButton("売る", new Color(220, 80, 80));
        buyButton = createModeButton("買う", new Color(80, 150, 220));
        
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(sellButton);
        modeGroup.add(buyButton);
        sellButton.setSelected(true);
        
        modePanel.add(sellButton);
        modePanel.add(buyButton);
        contentPanel.add(modePanel, gbc);
        
        // 取引内容パネル（お酒とお金）
        gbc.gridy = 3;
        JPanel itemsPanel = createItemsPanel(currentPlayer);
        contentPanel.add(itemsPanel, gbc);
        
        // ボタンパネル
        gbc.gridy = 4;
        JPanel buttonPanel = createTransparentPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 5));
        
        JButton sendButton = createStyledButton("送る", new Color(60, 140, 60));
        sendButton.addActionListener(e -> handleTradeSubmit(currentPlayer));
        buttonPanel.add(sendButton);
        
        JButton endButton = createStyledButton("取引終了", new Color(100, 100, 100));
        endButton.addActionListener(e -> {
            if (tradeEndCallback != null) {
                tradeEndCallback.run();
            }
        });
        buttonPanel.add(endButton);
        
        contentPanel.add(buttonPanel, gbc);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JPanel createTransparentPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(40, 40, 40, 160));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        return panel;
    }
    
    private JToggleButton createModeButton(String text, Color selectedColor) {
        JToggleButton button = new JToggleButton(text);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        button.setPreferredSize(new Dimension(80, 32));
        button.setFocusPainted(false);
        button.setBackground(new Color(80, 80, 80));
        button.setForeground(Color.WHITE);
        
        button.addChangeListener(e -> {
            if (button.isSelected()) {
                button.setBackground(selectedColor);
            } else {
                button.setBackground(new Color(80, 80, 80));
            }
        });
        
        return button;
    }
    
    private JPanel createItemsPanel(Player currentPlayer) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(40, 40, 40, 180));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        // お酒の行
        for (AlcoholType type : AlcoholType.values()) {
            JPanel row = createItemRow(type, currentPlayer);
            panel.add(row);
            panel.add(Box.createVerticalStrut(3));
        }
        
        // お金の行
        JPanel moneyRow = createMoneyRow(currentPlayer);
        panel.add(moneyRow);
        
        return panel;
    }
    
    private JPanel createItemRow(AlcoholType type, Player currentPlayer) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(280, 32));
        
        // 左側: アイコンと名前
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        leftPanel.setOpaque(false);
        
        // アイコン（画像を使用）
        Image alcoholImage = alcoholIcons.get(type);
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                if (alcoholImage != null) {
                    g2d.drawImage(alcoholImage, 0, 0, 24, 24, null);
                } else {
                    // フォールバック：円を描画
                    Color iconColor = getAlcoholColor(type);
                    g2d.setColor(iconColor);
                    g2d.fillOval(2, 2, 20, 20);
                }
                g2d.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(24, 24));
        leftPanel.add(iconPanel);
        
        JLabel nameLabel = new JLabel(getAlcoholDisplayName(type));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        leftPanel.add(nameLabel);
        
        row.add(leftPanel, BorderLayout.WEST);
        
        // 右側: [-] 数字 [+]
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 0));
        controlPanel.setOpaque(false);
        
        int maxValue = currentPlayer.getInventory().getOrDefault(type, 0) + 10;
        
        JButton minusBtn = createSmallButton("-");
        SpinnerNumberModel model = new SpinnerNumberModel(0, 0, maxValue, 1);
        JSpinner spinner = new JSpinner(model);
        spinner.setPreferredSize(new Dimension(50, 24));
        spinner.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        JFormattedTextField tf = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        tf.setEditable(false);
        tf.setHorizontalAlignment(JTextField.CENTER);
        
        JButton plusBtn = createSmallButton("+");
        
        minusBtn.addActionListener(e -> {
            int val = (Integer) spinner.getValue();
            if (val > 0) spinner.setValue(val - 1);
        });
        plusBtn.addActionListener(e -> {
            int val = (Integer) spinner.getValue();
            if (val < maxValue) spinner.setValue(val + 1);
        });
        
        alcoholSpinners.put(type, spinner);
        
        controlPanel.add(minusBtn);
        controlPanel.add(spinner);
        controlPanel.add(plusBtn);
        
        row.add(controlPanel, BorderLayout.EAST);
        
        return row;
    }
    
    private JPanel createMoneyRow(Player currentPlayer) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(280, 32));
        
        // 左側: アイコンと名前
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        leftPanel.setOpaque(false);
        
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                if (moneyIcon != null) {
                    g2d.drawImage(moneyIcon, 0, 0, 24, 24, null);
                } else {
                    // フォールバック：円を描画
                    g2d.setColor(MONEY_COLOR);
                    g2d.fillOval(2, 2, 20, 20);
                }
                g2d.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(24, 24));
        leftPanel.add(iconPanel);
        
        JLabel nameLabel = new JLabel("お金");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        leftPanel.add(nameLabel);
        
        row.add(leftPanel, BorderLayout.WEST);
        
        // 右側
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 0));
        controlPanel.setOpaque(false);
        
        int maxMoney = (currentPlayer != null) ? currentPlayer.getMoney() : 0;
        
        JButton minusBtn = createSmallButton("-");
        SpinnerNumberModel model = new SpinnerNumberModel(0, 0, maxMoney, 1);
        moneySpinner = new JSpinner(model);
        moneySpinner.setPreferredSize(new Dimension(50, 24));
        moneySpinner.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        JFormattedTextField tf = ((JSpinner.DefaultEditor) moneySpinner.getEditor()).getTextField();
        tf.setEditable(false);
        tf.setHorizontalAlignment(JTextField.CENTER);
        
        JButton plusBtn = createSmallButton("+");
        
        minusBtn.addActionListener(e -> {
            int val = (Integer) moneySpinner.getValue();
            if (val > 0) moneySpinner.setValue(val - 1);
        });
        plusBtn.addActionListener(e -> {
            int val = (Integer) moneySpinner.getValue();
            if (val < maxMoney) moneySpinner.setValue(val + 1);
        });
        
        controlPanel.add(minusBtn);
        controlPanel.add(moneySpinner);
        controlPanel.add(plusBtn);
        
        row.add(controlPanel, BorderLayout.EAST);
        
        return row;
    }
    
    private JButton createSmallButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        button.setPreferredSize(new Dimension(32, 24));
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        return button;
    }
    
    private void handleTradeSubmit(Player currentPlayer) {
        PlayerComboItem selectedItem = (PlayerComboItem) playerComboBox.getSelectedItem();
        if (selectedItem == null) return;
        
        Player target = selectedItem.player;
        boolean isSelling = sellButton.isSelected();
        
        Map<AlcoholType, Integer> alcoholOffered = new HashMap<>();
        Map<AlcoholType, Integer> alcoholRequested = new HashMap<>();
        int moneyOffered = 0;
        int moneyRequested = 0;
        
        // お酒
        for (AlcoholType type : AlcoholType.values()) {
            int amount = (Integer) alcoholSpinners.get(type).getValue();
            if (amount > 0) {
                if (isSelling) {
                    alcoholOffered.put(type, amount);
                } else {
                    alcoholRequested.put(type, amount);
                }
            }
        }
        
        // お金
        int moneyAmount = (Integer) moneySpinner.getValue();
        if (moneyAmount > 0) {
            if (isSelling) {
                moneyRequested = moneyAmount;
            } else {
                moneyOffered = moneyAmount;
            }
        }
        
        // 何も選んでいない場合
        boolean hasContent = !alcoholOffered.isEmpty() || !alcoholRequested.isEmpty() 
                            || moneyOffered > 0 || moneyRequested > 0;
        if (!hasContent) return;
        
        TradeProposal proposal = new TradeProposal(
            currentPlayer, target,
            alcoholOffered, alcoholRequested,
            moneyOffered, moneyRequested
        );
        
        if (tradeSubmitCallback != null) {
            tradeSubmitCallback.accept(proposal);
        }
    }
    
    /**
     * プレイヤー選択用コンボボックスのアイテム
     */
    private static class PlayerComboItem {
        Player player;
        
        PlayerComboItem(Player p) {
            this.player = p;
        }
        
        @Override
        public String toString() {
            return player.getName();
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
            
            double scaleX = (double) panelWidth / imgWidth;
            double scaleY = (double) panelHeight / imgHeight;
            double scale = Math.max(scaleX, scaleY);
            
            int scaledWidth = (int) (imgWidth * scale);
            int scaledHeight = (int) (imgHeight * scale);
            
            int x = (panelWidth - scaledWidth) / 2;
            int y = (panelHeight - scaledHeight) / 2;
            
            Shape clip = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
            g2d.setClip(clip);
            g2d.drawImage(backgroundImage, x, y, scaledWidth, scaledHeight, this);
            g2d.setClip(null);
        }
        
        // 角丸の枠を描画
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(3.0f));
        g2d.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, CORNER_RADIUS, CORNER_RADIUS);
        
        // プレイヤー名を描画
        drawPlayerNames(g2d);
        
        g2d.dispose();
    }
    
    /**
     * プレイヤー名をCenterPanel外枠の内側に描画
     * 南→下部（インベントリの上）、北→上部（インベントリの下）
     * 東→右部（インベントリの左）、西→左部（インベントリの右）
     */
    private void drawPlayerNames(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Font nameFont = new Font(Font.SERIF, Font.BOLD, 16);
        g2d.setFont(nameFont);
        FontMetrics fm = g2d.getFontMetrics();
        
        int padding = 6;  // 枠からの距離
        int bgPadding = 4;
        
        // 北のプレイヤー（CenterPanel上部内側、横書き）→ インベントリは上にあるのでその下
        if (!northPlayerName.isEmpty()) {
            int textWidth = fm.stringWidth(northPlayerName);
            int x = (getWidth() - textWidth) / 2;
            int y = padding + fm.getAscent();
            // 半透明背景
            g2d.setColor(new Color(40, 40, 40, 180));
            g2d.fillRoundRect(x - bgPadding, padding - bgPadding, textWidth + bgPadding * 2, fm.getHeight() + bgPadding, 6, 6);
            // テキスト
            g2d.setColor(Color.WHITE);
            g2d.drawString(northPlayerName, x, y);
        }
        
        // 南のプレイヤー（CenterPanel下部内側、横書き）→ インベントリは下にあるのでその上
        if (!southPlayerName.isEmpty()) {
            int textWidth = fm.stringWidth(southPlayerName);
            int x = (getWidth() - textWidth) / 2;
            int y = getHeight() - padding - fm.getDescent();
            // 半透明背景
            g2d.setColor(new Color(40, 40, 40, 180));
            g2d.fillRoundRect(x - bgPadding, y - fm.getAscent() - bgPadding, textWidth + bgPadding * 2, fm.getHeight() + bgPadding, 6, 6);
            // テキスト
            g2d.setColor(Color.WHITE);
            g2d.drawString(southPlayerName, x, y);
        }
        
        // 東のプレイヤー（CenterPanel右部内側、縦書き=右90度回転）→ インベントリは右にあるのでその左
        if (!eastPlayerName.isEmpty()) {
            int textWidth = fm.stringWidth(eastPlayerName);
            int x = getWidth() - padding - fm.getAscent();
            int y = (getHeight() + textWidth) / 2;
            
            AffineTransform original = g2d.getTransform();
            g2d.translate(x, y);
            g2d.rotate(-Math.PI / 2);
            
            // 半透明背景
            g2d.setColor(new Color(40, 40, 40, 180));
            g2d.fillRoundRect(-bgPadding, -fm.getAscent() - bgPadding, textWidth + bgPadding * 2, fm.getHeight() + bgPadding, 6, 6);
            // テキスト
            g2d.setColor(Color.WHITE);
            g2d.drawString(eastPlayerName, 0, 0);
            
            g2d.setTransform(original);
        }
        
        // 西のプレイヤー（CenterPanel左部内側、縦書き=左90度回転）→ インベントリは左にあるのでその右
        if (!westPlayerName.isEmpty()) {
            int textWidth = fm.stringWidth(westPlayerName);
            int x = padding + fm.getAscent();
            int y = (getHeight() - textWidth) / 2;
            
            AffineTransform original = g2d.getTransform();
            g2d.translate(x, y);
            g2d.rotate(Math.PI / 2);
            
            // 半透明背景
            g2d.setColor(new Color(40, 40, 40, 180));
            g2d.fillRoundRect(-bgPadding, -fm.getAscent() - bgPadding, textWidth + bgPadding * 2, fm.getHeight() + bgPadding, 6, 6);
            // テキスト
            g2d.setColor(Color.WHITE);
            g2d.drawString(westPlayerName, 0, 0);
            
            g2d.setTransform(original);
        }
    }
    
    // ============ 内部クラス: TruckCardPanel ============
    
    /**
     * トラックカードを4x4グリッドで表示するパネル
     */
    private class TruckCardPanel extends JPanel {
        private TruckCard truck;
        private AlcoholPanel[][] alcoholSlots = new AlcoholPanel[4][4];
        
        public TruckCardPanel(TruckCard truck) {
            this.truck = truck;
            this.setOpaque(false);
            // グリッドのギャップを0にして背景画像のセルと合わせる
            this.setLayout(new GridLayout(4, 4, 0, 0));
            // パディングを調整してトラックカード画像のグリッドに合わせる（上、左、下、右）
            this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            
            // 4x4のスロットを作成
            int slotIndex = 0;
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 4; col++) {
                    alcoholSlots[row][col] = new AlcoholPanel();
                    this.add(alcoholSlots[row][col]);
                }
            }
            
            // トラックカードの中身を配置
            if (truck != null) {
                int index = 0;
                for (AlcoholType type : AlcoholType.values()) {
                    int count = truck.getCount(type);
                    for (int i = 0; i < count && index < 16; i++) {
                        int row = index / 4;
                        int col = index % 4;
                        alcoholSlots[row][col].setAlcohol(type, 1);
                        index++;
                    }
                }
            }
            
            // サイズ設定（背景画像のグリッドに合わせて調整）
            Dimension size = new Dimension(232, 232);
            this.setPreferredSize(size);
            this.setMinimumSize(size);
            this.setMaximumSize(size);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            // トラックカード背景を描画
            if (truckCardImage != null) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(truckCardImage, 0, 0, getWidth(), getHeight(), this);
                g2d.dispose();
            }
            super.paintComponent(g);
        }
    }
    
    // ============ 内部クラス: BidPanel ============
    
    /**
     * 入札UIを表示するパネル
     */
    private class BidPanel extends JPanel {
        /**
         * @param player 入札するプレイヤー（nullの場合は「あなた」と表示）
         * @param isMyTurn 自分の番かどうか（falseなら待機中表示）
         * @param onSubmit 入札コールバック
         */
        public BidPanel(Player player, boolean isMyTurn, Consumer<Integer> onSubmit) {
            this.setOpaque(false);
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
            
            if (!isMyTurn) {
                // 他のプレイヤーの番：待機中表示（マルチプレイでは使用されない）
                JLabel waitingLabel = new JLabel("入札中...");
                waitingLabel.setFont(new Font(Font.SERIF, Font.ITALIC, 18));
                waitingLabel.setForeground(new Color(200, 200, 200));
                waitingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                this.add(waitingLabel);
                return;
            }
            
            // 所持金表示
            int money = (player != null) ? player.getMoney() : 0;
            JLabel moneyLabel = new JLabel("所持金: " + money + "円");
            moneyLabel.setFont(new Font(Font.SERIF, Font.BOLD, 18));
            moneyLabel.setForeground(Color.WHITE);
            moneyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.add(moneyLabel);
            this.add(Box.createVerticalStrut(15));
            
            // 入札額入力パネル
            JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            inputPanel.setOpaque(false);
            
            JLabel bidLabel = new JLabel("入札額: ");
            bidLabel.setForeground(Color.WHITE);
            bidLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 16));
            inputPanel.add(bidLabel);
            
            int maxBidMoney = (player != null) ? player.getMoney() : 0;
            SpinnerNumberModel model = new SpinnerNumberModel(0, 0, maxBidMoney, 1);
            JSpinner bidSpinner = new JSpinner(model);
            bidSpinner.setPreferredSize(new Dimension(100, 30));
            bidSpinner.setFont(new Font(Font.SERIF, Font.PLAIN, 16));
            inputPanel.add(bidSpinner);
            
            JLabel yenLabel = new JLabel("円");
            yenLabel.setForeground(Color.WHITE);
            yenLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 16));
            inputPanel.add(yenLabel);
            
            this.add(inputPanel);
            this.add(Box.createVerticalStrut(15));
            
            // ボタンパネル
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setOpaque(false);
            
            JButton submitButton = new JButton("入札する");
            submitButton.setFont(new Font(Font.SERIF, Font.BOLD, 16));
            submitButton.setBackground(new Color(139, 69, 19));
            submitButton.setForeground(Color.WHITE);
            submitButton.setFocusPainted(false);
            submitButton.setPreferredSize(new Dimension(120, 40));
            submitButton.addActionListener(e -> {
                int bid = (Integer) bidSpinner.getValue();
                onSubmit.accept(bid);
            });
            buttonPanel.add(submitButton);
            
            JButton passButton = new JButton("パス");
            passButton.setFont(new Font(Font.SERIF, Font.BOLD, 16));
            passButton.setBackground(new Color(100, 100, 100));
            passButton.setForeground(Color.WHITE);
            passButton.setFocusPainted(false);
            passButton.setPreferredSize(new Dimension(100, 40));
            passButton.addActionListener(e -> {
                onSubmit.accept(0);
            });
            buttonPanel.add(passButton);
            
            this.add(buttonPanel);
            
            // サイズ設定
            Dimension size = new Dimension(320, 200);
            this.setPreferredSize(size);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // 薄い灰色の半透明背景
            g2d.setColor(new Color(50, 50, 50, 180));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2d.dispose();
            super.paintComponent(g);
        }
    }
    
    // ============ ペナルティ関連 ============
    
    private Consumer<Boolean> coinTossCallback;
    
    /**
     * ペナルティフェーズの表示を開始
     */
    public void showPenaltyPhase(String title, String description) {
        setPhase(PhaseType.PENALTY);
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // 警告アイコン的な表示
        JPanel warningPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(180, 50, 50, 200));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.setColor(new Color(255, 100, 100));
                g2d.setStroke(new BasicStroke(3f));
                g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 15, 15);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        warningPanel.setOpaque(false);
        warningPanel.setLayout(new BoxLayout(warningPanel, BoxLayout.Y_AXIS));
        warningPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(Font.SERIF, Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        warningPanel.add(titleLabel);
        
        warningPanel.add(Box.createVerticalStrut(10));
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 16));
        descLabel.setForeground(new Color(255, 220, 220));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        warningPanel.add(descLabel);
        
        contentPanel.add(warningPanel, gbc);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * ペナルティメッセージを表示
     */
    public void showPenaltyMessage(String title, String message) {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        JPanel messagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(60, 60, 60, 220));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        messagePanel.setOpaque(false);
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(Font.SERIF, Font.BOLD, 22));
        titleLabel.setForeground(new Color(255, 200, 100));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messagePanel.add(titleLabel);
        
        messagePanel.add(Box.createVerticalStrut(10));
        
        // 複数行対応
        String[] lines = message.split("\n");
        for (String line : lines) {
            JLabel lineLabel = new JLabel(line);
            lineLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            lineLabel.setForeground(Color.WHITE);
            lineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            messagePanel.add(lineLabel);
        }
        
        contentPanel.add(messagePanel, gbc);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    // コイントス用画像
    private Image coinTossGif;
    private Image coinMoneyImage;
    private Image coinAlcoholImage;
    private JLabel coinAnimLabel; // GIFアニメーション用
    private boolean pendingCoinResult;
    
    /**
     * コイントス用の画像を読み込む
     */
    private void loadCoinImages() {
        if (coinMoneyImage == null) {
            try {
                java.net.URL url = getClass().getResource("/images/ui/gameplay/coin_money.png");
                if (url != null) {
                    coinMoneyImage = javax.imageio.ImageIO.read(url);
                }
            } catch (Exception e) {
                System.err.println("coin_money.png の読み込みに失敗: " + e.getMessage());
            }
        }
        if (coinAlcoholImage == null) {
            try {
                java.net.URL url = getClass().getResource("/images/ui/gameplay/coin_alcohol.png");
                if (url != null) {
                    coinAlcoholImage = javax.imageio.ImageIO.read(url);
                }
            } catch (Exception e) {
                System.err.println("coin_alcohol.png の読み込みに失敗: " + e.getMessage());
            }
        }
    }
    
    /**
     * コイントスUIを表示
     */
    public void showCoinToss(Player culprit, boolean canToss, Consumer<Boolean> onResult) {
        this.coinTossCallback = onResult;
        loadCoinImages();
        
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // タイトル
        JPanel titlePanel = createLabelWithBackground(culprit.getName() + " がカポネコインを投げる",
            new Font(Font.SERIF, Font.BOLD, 20), Color.WHITE);
        contentPanel.add(titlePanel, gbc);
        
        // コイン表示パネル（初期状態：お金の面を表示）
        gbc.gridy = 1;
        JPanel coinDisplayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (coinMoneyImage != null) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    int size = Math.min(getWidth(), getHeight());
                    int x = (getWidth() - size) / 2;
                    int y = (getHeight() - size) / 2;
                    g2d.drawImage(coinMoneyImage, x, y, size, size, this);
                    g2d.dispose();
                }
            }
        };
        coinDisplayPanel.setOpaque(false);
        coinDisplayPanel.setPreferredSize(new Dimension(150, 150));
        contentPanel.add(coinDisplayPanel, gbc);
        
        // ボタン（戦犯のプレイヤーのみ有効）
        gbc.gridy = 2;
        JButton tossButton = new JButton("コインを投げる");
        tossButton.setFont(new Font(Font.SERIF, Font.BOLD, 18));
        tossButton.setPreferredSize(new Dimension(180, 45));
        tossButton.setBackground(new Color(180, 140, 60));
        tossButton.setForeground(Color.WHITE);
        tossButton.setFocusPainted(false);
        tossButton.setEnabled(canToss);
        
        if (!canToss) {
            tossButton.setText("待機中...");
            tossButton.setBackground(new Color(100, 100, 100));
        }
        
        tossButton.addActionListener(e -> {
            tossButton.setEnabled(false);
            tossButton.setText("回転中...");
            // ランダムで結果を決定
            boolean result = Math.random() > 0.5;
            executeCoinToss(result);
        });
        
        contentPanel.add(tossButton, gbc);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * コイントスを実行（GIFアニメーション後に結果を表示）
     */
    public void executeCoinToss(boolean isAlcoholSide) {
        this.pendingCoinResult = isAlcoholSide;
        
        // GIFアニメーションを表示
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // タイトル
        JPanel titlePanel = createLabelWithBackground("コイントス中...",
            new Font(Font.SERIF, Font.BOLD, 20), new Color(255, 215, 0));
        contentPanel.add(titlePanel, gbc);
        
        // GIFアニメーション（スケーリングして表示）
        gbc.gridy = 1;
        java.net.URL gifUrl = getClass().getResource("/images/ui/gameplay/cointoss.gif");
        int coinDisplaySize = 180;  // coin画像と同じサイズに統一
        if (gifUrl != null) {
            ImageIcon gifIcon = new ImageIcon(gifUrl);
            // GIFをスケーリング
            Image scaledGif = gifIcon.getImage().getScaledInstance(coinDisplaySize, coinDisplaySize, Image.SCALE_DEFAULT);
            ImageIcon scaledGifIcon = new ImageIcon(scaledGif);
            
            coinAnimLabel = new JLabel(scaledGifIcon);
            coinAnimLabel.setHorizontalAlignment(JLabel.CENTER);
            coinAnimLabel.setVerticalAlignment(JLabel.CENTER);
            coinAnimLabel.setPreferredSize(new Dimension(coinDisplaySize, coinDisplaySize));
            // GIFアニメーションを開始
            scaledGifIcon.setImageObserver(coinAnimLabel);
            
            contentPanel.add(coinAnimLabel, gbc);
        } else {
            // GIFが見つからない場合は仮表示
            JLabel waitLabel = new JLabel("回転中...");
            waitLabel.setFont(new Font(Font.SERIF, Font.BOLD, 24));
            waitLabel.setForeground(Color.WHITE);
            contentPanel.add(waitLabel, gbc);
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
        
        // 3秒後に結果画像を表示
        Timer animTimer = new Timer(3000, e -> {
            showCoinResultImage(pendingCoinResult);
        });
        animTimer.setRepeats(false);
        animTimer.start();
    }
    
    /**
     * コイントス結果の画像を表示（1秒後に結果テキストへ遷移）
     */
    private void showCoinResultImage(boolean isAlcoholSide) {
        loadCoinImages();
        
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // 結果画像を表示
        Image resultImage = isAlcoholSide ? coinAlcoholImage : coinMoneyImage;
        int coinSize = 180;  // GIFと同じサイズに統一
        
        // 画像が読み込めている場合は画像表示、そうでなければフォールバック
        if (resultImage != null) {
            JPanel coinImagePanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    int x = (getWidth() - coinSize) / 2;
                    int y = (getHeight() - coinSize) / 2;
                    g2d.drawImage(resultImage, x, y, coinSize, coinSize, this);
                    g2d.dispose();
                }
            };
            coinImagePanel.setOpaque(false);
            coinImagePanel.setPreferredSize(new Dimension(coinSize, coinSize));
            contentPanel.add(coinImagePanel, gbc);
        } else {
            // フォールバック：円を描画
            Color coinColor = isAlcoholSide ? new Color(180, 100, 50) : new Color(50, 180, 50);
            JPanel fallbackPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int x = (getWidth() - coinSize) / 2;
                    int y = (getHeight() - coinSize) / 2;
                    g2d.setColor(coinColor);
                    g2d.fillOval(x, y, coinSize, coinSize);
                    g2d.setColor(Color.DARK_GRAY);
                    g2d.setStroke(new BasicStroke(4f));
                    g2d.drawOval(x, y, coinSize, coinSize);
                    // ラベル
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font(Font.SERIF, Font.BOLD, 24));
                    String label = isAlcoholSide ? "酒" : "金";
                    FontMetrics fm = g2d.getFontMetrics();
                    int textX = x + (coinSize - fm.stringWidth(label)) / 2;
                    int textY = y + (coinSize + fm.getAscent()) / 2 - 5;
                    g2d.drawString(label, textX, textY);
                    g2d.dispose();
                }
            };
            fallbackPanel.setOpaque(false);
            fallbackPanel.setPreferredSize(new Dimension(coinSize, coinSize));
            contentPanel.add(fallbackPanel, gbc);
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
        
        // 1秒後に結果テキストを表示してコールバック
        Timer resultTimer = new Timer(1000, e -> {
            showCoinResult(isAlcoholSide);
            if (coinTossCallback != null) {
                coinTossCallback.accept(isAlcoholSide);
            }
        });
        resultTimer.setRepeats(false);
        resultTimer.start();
    }
    
    /**
     * コイントスの結果を表示
     */
    private void showCoinResult(boolean isAlcoholSide) {
        loadCoinImages();
        
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        String resultText = isAlcoholSide ? "お酒の面！" : "お金の面！";
        Color resultColor = isAlcoholSide ? new Color(200, 100, 50) : new Color(50, 200, 100);
        Image resultImage = isAlcoholSide ? coinAlcoholImage : coinMoneyImage;
        
        JPanel resultPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(50, 50, 50, 220));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.setColor(resultColor);
                g2d.setStroke(new BasicStroke(4f));
                g2d.drawRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        resultPanel.setOpaque(false);
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        // コイン結果画像（画像がなければフォールバック）
        JPanel coinIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = Math.min(getWidth(), getHeight());
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                if (resultImage != null) {
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.drawImage(resultImage, x, y, size, size, this);
                } else {
                    // フォールバック：円を描画
                    g2d.setColor(resultColor);
                    g2d.fillOval(x, y, size, size);
                    g2d.setColor(Color.DARK_GRAY);
                    g2d.setStroke(new BasicStroke(3f));
                    g2d.drawOval(x, y, size, size);
                    // ラベル
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font(Font.SERIF, Font.BOLD, 20));
                    String label = isAlcoholSide ? "酒" : "金";
                    FontMetrics fm = g2d.getFontMetrics();
                    int textX = x + (size - fm.stringWidth(label)) / 2;
                    int textY = y + (size + fm.getAscent()) / 2 - 5;
                    g2d.drawString(label, textX, textY);
                }
                g2d.dispose();
            }
        };
        coinIcon.setOpaque(false);
        coinIcon.setPreferredSize(new Dimension(120, 120));
        coinIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultPanel.add(coinIcon);
        
        resultPanel.add(Box.createVerticalStrut(15));
        
        JLabel resultLabel = new JLabel(resultText);
        resultLabel.setFont(new Font(Font.SERIF, Font.BOLD, 28));
        resultLabel.setForeground(resultColor);
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultPanel.add(resultLabel);
        
        contentPanel.add(resultPanel, gbc);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
