package com.speakeasy.model.alcohol;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * TruckCard
 * ------------------------------
 * 酒を複数本まとめて運ぶ「トラック1台分」を表すカード。
 */
public class TruckCard {

    /**
     * トラックに積まれている酒の内容
     *  酒の種類（AlcoholType）→ 本数
     */
    private final EnumMap<AlcoholType, Integer> cargo;

    /**
     * コンストラクタ
     *
     * @param cargo
     *  トラックに積む酒の内容を
     *  {BEER=4, WINE=2, WHISKEY=1} のような Map で渡す
     */
    public TruckCard(Map<AlcoholType, Integer> cargo) {

        // null の Map が渡されるのを防ぐ（不正な TruckCard を作らせない）
        Objects.requireNonNull(cargo, "cargo must not be null");

        // 外部から中身を書き換えられるのを防ぐため引数の Msp をそのまま使わない
        this.cargo = new EnumMap<>(AlcoholType.class);

        // 渡された酒の内容を1つずつチェックしてコピー
        for (var entry : cargo.entrySet()) {

            // 酒の本数が null または負数でないかチェック
            if (entry.getValue() == null || entry.getValue() < 0) {
                throw new IllegalArgumentException("alcohol count must be >= 0");
            }

            // 問題なければ cargo に登録
            this.cargo.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * トラックの積荷内容を取得（参照専用）
     *
     * @return
     *  外部から変更できない Map（unmodifiableMap）
     *
     */
    public Map<AlcoholType, Integer> getCargoView() {
        return Collections.unmodifiableMap(cargo);
    }

    /**
     * デバッグ・ログ出力用
     * トラックの中身が一目で分かる文字列表現
     */
    @Override
    public String toString() {
        return "TruckCard{cargo=" + cargo + "}";
    }
}
