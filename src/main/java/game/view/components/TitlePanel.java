package game.view.components;

import java.awt.*;
import javax.swing.*;

/**
 * タイトル画面用のパネル
 */
public class TitlePanel extends JPanel {
    private Image backgroundImage;
    private Image titleImage;
    private Image startImage;
    private JTextField playerNameField;
    private JButton joinButton;
    private JButton startButton;
    private JPanel playerListPanel;
    private java.util.List<String> players = new java.util.ArrayList<>();
    
    public TitlePanel() {
        this.setLayout(null); // 絶対位置指定レイアウト
        this.setOpaque(false);
        
        // 背景画像を読み込む
        java.net.URL backgroundUrl = getClass().getResource("/images/ui/title/background.png");
        if (backgroundUrl != null) {
            ImageIcon icon = new ImageIcon(backgroundUrl);
            backgroundImage = icon.getImage();
        } else {
            backgroundImage = null;
        }
        
        // タイトル画像を読み込む
        java.net.URL titleUrl = getClass().getResource("/images/ui/title/title.png");
        if (titleUrl != null) {
            ImageIcon icon = new ImageIcon(titleUrl);
            titleImage = icon.getImage();
        } else {
            titleImage = null;
        }

        java.net.URL startUrl = getClass().getResource("/images/ui/gameplay/log.png");
        if (startUrl != null) {
            ImageIcon icon = new ImageIcon(startUrl);
            startImage = icon.getImage();
        } else {
            startImage = null;
        }
        
        // スタートパネル（log.pngの上に配置）
        JPanel startPanel = createStartPanel();
        this.add(startPanel);
    }
    
    /**
     * スタートパネルを作成（プレイヤー名入力欄とスタートボタンを含む）
     */
    private JPanel createStartPanel() {
        // 完全に透明なパネル（背景はpaintComponentで描画される）
        JPanel startPanel = new JPanel();
        startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.Y_AXIS));
        startPanel.setOpaque(false);
        startPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        
        // プレイヤー名入力欄
        JLabel nameLabel = new JLabel("プレイヤー名:");
        nameLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
        nameLabel.setForeground(new Color(80, 60, 40)); // ダークブラウン
        nameLabel.setOpaque(false);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        startPanel.add(nameLabel);
        startPanel.add(Box.createVerticalStrut(10));
        
        playerNameField = new JTextField(20);
        playerNameField.setFont(new Font(Font.SERIF, Font.PLAIN, 16));
        playerNameField.setMaximumSize(new Dimension(400, 35));
        playerNameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(120, 100, 80), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        playerNameField.setBackground(new Color(255, 250, 240));
        playerNameField.setForeground(new Color(60, 40, 20));
        startPanel.add(playerNameField);
        startPanel.add(Box.createVerticalStrut(15));
        
        // 参加ボタン
        joinButton = new JButton("参加");
        joinButton.setFont(new Font(Font.SERIF, Font.BOLD, 18));
        joinButton.setPreferredSize(new Dimension(200, 40));
        joinButton.setMaximumSize(new Dimension(200, 40));
        joinButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        joinButton.setBackground(new Color(139, 69, 19)); // サドルブラウン
        joinButton.setForeground(Color.WHITE);
        joinButton.setBorder(BorderFactory.createRaisedBevelBorder());
        joinButton.setFocusPainted(false);
        
        // 参加ボタンのホバー効果
        joinButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                joinButton.setBackground(new Color(160, 82, 45));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                joinButton.setBackground(new Color(139, 69, 19));
            }
        });
        
        // 参加ボタンのクリックイベント
        joinButton.addActionListener(e -> {
            String playerName = playerNameField.getText().trim();
            if (playerName.isEmpty()) {
                playerName = "プレイヤー" + (players.size() + 1);
            }
            if (players.size() < 4 && !players.contains(playerName)) {
                players.add(playerName);
                updatePlayerList();
                playerNameField.setText(""); // 入力欄をクリア
            }
        });
        
        startPanel.add(joinButton);
        startPanel.add(Box.createVerticalStrut(15));
        
        // プレイヤーリストパネル（常に同じ高さを確保してゲーム開始ボタンの位置を固定）
        playerListPanel = new JPanel();
        playerListPanel.setLayout(new BoxLayout(playerListPanel, BoxLayout.Y_AXIS));
        playerListPanel.setOpaque(false);
        playerListPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // プレイヤーリストの高さを固定（非表示でもスペースを確保）
        playerListPanel.setPreferredSize(new Dimension(400, 200));
        playerListPanel.setMinimumSize(new Dimension(400, 200));
        playerListPanel.setMaximumSize(new Dimension(400, 200));
        startPanel.add(playerListPanel);
        startPanel.add(Box.createVerticalStrut(15));
        
        // ゲームスタートボタン
        startButton = new JButton("ゲームを開始");
        startButton.setEnabled(false); // 最初は無効
        startButton.setFont(new Font(Font.SERIF, Font.BOLD, 20));
        startButton.setPreferredSize(new Dimension(250, 50));
        startButton.setMaximumSize(new Dimension(250, 50));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setBackground(new Color(139, 69, 19)); // サドルブラウン
        startButton.setForeground(Color.WHITE);
        startButton.setBorder(BorderFactory.createRaisedBevelBorder());
        startButton.setFocusPainted(false);
        
        // ボタンのホバー効果
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                startButton.setBackground(new Color(160, 82, 45)); // より明るいブラウン
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                startButton.setBackground(new Color(139, 69, 19));
            }
        });
        
        // スタートボタンのクリックイベント
        startButton.addActionListener(e -> {
            if (players.size() == 4) {
                startGame();
            }
        });
        
        startPanel.add(startButton);
        
        // パネルの位置とサイズを設定（startImageの位置に合わせる）
        startPanel.setBounds(0, 0, 800, 600); // 初期サイズ、後で調整される
        
        return startPanel;
    }
    
    @Override
    public void doLayout() {
        super.doLayout();
        // startPanelの位置をstartImageの位置に合わせて調整
        if (startImage != null && playerNameField != null) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            
            // startImageの位置（上から40%、中央）
            int startImageY = (int) (panelHeight * 0.4);
            int maxStartWidth = (int) (panelWidth * 0.4);
            double startScale = (double) maxStartWidth / startImage.getWidth(this);
            int scaledStartHeight = (int) (startImage.getHeight(this) * startScale);
            
            // startPanelをstartImageの上に配置
            Component[] components = getComponents();
            for (Component comp : components) {
                if (comp instanceof JPanel) {
                    JPanel panel = (JPanel) comp;
                    // startImageの中央に合わせて配置
                    int panelX = (panelWidth - 520) / 2; // パネルの幅（border含む）を考慮
                    int panelY = startImageY + 50; // startImageの上に配置
                    // プレイヤーリストが表示される場合、パネルの高さを調整
                    int startPanelHeight = playerListPanel.isVisible() ? 350 : 200;
                    panel.setBounds(panelX, panelY, 520, startPanelHeight);
                    break;
                }
            }
        }
    }
    
    /**
     * プレイヤーリストを更新する
     */
    private void updatePlayerList() {
        playerListPanel.removeAll();
        
        // 常にプレイヤーリストを表示（空の時もスペースを確保）
        JLabel listTitle = new JLabel("参加プレイヤー:");
        listTitle.setFont(new Font(Font.SERIF, Font.BOLD, 20));
        listTitle.setForeground(new Color(80, 60, 40));
        listTitle.setOpaque(false);
        listTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerListPanel.add(listTitle);
        playerListPanel.add(Box.createVerticalStrut(15));
        
        for (int i = 0; i < 4; i++) {
            JLabel playerLabel;
            if (i < players.size()) {
                playerLabel = new JLabel((i + 1) + ". " + players.get(i));
            } else {
                playerLabel = new JLabel((i + 1) + ". (待機中...)");
            }
            playerLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
            playerLabel.setForeground(new Color(80, 60, 40));
            playerLabel.setOpaque(false);
            playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            playerListPanel.add(playerLabel);
            playerListPanel.add(Box.createVerticalStrut(5));
        }
        
        // 4人揃ったらゲーム開始ボタンを有効化
        startButton.setEnabled(players.size() == 4);
        
        playerListPanel.revalidate();
        playerListPanel.repaint();
        revalidate();
        repaint();
    }
    
    /**
     * ゲームを開始する
     */
    private void startGame() {
        System.out.println("ゲームを開始: プレイヤー = " + players);
        
        // MainFrameを取得
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (!(frame instanceof game.view.MainFrame)) {
            return;
        }
        
        game.view.MainFrame mainFrame = (game.view.MainFrame) frame;
        
        // プレイヤーリストからPlayerオブジェクトを作成
        java.util.List<game.model.Player> playerList = new java.util.ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            playerList.add(new game.model.Player(i, game.util.Constants.INITIAL_MONEY));
        }
        
        // GameStateとGameManagerを作成
        game.model.GameState gameState = new game.model.GameState(playerList);
        game.controller.GameManager gameManager = new game.controller.GameManager(gameState, mainFrame);
        
        // MainFrameにGameManagerを設定
        mainFrame.setGameManager(gameManager);
        
        // 画面を切り替え
        mainFrame.switchToGameBoard();
        
        // ゲームを開始
        gameManager.startGame();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 背景を黒で塗りつぶし
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // 背景画像を描画（縦横比を保ったまま、画面全体を覆う）
        if (backgroundImage != null) {
            int imgWidth = backgroundImage.getWidth(this);
            int imgHeight = backgroundImage.getHeight(this);
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            
            // 縦横比を保ったままスケールを計算（画面全体を覆うように）
            double scaleX = (double) panelWidth / imgWidth;
            double scaleY = (double) panelHeight / imgHeight;
            double scale = Math.max(scaleX, scaleY); // 大きい方を使用して画面全体を覆う
            
            int scaledWidth = (int) (imgWidth * scale);
            int scaledHeight = (int) (imgHeight * scale);
            
            // 中央に配置
            int x = (panelWidth - scaledWidth) / 2;
            int y = (panelHeight - scaledHeight) / 2;
            
            g2d.drawImage(backgroundImage, x, y, scaledWidth, scaledHeight, this);
        }
        
        // タイトル画像を描画（中央上部に配置）
        if (titleImage != null) {
            int titleWidth = titleImage.getWidth(this);
            int titleHeight = titleImage.getHeight(this);
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            
            // タイトル画像のサイズを調整（画面幅の30%程度、縦横比を保つ）
            int maxTitleWidth = (int) (panelWidth * 0.3);
            double titleScale = (double) maxTitleWidth / titleWidth;
            int scaledTitleWidth = (int) (titleWidth * titleScale);
            int scaledTitleHeight = (int) (titleHeight * titleScale);
            
            // 中央上部に配置（上から20%の位置）
            int titleX = (panelWidth - scaledTitleWidth) / 2;
            int titleY = (int) (panelHeight * 0.2);
            
            g2d.drawImage(titleImage, titleX, titleY, scaledTitleWidth, scaledTitleHeight, this);
        }
        
        // スタート画像を描画（中央に配置）
        if (startImage != null) {
            int startWidth = startImage.getWidth(this);
            int startHeight = startImage.getHeight(this);
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            
            // スタート画像のサイズを調整（画面幅の40%程度、縦横比を保つ）
            int maxStartWidth = (int) (panelWidth * 0.4);
            double startScale = (double) maxStartWidth / startWidth;
            int scaledStartWidth = (int) (startWidth * startScale);
            int scaledStartHeight = (int) (startHeight * startScale);
            
            // 中央下部に配置（上から40%の位置）
            int startX = (panelWidth - scaledStartWidth) / 2;
            int startY = (int) (panelHeight * 0.4);
            
            g2d.drawImage(startImage, startX, startY, scaledStartWidth, scaledStartHeight, this);
        }
        
        g2d.dispose();
    }
}
