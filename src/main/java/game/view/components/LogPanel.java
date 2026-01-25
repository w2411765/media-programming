package game.view.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LogPanel extends JPanel {
    private Image backgroundImage;
    private static final int CORNER_RADIUS = 10; // 角の丸み
    private JTextArea logTextArea;
    private JScrollPane logScrollPane;
    
    // Chat関連
    private JTextArea chatTextArea;
    private JScrollPane chatScrollPane;
    private JTextField chatInputField;
    private JButton sendButton;
    private String playerName = "Player"; // 南プレイヤーの名前
    
    // チャット送信コールバック（マルチプレイヤー同期用）
    private java.util.function.BiConsumer<String, String> onChatSend;

    public LogPanel() {
        this.setOpaque(false);
        this.setLayout(new GridLayout(2, 1, 0, 0)); // 上下に2分割（間隔0）
        this.setPreferredSize(new Dimension(520, 0));
        
        // === 上半分: Log ===
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setOpaque(false);
        // 下に黒い境界線
        logPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
        
        JLabel logTitleLabel = new JLabel("-Log-", SwingConstants.CENTER);
        logTitleLabel.setFont(new Font(Font.SERIF, Font.BOLD, 20));
        logTitleLabel.setForeground(Color.BLACK);
        logTitleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        logPanel.add(logTitleLabel, BorderLayout.NORTH);
        
        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        logTextArea.setOpaque(false);
        logTextArea.setForeground(Color.BLACK);
        logTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        logTextArea.setLineWrap(true);
        logTextArea.setWrapStyleWord(true);
        
        logScrollPane = new JScrollPane(logTextArea);
        logScrollPane.setOpaque(false);
        logScrollPane.getViewport().setOpaque(false);
        logScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        setupMinimalScrollBar(logScrollPane);
        logPanel.add(logScrollPane, BorderLayout.CENTER);
        
        this.add(logPanel);
        
        // === 下半分: Chat ===
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setOpaque(false);
        
        JLabel chatTitleLabel = new JLabel("-Chat-", SwingConstants.CENTER);
        chatTitleLabel.setFont(new Font(Font.SERIF, Font.BOLD, 20));
        chatTitleLabel.setForeground(Color.BLACK);
        chatTitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        chatPanel.add(chatTitleLabel, BorderLayout.NORTH);
        
        chatTextArea = new JTextArea();
        chatTextArea.setEditable(false);
        chatTextArea.setOpaque(false);
        chatTextArea.setForeground(Color.BLACK);
        chatTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        chatTextArea.setLineWrap(true);
        chatTextArea.setWrapStyleWord(true);
        
        chatScrollPane = new JScrollPane(chatTextArea);
        chatScrollPane.setOpaque(false);
        chatScrollPane.getViewport().setOpaque(false);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        setupMinimalScrollBar(chatScrollPane);
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);
        
        // 入力エリア（テキストフィールド + 送信ボタン）
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        chatInputField = new JTextField();
        chatInputField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        chatInputField.addActionListener(e -> sendChat()); // Enterキーで送信
        inputPanel.add(chatInputField, BorderLayout.CENTER);
        
        sendButton = new JButton("送信");
        sendButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        sendButton.addActionListener(e -> sendChat());
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        chatPanel.add(inputPanel, BorderLayout.SOUTH);
        
        this.add(chatPanel);
        
        // 背景画像読み込み
        java.net.URL imageUrl = getClass().getResource("/images/ui/gameplay/log.png");
        if (imageUrl != null) {
            ImageIcon icon = new ImageIcon(imageUrl);
            backgroundImage = icon.getImage();
        } else {
            backgroundImage = null;
        }
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
                this.thumbColor = new Color(100, 100, 100, 150);
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
    
    /**
     * 南プレイヤーの名前を設定する
     */
    public void setPlayerName(String name) {
        this.playerName = name;
    }
    
    /**
     * チャットメッセージを送信する
     */
    private void sendChat() {
        String message = chatInputField.getText().trim();
        if (!message.isEmpty()) {
            if (onChatSend != null) {
                // コールバックを呼び出す（外部で同期処理）
                onChatSend.accept(playerName, message);
            } else {
                // コールバックがない場合はローカルに追加
                addChatMessage(playerName, message);
            }
            chatInputField.setText("");
        }
    }
    
    /**
     * チャット送信コールバックを設定
     */
    public void setOnChatSend(java.util.function.BiConsumer<String, String> callback) {
        this.onChatSend = callback;
    }
    
    /**
     * チャットにメッセージを追加する
     */
    public void addChatMessage(String sender, String message) {
        if (chatTextArea != null) {
            chatTextArea.append("<" + sender + "> " + message + "\n");
            chatTextArea.setCaretPosition(chatTextArea.getDocument().getLength());
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
    
    /**
     * ログメッセージを追加する
     */
    public void addMessage(String message) {
        if (logTextArea != null) {
            logTextArea.append(message + "\n");
            logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
        }
    }
    
    /**
     * ログをクリアする
     */
    public void clearLog() {
        if (logTextArea != null) {
            logTextArea.setText("");
        }
    }
}
