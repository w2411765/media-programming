// 入札処理のロジック
package game.controller;

import game.model.GameState;
import game.model.Player;
import game.model.alcohol.TruckCard;
import game.view.MainFrame;

/**
 * オークションフェーズの進行を担当するクラス。
 * 入札額の受付と、GameState への委譲を行う。
 */
public class AuctionManager {

    private final GameState gameState;
    private final MainFrame mainFrame;
    private final GameManager gameManager;

    public AuctionManager(GameState gameState, MainFrame mainFrame,
                          GameManager gameManager) {
        this.gameState = gameState;
        this.mainFrame = mainFrame;
        this.gameManager = gameManager;
    }

    /** 1ラウンド分のオークションを開始する。 */
    public void startAuction() {
        // 人数×4 本の酒をまとめた TruckCard を1枚用意する想定
        TruckCard truck = gameState.prepareTruckCardForAuction();

        mainFrame.showAuctionTruck(truck);
        mainFrame.showMessage("オークションを開始します。入札額を入力してください。");
    }

    /**
     * プレイヤーからの入札を処理する。
     */
    public void handleBid(Player player, int amount) {
        gameState.setBid(player, amount);  // 入札額をモデルに記録
        if (gameState.allBidsSubmitted()) {
            resolveAuction();
        }
    }

    private void resolveAuction() {
        Player winner = gameState.resolveAuction();  // 勝者決定＋在庫・所持金更新
        mainFrame.updateAllPlayersState(gameState.getPlayers());
        mainFrame.showAuctionResult(winner);         // TruckCard は GameState 側で消費済み想定
        mainFrame.showMessage(winner.getName() + " がオークションに勝利しました。");

        gameManager.onAuctionFinished();
    }
}
