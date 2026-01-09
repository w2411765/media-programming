papackage com.speakeasy.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Deck
 * ------------------------------
 * カードの山札を表すクラス。
 *
 * ・シャッフルする
 * ・カードを1枚引く
 *
 */

public class Deck<T> {

    // 山札の中身を追加
    private List<T> cards = new arrayList<>(); 

    public void add(T card) {
        cards.add(card);
    }

    public void addAll(List<T> list) {
        cards.addAll(list);
    }

    // 山札をシャッフル
    
 }