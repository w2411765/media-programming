package game.view.components;

import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Map;
import java.util.function.Consumer;
import game.model.Player;
import game.model.alcohol.AlcoholType;
import game.model.alcohol.TruckCard;

public class CenterPanel extends JPanel {
    private Image backgroundImage;
    private Image truckCardImage;
    private static final int CORNER_RADIUS = 10;
    
    // フェーズの種類
    public enum PhaseType {
        AUCTION,    // bootlegging.png
        TRADE,      // trade.png
        PENALTY,    // penalty.png (speakeasy.png使用)
        DEFAULT     // bootlegging.png
    }
    
    private PhaseType currentPhase = PhaseType.DEFAULT;
    
    // オークション用のコンテンツパネル
    private JPanel contentPanel;
    private TruckCardPanel truckCardPanel;
    private BidPanel bidPanel;
    
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
                imagePath = "/images/ui/gameplay/speakeasy.png";
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
    
    /**
     * オークション表示を開始（フェーズ紹介を表示後、トラックカードを表示）
     */
    public void showAuction(TruckCard truck, Player currentPlayer, Consumer<Integer> onBidSubmit) {
        this.bidCallback = onBidSubmit;
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
        
        // 入札パネル
        bidPanel = new BidPanel(currentPlayer, bid -> {
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
     */
    public void showBidForPlayer(Player player, Consumer<Integer> onBidSubmit) {
        this.bidCallback = onBidSubmit;
        
        if (bidPanel != null) {
            contentPanel.remove(bidPanel);
        }
        
        bidPanel = new BidPanel(player, bid -> {
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
     * オークション結果を表示
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
        public BidPanel(Player player, Consumer<Integer> onSubmit) {
            this.setOpaque(false);
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            
            // プレイヤー名
            JLabel playerLabel = new JLabel(player.getName() + " の入札");
            playerLabel.setFont(new Font(Font.SERIF, Font.BOLD, 20));
            playerLabel.setForeground(Color.WHITE);
            playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.add(playerLabel);
            this.add(Box.createVerticalStrut(10));
            
            // 所持金表示
            JLabel moneyLabel = new JLabel("所持金: " + player.getMoney() + "円");
            moneyLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 16));
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
            
            SpinnerNumberModel model = new SpinnerNumberModel(0, 0, player.getMoney(), 1);
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
            Dimension size = new Dimension(300, 180);
            this.setPreferredSize(size);
        }
    }
}
