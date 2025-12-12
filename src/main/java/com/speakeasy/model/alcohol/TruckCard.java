package com.speakeasy.model.alcohol;

/**
 * TruckCard
 * ----------------------------------------
 * 複数の AlcoholCard をまとめて保持するデータ構造。
 *
 * 今回は1セット12本の酒をまとめた構造として利用する。
 *
 */
public class TruckCard {

    /** このトラックに積まれているお酒のリスト */
    private final List<AlcoholCard> alcohols;

    public TruckCard(List<AlcoholCard> alcohols) {
        this.alcohols = alcohols;
    }

    /** トラックに積まれた酒一覧を返す（読み取り用） */
    public List<AlcoholCard> getAlcohols() {
        return alcohols;
    }
}

