package game.view.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import game.model.Player;
import game.model.alcohol.AlcoholType;
import game.controller.TradeProposal;

/**
 * 取引ダイアログ
 * - 取引相手をプルダウンで選択
 * - [売る]/[買う]ボタン（排他的）
 * - 4種類のお酒と紙幣の数量調整
 * - [送る]ボタン
 */
public class TradeDialog extends JDialog {
    private Player currentPlayer;
    private List<Player> otherPlayers;
    private Consumer<TradeProposal> onProposalSubmit;
    private Runnable onCancel;
    private Runnable onEndTrade; // 取引終了コールバック
    
    // UI部品
    private JComboBox<PlayerItem> playerComboBox;
    private JToggleButton sellButton;
    private JToggleButton buyButton;
    private ButtonGroup tradeModeGroup;
    private Map<AlcoholType, JSpinner> alcoholSpinners = new HashMap<>();
    private JSpinner moneySpinner;
    
    // お酒の色
    private static final Color BEER_COLOR = new Color(255, 200, 50);
    private static final Color RUM_COLOR = new Color(139, 69, 19);
    private static final Color VODKA_COLOR = new Color(200, 200, 255);
    private static final Color GIN_COLOR = new Color(150, 255, 150);
    private static final Color MONEY_COLOR = new Color(85, 170, 85);
    
    /**
     * 取引作成ダイアログ
     */
    public TradeDialog(JFrame parent, Player currentPlayer, List<Player> otherPlayers,
                       Consumer<TradeProposal> onSubmit, Runnable onCancel, Runnable onEndTrade) {
        super(parent, "取引提案", false); // モーダレス
        this.currentPlayer = currentPlayer;
        this.otherPlayers = otherPlayers;
        this.onProposalSubmit = onSubmit;
        this.onCancel = onCancel;
        this.onEndTrade = onEndTrade;
        
        initUI();
        
        setSize(400, 520);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                handleCancel();
            }
        });
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(50, 50, 50));
        
        // === タイトル ===
        JLabel titleLabel = new JLabel("取引を作成", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SERIF, Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // === メインパネル ===
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // --- 取引相手選択 ---
        JPanel targetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        targetPanel.setOpaque(false);
        targetPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JLabel targetLabel = new JLabel("取引相手:");
        targetLabel.setForeground(Color.WHITE);
        targetLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        targetPanel.add(targetLabel);
        
        playerComboBox = new JComboBox<>();
        for (Player p : otherPlayers) {
            if (p.getId() != currentPlayer.getId()) {
                playerComboBox.addItem(new PlayerItem(p));
            }
        }
        playerComboBox.setPreferredSize(new Dimension(180, 28));
        targetPanel.add(playerComboBox);
        
        mainPanel.add(targetPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // --- 売る/買う ボタン ---
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        modePanel.setOpaque(false);
        modePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        sellButton = createToggleButton("売る", new Color(220, 80, 80));
        buyButton = createToggleButton("買う", new Color(80, 150, 220));
        
        tradeModeGroup = new ButtonGroup();
        tradeModeGroup.add(sellButton);
        tradeModeGroup.add(buyButton);
        
        // デフォルトで「売る」を選択
        sellButton.setSelected(true);
        
        modePanel.add(sellButton);
        modePanel.add(buyButton);
        
        mainPanel.add(modePanel);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // --- 取引内容 ---
        JPanel itemsPanel = new JPanel();
        itemsPanel.setOpaque(false);
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "取引内容",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font(Font.SANS_SERIF, Font.BOLD, 12),
            Color.WHITE
        ));
        
        // お酒のアイテム行
        for (AlcoholType type : AlcoholType.values()) {
            JPanel row = createItemRow(type.name(), getAlcoholDisplayName(type), 
                                       getAlcoholColor(type), 0, getMaxForAlcohol(type));
            JSpinner spinner = (JSpinner) ((JPanel) row.getComponent(1)).getComponent(1);
            alcoholSpinners.put(type, spinner);
            itemsPanel.add(row);
            itemsPanel.add(Box.createVerticalStrut(5));
        }
        
        // お金の行
        JPanel moneyRow = createItemRow("MONEY", "お金", MONEY_COLOR, 0, Integer.MAX_VALUE);
        moneySpinner = (JSpinner) ((JPanel) moneyRow.getComponent(1)).getComponent(1);
        itemsPanel.add(moneyRow);
        
        mainPanel.add(itemsPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        
        add(mainPanel, BorderLayout.CENTER);
        
        // === ボタンパネル ===
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));
        
        // 上段: 送るボタンとキャンセルボタン
        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        topButtonPanel.setOpaque(false);
        
        JButton sendButton = new JButton("送る");
        sendButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        sendButton.setPreferredSize(new Dimension(120, 40));
        sendButton.setBackground(new Color(60, 140, 60));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.addActionListener(e -> handleSubmit());
        topButtonPanel.add(sendButton);
        
        JButton cancelButton = new JButton("キャンセル");
        cancelButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.addActionListener(e -> handleCancel());
        topButtonPanel.add(cancelButton);
        
        buttonPanel.add(topButtonPanel);
        
        // 下段: 取引終了ボタン
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        bottomButtonPanel.setOpaque(false);
        
        JButton endTradeButton = new JButton("取引を終了する");
        endTradeButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        endTradeButton.setPreferredSize(new Dimension(200, 35));
        endTradeButton.setBackground(new Color(100, 100, 100));
        endTradeButton.setForeground(Color.WHITE);
        endTradeButton.setFocusPainted(false);
        endTradeButton.addActionListener(e -> handleEndTrade());
        bottomButtonPanel.add(endTradeButton);
        
        buttonPanel.add(bottomButtonPanel);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void handleEndTrade() {
        if (onEndTrade != null) {
            onEndTrade.run();
        }
        dispose();
    }
    
    private JToggleButton createToggleButton(String text, Color selectedColor) {
        JToggleButton button = new JToggleButton(text);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        button.setPreferredSize(new Dimension(100, 40));
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
    
    /**
     * アイテム行（アイコン、[-]数字[+]）を作成
     */
    private JPanel createItemRow(String id, String displayName, Color iconColor, int min, int max) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        // 左側: アイコンと名前
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftPanel.setOpaque(false);
        
        // アイコン（円形）
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(iconColor);
                g2d.fillOval(2, 2, 26, 26);
                g2d.setColor(Color.DARK_GRAY);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawOval(2, 2, 26, 26);
                g2d.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(30, 30));
        leftPanel.add(iconPanel);
        
        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        leftPanel.add(nameLabel);
        
        row.add(leftPanel, BorderLayout.WEST);
        
        // 右側: [-] 数字 [+]
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        controlPanel.setOpaque(false);
        
        JButton minusButton = new JButton("-");
        minusButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        minusButton.setPreferredSize(new Dimension(40, 30));
        minusButton.setFocusPainted(false);
        
        SpinnerNumberModel model = new SpinnerNumberModel(0, min, max, 1);
        JSpinner spinner = new JSpinner(model);
        spinner.setPreferredSize(new Dimension(60, 30));
        spinner.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        // スピナーの編集を無効化（ボタンのみで操作）
        JFormattedTextField tf = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        tf.setEditable(false);
        tf.setHorizontalAlignment(JTextField.CENTER);
        
        JButton plusButton = new JButton("+");
        plusButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        plusButton.setPreferredSize(new Dimension(40, 30));
        plusButton.setFocusPainted(false);
        
        minusButton.addActionListener(e -> {
            int val = (Integer) spinner.getValue();
            if (val > min) spinner.setValue(val - 1);
        });
        
        plusButton.addActionListener(e -> {
            int val = (Integer) spinner.getValue();
            if (val < max) spinner.setValue(val + 1);
        });
        
        controlPanel.add(minusButton);
        controlPanel.add(spinner);
        controlPanel.add(plusButton);
        
        row.add(controlPanel, BorderLayout.EAST);
        
        return row;
    }
    
    private void handleSubmit() {
        PlayerItem selectedItem = (PlayerItem) playerComboBox.getSelectedItem();
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this, "取引相手を選択してください", "エラー", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Player target = selectedItem.player;
        boolean isSelling = sellButton.isSelected();
        
        // 取引内容を収集
        Map<AlcoholType, Integer> alcoholOffered = new HashMap<>();
        Map<AlcoholType, Integer> alcoholRequested = new HashMap<>();
        int moneyOffered = 0;
        int moneyRequested = 0;
        
        // お酒の内容
        for (AlcoholType type : AlcoholType.values()) {
            int amount = (Integer) alcoholSpinners.get(type).getValue();
            if (amount > 0) {
                if (isSelling) {
                    // 売る = 自分が渡す
                    alcoholOffered.put(type, amount);
                } else {
                    // 買う = 相手から受け取る
                    alcoholRequested.put(type, amount);
                }
            }
        }
        
        // お金の内容
        int moneyAmount = (Integer) moneySpinner.getValue();
        if (moneyAmount > 0) {
            if (isSelling) {
                // 売る = 相手からお金を受け取る
                moneyRequested = moneyAmount;
            } else {
                // 買う = 自分がお金を渡す
                moneyOffered = moneyAmount;
            }
        }
        
        // 何も選んでいない場合はエラー
        boolean hasContent = !alcoholOffered.isEmpty() || !alcoholRequested.isEmpty() 
                            || moneyOffered > 0 || moneyRequested > 0;
        if (!hasContent) {
            JOptionPane.showMessageDialog(this, "取引内容を設定してください", "エラー", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // TradeProposal を作成
        TradeProposal proposal = new TradeProposal(
            currentPlayer, target,
            alcoholOffered, alcoholRequested,
            moneyOffered, moneyRequested
        );
        
        if (onProposalSubmit != null) {
            onProposalSubmit.accept(proposal);
        }
        
        dispose();
    }
    
    private void handleCancel() {
        if (onCancel != null) {
            onCancel.run();
        }
        dispose();
    }
    
    private String getAlcoholDisplayName(AlcoholType type) {
        switch (type) {
            case BEER: return "ビール";
            case RUM: return "ラム";
            case VODKA: return "ウォッカ";
            case GIN: return "ジン";
            default: return type.name();
        }
    }
    
    private Color getAlcoholColor(AlcoholType type) {
        switch (type) {
            case BEER: return BEER_COLOR;
            case RUM: return RUM_COLOR;
            case VODKA: return VODKA_COLOR;
            case GIN: return GIN_COLOR;
            default: return Color.GRAY;
        }
    }
    
    private int getMaxForAlcohol(AlcoholType type) {
        // 売る場合は自分の在庫、買う場合は相手の在庫（とりあえず99まで許可）
        return currentPlayer.getInventory().getOrDefault(type, 0) + 10; // 余裕を持たせる
    }
    
    /**
     * プレイヤー選択用コンボボックスのアイテム
     */
    private static class PlayerItem {
        Player player;
        
        PlayerItem(Player p) {
            this.player = p;
        }
        
        @Override
        public String toString() {
            return player.getName();
        }
    }
    
    // ===== 静的メソッド =====
    
    /**
     * 取引ダイアログを表示
     */
    public static void showTradeDialog(JFrame parent, Player currentPlayer, 
                                       List<Player> otherPlayers,
                                       Consumer<TradeProposal> onSubmit,
                                       Runnable onCancel,
                                       Runnable onEndTrade) {
        TradeDialog dialog = new TradeDialog(parent, currentPlayer, otherPlayers, onSubmit, onCancel, onEndTrade);
        dialog.setVisible(true);
    }
    
    /**
     * 取引ダイアログを表示（旧API互換）
     */
    public static void showTradeDialog(JFrame parent, Player currentPlayer, 
                                       List<Player> otherPlayers,
                                       Consumer<TradeProposal> onSubmit,
                                       Runnable onCancel) {
        showTradeDialog(parent, currentPlayer, otherPlayers, onSubmit, onCancel, onCancel);
    }
    
    /**
     * 送信した取引の確認ダイアログ（送り主用）
     * 「破談」ボタンのみ（キャンセルして再度取引を作り直す）
     */
    public static JDialog showSentTradeDialog(JFrame parent, TradeProposal proposal, Consumer<String> onAction) {
        JDialog dialog = new JDialog(parent, "送信中の取引", false);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(new Color(50, 50, 50));
        
        // タイトル
        JLabel titleLabel = new JLabel(proposal.getTo().getName() + " への取引提案", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SERIF, Font.BOLD, 16));
        titleLabel.setForeground(new Color(100, 180, 255));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        dialog.add(titleLabel, BorderLayout.NORTH);
        
        // 取引内容
        JPanel contentPanel = createTradeContentPanel(proposal);
        dialog.add(contentPanel, BorderLayout.CENTER);
        
        // 待機中メッセージ
        JLabel waitingLabel = new JLabel("相手の応答を待っています...", SwingConstants.CENTER);
        waitingLabel.setFont(new Font(Font.SERIF, Font.ITALIC, 12));
        waitingLabel.setForeground(new Color(180, 180, 180));
        
        // ボタン
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        buttonPanel.add(waitingLabel, BorderLayout.NORTH);
        
        JPanel btnContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnContainer.setOpaque(false);
        
        JButton cancelButton = createDialogButton("破談", new Color(180, 60, 60));
        cancelButton.addActionListener(e -> {
            if (onAction != null) onAction.accept("cancel");
            dialog.dispose();
        });
        btnContainer.add(cancelButton);
        
        buttonPanel.add(btnContainer, BorderLayout.SOUTH);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setSize(280, 300);
        dialog.setLocationRelativeTo(parent);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);
        
        return dialog;
    }
    
    /**
     * 受信した取引の確認ダイアログ（送り先用）
     * 「交渉成立」「破談」ボタン
     */
    public static JDialog showReceivedTradeDialog(JFrame parent, TradeProposal proposal, Consumer<Boolean> onResponse) {
        JDialog dialog = new JDialog(parent, "取引提案", false);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(new Color(50, 50, 50));
        
        // タイトル
        JLabel titleLabel = new JLabel(proposal.getFrom().getName() + " からの取引", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SERIF, Font.BOLD, 16));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        dialog.add(titleLabel, BorderLayout.NORTH);
        
        // 取引内容
        JPanel contentPanel = createTradeContentPanel(proposal);
        dialog.add(contentPanel, BorderLayout.CENTER);
        
        // ボタン
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        
        JButton acceptButton = createDialogButton("交渉成立", new Color(60, 140, 60));
        acceptButton.addActionListener(e -> {
            if (onResponse != null) onResponse.accept(true);
            dialog.dispose();
        });
        buttonPanel.add(acceptButton);
        
        JButton rejectButton = createDialogButton("破談", new Color(180, 60, 60));
        rejectButton.addActionListener(e -> {
            if (onResponse != null) onResponse.accept(false);
            dialog.dispose();
        });
        buttonPanel.add(rejectButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setSize(280, 280);
        dialog.setLocationRelativeTo(parent);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);
        
        return dialog;
    }
    
    /**
     * 取引内容パネルを作成
     */
    private static JPanel createTradeContentPanel(TradeProposal proposal) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(40, 40, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // 渡すもの
        if (!proposal.getAlcoholOffered().isEmpty() || proposal.getMoneyOffered() > 0) {
            JLabel offerLabel = new JLabel("【渡すもの】");
            offerLabel.setForeground(new Color(255, 150, 150));
            offerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            offerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(offerLabel);
            
            for (Map.Entry<AlcoholType, Integer> entry : proposal.getAlcoholOffered().entrySet()) {
                JLabel itemLabel = new JLabel("  " + getDisplayName(entry.getKey()) + " x" + entry.getValue());
                itemLabel.setForeground(Color.WHITE);
                itemLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
                itemLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(itemLabel);
            }
            if (proposal.getMoneyOffered() > 0) {
                JLabel moneyLabel = new JLabel("  お金 " + proposal.getMoneyOffered() + "円");
                moneyLabel.setForeground(Color.WHITE);
                moneyLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
                moneyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(moneyLabel);
            }
            panel.add(Box.createVerticalStrut(8));
        }
        
        // 受け取るもの
        if (!proposal.getAlcoholRequested().isEmpty() || proposal.getMoneyRequested() > 0) {
            JLabel requestLabel = new JLabel("【受け取るもの】");
            requestLabel.setForeground(new Color(150, 200, 255));
            requestLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            requestLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(requestLabel);
            
            for (Map.Entry<AlcoholType, Integer> entry : proposal.getAlcoholRequested().entrySet()) {
                JLabel itemLabel = new JLabel("  " + getDisplayName(entry.getKey()) + " x" + entry.getValue());
                itemLabel.setForeground(Color.WHITE);
                itemLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
                itemLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(itemLabel);
            }
            if (proposal.getMoneyRequested() > 0) {
                JLabel moneyLabel = new JLabel("  お金 " + proposal.getMoneyRequested() + "円");
                moneyLabel.setForeground(Color.WHITE);
                moneyLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
                moneyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(moneyLabel);
            }
        }
        
        return panel;
    }
    
    private static String getDisplayName(AlcoholType type) {
        switch (type) {
            case BEER: return "ビール";
            case RUM: return "ラム";
            case VODKA: return "ウォッカ";
            case GIN: return "ジン";
            default: return type.name();
        }
    }
    
    private static JButton createDialogButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        button.setPreferredSize(new Dimension(90, 32));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }
    
    /**
     * ダイアログにメッセージを表示して、指定時間後に閉じる
     */
    public static void showResultAndClose(JDialog dialog, String message, Color messageColor, int delayMs) {
        if (dialog == null || !dialog.isVisible()) return;
        
        // ダイアログの内容を更新
        dialog.getContentPane().removeAll();
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(50, 50, 50));
        
        // メッセージを中央に表示
        JLabel messageLabel = new JLabel("<html><center>" + message.replace("\n", "<br>") + "</center></html>", SwingConstants.CENTER);
        messageLabel.setFont(new Font(Font.SERIF, Font.BOLD, 14));
        messageLabel.setForeground(messageColor);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dialog.add(messageLabel, BorderLayout.CENTER);
        
        dialog.revalidate();
        dialog.repaint();
        
        // 指定時間後に閉じる
        Timer timer = new Timer(delayMs, e -> {
            dialog.dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }
}
