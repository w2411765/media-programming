package game.model.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    public void shuffle() {
        Collections.shuffle(cards);
    }

    // 山札から1枚引く
    public T draw() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("deck is empty");
        }
        return cards.remove(0);
    }

    // 残り枚数
    public int size() {
        return cards.size();
    }

 }