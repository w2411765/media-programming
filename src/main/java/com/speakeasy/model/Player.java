package com.speakeasy.model;

import com.speakeasy.model.alcohol.AlcoholType;
import com.speakeasy.model.order.OrderCard;

import java.util.*;

/**
 * Player
 * ----------------------------------------
 * ゲームに参加する1人のプレイヤーを表すクラス。
 *
 * 管理する情報：
 * ・所持金
 * ・酒の在庫（種類 → 本数）
 * ・手元の注文カード
 * ・達成済みの注文カード
 *
 * プレイヤー単体で完結する処理（在庫操作、注文達成）はここに実装している。
 */
public class Player {

    /** プレイヤーID（0,1,2 など） */
    private final int id;

    /** 現在の所持金 */
    private int money;

    /**
     * 酒の在庫
     *  酒の種類（AlcoholType）→ 本数
     */
    private final EnumMap<AlcoholType, Integer> inventory
            = new EnumMap<>(AlcoholType.class);

    /** 現在手元にある注文カード */
    private final List<OrderCard> orders = new ArrayList<>();

    /** 達成済みの注文カード（得点・実績用） */
    private final List<OrderCard> completedOrders = new ArrayList<>();

    /**
     * コンストラクタ
     *
     * @param id
     *  プレイヤーID
     * @param initialMoney
     *  初期所持金
     */
    public Player(int id, int initialMoney) {
        if (id < 0) throw new IllegalArgumentException("id must be >= 0");
        if (initialMoney < 0) throw new IllegalArgumentException("initialMoney must be >= 0");
        this.id = id;
        this.money = initialMoney;
    }

    // ---------- 基本情報 ----------

    public int getId() {
        return id;
    }

    public int getMoney() {
        return money;
    }

    public Map<AlcoholType, Integer> getInventoryView() {
        return Collections.unmodifiableMap(inventory);
    }

    public List<OrderCard> getOrdersView() {
        return Collections.unmodifiableList(orders);
    }

    public List<OrderCard> getCompletedOrdersView() {
        return Collections.unmodifiableList(completedOrders);
    }

    // ---------- 所持金操作 ----------

    /** 所持金を増やす */
    public void addMoney(int amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must be >= 0");
        money += amount;
    }

    /** 指定金額を支払えるか */
    public boolean canPay(int amount) {
        if (amount < 0) throw new IllegalArgumentException("amount must be >= 0");
        return money >= amount;
    }

    /** 所持金を支払う */
    public void payMoney(int amount) {
        if (!canPay(amount)) {
            throw new IllegalStateException("not enough money");
        }
        money -= amount;
    }

    // ---------- 在庫操作 ----------

    /** 指定の酒を何本持っているか */
    public int getAlcoholCount(AlcoholType type) {
        return inventory.getOrDefault(type, 0);
    }

    /** 酒を在庫に追加 */
    public void addAlcohol(AlcoholType type, int count) {
        if (count < 0) throw new IllegalArgumentException("count must be >= 0");
        inventory.put(type, getAlcoholCount(type) + count);
    }

    /** 指定本数の酒を持っているか */
    public boolean hasAlcohol(AlcoholType type, int count) {
        return getAlcoholCount(type) >= count;
    }

    /** 酒を在庫から減らす */
    public void removeAlcohol(AlcoholType type, int count) {
        if (!hasAlcohol(type, count)) {
            throw new IllegalStateException("not enough alcohol");
        }
        int next = getAlcoholCount(type) - count;
        if (next == 0) inventory.remove(type);
        else inventory.put(type, next);
    }

    // ---------- 注文処理 ----------

    /** 注文を達成できるか判定 */
    public boolean canComplete(OrderCard order) {
        for (var e : order.getRequiredView().entrySet()) {
            if (!hasAlcohol(e.getKey(), e.getValue())) return false;
        }
        return true;
    }

    /**
     * 注文を達成する
     * ・必要な酒を消費
     * ・報酬を所持金に加算
     * ・注文カードを達成済みに移動
     */
    public void completeOrder(int orderIndex) {
        OrderCard order = orders.get(orderIndex);

        if (!canComplete(order)) {
            throw new IllegalStateException("cannot complete order");
        }

        for (var e : order.getRequiredView().entrySet()) {
            removeAlcohol(e.getKey(), e.getValue());
        }

        addMoney(order.getReward());

        orders.remove(orderIndex);
        completedOrders.add(order);
    }

    // ---------- 注文カード操作 ----------

    public void addOrder(OrderCard card) {
        orders.add(card);
    }

    public void clearOrders() {
        orders.clear();
    }

    @Override
    public String toString() {
        return "Player{id=" + id +
                ", money=" + money +
                ", inventory=" + inventory +
                ", orders=" + orders.size() +
                ", completed=" + completedOrders.size() + "}";
    }
}
