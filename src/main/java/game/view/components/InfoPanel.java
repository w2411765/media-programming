package game.view.components;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.RoundRectangle2D;

public class InfoPanel extends JPanel {
    private Image backgroundImage;
    private Image billsImage;
    private static final int CORNER_RADIUS = 10; // 角の丸み
    private JLabel roundLabel;
    private JLabel moneyLabel;
    private int currentRound = 1;
    private int maxRounds = 3;
    private int playerMoney = 0;
    
    public InfoPanel() {
        this.setLayout(new BorderLayout());
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(0, 100)); // 高さを100に設定（半分）
        
        java.net.URL imageUrl = getClass().getResource("/images/ui/gameplay/info.png");
        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            backgroundImage = icon.getImage();
        } else {
            backgroundImage = null;
        }
        
        // bills.pngを読み込み
        java.net.URL billsUrl = getClass().getResource("/images/ui/gameplay/bills.png");
        if (billsUrl != null) {
            ImageIcon icon = new ImageIcon(billsUrl);
            billsImage = icon.getImage();
        } else {
            billsImage = null;
        }
        
        // コンテンツパネル
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        
        // 左側：ラウンド情報
        roundLabel = new JLabel("Round 1/3");
        roundLabel.setFont(new Font(Font.SERIF, Font.BOLD, 36));
        roundLabel.setForeground(Color.BLACK);
        
        JPanel roundPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        roundPanel.setOpaque(false);
        roundPanel.add(roundLabel);
        contentPanel.add(roundPanel, BorderLayout.WEST);
        
        // 右側：所持金（お金アイコン付き）
        JPanel moneyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        moneyPanel.setOpaque(false);
        
        // お金アイコン表示用パネル
        JPanel billsIconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (billsImage != null) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.drawImage(billsImage, 0, 0, getWidth(), getHeight(), this);
                    g2d.dispose();
                }
            }
        };
        billsIconPanel.setOpaque(false);
        billsIconPanel.setPreferredSize(new Dimension(70, 70));
        moneyPanel.add(billsIconPanel);
        
        moneyLabel = new JLabel("0円");
        moneyLabel.setFont(new Font(Font.SERIF, Font.BOLD, 36));
        moneyLabel.setForeground(Color.BLACK);
        moneyPanel.add(moneyLabel);
        
        contentPanel.add(moneyPanel, BorderLayout.EAST);
        
        this.add(contentPanel, BorderLayout.CENTER);
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
            
            Shape clip = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
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
    
    /**
     * ラウンド情報を更新する
     */
    public void updateRoundInfo(int round) {
        this.currentRound = round;
        if (roundLabel != null) {
            roundLabel.setText("Round " + currentRound + "/" + maxRounds);
        }
    }
    
    /**
     * 最大ラウンド数を設定する
     */
    public void setMaxRounds(int maxRounds) {
        this.maxRounds = maxRounds;
        updateRoundInfo(currentRound);
    }
    
    /**
     * 所持金を更新する（南プレイヤー=自分の所持金）
     */
    public void updateMoney(int money) {
        this.playerMoney = money;
        if (moneyLabel != null) {
            moneyLabel.setText(money + "円");
        }
    }
}
