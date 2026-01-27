package game;

import game.model.*;
import game.model.alcohol.*;
import game.model.order.*;
import game.controller.*;
import game.util.Constants;
import game.view.MainFrame;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.*;

/**
 * Alice視点でGUI付き、他のプレイヤーは自動操作のテスト
 * 
 * このテストでは、Alice（プレイヤー0）だけGUI付きで操作でき、
 * 他のプレイヤー（Bob, Charlie, Diana）は自動操作（CPU）で動作します。
 */
public class GameSimulationTest {
    
    private static GameState gameState;
    private static GameManager gameManager;
    private static AliceMainFrame aliceFrame;
    private static List<Player> players;
    
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
        System.out.println("=== CAPONE ゲーム シミュレーションテスト ===");
        System.out.println("Alice視点（GUI付き）、他のプレイヤーは自動操作\n");
        
        // プレイヤーを作成
        players = new ArrayList<>();
        String[] names = {"Alice", "Bob", "Charlie", "Diana"};
        for (int i = 0; i < 4; i++) {
            Player player = new Player(i, Constants.INITIAL_MONEY);
            player.setName(names[i]);
            players.add(player);
        }
        
        // GameStateを作成
        gameState = new GameState(players);
        
        // Alice用のMainFrameを作成（GUI付き、myPlayerId = 0）
        aliceFrame = new AliceMainFrame(players, gameState);
        aliceFrame.setMyPlayerId(0); // Aliceはプレイヤー0
        aliceFrame.setTitle("CAPONE - Alice視点（他のプレイヤーは自動操作）");
        
        // GameManagerを作成（AliceのMainFrameを使用）
        gameManager = new GameManager(gameState, aliceFrame);
        aliceFrame.setGameManager(gameManager);
        
        // タイトル画面からゲーム画面に切り替え
        aliceFrame.switchToGameBoard();
        
        // プレイヤー名を設定
        aliceFrame.setPlayerNames(players);
        
        // 少し待ってからゲームを開始
        javax.swing.Timer startTimer = new javax.swing.Timer(500, e -> {
            startGame();
        });
        startTimer.setRepeats(false);
        startTimer.start();
    }
    
    /**
     * ゲームを開始
     */
    private static void startGame() {
        System.out.println("ゲーム開始！");
        gameManager.startGame();
    }
    
    /**
     * Alice用のMainFrame（CPU自動操作を実装）
     */
    private static class AliceMainFrame extends MainFrame {
        private List<Player> allPlayers;
        private GameState gameState;
        private Random random = new Random();
        
        public AliceMainFrame(List<Player> players, GameState gameState) {
            super(false, true); // ウィンドウモード、表示あり
            this.allPlayers = players;
            this.gameState = gameState;
        }
        
        @Override
        public boolean isMultiPlayerMode() {
            return false; // CPUが自動操作するため
        }
        
        @Override
        public void showAuctionInCenterPanel(TruckCard truck, BiConsumer<Integer, Integer> onBidSubmit) {
            // 親クラスのメソッドを呼んでAliceの入札UIを表示
            super.showAuctionInCenterPanel(truck, onBidSubmit);
            
            // CPUプレイヤーの自動入札を開始
            simulateCPUBids(truck, onBidSubmit);
        }
        
        /**
         * CPUプレイヤーの自動入札を実行
         */
        private void simulateCPUBids(TruckCard truck, BiConsumer<Integer, Integer> onBidSubmit) {
            // 全プレイヤー（Alice含む）が自動入札（テスト用）
            for (Player player : allPlayers) {
                int playerId = player.getId();
                int maxBid = Math.min(player.getMoney(), 50);
                int bid = maxBid > 0 ? random.nextInt(maxBid) + 1 : 0;
                
                // Aliceは少し遅めに、他のプレイヤーは3-6秒後に自動入札（目で追える速度）
                int delay;
                if (playerId == 0) {
                    // Alice: 5-8秒後（他のプレイヤーより少し遅く）
                    delay = 5000 + random.nextInt(3000);
                } else {
                    // 他のプレイヤー: 3-6秒後
                    delay = 3000 + random.nextInt(3000);
                }
                
                javax.swing.Timer timer = new javax.swing.Timer(delay, e -> {
                    if (gameState.getPhase() == Phase.AUCTION && onBidSubmit != null) {
                        onBidSubmit.accept(playerId, bid);
                        String prefix = (playerId == 0) ? "[Alice] " : "[CPU] ";
                        System.out.println(prefix + player.getName() + " が自動入札: " + bid + "円");
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
        
        @Override
        public void showServePhaseUI(Runnable onEndServe) {
            // 親クラスのメソッドを呼んでAliceの提供UIを表示
            super.showServePhaseUI(onEndServe);
            
            // CPUプレイヤーの自動提供を開始
            simulateCPUServe();
        }
        
        /**
         * CPUプレイヤーの自動提供を実行
         */
        private void simulateCPUServe() {
            GameManager gm = getGameManager();
            if (gm == null) return;
            
            // Alice以外のプレイヤーが自動で提供
            for (Player player : allPlayers) {
                if (player.getId() != 0) { // Alice以外
                    int playerId = player.getId();
                    Player p = player;
                    
                    // 4-8秒後に自動提供（目で追える速度）
                    int delay = 4000 + random.nextInt(4000);
                    javax.swing.Timer timer = new javax.swing.Timer(delay, e -> {
                        if (gameState.getPhase() == Phase.SERVE) {
                            // 提供可能な注文カードを探して提供
                            List<OrderCard> orders = new ArrayList<>(p.getOrders());
                            for (OrderCard order : orders) {
                                if (p.canComplete(order)) {
                                    gm.onServeCustomerRequested(p, order);
                                    System.out.println("[CPU] " + p.getName() + " が自動提供: " + order.getCustomerName());
                                    break; // 1つだけ提供
                                }
                            }
                            
                            // 提供後、2-4秒後に提供終了を選択（目で追える速度）
                            javax.swing.Timer endTimer = new javax.swing.Timer(2000 + random.nextInt(2000), e2 -> {
                                if (gameState.getPhase() == Phase.SERVE) {
                                    gm.onEndServeRequested(playerId);
                                    System.out.println("[CPU] " + p.getName() + " が提供終了を選択");
                                }
                            });
                            endTimer.setRepeats(false);
                            endTimer.start();
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        }
    }
}
