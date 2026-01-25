package game.model;

import game.model.alcohol.AlcoholCard;
import game.model.alcohol.AlcoholType;
import game.model.alcohol.TruckCard;
import game.model.order.Deck;
import game.model.order.OrderCard;
import game.util.Constants;
import game.util.OrderCardLoader;
import game.controller.TradeProposal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * GameState
 * ------------------------------
 * ゲーム全体の「現在の状態」をまとめて管理するクラス。
 *
 * ・ラウンド数
 * ・現在のフェーズ
 * ・プレイヤー一覧
 * ・マーケットに出ている酒
 *
 */
public class GameState {

    // 現在のラウンド数
    private int round = 1;

    // 現在のフェーズ
    private Phase phase = Phase.ORDER_DISTRIBUTION;

    // プレイヤー一覧
    private List<Player> players;

    // マーケットに公開されている酒カード
    private List<AlcoholCard> market = new ArrayList<>();
    
    // 注文カードの山札
    private Deck<OrderCard> orderDeck = new Deck<>();
    
    // 現在のオークション用トラックカード
    private TruckCard currentTruckCard;
    
    // プレイヤーの入札額（プレイヤー → 入札額）
    private Map<Player, Integer> bids = new HashMap<>();
    
    // 最後のオークションの勝利入札額
    private int lastWinningBid = 0;
    
    // 乱数生成器
    private Random random = new Random();

    /**
     * コンストラクタ
     *
     * @param players プレイヤー一覧
     */
    public GameState(List<Player> players) {
        this.players = players;
    }

    // ----- ラウンド管理 -----

    public int getRound() {
        return round;
    }

    // getRoundNumber()のエイリアス（既存コードとの互換性のため）
    public int getRoundNumber() {
        return round;
    }

    // 次のラウンドへ進む
    public void nextRound() {
        round++;
    }

    // proceedToNextRound()のエイリアス（既存コードとの互換性のため）
    public void proceedToNextRound() {
        nextRound();
    }

    // ----- フェーズ管理 -----

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    // ----- 状態取得 -----

    public List<Player> getPlayers() {
        return players;
    }

    public List<AlcoholCard> getMarket() {
        return market;
    }

    // ----- ゲーム初期化・リセット -----

    /**
     * ゲームを初期化する
     */
    public void reset() {
        round = 1;
        phase = Phase.ORDER_DISTRIBUTION;
        
        // プレイヤーの状態を初期化
        for (Player player : players) {
            // 所持金を初期値にリセット
            player.setMoney(Constants.INITIAL_MONEY);
            // 在庫をクリア
            player.getInventory().clear();
            // 手札をクリア
            player.clearOrders();
        }
        
        // 山札を初期化
        initializeOrderDeck();
        
        // 入札情報をクリア
        bids.clear();
        currentTruckCard = null;
    }
    
    /**
     * 注文カードの山札を初期化する
     * JSONファイルからカードを読み込み、設定に応じて山札を構築する
     */
    private void initializeOrderDeck() {
        orderDeck = new Deck<>();
        
        // JSONからカードを読み込む
        List<OrderCard> loadedCards = OrderCardLoader.loadFromJson(Constants.CUSTOMER_JSON_PATH);
        
        if (loadedCards.isEmpty()) {
            // JSONが読み込めなかった場合はフォールバック（ランダム生成）
            System.out.println("JSONからの読み込みに失敗したため、ランダムカードを生成します");
            generateRandomOrderCards();
        } else {
            // 各カードを複製して山札に追加
            int copiesPerCustomer = Constants.COPIES_PER_CUSTOMER;
            int maxCards = Constants.MAX_ORDER_CARDS;
            int addedCount = 0;
            
            for (OrderCard card : loadedCards) {
                for (int i = 0; i < copiesPerCustomer; i++) {
                    // 最大枚数のチェック（-1は無制限）
                    if (maxCards > 0 && addedCount >= maxCards) {
                        break;
                    }
                    
                    // 新しいインスタンスを作成して追加
                    OrderCard copy = new OrderCard(
                        card.getCustomerName(),
                        new HashMap<>(card.getRequired()),
                        card.getReward()
                    );
                    orderDeck.add(copy);
                    addedCount++;
                }
                
                if (maxCards > 0 && addedCount >= maxCards) {
                    break;
                }
            }
            
            System.out.println("山札に追加したカード数: " + addedCount);
        }
        
        orderDeck.shuffle();
    }
    
    /**
     * フォールバック用：ランダムな注文カードを生成する
     */
    private void generateRandomOrderCards() {
        String[] names = {"お客さん", "常連客", "新規客", "VIP", "紳士", "淑女"};
        
        for (int i = 0; i < 20; i++) {
            Map<AlcoholType, Integer> required = new HashMap<>();
            AlcoholType[] types = AlcoholType.values();
            
            // ランダムに1-3種類の酒を選択
            int numTypes = random.nextInt(3) + 1;
            for (int j = 0; j < numTypes; j++) {
                AlcoholType type = types[random.nextInt(types.length)];
                int count = random.nextInt(3) + 1; // 1-3本
                required.put(type, required.getOrDefault(type, 0) + count);
            }
            
            // 報酬は必要本数×10 + 基本報酬
            int totalBottles = required.values().stream().mapToInt(Integer::intValue).sum();
            int reward = totalBottles * 10 + 20;
            
            String name = names[random.nextInt(names.length)];
            orderDeck.add(new OrderCard(name, required, reward));
        }
    }

    /**
     * 全プレイヤーに注文カードを配布する
     * @param count 各プレイヤーに配る枚数
     */
    public void dealCustomerCardsToAllPlayers(int count) {
        for (Player player : players) {
            player.clearOrders();
            for (int i = 0; i < count; i++) {
                if (orderDeck.size() > 0) {
                    OrderCard card = orderDeck.draw();
                    player.addOrder(card);
                }
            }
        }
    }

    /**
     * ゲーム終了条件をチェック
     * @return ゲーム終了ならtrue
     */
    public boolean isGameOver() {
        // 勝利条件：誰かが目標金額に達したか
        for (Player player : players) {
            if (player.getMoney() >= Constants.WIN_CONDITION_MONEY) {
                return true;
            }
        }
        
        // 最大ラウンド数のチェック（-1の場合は制限なし）
        if (Constants.MAX_ROUNDS > 0 && round > Constants.MAX_ROUNDS) {
            return true;
        }
        
        return false;
    }

    /**
     * 勝者を取得する
     * @return 最も所持金が多いプレイヤー
     */
    public Player getWinner() {
        Player winner = null;
        int maxMoney = -1;
        
        for (Player player : players) {
            if (player.getMoney() > maxMoney) {
                maxMoney = player.getMoney();
                winner = player;
            }
        }
        
        return winner;
    }

    /**
     * プレイヤーが客に提供する
     * @param player 提供するプレイヤー
     * @param orderCard 提供する注文カード
     * @return 提供成功ならtrue
     */
    public boolean serveCustomer(Player player, OrderCard orderCard) {
        if (player.canComplete(orderCard)) {
            player.completeOrder(orderCard);
            return true;
        }
        return false;
    }

    /**
     * オークション用のトラックカードを準備する
     * @return 準備されたトラックカード
     */
    public TruckCard prepareTruckCardForAuction() {
        Map<AlcoholType, Integer> cargo = new HashMap<>();
        int totalBottles = players.size() * Constants.ALCOHOL_PER_PLAYER;
        AlcoholType[] types = AlcoholType.values();
        
        // ランダムに酒を分配
        for (int i = 0; i < totalBottles; i++) {
            AlcoholType type = types[random.nextInt(types.length)];
            cargo.put(type, cargo.getOrDefault(type, 0) + 1);
        }
        
        currentTruckCard = new TruckCard(cargo);
        return currentTruckCard;
    }

    /**
     * プレイヤーの入札額を設定する
     * @param player 入札するプレイヤー
     * @param amount 入札額
     */
    public void setBid(Player player, int amount) {
        // 所持金チェック
        if (player.getMoney() < amount) {
            throw new IllegalArgumentException("所持金が足りません: " + player.getName() + " 所持金=" + player.getMoney() + " 入札額=" + amount);
        }
        
        bids.put(player, amount);
    }

    /**
     * 全プレイヤーが入札したかチェック
     * @return 全員入札済みならtrue
     */
    public boolean allBidsSubmitted() {
        return bids.size() == players.size();
    }

    /**
     * オークションを解決し、勝者を決定する
     * @return オークションの勝者
     */
    public Player resolveAuction() {
        if (currentTruckCard == null) {
            throw new IllegalStateException("トラックカードが準備されていません");
        }
        
        if (!allBidsSubmitted()) {
            throw new IllegalStateException("全員が入札していません");
        }
        
        // 最高入札額のプレイヤーを探す
        Player winner = null;
        int maxBid = -1;
        
        for (Map.Entry<Player, Integer> entry : bids.entrySet()) {
            if (entry.getValue() > maxBid) {
                maxBid = entry.getValue();
                winner = entry.getKey();
            }
        }
        
        if (winner == null) {
            throw new IllegalStateException("勝者が決定できませんでした");
        }
        
        // 勝者の所持金から入札額を減算
        winner.payMoney(maxBid);
        
        // 勝利入札額を保存
        lastWinningBid = maxBid;
        
        // トラックカードの酒を勝者の在庫に追加
        for (Map.Entry<AlcoholType, Integer> entry : currentTruckCard.getCargo().entrySet()) {
            winner.addAlcohol(entry.getKey(), entry.getValue());
        }
        
        // 入札情報をクリア
        bids.clear();
        currentTruckCard = null;
        
        return winner;
    }
    
    /**
     * 最後のオークションの勝利入札額を取得
     * @return 勝利入札額
     */
    public int getWinningBid() {
        return lastWinningBid;
    }

    /**
     * 取引を適用する
     * @param proposal 取引提案
     */
    public void applyTrade(TradeProposal proposal) {
        Player from = proposal.getFrom();
        Player to = proposal.getTo();
        
        // 提案者の酒を減らし、相手に追加
        for (Map.Entry<AlcoholType, Integer> entry : proposal.getAlcoholOffered().entrySet()) {
            from.removeAlcohol(entry.getKey(), entry.getValue());
            to.addAlcohol(entry.getKey(), entry.getValue());
        }
        
        // 相手の酒を減らし、提案者に追加
        for (Map.Entry<AlcoholType, Integer> entry : proposal.getAlcoholRequested().entrySet()) {
            to.removeAlcohol(entry.getKey(), entry.getValue());
            from.addAlcohol(entry.getKey(), entry.getValue());
        }
        
        // お金の交換
        from.payMoney(proposal.getMoneyOffered());
        to.receiveMoney(proposal.getMoneyOffered());
        
        to.payMoney(proposal.getMoneyRequested());
        from.receiveMoney(proposal.getMoneyRequested());
    }
}

