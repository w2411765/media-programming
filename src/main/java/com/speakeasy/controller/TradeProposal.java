package game.controller;

import java.util.Map;

import game.model.Player;
import game.model.alcohol.AlcoholType;

/**
 * 取引の内容を表すデータクラス。
 * from が to に対して「この酒とお金を渡すので、その代わりにこの酒とお金をください」と提案する。
 */
public class TradeProposal {

    private final Player from;
    private final Player to;

    // 渡す酒・受け取る酒（種類と本数）
    private final Map<AlcoholType, Integer> alcoholOffered;
    private final Map<AlcoholType, Integer> alcoholRequested;

    // 渡すお金・受け取るお金
    private final int moneyOffered;
    private final int moneyRequested;

    public TradeProposal(Player from,
                         Player to,
                         Map<AlcoholType, Integer> alcoholOffered,
                         Map<AlcoholType, Integer> alcoholRequested,
                         int moneyOffered,
                         int moneyRequested) {
        this.from = from;
        this.to = to;
        this.alcoholOffered = alcoholOffered;
        this.alcoholRequested = alcoholRequested;
        this.moneyOffered = moneyOffered;
        this.moneyRequested = moneyRequested;
    }

    public Player getFrom() { return from; }
    public Player getTo() { return to; }

    public Map<AlcoholType, Integer> getAlcoholOffered() { return alcoholOffered; }
    public Map<AlcoholType, Integer> getAlcoholRequested() { return alcoholRequested; }

    public int getMoneyOffered() { return moneyOffered; }
    public int getMoneyRequested() { return moneyRequested; }
}