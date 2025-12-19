// 取引・交渉のロジック
package game.controller;

import game.model.GameState;
import game.model.Player;
import game.view.MainFrame;

/**
 * プレイヤー間の取引フェーズを担当するクラス。
 * 取引提案と承諾／拒否を受け取り、GameState に反映させる。
 */
public class TradeManager {

    private final GameState gameState;
    private final MainFrame mainFrame;
    private final GameManager gameManager;

    public TradeManager(GameState gameState, MainFrame mainFrame,
                        GameManager gameManager) {
        this.gameState = gameState;
        this.mainFrame = mainFrame;
        this.gameManager = gameManager;
    }

    /**
     * 取引フェーズ開始時に呼び出す。
     */
    public void startTradePhase() {
        mainFrame.enableTradeUI(true);
        mainFrame.showMessage("取引フェーズです。必要なら他プレイヤーと酒の交換を行ってください。");
    }

    /**
     * 取引提案を処理する。
     * View 側のダイアログで入力された内容を TradeProposal として受け取る想定。
     */
    public void handleTradeProposed(Player from, Player to, TradeProposal proposal) {
        mainFrame.showTradeOfferDialog(from, to, proposal);
    }

    /**
     * 相手プレイヤーが取引に応答したときに呼び出す。
     */
    public void handleTradeResponse(TradeProposal proposal, boolean accepted) {
        if (accepted) {
            gameState.applyTrade(proposal); // 酒・お金の移動ロジックはモデル側に集約
            mainFrame.updateAllPlayersState(gameState.getPlayers());
            mainFrame.showMessage("取引が成立しました。");
        } else {
            mainFrame.showMessage("取引は拒否されました。");
        }
    }

    /**
     * View から「取引フェーズを終了する」操作があったときに呼ぶ。
     */
    public void endTradePhase() {
        mainFrame.enableTradeUI(false);
        gameManager.onTradePhaseFinished();
    }
}
