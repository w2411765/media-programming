// ペナルティイベントのロジック
package game.controller;

import game.model.GameState;
import game.model.Player;
import game.model.alcohol.AlcoholType;
import game.model.order.OrderCard;
import game.view.MainFrame;

import javax.swing.Timer;
import java.util.*;

/**
 * ペナルティイベントを管理するクラス
 * 
 * ペナルティ1: 取引フェーズ終了時に10本を超える酒を所持しているプレイヤーへの罰則
 * ペナルティ2: 全ラウンド終了時の余った酒・客カードへの罰則
 */
public class PenaltyManager {

    private static final int ALCOHOL_LIMIT = 10; // お酒の上限
    
    private final GameState gameState;
    private final MainFrame mainFrame;
    private final GameManager gameManager;
    
    // コイントス結果（true: お酒の面, false: お金の面）
    private boolean coinResultIsAlcohol;
    private Runnable onPenaltyComplete;
    
    public PenaltyManager(GameState gameState, MainFrame mainFrame, GameManager gameManager) {
        this.gameState = gameState;
        this.mainFrame = mainFrame;
        this.gameManager = gameManager;
    }
    
    /**
     * ペナルティイベント1をチェックして実行
     * 取引フェーズ終了時に呼び出す
     */
    public void checkAndExecutePenalty1(Runnable onComplete) {
        this.onPenaltyComplete = onComplete;
        
        // 10本を超えるお酒を持っているプレイヤーを検索
        List<Player> violators = findViolators();
        
        if (violators.isEmpty()) {
            // ペナルティなし - すぐに次へ
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }
        
        // ペナルティイベント開始
        executePenalty1(violators);
    }
    
    /**
     * 10本を超えるお酒を持っているプレイヤーを取得
     */
    private List<Player> findViolators() {
        List<Player> violators = new ArrayList<>();
        for (Player p : gameState.getPlayers()) {
            if (getTotalAlcohol(p) > ALCOHOL_LIMIT) {
                violators.add(p);
            }
        }
        return violators;
    }
    
    /**
     * プレイヤーの所持酒の合計本数を取得
     */
    private int getTotalAlcohol(Player player) {
        int total = 0;
        for (int count : player.getInventory().values()) {
            total += count;
        }
        return total;
    }
    
    /**
     * ペナルティイベント1を実行
     */
    private void executePenalty1(List<Player> violators) {
        // ① ペナルティイベント発生の合図
        mainFrame.showPenaltyPhase("禁酒局の捜査官だ！", "違法な量の酒を所持しているプレイヤーがいる...");
        mainFrame.showMessage("=== ペナルティイベント発生 ===");
        mainFrame.showMessage("禁酒局の捜査官が現れた！");
        
        // 2秒後に違反者からお酒を没収
        Timer timer1 = new Timer(2500, e -> {
            confiscateAlcohol(violators);
        });
        timer1.setRepeats(false);
        timer1.start();
    }
    
    /**
     * ② 違反者からお酒を全没収
     */
    private void confiscateAlcohol(List<Player> violators) {
        // 最もお酒を持っていたプレイヤー（戦犯）を特定
        Player culprit = null;
        int maxAlcohol = 0;
        
        for (Player p : violators) {
            int total = getTotalAlcohol(p);
            mainFrame.showMessage(p.getName() + " は " + total + " 本のお酒を所持していた → 全没収！");
            
            if (total > maxAlcohol) {
                maxAlcohol = total;
                culprit = p;
            }
            
            // お酒を全没収
            for (AlcoholType type : AlcoholType.values()) {
                int count = p.getInventory().getOrDefault(type, 0);
                if (count > 0) {
                    p.removeAlcohol(type, count);
                }
            }
            
            mainFrame.updatePlayerInventory(p);
        }
        
        // CenterPanelに没収完了を表示
        mainFrame.showPenaltyMessage("お酒没収完了", "違反者の酒は全て没収されました。");
        
        // 戦犯がコインを投げる
        final Player finalCulprit = culprit;
        Timer timer2 = new Timer(2000, e -> {
            showCoinToss(finalCulprit, violators);
        });
        timer2.setRepeats(false);
        timer2.start();
    }
    
    /**
     * ③④ コイントスを表示
     */
    private void showCoinToss(Player culprit, List<Player> violators) {
        mainFrame.showMessage(culprit.getName() + " がカポネコインを投げる...");
        
        // コイントスUIを表示（戦犯のみボタンを押せる）
        // 南プレイヤー（ID=0）が戦犯の場合のみボタンを有効化
        boolean isPlayerCulprit = (culprit.getId() == 0);
        
        mainFrame.showCoinToss(culprit, isPlayerCulprit, result -> {
            this.coinResultIsAlcohol = result;
            applyCoinResult(culprit, violators, result);
        });
        
        // CPUが戦犯の場合は自動でコインを投げる
        if (!isPlayerCulprit) {
            Timer autoToss = new Timer(2000, e -> {
                boolean result = Math.random() > 0.5; // 50/50
                mainFrame.executeCoinToss(result);
            });
            autoToss.setRepeats(false);
            autoToss.start();
        }
    }
    
    /**
     * ⑤ コイントスの結果を適用
     */
    private void applyCoinResult(Player culprit, List<Player> violators, boolean isAlcoholSide) {
        Set<Integer> violatorIds = new HashSet<>();
        for (Player v : violators) {
            violatorIds.add(v.getId());
        }
        
        if (isAlcoholSide) {
            // お酒の面 → 他のプレイヤーのお酒半分没収
            mainFrame.showMessage("お酒の面が出た！他のプレイヤーのお酒が半分没収される！");
            mainFrame.showPenaltyMessage("お酒の面！", "違反者以外のプレイヤーのお酒が半分没収されます。");
            
            for (Player p : gameState.getPlayers()) {
                if (!violatorIds.contains(p.getId())) {
                    int totalBefore = getTotalAlcohol(p);
                    int toRemove = totalBefore / 2;
                    
                    if (toRemove > 0) {
                        removeHalfAlcohol(p);
                        int totalAfter = getTotalAlcohol(p);
                        mainFrame.showMessage(p.getName() + " のお酒: " + totalBefore + " → " + totalAfter + " 本");
                        mainFrame.updatePlayerInventory(p);
                    }
                }
            }
        } else {
            // お金の面 → 他のプレイヤーのお金半分没収
            mainFrame.showMessage("お金の面が出た！他のプレイヤーのお金が半分没収される！");
            mainFrame.showPenaltyMessage("お金の面！", "違反者以外のプレイヤーのお金が半分没収されます。");
            
            for (Player p : gameState.getPlayers()) {
                if (!violatorIds.contains(p.getId())) {
                    int moneyBefore = p.getMoney();
                    int penalty = moneyBefore / 2;
                    
                    if (penalty > 0) {
                        p.payMoney(penalty);
                        mainFrame.showMessage(p.getName() + " のお金: " + moneyBefore + " → " + p.getMoney() + " 円");
                        mainFrame.updatePlayerInventory(p);
                    }
                }
            }
        }
        
        // ⑥ ペナルティイベント終了
        Timer endTimer = new Timer(2500, e -> {
            mainFrame.showMessage("=== ペナルティイベント終了 ===");
            if (onPenaltyComplete != null) {
                onPenaltyComplete.run();
            }
        });
        endTimer.setRepeats(false);
        endTimer.start();
    }
    
    /**
     * プレイヤーのお酒を半分没収（各種類を半分に）
     */
    private void removeHalfAlcohol(Player player) {
        for (AlcoholType type : AlcoholType.values()) {
            int count = player.getInventory().getOrDefault(type, 0);
            int toRemove = count / 2;
            if (toRemove > 0) {
                player.removeAlcohol(type, toRemove);
            }
        }
    }
    
    /**
     * ペナルティイベント2を実行
     * 全ラウンド終了時に呼び出す
     */
    public void executePenalty2(Runnable onComplete) {
        this.onPenaltyComplete = onComplete;
        
        // ① ペナルティフェーズ開始の合図
        mainFrame.showPenaltyPhase("決算ペナルティ", "余った酒と未提供の客に対するペナルティを適用します。");
        mainFrame.showMessage("=== 決算ペナルティフェーズ ===");
        
        // 各プレイヤーに対してペナルティを適用
        Timer timer = new Timer(2000, e -> {
            applyPenalty2ToAllPlayers(0);
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * 全プレイヤーにペナルティ2を順番に適用
     */
    private void applyPenalty2ToAllPlayers(int playerIndex) {
        List<Player> players = gameState.getPlayers();
        
        if (playerIndex >= players.size()) {
            // 全員完了
            Timer endTimer = new Timer(1500, e -> {
                mainFrame.showMessage("=== 決算ペナルティ終了 ===");
                if (onPenaltyComplete != null) {
                    onPenaltyComplete.run();
                }
            });
            endTimer.setRepeats(false);
            endTimer.start();
            return;
        }
        
        Player player = players.get(playerIndex);
        
        // ② 余ったお酒のペナルティ
        int alcoholCount = getTotalAlcohol(player);
        int alcoholPenalty = alcoholCount; // 1本につき1円
        
        // ③ 余った客カードのペナルティ
        int orderCount = player.getOrders().size();
        int orderPenalty = orderCount; // 1枚につき1円
        
        int totalPenalty = alcoholPenalty + orderPenalty;
        
        StringBuilder message = new StringBuilder();
        message.append("【").append(player.getName()).append("】\n");
        
        if (alcoholCount > 0) {
            message.append("余った酒: ").append(alcoholCount).append("本 → -").append(alcoholPenalty).append("円\n");
        }
        if (orderCount > 0) {
            message.append("未提供の客: ").append(orderCount).append("枚 → -").append(orderPenalty).append("円\n");
        }
        
        if (totalPenalty > 0) {
            int moneyBefore = player.getMoney();
            player.payMoney(Math.min(totalPenalty, moneyBefore)); // 所持金以上は没収しない
            message.append("合計ペナルティ: -").append(totalPenalty).append("円");
            message.append(" (").append(moneyBefore).append(" → ").append(player.getMoney()).append("円)");
        } else {
            message.append("ペナルティなし！");
        }
        
        mainFrame.showMessage(message.toString());
        mainFrame.showPenaltyMessage(player.getName() + " の決算", message.toString());
        mainFrame.updatePlayerInventory(player);
        
        // 次のプレイヤーへ
        Timer nextTimer = new Timer(2500, e -> {
            applyPenalty2ToAllPlayers(playerIndex + 1);
        });
        nextTimer.setRepeats(false);
        nextTimer.start();
    }
}
