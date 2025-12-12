package com.speakeasy.model.alcohol;

import java.util.List;

/**
 * TruckCard
 * ----------------------------------------
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
    @Override
    public String toString() {
        return "TruckCard{" + "alcohols=" + alcohols + '}';
    }
}

