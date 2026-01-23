package game.util;

/**
 * ゲームの定数定義
 */
public class Constants {
    
    /** プレイヤーの初期所持金 */
    public static final int INITIAL_MONEY = 50;
    
    /** ゲーム終了条件：目標金額 */
    public static final int WIN_CONDITION_MONEY = 200;
    
    /** 最大ラウンド数（無限に続く場合は-1） */
    public static final int MAX_ROUNDS = -1; // -1は制限なし
    
    /** 各ラウンドで配布する注文カードの枚数 */
    public static final int CARDS_PER_PLAYER = 4;
    
    /** オークション用トラックカードの酒の本数（人数×この値） */
    public static final int ALCOHOL_PER_PLAYER = 4;
    
    /** プレイヤー数 */
    public static final int NUM_PLAYERS = 4;
}
