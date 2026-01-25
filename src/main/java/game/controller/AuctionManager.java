// 入札処理のロジック
package game.controller;

import game.model.GameState;
import game.model.Player;
import game.model.alcohol.TruckCard;
import game.view.MainFrame;

import java.util.List;

/**
 * オークションフェーズの進行を担当するクラス。
 * 入札額の受付と、GameState への委譲を行う。
 */
public class AuctionManager {

    private final GameState gameState;
    private final MainFrame mainFrame;
    private final GameManager gameManager;
    
    private TruckCard currentTruck;
    private int currentPlayerIndex = 0;
    private List<Player> players;

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
        currentPlayerIndex = 0;

        mainFrame.showMessage("オークションを開始します。入札額を入力してください。");
        
        // CenterPanelにオークション表示を開始
        showBidForCurrentPlayer();
    }
    
    /**
     * 現在のプレイヤーの入札UIを表示
     */
    private void showBidForCurrentPlayer() {
        if (currentPlayerIndex >= players.size()) {
            // 全員の入札が完了
            resolveAuction();
            return;
        }
        
        Player currentPlayer = players.get(currentPlayerIndex);
        
        if (currentPlayerIndex == 0) {
            // 最初のプレイヤー - トラックカードも表示
            mainFrame.showAuctionInCenterPanel(currentTruck, currentPlayer, this::onBidReceived);
        } else {
            // 2番目以降 - 入札パネルのみ更新
            mainFrame.showBidForPlayer(currentPlayer, this::onBidReceived);
        }
    }
    
    /**
     * 入札を受け取ったときのコールバック
     */
    private void onBidReceived(int amount) {
        Player player = players.get(currentPlayerIndex);
        handleBid(player, amount);
        
        // 次のプレイヤーへ
        currentPlayerIndex++;
        showBidForCurrentPlayer();
    }

    /**
     * プレイヤーからの入札を処理する。
     */
    public void handleBid(Player player, int amount) {
        try {
            gameState.setBid(player, amount);  // 入札額をモデルに記録
            mainFrame.showMessage(player.getName() + " が " + amount + "円 で入札しました。");
        } catch (IllegalArgumentException e) {
            mainFrame.showMessage("入札エラー: " + e.getMessage());
        }
    }

    private void resolveAuction() {
        Player winner = gameState.resolveAuction();  // 勝者決定＋在庫・所持金更新
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
}
