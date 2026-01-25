package game;

import game.model.*;
import game.model.alcohol.*;
import game.model.order.*;
import game.controller.*;
import game.controller.TradeProposal;
import game.view.*;
import game.view.components.*;
import game.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * 4人同時プレイテスト（タブ切り替え式）
 * 
 * 各プレイヤーは自分自身を「南」として見ます。
 * 全てのビューは同期され、同じゲーム状態を共有します。
 */
public class MultiPlayerTest {
    
    private static GameState gameState;
    private static List<PlayerView> playerViews;
    private static GameManager gameManager;
    private static BroadcastMainFrame broadcastFrame;
    private static JFrame mainFrame;
    private static JTabbedPane tabbedPane;
    private static JTextArea logArea;
    private static JPanel controlPanel;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            initializeGame();
        });
    }
    
    /**
     * ゲームを初期化
     */
    private static void initializeGame() {
        // プレイヤーを作成
        List<Player> players = new ArrayList<>();
        String[] names = {"Alice", "Bob", "Charlie", "Diana"};
        for (int i = 0; i < 4; i++) {
            Player player = new Player(i, Constants.INITIAL_MONEY);
            player.setName(names[i]);
            players.add(player);
        }
        
        // 共有のGameStateを作成
        gameState = new GameState(players);
        
        // メインフレームを作成
        mainFrame = new JFrame("CAPONE - 4人同時プレイテスト");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        
        // タブパネルを作成
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        
        // 4人分のビューを作成
        playerViews = new ArrayList<>();
        
        Color[] tabColors = {
            new Color(100, 150, 255),  // 青
            new Color(255, 150, 100),  // オレンジ
            new Color(100, 255, 150),  // 緑
            new Color(255, 100, 150)   // ピンク
        };
        
        for (int i = 0; i < 4; i++) {
            PlayerView view = new PlayerView(i, players, gameState);
            playerViews.add(view);
            
            // タブに追加
            String tabTitle = names[i] + "の視点 (P" + i + ")";
            tabbedPane.addTab(tabTitle, view.getPanel());
            tabbedPane.setBackgroundAt(i, tabColors[i]);
        }
        
        // ブロードキャスト用のMainFrameを作成（全ビューに通知を転送）
        broadcastFrame = new BroadcastMainFrame(playerViews);
        
        // GameManagerは1つだけ（ブロードキャストフレームを使用）
        gameManager = new GameManager(gameState, broadcastFrame);
        
        // 全ビューにGameManagerを設定
        for (PlayerView view : playerViews) {
            view.setGameManager(gameManager);
        }
        
        // タブ切り替え時のイベント
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            log(">>> " + names[selectedIndex] + "の視点に切り替えました（自分=南）");
        });
        
        // タブパネルのサイズを固定（ゲーム画面: 1920x1080）
        tabbedPane.setPreferredSize(new Dimension(1920, 1080 + 35)); // +35 はタブの高さ
        
        // メインコンテンツ（左: ゲーム画面、右: ツールパネル）
        JPanel contentPanel = new JPanel(new BorderLayout(5, 0));
        contentPanel.setBackground(new Color(30, 30, 30));
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // 右側ツールパネル
        JPanel toolPanel = createToolPanel(names);
        contentPanel.add(toolPanel, BorderLayout.EAST);
        
        mainFrame.add(contentPanel, BorderLayout.CENTER);
        
        // 下部ステータスバー
        JPanel statusBar = createStatusBar();
        mainFrame.add(statusBar, BorderLayout.SOUTH);
        
        // ウィンドウサイズを調整して表示（フルスクリーンではなく）
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
        
        log("=== CAPONE 4人同時プレイテスト ===");
        log("各タブは各プレイヤーの視点です。自分は常に「南」。");
        log("「全員ゲーム開始」ボタンを押してください。");
    }
    
    /**
     * 右側ツールパネルを作成
     */
    private static JPanel createToolPanel(String[] names) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(40, 40, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(280, 0));
        
        // タイトル
        JLabel titleLabel = new JLabel("テストツール");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));
        
        // ゲーム開始ボタン
        JButton startButton = new JButton("▶ 全員ゲーム開始");
        startButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        startButton.setBackground(new Color(50, 150, 50));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setMaximumSize(new Dimension(250, 40));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> startAllGames());
        panel.add(startButton);
        panel.add(Box.createVerticalStrut(10));
        
        // 状態表示ボタン
        JButton statusButton = new JButton("状態表示");
        statusButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        statusButton.setMaximumSize(new Dimension(250, 30));
        statusButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusButton.addActionListener(e -> printGameStatus());
        panel.add(statusButton);
        panel.add(Box.createVerticalStrut(20));
        
        // 視点切替セクション
        JLabel switchLabel = new JLabel("視点切替");
        switchLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        switchLabel.setForeground(Color.LIGHT_GRAY);
        switchLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(switchLabel);
        panel.add(Box.createVerticalStrut(10));
        
        Color[] buttonColors = {
            new Color(100, 150, 255),
            new Color(255, 150, 100),
            new Color(100, 255, 150),
            new Color(255, 100, 150)
        };
        
        for (int i = 0; i < 4; i++) {
            final int index = i;
            JButton switchButton = new JButton((i + 1) + ". " + names[i]);
            switchButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
            switchButton.setBackground(buttonColors[i]);
            switchButton.setForeground(Color.BLACK);
            switchButton.setMaximumSize(new Dimension(250, 35));
            switchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            switchButton.addActionListener(e -> {
                tabbedPane.setSelectedIndex(index);
            });
            panel.add(switchButton);
            panel.add(Box.createVerticalStrut(5));
        }
        
        panel.add(Box.createVerticalStrut(15));
        
        // ログエリア
        JLabel logLabel = new JLabel("システムログ");
        logLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        logLabel.setForeground(Color.LIGHT_GRAY);
        logLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(logLabel);
        panel.add(Box.createVerticalStrut(5));
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        logArea.setBackground(new Color(25, 25, 25));
        logArea.setForeground(new Color(150, 255, 150));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(logScroll);
        
        return panel;
    }
    
    /**
     * 下部ステータスバーを作成
     */
    private static JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 3));
        statusBar.setBackground(new Color(30, 30, 30));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        
        JLabel infoLabel = new JLabel("ゲーム画面: 1920x1080 | タブを切り替えて各プレイヤーの視点を確認できます");
        infoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        infoLabel.setForeground(Color.LIGHT_GRAY);
        statusBar.add(infoLabel);
        
        return statusBar;
    }
    
    /**
     * 全員のゲームを開始
     */
    private static void startAllGames() {
        log("ゲームを開始します...");
        
        // 全ビューをゲームボードに切り替え
        for (PlayerView view : playerViews) {
            view.switchToGameBoard();
        }
        
        // 各ビューにチャットコールバックを設定（全ビューに同期）
        for (PlayerView view : playerViews) {
            view.setupChatCallback(broadcastFrame);
        }
        
        // 少し待ってからゲームを開始
        javax.swing.Timer startTimer = new javax.swing.Timer(500, e -> {
            // GameManagerでゲームを開始（ブロードキャストで全ビューに通知される）
            gameManager.startGame();
            
            // 全ビューにプレイヤー名を設定（各自の視点で）
            for (PlayerView view : playerViews) {
                view.updatePlayerNamesForMyView();
            }
            
            log("ゲームが開始されました！");
        });
        startTimer.setRepeats(false);
        startTimer.start();
    }
    
    /**
     * ログを出力
     */
    private static void log(String message) {
        System.out.println(message);
        if (logArea != null) {
            SwingUtilities.invokeLater(() -> {
                logArea.append(message + "\n");
                logArea.setCaretPosition(logArea.getDocument().getLength());
            });
        }
    }
    
    /**
     * ゲーム状態を表示
     */
    private static void printGameStatus() {
        log("\n=== ゲーム状態 ===");
        log("ラウンド: " + gameState.getRound() + " / フェーズ: " + gameState.getPhase());
        for (Player p : gameState.getPlayers()) {
            log("  " + p.getName() + "(P" + p.getId() + ")" +
                " - 所持金: " + p.getMoney() + "円, " +
                "在庫: " + p.getInventory().size() + "個");
        }
    }
    
    // ========================================
    // ブロードキャストMainFrame
    // GameManagerからの通知を全ビューに転送
    // ========================================
    
    private static class BroadcastMainFrame extends MainFrame {
        private List<PlayerView> views;
        
        public BroadcastMainFrame(List<PlayerView> views) {
            super(false, false);
            this.views = views;
        }
        
        @Override
        public boolean isMultiPlayerMode() {
            return true; // マルチプレイモード
        }
        
        @Override
        public void switchToGameBoard() {
            for (PlayerView view : views) {
                view.switchToGameBoard();
            }
        }
        
        @Override
        public void setPlayerNames(List<Player> players) {
            for (PlayerView view : views) {
                view.updatePlayerNamesForMyView();
            }
        }
        
        @Override
        public void showMessage(String message) {
            log("[GAME] " + message);
            for (PlayerView view : views) {
                view.showMessageInternal(message);
            }
        }
        
        @Override
        public void updateRoundInfo(int round) {
            for (PlayerView view : views) {
                view.updateRoundInfoInternal(round);
            }
        }
        
        @Override
        public void updatePlayerInventory(Player player) {
            for (PlayerView view : views) {
                view.updatePlayerInventoryInternal(player);
            }
        }
        
        @Override
        public void updateInfoPanelMoney(int money) {
            // 各ビューで自分の所持金を表示
            for (PlayerView view : views) {
                view.updateMyMoney();
            }
        }
        
        @Override
        public void updateAllPlayersState(List<Player> players) {
            for (Player player : players) {
                updatePlayerInventory(player);
            }
            // 各ビューで自分の所持金を更新
            for (PlayerView view : views) {
                view.updateMyMoney();
            }
        }
        
        @Override
        public void showAuctionInCenterPanel(TruckCard truck, Player currentPlayer,
                Consumer<Integer> onBidSubmit) {
            // 旧API - 使用しない
        }
        
        @Override
        public void showAuctionInCenterPanel(TruckCard truck, 
                java.util.function.BiConsumer<Integer, Integer> onBidSubmit) {
            // 全員に同時に入札画面を表示（各自が自分の入札をする）
            for (PlayerView view : views) {
                // 各ビューで「自分」のプレイヤーを取得して入札UIを表示
                Player myPlayer = gameState.getPlayers().get(view.getMyPlayerId());
                int playerId = view.getMyPlayerId();
                
                // 各ビューのコールバックに自分のIDを含める
                view.showAuctionInCenterPanelInternal(truck, myPlayer, amount -> {
                    if (onBidSubmit != null) {
                        onBidSubmit.accept(playerId, amount);
                    }
                });
            }
        }
        
        @Override
        public void showBidForPlayer(Player player, Consumer<Integer> onBidSubmit) {
            // 同時入札なので使用しない
        }
        
        @Override
        public void showBidComplete(Player player) {
            // 入札完了したプレイヤーのビューにのみ完了表示
            int playerId = player.getId();
            if (playerId >= 0 && playerId < views.size()) {
                views.get(playerId).showBidCompleteInternal(player);
            }
        }
        
        @Override
        public void showGameStart() {
            for (PlayerView view : views) {
                view.showGameStartInternal();
            }
        }
        
        @Override
        public void showRoundStart(int roundNumber) {
            for (PlayerView view : views) {
                view.showRoundStartInternal(roundNumber);
            }
        }
        
        @Override
        public void showAllBidsInCenterPanel(java.util.Map<String, Integer> bidResults) {
            for (PlayerView view : views) {
                view.showAllBidsInternal(bidResults);
            }
        }
        
        @Override
        public void showAuctionResultInCenterPanel(Player winner, int winningBid) {
            for (PlayerView view : views) {
                view.showAuctionResultInternal(winner, winningBid);
            }
        }
        
        @Override
        public void showTradePhaseComplete() {
            for (PlayerView view : views) {
                view.showTradePhaseCompleteInternal();
            }
        }
        
        @Override
        public void showTradePhase() {
            for (PlayerView view : views) {
                view.showTradePhaseInternal();
            }
        }
        
        @Override
        public void updatePlayerOrders(Player player) {
            // 各ビューで自分のオーダーのみ表示
            for (PlayerView view : views) {
                view.updateMyOrders();
            }
        }
        
        @Override
        public void showGameOverDialog(Player winner) {
            String msg = "ゲーム終了！勝者: " + winner.getName() + " (" + winner.getMoney() + "円)";
            log("[ゲーム終了] " + msg);
            JOptionPane.showMessageDialog(mainFrame, msg, "ゲーム終了", JOptionPane.INFORMATION_MESSAGE);
        }
        
        @Override
        public void enableServeUI(boolean enabled) {
            for (PlayerView view : views) {
                view.enableServeUIInternal(enabled);
            }
        }
        
        @Override
        public void enableTradeUI(boolean enabled) {
            // 各ビューで取引UIを有効化
        }
        
        @Override
        public void showTradeDialog(Player currentPlayer, List<Player> otherPlayers,
                Consumer<TradeProposal> onSubmit, Runnable onCancel, Runnable onEndTrade) {
            // 旧API - 使用しない（マルチプレイでは startTradePhaseForAllPlayers を使う）
        }
        
        @Override
        public void startTradePhaseForAllPlayers(
                java.util.function.BiConsumer<Integer, TradeProposal> onProposalSubmit,
                Consumer<Integer> onEndTradeRequest) {
            // 各ビューで自分の取引ダイアログを表示（IDを付与）
            for (PlayerView view : views) {
                int playerId = view.getMyPlayerId();
                view.showTradeDialogInternal(
                    proposal -> {
                        if (onProposalSubmit != null) {
                            onProposalSubmit.accept(playerId, proposal);
                        }
                    },
                    () -> {}, // onCancel
                    () -> {
                        if (onEndTradeRequest != null) {
                            onEndTradeRequest.accept(playerId);
                        }
                    }
                );
            }
        }
        
        @Override
        public void showTradeDialogForPlayer(int playerId,
                java.util.function.BiConsumer<Integer, TradeProposal> onProposalSubmit,
                Consumer<Integer> onEndTradeRequest) {
            // 指定されたプレイヤーのビューにのみ取引ダイアログを表示
            if (playerId >= 0 && playerId < views.size()) {
                PlayerView view = views.get(playerId);
                view.showTradeDialogInternal(
                    proposal -> {
                        if (onProposalSubmit != null) {
                            onProposalSubmit.accept(playerId, proposal);
                        }
                    },
                    () -> {},
                    () -> {
                        if (onEndTradeRequest != null) {
                            onEndTradeRequest.accept(playerId);
                        }
                    }
                );
            }
        }
        
        @Override
        public void showTradeEndWaitingForPlayer(int playerId, Player player, int completedCount, int totalCount) {
            // 該当プレイヤーのビューにのみ待機表示
            if (playerId >= 0 && playerId < views.size()) {
                views.get(playerId).showTradeEndWaitingInternal(player, completedCount, totalCount);
            }
        }
        
        @Override
        public void broadcastChatMessage(String sender, String message) {
            // 全ビューにチャットメッセージを追加
            log("[CHAT] <" + sender + "> " + message);
            for (PlayerView view : views) {
                view.addChatMessageInternal(sender, message);
            }
        }
        
        @Override
        public void showPenaltyPhase(String title, String description) {
            // 全ビューにペナルティフェーズを表示
            for (PlayerView view : views) {
                view.showPenaltyPhaseInternal(title, description);
            }
        }
        
        @Override
        public void showPenaltyMessage(String title, String message) {
            // 全ビューにペナルティメッセージを表示
            for (PlayerView view : views) {
                view.showPenaltyMessageInternal(title, message);
            }
        }
        
        @Override
        public void showCoinToss(Player culprit, boolean canToss, Consumer<Boolean> onResult) {
            // コイントスは戦犯のビューでのみ操作可能
            for (PlayerView view : views) {
                boolean isMyTurn = (view.getMyPlayerId() == culprit.getId());
                view.showCoinTossInternal(culprit, isMyTurn, onResult);
            }
        }
        
        // 受信ダイアログの参照を保持（キャンセル時に閉じるため）
        private javax.swing.JDialog receivedTradeDialog;
        private TradeProposal currentReceivedProposal;
        
        @Override
        public void notifyTradeProposal(TradeProposal proposal, Consumer<Boolean> onResponse) {
            currentReceivedProposal = proposal;
            int targetId = proposal.getTo().getId();
            
            // 送り先のビューに受信ダイアログを表示
            if (targetId >= 0 && targetId < views.size()) {
                PlayerView targetView = views.get(targetId);
                receivedTradeDialog = targetView.showReceivedTradeInternal(proposal, accepted -> {
                    receivedTradeDialog = null;
                    currentReceivedProposal = null;
                    if (onResponse != null) {
                        onResponse.accept(accepted);
                    }
                });
            }
        }
        
        @Override
        public void cancelTradeProposal(TradeProposal proposal) {
            // 送り先のダイアログを閉じる
            if (receivedTradeDialog != null && currentReceivedProposal == proposal) {
                receivedTradeDialog.dispose();
                receivedTradeDialog = null;
                currentReceivedProposal = null;
                
                // 送り先のビューに取引作成UIを再表示
                int targetId = proposal.getTo().getId();
                if (targetId >= 0 && targetId < views.size()) {
                    views.get(targetId).showTradeCancelledInternal(proposal);
                }
            }
        }
    }
    
    // ========================================
    // プレイヤービュー
    // 各プレイヤーは自分を「南」として見る
    // ========================================
    
    private static class PlayerView extends MainFrame {
        private int myPlayerId;
        private List<Player> allPlayers;
        private GameState gameState;
        
        private JPanel containerPanel;
        private TitlePanel titlePanel;
        private GameBoardPanel gameBoardPanel;
        private boolean isGameBoard = false;
        
        public PlayerView(int myPlayerId, List<Player> allPlayers, GameState gameState) {
            super(false, false);
            
            this.myPlayerId = myPlayerId;
            this.allPlayers = allPlayers;
            this.gameState = gameState;
            
            // MainFrameのmyPlayerIdを設定
            setMyPlayerId(myPlayerId);
            
            Player myPlayer = allPlayers.get(myPlayerId);
            
            // コンテナパネルを作成
            containerPanel = new JPanel(new BorderLayout());
            containerPanel.setBackground(Color.BLACK);
            
            // プレイヤー情報バー
            JPanel infoBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
            infoBar.setBackground(new Color(60, 60, 60));
            infoBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            JLabel playerLabel = new JLabel("★ " + myPlayer.getName() + "の視点 - 自分は「南」");
            playerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            playerLabel.setForeground(Color.YELLOW);
            infoBar.add(playerLabel);
            
            JLabel positionLabel = new JLabel("  |  " + getRelativePositionDescription());
            positionLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            positionLabel.setForeground(Color.LIGHT_GRAY);
            infoBar.add(positionLabel);
            
            containerPanel.add(infoBar, BorderLayout.NORTH);
            
            // タイトル画面を作成
            titlePanel = new TitlePanel();
            containerPanel.add(titlePanel, BorderLayout.CENTER);
        }
        
        private String getRelativePositionDescription() {
            int south = myPlayerId;
            int east = (myPlayerId + 1) % 4;
            int north = (myPlayerId + 2) % 4;
            int west = (myPlayerId + 3) % 4;
            
            return "南:" + allPlayers.get(south).getName() + 
                   " / 東:" + allPlayers.get(east).getName() + 
                   " / 北:" + allPlayers.get(north).getName() + 
                   " / 西:" + allPlayers.get(west).getName();
        }
        
        public void updatePlayerNamesForMyView() {
            if (gameBoardPanel == null || gameBoardPanel.getCenterPanel() == null) return;
            
            int southId = myPlayerId;
            int eastId = (myPlayerId + 1) % 4;
            int northId = (myPlayerId + 2) % 4;
            int westId = (myPlayerId + 3) % 4;
            
            String south = allPlayers.get(southId).getName() + "(自分)";
            String east = allPlayers.get(eastId).getName();
            String north = allPlayers.get(northId).getName();
            String west = allPlayers.get(westId).getName();
            
            gameBoardPanel.getCenterPanel().setPlayerNames(north, east, south, west);
            
            if (gameBoardPanel.getLogPanel() != null) {
                gameBoardPanel.getLogPanel().setPlayerName(allPlayers.get(myPlayerId).getName());
            }
        }
        
        private int getRelativePosition(int playerId) {
            return (playerId - myPlayerId + 4) % 4;
        }
        
        public JPanel getPanel() {
            return containerPanel;
        }
        
        @Override
        public void switchToGameBoard() {
            if (isGameBoard) return;
            isGameBoard = true;
            
            containerPanel.remove(titlePanel);
            gameBoardPanel = new GameBoardPanel();
            containerPanel.add(gameBoardPanel, BorderLayout.CENTER);
            
            containerPanel.revalidate();
            containerPanel.repaint();
        }
        
        // ========== 内部メソッド（BroadcastMainFrameから呼ばれる） ==========
        
        public void showMessageInternal(String message) {
            if (gameBoardPanel != null && gameBoardPanel.getLogPanel() != null) {
                gameBoardPanel.getLogPanel().addMessage(message);
            }
        }
        
        public void updateRoundInfoInternal(int round) {
            if (gameBoardPanel != null && gameBoardPanel.getInfoPanel() != null) {
                gameBoardPanel.getInfoPanel().updateRoundInfo(round);
            }
        }
        
        public void updatePlayerInventoryInternal(Player player) {
            if (gameBoardPanel != null && gameBoardPanel.getMarketPanel() != null) {
                int relativePos = getRelativePosition(player.getId());
                gameBoardPanel.getMarketPanel().updatePlayerInventoryAtPosition(player, relativePos);
            }
        }
        
        public void updateMyMoney() {
            if (gameBoardPanel != null && gameBoardPanel.getInfoPanel() != null) {
                Player myPlayer = allPlayers.get(myPlayerId);
                gameBoardPanel.getInfoPanel().updateMoney(myPlayer.getMoney());
            }
        }
        
        public void updateMyOrders() {
            if (gameBoardPanel != null && gameBoardPanel.getCustomerPanel() != null) {
                Player myPlayer = allPlayers.get(myPlayerId);
                gameBoardPanel.getCustomerPanel().setOrderCards(myPlayer.getOrders());
            }
        }
        
        public void showAuctionInCenterPanelInternal(TruckCard truck, Player currentPlayer,
                Consumer<Integer> onBidSubmit) {
            if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
                gameBoardPanel.getCenterPanel().showAuction(truck, currentPlayer, onBidSubmit);
            }
        }
        
        public void showAuctionInfoOnly(TruckCard truck, Player currentPlayer) {
            if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
                // オークション情報のみ表示（入札UIなし）
                gameBoardPanel.getCenterPanel().showAuction(truck, currentPlayer, null);
            }
        }
        
        public void showBidForPlayerInternal(Player player, Consumer<Integer> onBidSubmit) {
            if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
                gameBoardPanel.getCenterPanel().showBidForPlayer(player, onBidSubmit);
            }
        }
        
        public void showBidCompleteInternal(Player player) {
            if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
                gameBoardPanel.getCenterPanel().showBidComplete(player);
            }
        }
        
        public void showGameStartInternal() {
            if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
                gameBoardPanel.getCenterPanel().showGameStart();
            }
        }
        
        public void showRoundStartInternal(int roundNumber) {
            if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
                gameBoardPanel.getCenterPanel().showRoundStart(roundNumber);
            }
        }
        
        public void showAllBidsInternal(java.util.Map<String, Integer> bidResults) {
            if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
                gameBoardPanel.getCenterPanel().showAllBids(bidResults);
            }
        }
        
        public void showAuctionResultInternal(Player winner, int winningBid) {
            if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
                gameBoardPanel.getCenterPanel().showAuctionResult(winner, winningBid);
            }
        }
        
        public void showTradePhaseCompleteInternal() {
            if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
                gameBoardPanel.getCenterPanel().showTradePhaseComplete();
            }
        }
        
        public void showTradePhaseInternal() {
            if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
                gameBoardPanel.getCenterPanel().showTradePhase();
            }
        }
        
        public void enableServeUIInternal(boolean enabled) {
            // 提供UIの有効化（必要に応じて実装）
        }
        
        public void showTradeDialogInternal(Consumer<TradeProposal> onSubmit, 
                Runnable onCancel, Runnable onEndTrade) {
            if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
                Player myPlayer = allPlayers.get(myPlayerId);
                gameBoardPanel.getCenterPanel().showTradeCreationUI(
                    myPlayer, allPlayers, onSubmit, onEndTrade);
            }
        }
        
        public void showTradeEndWaitingInternal(Player player, int completedCount, int totalCount) {
            if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
                gameBoardPanel.getCenterPanel().showTradeEndWaiting(player, completedCount, totalCount);
            }
        }
        
        public void addChatMessageInternal(String sender, String message) {
            if (gameBoardPanel != null && gameBoardPanel.getLogPanel() != null) {
                gameBoardPanel.getLogPanel().addChatMessage(sender, message);
            }
        }
        
        /**
         * チャット送信コールバックを設定（broadcastMainFrame経由で全ビューに同期）
         */
        public void setupChatCallback(MainFrame broadcastFrame) {
            if (gameBoardPanel != null && gameBoardPanel.getLogPanel() != null) {
                Player myPlayer = allPlayers.get(myPlayerId);
                gameBoardPanel.getLogPanel().setPlayerName(myPlayer.getName());
                gameBoardPanel.getLogPanel().setOnChatSend((sender, message) -> {
                    broadcastFrame.broadcastChatMessage(sender, message);
                });
            }
        }
        
        public void showPenaltyPhaseInternal(String title, String description) {
            if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
                gameBoardPanel.getCenterPanel().showPenaltyPhase(title, description);
            }
        }
        
        public void showPenaltyMessageInternal(String title, String message) {
            if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
                gameBoardPanel.getCenterPanel().showPenaltyMessage(title, message);
            }
        }
        
        public void showCoinTossInternal(Player culprit, boolean canToss, Consumer<Boolean> onResult) {
            if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
                gameBoardPanel.getCenterPanel().showCoinToss(culprit, canToss, onResult);
            }
        }
        
        public javax.swing.JDialog showReceivedTradeInternal(TradeProposal proposal, Consumer<Boolean> onResponse) {
            // このビューのフレームを取得して受信ダイアログを表示
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(containerPanel);
            return game.view.dialogs.TradeDialog.showReceivedTradeDialog(frame, proposal, onResponse);
        }
        
        public void showTradeCancelledInternal(TradeProposal proposal) {
            // 送り主がキャンセルした通知を表示
            if (gameBoardPanel != null && gameBoardPanel.getLogPanel() != null) {
                gameBoardPanel.getLogPanel().addMessage(
                    proposal.getFrom().getName() + " が取引を取り消しました。");
            }
        }
        
        @Override
        public int getMyPlayerId() {
            return myPlayerId;
        }
        
        @Override
        public boolean isMultiPlayerMode() {
            return true;
        }
        
        // ========== MainFrameのオーバーライド（直接呼ばれる場合用） ==========
        
        @Override
        public void setPlayerNames(List<Player> players) {
            updatePlayerNamesForMyView();
        }
        
        @Override
        public void showMessage(String message) {
            showMessageInternal(message);
        }
        
        @Override
        public void updateRoundInfo(int round) {
            updateRoundInfoInternal(round);
        }
        
        @Override
        public void updatePlayerInventory(Player player) {
            updatePlayerInventoryInternal(player);
        }
        
        @Override
        public void updateInfoPanelMoney(int money) {
            if (gameBoardPanel != null && gameBoardPanel.getInfoPanel() != null) {
                gameBoardPanel.getInfoPanel().updateMoney(money);
            }
        }
        
        @Override
        public void showAuctionInCenterPanel(TruckCard truck, Player currentPlayer,
                Consumer<Integer> onBidSubmit) {
            showAuctionInCenterPanelInternal(truck, currentPlayer, onBidSubmit);
        }
        
        @Override
        public void showBidForPlayer(Player player, Consumer<Integer> onBidSubmit) {
            showBidForPlayerInternal(player, onBidSubmit);
        }
        
        @Override
        public void showGameStart() {
            showGameStartInternal();
        }
        
        @Override
        public void showRoundStart(int roundNumber) {
            showRoundStartInternal(roundNumber);
        }
        
        @Override
        public void showAllBidsInCenterPanel(java.util.Map<String, Integer> bidResults) {
            showAllBidsInternal(bidResults);
        }
        
        @Override
        public void showAuctionResultInCenterPanel(Player winner, int winningBid) {
            showAuctionResultInternal(winner, winningBid);
        }
        
        @Override
        public void showTradePhaseComplete() {
            showTradePhaseCompleteInternal();
        }
        
        @Override
        public void showTradePhase() {
            showTradePhaseInternal();
        }
        
        @Override
        public void updatePlayerOrders(Player player) {
            if (player != null && player.getId() == myPlayerId) {
                updateMyOrders();
            }
        }
        
        @Override
        public void showGameOverDialog(Player winner) {
            String msg = "ゲーム終了！勝者: " + winner.getName() + " (" + winner.getMoney() + "円)";
            JOptionPane.showMessageDialog(containerPanel, msg, "ゲーム終了", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
