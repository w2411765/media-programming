package com.speakeasy.model.alcohol;

/**
 * AlcoholCard
 * ----------------------------------------
 * 1本のお酒を表すカード。
 * 属性として「酒の種類（AlcoholType）」のみを持つ。
 *
 * ゲーム中では：
 * - 酒プール（山札）に存在
 * -   種類の指定
 * -      取得
 * - ラウンド開始時に12本公開される対象
 * - プレイヤーの在庫として保持せ
 * 
 */
public class AlcoholCard {

    /** 酒の種類 */
    private final AlcoholType type;

    /** 生成時に種類を指定する */
    public AlcoholCard(AlcoholType type) {
        this.type = type;
    }

    /** 酒カードの種類を取得する */
    public AlcoholType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "AlcoholCard{type=" + type + '}';
    }
}

