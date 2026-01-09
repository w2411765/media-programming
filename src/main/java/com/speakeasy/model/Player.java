package com.speakeasy.model;

import com.speakeasy.model.alcohol.AlcoholType;
import com.speakeasy.model.order.OrderCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Player
 * ------------------------------
 * ゲームに参加する1人のプレイヤーを表すクラス。
 *
 * 管理する情報：
 * ・所持金
 * ・酒の在庫
 * ・手元の注文カード
 */
public class Player {

    // プレイヤーID（0,1,2 など）
    private int id;

    // 現在の所持金
    private int money;

    // 酒の在庫（酒の種類 → 本数）
    private Map<AlcoholType, Integer> inventory = new HashMap<>();

    // 手元にある注文カード
    private List<OrderCard> orders = new ArrayList<>();

    public Player(int id, int money) {
        this.id = id;
        this.money = money;
    }

    // 酒を在庫に追加する
    public void addAlcohol(AlcoholType type, int count) {
        int current = inventory.getOrDefault(type, 0);
        inventory.put(type, current + count);
    }

    // 注文を達成できるかどうかを確認
    public boolean canComplete(OrderCard order) {
        for (AlcoholType type : order.getRequired().keySet()) {
            int need = order.getRequired().get(type);
            int have = inventory.getOrDefault(type, 0);
            if (have < need) return false;
        }
        return true;
    }

    // 注文を達成する
    public void completeOrder(OrderCard order) {
        if (!canComplete(order)) {
            throw new IllegalStateException("not enough alcohol");
        }

        // 酒を消費
        for (AlcoholType type : order.getRequired().keySet()) {
            int remain = inventory.getOrDefault(type, 0) - order.getRequired().get(type);

            // 0本になったらMapから消す（表示が綺麗になる）
            if (remain <= 0) {
                inventory.remove(type);
            } else {
                inventory.put(type, remain);
            }
        }

        // お金をもらう
        money += order.getReward();

        // 注文カードを手元から削除
        orders.remove(order);
    }

    // ----- getter -----
    public int getMoney() {
        return money;
    }

    public Map<AlcoholType, Integer> getInventory() {
        return inventory;
    }

    public List<OrderCard> getOrders() {
        return orders;
    }

    // 追加：注文カードを配る用（Controllerが使う）
    public void addOrder(OrderCard order) {
        orders.add(order);
    }

    // 追加：ラウンド開始時に手札を入れ替える用（Controllerが使う）
    public void clearOrders() {
        orders.clear();
    }
}
