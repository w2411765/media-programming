// 全体の進行（ラウンド管理、勝敗判定）
package game.controller;

import game.model.GameState;
import game.model.Player;
import game.model.order.OrderCard;
import game.view.MainFrame;

public class GameManager {

    /**
     * 現在のフェーズ。
     * DEAL_CUSTOMERS -> AUCTION -> TRADE -> SERVE -> END_ROUND -> (次ラウンド) DEAL_CUSTOMERS ...
     */
    public enum Phase {
        DEAL_CUSTOMERS,  // 客カード配布フェーズ
        AUCTION,         // オークションフェーズ
        TRADE,           // プレイヤー間取引フェーズ
        SERVE,           // 客に提供するフェーズ
        END_ROUND        // ラウンド終了・勝利判定
    }

    private final GameState gameState;
    private final MainFrame mainFrame;
    private final AuctionManager auctionManager;
    private final TradeManager tradeManager;

    private Phase phase = Phase.DEAL_CUSTOMERS;

    public GameManager(GameState gameState, MainFrame mainFrame) {
        this.gameState = gameState;
        this.mainFrame = mainFrame;

        this.auctionManager = new AuctionManager(gameState, mainFrame, this);
        this.tradeManager = new TradeManager(gameState, mainFrame, this);
    }

    /**
     * ゲーム開始時に呼び出す。
     */
    public void startGame() {
        gameState.reset();               // 山札・プレイヤー状態などを初期化しておく想定
        startDealPhase();
    }

    // ---------------- フェーズごとの開始メソッド ----------------

    private void startDealPhase() {
        phase = Phase.DEAL_CUSTOMERS;

        // 各プレイヤーに客カードを4枚ずつ配る（山札から引く処理は GameState 側）
        gameState.dealCustomerCardsToAllPlayers(4);

        // 画面更新
        mainFrame.updateRoundInfo(gameState.getRoundNumber());
        mainFrame.updateAllPlayersState(gameState.getPlayers());

        // 客カード配布が終わったらオークションへ
        startAuctionPhase();
    }

    private void startAuctionPhase() {
        phase = Phase.AUCTION;

        auctionManager.startAuction();

        // View 側では表示のみ行い、入札額が入力されたら
        // MainFrame -> GameManager#onBidSubmitted(...) が呼ばれる想定。
    }

    public void onAuctionFinished() {
        // AuctionManager から呼び出されるコールバック
        startTradePhase();
    }

    private void startTradePhase() {
        phase = Phase.TRADE;

        tradeManager.startTradePhase();
        // 取引ダイアログ等は TradeManager / MainFrame が開く。
    }

    public void onTradePhaseFinished() {
        startServePhase();
    }

    private void startServePhase() {
        phase = Phase.SERVE;

        mainFrame.enableServeUI(true);
        // 「客に提供する」ボタンなどを有効化して、提供要求が来たら
        // GameManager#onServeCustomerRequested(...) を呼んでもらう。
    }

    private void finishRound() {
        phase = Phase.END_ROUND;

        if (gameState.isGameOver()) {
            Player winner = gameState.getWinner();
            mainFrame.showGameOverDialog(winner);
        } else {
            gameState.proceedToNextRound();
            startDealPhase();
        }
    }

    // ---------------- View から呼ばれるイベントハンドラ ----------------

    /**
     * View で入札額が入力されたときに呼び出してもらう。
     */
    public void onBidSubmitted(Player player, int amount) {
        if (phase != Phase.AUCTION) {
            return;
        }
        auctionManager.handleBid(player, amount);
    }

    /**
     * 取引フェーズで「取引を提案する」操作があったときに呼び出してもらう。
     */
    public void onTradeProposed(Player from, Player to,
                                TradeProposal proposal) {
        if (phase != Phase.TRADE) {
            return;
        }
        tradeManager.handleTradeProposed(from, to, proposal);
    }

    /**
     * 取引に対する承諾／拒否が行われたとき。
     */
    public void onTradeResponse(TradeProposal proposal, boolean accepted) {
        if (phase != Phase.TRADE) {
            return;
        }
        tradeManager.handleTradeResponse(proposal, accepted);
    }

    /**
     * 取引フェーズを終了するボタンから呼び出してもらう。
     */
    public void onEndTradePhaseRequested() {
        if (phase != Phase.TRADE) {
            return;
        }
        onTradePhaseFinished();
    }

    /**
     * 「この客に提供する」操作が行われたとき。
     */
    public void onServeCustomerRequested(Player player, OrderCard orderCard) {
        if (phase != Phase.SERVE) {
            return;
        }

        boolean success = gameState.serveCustomer(player, orderCard);
        if (success) {
            mainFrame.updateAllPlayersState(gameState.getPlayers());
            mainFrame.showMessage(player.getName() + " が会計を完了しました。");
        } else {
            mainFrame.showMessage("必要な酒が足りません。");
        }
    }

    /**
     * 提供フェーズを終了するボタンから呼び出してもらう。
     */
    public void onEndServePhaseRequested() {
        if (phase != Phase.SERVE) {
            return;
        }
        finishRound();
    }

    // 現在フェーズをView側から参照したい場合用
    public Phase getPhase() {
        return phase;
    }
}