// 全体の進行（ラウンド管理、勝敗判定）
package game.controller;

import game.model.GameState;
import game.model.Phase;
import game.model.Player;
import game.model.order.OrderCard;
import game.view.MainFrame;

public class GameManager {

    private final GameState gameState;
    private final MainFrame mainFrame;
    private final AuctionManager auctionManager;
    private final TradeManager tradeManager;
    private final PenaltyManager penaltyManager;

    public GameManager(GameState gameState, MainFrame mainFrame) {
        this.gameState = gameState;
        this.mainFrame = mainFrame;

        this.auctionManager = new AuctionManager(gameState, mainFrame, this);
        this.tradeManager = new TradeManager(gameState, mainFrame, this);
        this.penaltyManager = new PenaltyManager(gameState, mainFrame, this);
    }

    /**
     * ゲーム開始時に呼び出す。
     */
    public void startGame() {
        // 山札・プレイヤー状態などを初期化しておく想定
        gameState.reset();
        
        // ゲーム開始表示（2秒）
        mainFrame.showGameStart();
        mainFrame.showMessage("=== ゲーム開始 ===");
        
        javax.swing.Timer startTimer = new javax.swing.Timer(2000, e -> {
            showRoundStartAndBegin();
        });
        startTimer.setRepeats(false);
        startTimer.start();
    }
    
    /**
     * ラウンド開始表示を出してからDealPhaseを開始
     */
    private void showRoundStartAndBegin() {
        int round = gameState.getRoundNumber();
        mainFrame.showRoundStart(round);
        mainFrame.showMessage("=== ラウンド " + round + " ===");
        
        javax.swing.Timer roundTimer = new javax.swing.Timer(2000, e -> {
            startDealPhase();
        });
        roundTimer.setRepeats(false);
        roundTimer.start();
    }

    // ---------------- フェーズごとの開始メソッド ----------------

    /**
     * 注文（客カード）配布フェーズの開始。
     * ORDER_DISTRIBUTION フェーズに対応。
     */
    private void startDealPhase() {
        gameState.setPhase(Phase.ORDER_DISTRIBUTION);

        // 各プレイヤーに客カードを4枚ずつ配る（山札から引く処理は GameState 側）
        gameState.dealCustomerCardsToAllPlayers(4);

        // 画面更新
        mainFrame.updateRoundInfo(gameState.getRoundNumber());
        mainFrame.updateAllPlayersState(gameState.getPlayers());
        
        // 自分の手札をCustomerPanelに表示
        Player myPlayer = getMyPlayer();
        if (myPlayer != null) {
            mainFrame.updatePlayerOrders(myPlayer);
        }

        // 客カード配布が終わったらオークションへ
        startAuctionPhase();
    }
    
    /**
     * このビューの所有者プレイヤー（自分）を取得
     */
    private Player getMyPlayer() {
        int myPlayerId = mainFrame.getMyPlayerId();
        for (Player p : gameState.getPlayers()) {
            if (p.getId() == myPlayerId) {
                return p;
            }
        }
        return null;
    }

    /**
     * オークションフェーズの開始。
     * Phase.AUCTION に対応。
     */
    private void startAuctionPhase() {
        gameState.setPhase(Phase.AUCTION);

        auctionManager.startAuction();

        // View 側では表示のみ行い、入札額が入力されたら
        // MainFrame -> GameManager#onBidSubmitted(...) が呼ばれる想定。
    }

    /**
     * オークション終了時に AuctionManager から呼ばれるコールバック。
     */
    public void onAuctionFinished() {
        startTradePhase();
    }

    /**
     * プレイヤー間取引フェーズの開始。
     * Phase.TRADE に対応。
     */
    private void startTradePhase() {
        gameState.setPhase(Phase.TRADE);

        tradeManager.startTradePhase();
        // 取引ダイアログ等は TradeManager / MainFrame が開く。
    }

    /**
     * 取引フェーズ終了後に呼ばれる。
     * ペナルティイベント1をチェックしてから提供フェーズへ
     */
    public void onTradePhaseFinished() {
        // ペナルティイベント1をチェック（10本超のお酒所持）
        penaltyManager.checkAndExecutePenalty1(() -> {
            startServePhase();
        });
    }

    /**
     * 客に提供するフェーズの開始。
     * Phase.SERVE に対応。
     */
    private void startServePhase() {
        gameState.setPhase(Phase.SERVE);

        mainFrame.enableServeUI(true);
        // 「客に提供する」ボタンなどを有効化して、提供要求が来たら
        // GameManager#onServeCustomerRequested(...) を呼んでもらう。
    }

    /**
     * ラウンド終了処理とゲーム終了判定。
     * Phase.END に一時的に遷移させるが、ゲーム継続時は
     * 次のラウンドの ORDER_DISTRIBUTION へ進む。
     */
    private void finishRound() {
        gameState.setPhase(Phase.END);

        if (gameState.isGameOver()) {
            // ゲーム終了時はペナルティ2を実行してから勝者表示
            penaltyManager.executePenalty2(() -> {
                Player winner = gameState.getWinner();
                mainFrame.showGameOverDialog(winner);
            });
        } else {
            gameState.proceedToNextRound();
            // 次のラウンド表示を出してから開始
            showRoundStartAndBegin();
        }
    }

    // ---------------- View から呼ばれるイベントハンドラ ----------------

    /**
     * View で入札額が入力されたときに呼び出してもらう。
     */
    public void onBidSubmitted(Player player, int amount) {
        if (gameState.getPhase() != Phase.AUCTION) {
            return;
        }
        auctionManager.handleBid(player, amount);
    }

    /**
     * 取引フェーズで「取引を提案する」操作があったときに呼び出してもらう。
     */
    public void onTradeProposed(Player from, Player to,
                                TradeProposal proposal) {
        if (gameState.getPhase() != Phase.TRADE) {
            return;
        }
        tradeManager.handleTradeProposed(from, to, proposal);
    }

    /**
     * 取引に対する承諾／拒否が行われたとき。
     */
    public void onTradeResponse(TradeProposal proposal, boolean accepted) {
        if (gameState.getPhase() != Phase.TRADE) {
            return;
        }
        tradeManager.handleTradeResponse(proposal, accepted);
    }

    /**
     * 取引フェーズを終了するボタンから呼び出してもらう。
     */
    public void onEndTradePhaseRequested() {
        if (gameState.getPhase() != Phase.TRADE) {
            return;
        }
        onTradePhaseFinished();
    }

    /**
     * 「この客に提供する」操作が行われたとき。
     */
    public void onServeCustomerRequested(Player player, OrderCard orderCard) {
        if (gameState.getPhase() != Phase.SERVE) {
            return;
        }

        boolean success = gameState.serveCustomer(player, orderCard);
        if (success) {
            mainFrame.updateAllPlayersState(gameState.getPlayers());
            mainFrame.showMessage(player.getName() + " が会計を完了しました。");
            
            // 自分の場合、CustomerPanelを更新
            if (mainFrame.isMyPlayer(player)) {
                mainFrame.updatePlayerOrders(player);
            }
        } else {
            mainFrame.showMessage("必要な酒が足りません。");
        }
    }

    /**
     * 提供フェーズを終了するボタンから呼び出してもらう。
     */
    public void onEndServePhaseRequested() {
        if (gameState.getPhase() != Phase.SERVE) {
            return;
        }
        finishRound();
    }

    // 現在フェーズを View 側から参照したい場合用
    public Phase getPhase() {
        return gameState.getPhase();
    }
}