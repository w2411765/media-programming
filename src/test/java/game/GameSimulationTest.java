package game;

import game.model.*;
import game.model.alcohol.*;
import game.model.order.*;
import game.controller.*;
import game.util.Constants;
import java.util.*;

/**
 * 仮想の試合を実行するテストクラス
 * 
 * このテストでは、実際のUIを使わずにゲームのロジックをテストします。
 */
public class GameSimulationTest {
    
    public static void main(String[] args) {
        System.out.println("=== CAPONE ゲーム シミュレーションテスト ===\n");
        
        // プレイヤーを作成
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < Constants.NUM_PLAYERS; i++) {
            players.add(new Player(i, Constants.INITIAL_MONEY));
        }
        
        // GameStateを作成
        GameState gameState = new GameState(players);
        
        // MainFrameの代わりにモックを使用（UIなしでテスト）
        MockMainFrame mockFrame = new MockMainFrame();
        
        // GameManagerを作成
        GameManager gameManager = new GameManager(gameState, mockFrame);
        
        // ゲームを開始
        System.out.println("ゲーム開始！");
        gameManager.startGame();
        
        // 複数ラウンドをシミュレーション（最大5ラウンドまたはゲーム終了まで）
        int maxRounds = 5;
        for (int round = 1; round <= maxRounds && !gameState.isGameOver(); round++) {
            simulateRound(gameState, gameManager, mockFrame);
            
            if (gameState.isGameOver()) {
                break;
            }
            
            // 次のラウンドへ（GameManagerが自動的に進める想定だが、テストでは手動で進める）
            if (round < maxRounds) {
                System.out.println("\n=== 次のラウンドへ ===\n");
                // 次のラウンドの準備（GameManager.startGame()が呼ばれる想定）
                // ここでは簡易的に次のラウンドのフェーズを開始
                gameState.setPhase(Phase.ORDER_DISTRIBUTION);
                gameState.dealCustomerCardsToAllPlayers(Constants.CARDS_PER_PLAYER);
            }
        }
        
        // 最終結果
        System.out.println("\n=== 最終結果 ===");
        for (Player player : players) {
            System.out.println(player.getName() + " - 所持金: " + player.getMoney() + "円");
        }
        
        if (gameState.isGameOver()) {
            Player winner = gameState.getWinner();
            System.out.println("\n勝者: " + winner.getName() + " (所持金: " + winner.getMoney() + "円)");
        }
        
        System.out.println("\n=== テスト完了 ===");
    }
    
    /**
     * 1ラウンド分のシミュレーション
     */
    private static void simulateRound(GameState gameState, GameManager gameManager, MockMainFrame mockFrame) {
        System.out.println("\n--- ラウンド " + gameState.getRound() + " ---");
        
        // フェーズ1: 注文カード配布
        System.out.println("\n[フェーズ1] 注文カード配布");
        List<Player> players = gameState.getPlayers();
        for (Player player : players) {
            System.out.println("  " + player.getName() + " に " + player.getOrders().size() + " 枚のカードを配布");
        }
        
        // フェーズ2: オークション
        System.out.println("\n[フェーズ2] オークション");
        TruckCard truck = gameState.prepareTruckCardForAuction();
        System.out.println("  トラックカード: " + truck.getCargo());
        
        // 各プレイヤーが入札（簡易版：ランダムに入札）
        Random random = new Random();
        for (Player player : players) {
            int maxBid = Math.min(player.getMoney(), 30);
            int bid = random.nextInt(maxBid) + 1;
            try {
                gameState.setBid(player, bid);
                System.out.println("  " + player.getName() + " が " + bid + "円で入札");
            } catch (IllegalArgumentException e) {
                System.out.println("  " + player.getName() + " の入札失敗: " + e.getMessage());
            }
        }
        
        // オークション解決
        if (gameState.allBidsSubmitted()) {
            Player winner = gameState.resolveAuction();
            System.out.println("  勝者: " + winner.getName());
            System.out.println("  勝者の在庫: " + winner.getInventory());
        }
        
        // フェーズ3: 取引（簡易版：スキップ）
        System.out.println("\n[フェーズ3] 取引フェーズ（スキップ）");
        
        // フェーズ4: 提供
        System.out.println("\n[フェーズ4] 提供フェーズ");
        for (Player player : players) {
            List<OrderCard> orders = new ArrayList<>(player.getOrders());
            for (OrderCard order : orders) {
                if (player.canComplete(order)) {
                    boolean success = gameState.serveCustomer(player, order);
                    if (success) {
                        System.out.println("  " + player.getName() + " が注文を達成！報酬: " + order.getReward() + "円");
                        System.out.println("    現在の所持金: " + player.getMoney() + "円");
                    }
                }
            }
        }
        
        // ラウンド終了判定
        System.out.println("\n[ラウンド終了]");
        System.out.println("  各プレイヤーの状態:");
        for (Player player : players) {
            System.out.println("    " + player.getName() + 
                " - 所持金: " + player.getMoney() + "円, " +
                "在庫: " + player.getInventory() + ", " +
                "手札: " + player.getOrders().size() + "枚");
        }
        
        // ゲーム終了判定（ここでは表示のみ、実際の終了処理は呼び出し側で行う）
        if (gameState.isGameOver()) {
            System.out.println("\n  → ゲーム終了条件達成！");
        } else {
            System.out.println("\n  → ゲーム継続");
        }
    }
    
    /**
     * MainFrameのモッククラス（UIなしでテストするため）
     */
    private static class MockMainFrame extends game.view.MainFrame {
        @Override
        public void updateRoundInfo(int round) {
            System.out.println("[UI] ラウンド情報更新: " + round);
        }
        
        @Override
        public void updateAllPlayersState(List<Player> players) {
            // テストでは簡易出力
        }
        
        @Override
        public void showMessage(String message) {
            System.out.println("[UI] " + message);
        }
        
        @Override
        public void showAuctionTruck(TruckCard truck) {
            System.out.println("[UI] オークション: " + truck.toString());
        }
        
        @Override
        public void showAuctionResult(Player winner) {
            System.out.println("[UI] オークション結果: " + winner.getName() + " が勝利");
        }
        
        @Override
        public void showGameOverDialog(Player winner) {
            System.out.println("[UI] ゲーム終了: " + winner.getName() + " が勝利");
        }
    }
}
