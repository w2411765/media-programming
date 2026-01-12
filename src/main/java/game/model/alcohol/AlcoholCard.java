package com.speakeasy.model.alcohol;

/**
 * AlcoholCard
 * ------------------------------
 * 「酒1本」を表すカードクラス。
 *
 */
public class AlcoholCard {

    // このカードが表す酒の種類（BEER / WINE など）
    private AlcoholType type;

    /**
     * @param type 酒の種類
     */
    public AlcoholCard(AlcoholType type) {
        this.type = type;
    }

    /**
     * 酒の種類を取得
     */
    public AlcoholType getType() {
        return type;
    }

    /**
     * デバッグ用：カードの中身を文字列で見たいときに使う
     */
    @Override
    public String toString() {
        return "AlcoholCard{type=" + type + "}";
    }
}
