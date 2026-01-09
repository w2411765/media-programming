package com.speakeasy.model.order;

import com.speakeasy.model.alcohol.AlcoholType;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * OrderCard
 * ----------------------------------------
 * 客カードを表すクラス。
 *
 * ・どの酒を何本要求するか
 * ・その注文を達成したときの報酬金額
 *
 */
public class OrderCard {

    /**
     * 注文に必要な酒の内容
     *  酒の種類（AlcoholType）→ 必要本数
     *
     * 例：
     *  {BEER=2, WINE=1}
     */
    private final EnumMap<AlcoholType, Integer> required;

    /**
     * 注文を達成したときに得られる報酬金額
     */
    private final int reward;

    /**
     * コンストラクタ
     *
     * @param required
     *  注文に必要な酒の種類と本数
     * @param reward
     *  注文達成時の報酬金額
     */
    public OrderCard(Map<AlcoholType, Integer> required, int reward) {

        // null チェック
        Objects.requireNonNull(required, "required must not be null");

        // 報酬は負にならないよう制限
        if (reward < 0) {
            throw new IllegalArgumentException("reward must be >= 0");
        }

        // EnumMap を使って内容をコピー
        this.required = new EnumMap<>(AlcoholType.class);

        for (var entry : required.entrySet()) {
            // 酒の種類が null でないか
            if (entry.getKey() == null) {
                throw new IllegalArgumentException("alcohol type must not be null");
            }

            // 必要本数が正の値であることを保証
            if (entry.getValue() == null || entry.getValue() <= 0) {
                throw new IllegalArgumentException("required amount must be > 0");
            }

            this.required.put(entry.getKey(), entry.getValue());
        }

        // 注文内容が空である場合をチェックする
        if (this.required.isEmpty()) {
            throw new IllegalArgumentException("required must not be empty");
        }

        this.reward = reward;
    }

    /**
     * 注文に必要な酒の内容を取得
     */
    public Map<AlcoholType, Integer> getRequiredView() {
        return Collections.unmodifiableMap(required);
    }

    /**
     * 注文達成時の報酬金額を取得
     */
    public int getReward() {
        return reward;
    }

    /**
     * デバッグ・ログ用の文字列表現
     */
    @Override
    public String toString() {
        return "OrderCard{required=" + required + ", reward=" + reward + "}";
    }
}
