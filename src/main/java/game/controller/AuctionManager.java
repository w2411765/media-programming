// 入札処理のロジック
package game.controller;

import game.model.GameState;
import game.model.Player;
import game.model.alcohol.TruckCard;
import game.view.MainFrame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * オークションフェーズの進行を担当するクラス。
 * マルチプレイヤー対応：全員同時に入札し、全員揃ったら結果発表。
 */
public class AuctionManager {

    private final GameState gameState;
    private final MainFrame mainFrame;
    private final GameManager gameManager;
    
    private TruckCard currentTruck;
    private List<Player> players;
    
    // 同時入札用：各プレイヤーの入札状況を追跡
    private Map<Integer, Integer> bids = new HashMap<>();
    private int expectedBidCount = 0;

    public AuctionManager(GameState gameState, MainFrame mainFrame,
                          GameManager gameManager) {
        this.gameState = gameState;
        this.mainFrame = mainFrame;
        this.gameManager = gameManager;
    }

    /** 1ラウンド分のオークションを開始する。 */
    public void startAuction() {
        // 人数×4 本の酒をまとめた TruckCard を1枚用意する想定
        currentTruck = gameState.prepareTruckCardForAuction();
        players = gameState.getPlayers();
        
        // 入札状況をリセット
        bids.clear();
        expectedBidCount = players.size();

        mainFrame.showMessage("オークションを開始します。入札額を入力してください。");
        
        // 全員に同時に入札画面を表示
        showAuctionForAllPlayers();
    }
    
    /**
     * 全員に同時に入札画面を表示（マルチプレイヤー対応）
     * コールバックは (playerId, amount) の形式で呼ばれる
     */
    private void showAuctionForAllPlayers() {
        mainFrame.showAuctionInCenterPanel(currentTruck, this::onBidReceived);
    }
    
    /**
     * 入札を受け取ったときのコールバック
     * @param playerId 入札したプレイヤーのID
     * @param amount 入札額
     */
    public void onBidReceived(int playerId, int amount) {
        Player player = getPlayerById(playerId);
        
        if (player == null) {
            mainFrame.showMessage("エラー: 不明なプレイヤーID: " + playerId);
            return;
        }
        
        // 既に入札済みの場合は無視
        if (bids.containsKey(playerId)) {
            mainFrame.showMessage(player.getName() + " は既に入札済みです。");
            return;
        }
        
        // 入札を記録
        handleBid(player, amount);
        bids.put(playerId, amount);
        
        // 全員の入札が揃ったかチェック
        checkAllBidsReceived();
    }
    
    /**
     * プレイヤーIDからプレイヤーを取得
     */
    private Player getPlayerById(int playerId) {
        for (Player p : players) {
            if (p.getId() == playerId) {
                return p;
            }
        }
        return null;
    }

    /**
     * プレイヤーからの入札を処理する。
     */
    public void handleBid(Player player, int amount) {
        try {
            gameState.setBid(player, amount);  // 入札額をモデルに記録
            mainFrame.showMessage(player.getName() + " が入札しました。");
            
            // 入札完了を表示（金額は他のプレイヤーには見せない）
            mainFrame.showBidComplete(player);
        } catch (IllegalArgumentException e) {
            mainFrame.showMessage("入札エラー: " + e.getMessage());
        }
    }
    
    /**
     * 全員の入札が揃ったかチェック
     */
    private void checkAllBidsReceived() {
        if (bids.size() >= expectedBidCount) {
            // 全員の入札が揃った - 結果を発表
            mainFrame.showMessage("全員の入札が完了しました！");
            resolveAuction();
        } else {
            // まだ揃っていない
            mainFrame.showMessage("入札待ち: " + bids.size() + "/" + expectedBidCount + " 人完了");
        }
    }

    private void resolveAuction() {
        // 全員の入札額を公開
        StringBuilder bidResults = new StringBuilder("【入札結果】");
        for (Player p : players) {
            int bid = bids.getOrDefault(p.getId(), 0);
            bidResults.append(" ").append(p.getName()).append(":").append(bid).append("円");
        }
        mainFrame.showMessage(bidResults.toString());
        
        Player winner = gameState.resolveAuction();  // 勝者決定＋在庫・所持金更新
        
        if (winner == null) {
            // 全員同じ入札額 → 再入札
            mainFrame.showMessage("全員同じ入札額のため、再入札を行います！");
            mainFrame.showAuctionResultInCenterPanel(null, 0);  // 再入札メッセージ
            
            // 2秒後に再入札
            javax.swing.Timer retryTimer = new javax.swing.Timer(2000, e -> {
                bids.clear();
                expectedBidCount = players.size();
                mainFrame.showMessage("=== 再入札 ===");
                showAuctionForAllPlayers();
            });
            retryTimer.setRepeats(false);
            retryTimer.start();
            return;
        }
        
        int winningBid = gameState.getWinningBid();
        
        // CenterPanelに結果表示
        mainFrame.showAuctionResultInCenterPanel(winner, winningBid);
        
        mainFrame.updateAllPlayersState(gameState.getPlayers());
        mainFrame.updatePlayerInventory(winner);  // 勝者のインベントリを更新
        mainFrame.showMessage(winner.getName() + " がオークションに勝利しました！（" + winningBid + "円）");

        // 少し待ってから次のフェーズへ
        javax.swing.Timer timer = new javax.swing.Timer(2000, e -> {
            gameManager.onAuctionFinished();
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * トラックカードを取得（外部からの参照用）
     */
    public TruckCard getCurrentTruck() {
        return currentTruck;
    }
}
