package com.speakeasy.model.alcohol;

import java.util.HashMap;
import java.util.Map;

/**
 * TruckCard
 * ------------------------------
 * 酒を複数本まとめて運ぶ「トラック1台分」を表すカード。
 *
 * ・AlcoholCard：酒1本
 * ・TruckCard  ：酒の詰め合わせ（種類→本数）
 *
 */
public class TruckCard {

    // トラックの積荷
    // 酒の種類 → 本数
    // 例：{BEER=3, WINE=1}
    private Map<AlcoholType, Integer> cargo = new HashMap<>();

    /**
     * コンストラクタ
     *
     * @param cargo トラックに積む酒（種類→本数）
     */
    public TruckCard(Map<AlcoholType, Integer> cargo) {

        // null のときは空の積荷として扱う
        if (cargo == null) return;

        // 渡された内容をコピーして入れる
        this.cargo.putAll(cargo);
    }

    public Map<AlcoholType, Integer> getCargo() {
        return cargo;
    }

    /**
     * ある酒が何本積まれているか取得
     *
     * @param type 酒の種類
     * @return 本数（ない場合は0）
     */
    public int getCount(AlcoholType type) {
        return cargo.getOrDefault(type, 0);
    }

    /**
     * トラックに酒を追加する
     *
     * @param type  酒の種類
     * @param count 追加する本数
     */
    public void addCargo(AlcoholType type, int count) {
        int current = cargo.getOrDefault(type, 0);
        cargo.put(type, current + count);
    }

    /**
     * デバッグ用
     */
    @Override
    public String toString() {
        return "TruckCard{cargo=" + cargo + "}";
    }
}
