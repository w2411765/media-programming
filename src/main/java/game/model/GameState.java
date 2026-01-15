package game.model;

import game.model.alcohol.AlcoholCard;
import java.util.ArrayList;
import java.util.List;

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

    // ----- 最低限のスタブメソッド（実装は後で追加） -----

    public void reset() {
        // TODO: 実装が必要
    }

    public void dealCustomerCardsToAllPlayers(int count) {
        // TODO: 実装が必要
    }

    public boolean isGameOver() {
        // TODO: 実装が必要
        return false;
    }

    public Player getWinner() {
        // TODO: 実装が必要
        return null;
    }

    public boolean serveCustomer(Player player, game.model.order.OrderCard orderCard) {
        // TODO: 実装が必要
        return false;
    }

    public game.model.alcohol.TruckCard prepareTruckCardForAuction() {
        // TODO: 実装が必要
        return null;
    }

    public void setBid(Player player, int amount) {
        // TODO: 実装が必要
    }

    public boolean allBidsSubmitted() {
        // TODO: 実装が必要
        return false;
    }

    public Player resolveAuction() {
        // TODO: 実装が必要
        return null;
    }

    public void applyTrade(game.controller.TradeProposal proposal) {
        // TODO: 実装が必要
    }
}

