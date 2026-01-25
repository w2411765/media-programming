package game.util;

/**
 * ゲームの定数定義
 */
public class Constants {
    
    // ========== プレイヤー設定 ==========
    
    /** プレイヤー数 */
    public static final int NUM_PLAYERS = 4;
    
    /** プレイヤーの初期所持金 */
    public static final int INITIAL_MONEY = 20;
    
    // ========== ゲーム進行設定 ==========
    
    /** 最大ラウンド数（-1 = 制限なし） */
    public static final int MAX_ROUNDS = 3;
    
    /** ゲーム終了条件：目標金額 */
    public static final int WIN_CONDITION_MONEY = 200;
    
    // ========== オークション設定 ==========
    
    /** オークション用トラックカードの酒の本数（人数×この値） */
    public static final int ALCOHOL_PER_PLAYER = 4;
    
    // ========== 注文カード（お客さん）設定 ==========
    
    /** 注文カードのJSONファイルパス */
    public static final String CUSTOMER_JSON_PATH = "/data/customers.json";
    
    /** 各ラウンドで配布する注文カードの枚数 */
    public static final int CARDS_PER_PLAYER = 4;
    
    /** 
     * 山札に入れる注文カードの最大枚数
     * （JSONから読み込んだカードをこの枚数まで使用）
     * -1 = すべて使用
     */
    public static final int MAX_ORDER_CARDS = -1;
    
    /**
     * 同じお客さんのカードを山札に入れる最大枚数
     * （JSONの各カードをこの枚数まで複製して山札に入れる）
     */
    public static final int COPIES_PER_CUSTOMER = 2;
}
