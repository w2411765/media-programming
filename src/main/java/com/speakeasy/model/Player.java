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

    // 酒の在庫
    // 酒の種類 → 本数
    private Map<AlcoholType, Integer> inventory = new HashMap<>();

    // 手元にある注文カード
    private List<OrderCard> orders = new ArrayList<>();

    /**
     * コンストラクタ
     *
     * @param id    プレイヤーID
     * @param money 初期所持金
     */
    public Player(int id, int money) {
        this.id = id;
        this.money = money;
    }

    /**
     * 酒を在庫に追加する
     *
     * @param type  酒の種類
     * @param count 追加する本数
     */
    public void addAlcohol(AlcoholType type, int count) {

        // 今持っている本数を取得（なければ0）
        int current = inventory.getOrDefault(type, 0);

        // 本数を増やす
        inventory.put(type, current + count);
    }

    /**
     * 注文を達成できるかどうかを確認
     *
     * @param order 注文カード
     * @return 達成可能なら true
     */
    public boolean canComplete(OrderCard order) {

        // 注文に必要な酒を1種類ずつチェック
        for (AlcoholType type : order.getRequired().keySet()) {

            int need = order.getRequired().get(type);      // 必要本数
            int have = inventory.getOrDefault(type, 0);    // 持っている本数

            // 1種類でも足りなければ達成不可
            if (have < need) {
                return false;
            }
        }
        return true;
    }

    /**
     * 注文を達成する処理
     *
     * ・必要な酒を在庫から減らす
     * ・報酬金額を所持金に加える
     */
    public void completeOrder(OrderCard order) {

        // 達成できない注文はエラー
        if (!canComplete(order)) {
            throw new IllegalStateException("not enough alcohol");
        }

        // 酒を消費
        for (AlcoholType type : order.getRequired().keySet()) {
            int remain = inventory.get(type) - order.getRequired().get(type);
            inventory.put(type, remain);
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
}
// 所持金、在庫、完了した注文
