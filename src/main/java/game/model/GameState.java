package com.speakeasy.model;

import com.speakeasy.model.alcohol.AlcoholCard;
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

    // 次のラウンドへ進む
    public void nextRound() {
        round++;
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
}

