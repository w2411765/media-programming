package game.model.order;

import game.model.alcohol.AlcoholType;
import java.util.Map;

/**
 * OrderCard
 * ------------------------------
 * 客（注文）カードを表すクラス。
 *
 * ・お客さんの名前
 * ・どの酒を
 * ・何本渡せば
 * ・いくらお金がもらえるか
 *
 * という情報を持つ「データ用クラス」。
 */
public class OrderCard {

    // お客さんの名前
    private String customerName;

    // 注文に必要な酒の種類と本数
    // 例：{ BEER=2, RUM=1 }
    private Map<AlcoholType, Integer> required;

    // 注文を達成したときにもらえる金額
    private int reward;

    /**
     * コンストラクタ（名前なし - 後方互換用）
     *
     * @param required 必要な酒の種類と本数
     * @param reward   注文達成時の報酬
     */
    public OrderCard(Map<AlcoholType, Integer> required, int reward) {
        this("お客さん", required, reward);
    }

    /**
     * コンストラクタ（名前あり）
     *
     * @param customerName お客さんの名前
     * @param required     必要な酒の種類と本数
     * @param reward       注文達成時の報酬
     */
    public OrderCard(String customerName, Map<AlcoholType, Integer> required, int reward) {
        // 注文内容が空や null のまま作られるのを防ぐ
        if (required == null || required.isEmpty()) {
            throw new IllegalArgumentException("required is empty");
        }

        this.customerName = (customerName != null && !customerName.isEmpty()) ? customerName : "お客さん";
        this.required = required;
        this.reward = reward;
    }

    // お客さんの名前を取得
    public String getCustomerName() {
        return customerName;
    }

    // 必要な酒の内容を取得
    public Map<AlcoholType, Integer> getRequired() {
        return required;
    }

    // 報酬金額を取得
    public int getReward() {
        return reward;
    }

    @Override
    public String toString() {
        return customerName + " (報酬: " + reward + "円)";
    }
}
