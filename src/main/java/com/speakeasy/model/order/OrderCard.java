package com.speakeasy.model.order;

import com.speakeasy.model.alcohol.AlcoholType;
import java.util.Map;

/**
 * OrderCard
 * ------------------------------
 * 客（注文）カードを表すクラス。
 *
 * ・どの酒を
 * ・何本渡せば
 * ・いくらお金がもらえるか
 *
 * という情報だけを持つ「データ用クラス」。
 */
public class OrderCard {

    // 注文に必要な酒の種類と本数
    // 例：{ BEER=2, WINE=1 }
    private Map<AlcoholType, Integer> required;

    // 注文を達成したときにもらえる金額
    private int reward;

    /**
     * コンストラクタ
     *
     * @param required 必要な酒の種類と本数
     * @param reward   注文達成時の報酬
     */
    public OrderCard(Map<AlcoholType, Integer> required, int reward) {

        // 注文内容が空や null のまま作られるのを防ぐ
        if (required == null || required.isEmpty()) {
            throw new IllegalArgumentException("required is empty");
        }

        this.required = required;
        this.reward = reward;
    }

    // 必要な酒の内容を取得
    public Map<AlcoholType, Integer> getRequired() {
        return required;
    }

    // 報酬金額を取得
    public int getReward() {
        return reward;
    }
}


