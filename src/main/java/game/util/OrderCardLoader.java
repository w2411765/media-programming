package game.util;

import game.model.alcohol.AlcoholType;
import game.model.order.OrderCard;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OrderCardLoader
 * ------------------------------
 * JSONファイルからOrderCardを読み込むユーティリティクラス。
 */
public class OrderCardLoader {

    private static final Gson gson = new Gson();

    /**
     * JSONファイルからOrderCardのリストを読み込む
     *
     * @param resourcePath リソースパス（例: "/data/customers.json"）
     * @return OrderCardのリスト
     */
    public static List<OrderCard> loadFromJson(String resourcePath) {
        List<OrderCard> orderCards = new ArrayList<>();

        try (InputStream is = OrderCardLoader.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("リソースが見つかりません: " + resourcePath);
                return orderCards;
            }

            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            Type listType = new TypeToken<List<CustomerData>>(){}.getType();
            List<CustomerData> customers = gson.fromJson(reader, listType);

            if (customers != null) {
                for (CustomerData data : customers) {
                    OrderCard card = convertToOrderCard(data);
                    if (card != null) {
                        orderCards.add(card);
                    }
                }
            }

            System.out.println("読み込んだ注文カード数: " + orderCards.size());

        } catch (Exception e) {
            System.err.println("JSONの読み込みに失敗しました: " + e.getMessage());
            e.printStackTrace();
        }

        return orderCards;
    }

    /**
     * CustomerDataをOrderCardに変換
     */
    private static OrderCard convertToOrderCard(CustomerData data) {
        if (data == null || data.required == null || data.required.isEmpty()) {
            return null;
        }

        Map<AlcoholType, Integer> required = new HashMap<>();
        for (Map.Entry<String, Integer> entry : data.required.entrySet()) {
            try {
                AlcoholType type = AlcoholType.valueOf(entry.getKey().toUpperCase());
                required.put(type, entry.getValue());
            } catch (IllegalArgumentException e) {
                System.err.println("不明な酒の種類: " + entry.getKey());
            }
        }

        if (required.isEmpty()) {
            return null;
        }

        return new OrderCard(data.name, required, data.reward);
    }

    /**
     * JSONのデータ構造を表す内部クラス
     */
    private static class CustomerData {
        String name;
        Map<String, Integer> required;
        int reward;
    }
}
