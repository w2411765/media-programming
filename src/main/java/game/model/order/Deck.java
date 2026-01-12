package game.model.order;

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

    // 山札の中身
    private List<T> cards = new ArrayList<>(); 

    public void add(T card) {
        cards.add(card);
    }

    public void addAll(List<T> list) {
        cards.addAll(list);
    }

    // 山札をシャッフル
    
 }