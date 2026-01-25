// 取引・交渉のロジック
package game.controller;

import game.model.GameState;
import game.model.Player;
import game.model.alcohol.AlcoholType;
import game.view.MainFrame;

import javax.swing.JDialog;
import javax.swing.Timer;
import java.awt.Color;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

/**
 * プレイヤー間の取引フェーズを担当するクラス。
 * 取引提案と承諾／拒否を受け取り、GameState に反映させる。
 */
public class TradeManager {

    private final GameState gameState;
    private final MainFrame mainFrame;
    private final GameManager gameManager;
    
    // 現在進行中の取引
    private TradeProposal currentProposal;
    private boolean isWaitingForResponse;
    
    // 送り主側ダイアログの参照（応答時にメッセージ表示用）
    private JDialog sentTradeDialog;
    
    // 取引終了リクエストを追跡
    private Set<Integer> endTradeRequests = new HashSet<>();
    private boolean tradePhaseActive = false;

    public TradeManager(GameState gameState, MainFrame mainFrame,
                        GameManager gameManager) {
        this.gameState = gameState;
        this.mainFrame = mainFrame;
        this.gameManager = gameManager;
    }

    /**
     * 取引フェーズ開始時に呼び出す。
     */
    public void startTradePhase() {
        currentProposal = null;
        isWaitingForResponse = false;
        endTradeRequests.clear();
        tradePhaseActive = true;
        
        // CenterPanelを取引フェーズ表示に切り替え
        mainFrame.showTradePhase();
        mainFrame.enableTradeUI(true);
        mainFrame.showMessage("=== 取引フェーズ開始 ===");
        mainFrame.showMessage("他プレイヤーと酒の交換を行えます。");
        mainFrame.showMessage("全員が「取引終了」を押すとフェーズが終了します。");
        
        // 3秒後に取引ダイアログを表示
        Timer timer = new Timer(3000, e -> {
            if (tradePhaseActive) {
                if (mainFrame.isMultiPlayerMode()) {
                    // マルチプレイ：全員に同時表示（各自のIDをコールバックに含める）
                    mainFrame.startTradePhaseForAllPlayers(
                        this::onProposalSubmittedWithId,
                        this::onEndTradeRequestedWithId
                    );
                } else {
                    // シングルプレイ：従来通り
                    showTradeDialogForSinglePlayer();
                }
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * 取引ダイアログを表示（シングルプレイヤー用）
     */
    private void showTradeDialogForSinglePlayer() {
        if (!tradePhaseActive) return;
        
        // このビューの所有者（自分）を取得
        Player myPlayer = getMyPlayer();
        if (myPlayer == null) return;
        
        mainFrame.showTradeDialog(
            myPlayer,
            gameState.getPlayers(),
            this::onProposalSubmitted,
            this::onDialogCancelled,
            () -> onEndTradeRequestedWithId(myPlayer.getId())
        );
    }
    
    /**
     * 取引ダイアログを再表示（マルチプレイ用、特定プレイヤー向け）
     */
    public void showTradeDialogForPlayer(int playerId) {
        if (!tradePhaseActive) return;
        
        if (mainFrame.isMultiPlayerMode()) {
            // マルチプレイ：該当プレイヤーのビューにのみ表示
            mainFrame.showTradeDialogForPlayer(playerId,
                this::onProposalSubmittedWithId,
                this::onEndTradeRequestedWithId
            );
        } else {
            showTradeDialogForSinglePlayer();
        }
    }
    
    /**
     * 取引提案を受け取ったとき（プレイヤーID付き）
     */
    private void onProposalSubmittedWithId(int playerId, TradeProposal proposal) {
        // 提案者を確認（proposalのfromと一致するはず）
        if (proposal.getFrom().getId() != playerId) {
            mainFrame.showMessage("エラー: 取引提案者が一致しません");
            return;
        }
        onProposalSubmitted(proposal);
    }
    
    /**
     * 取引終了リクエストが送られたとき（プレイヤーID付き）
     */
    public void onEndTradeRequestedWithId(int playerId) {
        if (!tradePhaseActive) return;
        
        Player player = getPlayerById(playerId);
        if (player == null) {
            mainFrame.showMessage("エラー: 不明なプレイヤーID: " + playerId);
            return;
        }
        
        // 既に終了リクエスト済みの場合は無視
        if (endTradeRequests.contains(playerId)) {
            return;
        }
        
        endTradeRequests.add(playerId);
        mainFrame.showMessage(player.getName() + " が取引終了を選択しました。");
        
        // 該当プレイヤーのビューに待機表示
        int totalPlayers = gameState.getPlayers().size();
        mainFrame.showTradeEndWaitingForPlayer(playerId, player, endTradeRequests.size(), totalPlayers);
        
        // CPUプレイヤーも自動で終了を選択（シングルプレイのみ）
        simulateCPUEndTradeRequests();
        
        // 全員が終了を選択したかチェック
        checkAllEndTradeRequests();
    }
    
    /**
     * プレイヤーIDからプレイヤーを取得
     */
    private Player getPlayerById(int playerId) {
        for (Player p : gameState.getPlayers()) {
            if (p.getId() == playerId) {
                return p;
            }
        }
        return null;
    }
    
    /**
     * CPUプレイヤーの取引終了をシミュレート
     * マルチプレイの場合はスキップ（全員が人間）
     */
    private void simulateCPUEndTradeRequests() {
        // マルチプレイモードの場合はCPU処理をスキップ
        if (mainFrame.isMultiPlayerMode()) {
            return;
        }
        
        // 1-2秒後にCPUプレイヤーも終了を選択
        int myPlayerId = mainFrame.getMyPlayerId();
        for (Player p : gameState.getPlayers()) {
            if (p.getId() != myPlayerId && !endTradeRequests.contains(p.getId())) {
                int delay = 1000 + (int)(Math.random() * 1500); // 1-2.5秒
                Timer timer = new Timer(delay, e -> {
                    if (tradePhaseActive && !endTradeRequests.contains(p.getId())) {
                        endTradeRequests.add(p.getId());
                        mainFrame.showMessage(p.getName() + " が取引終了を選択しました。");
                        checkAllEndTradeRequests();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }
    
    /**
     * 全員が取引終了を選択したかチェック
     */
    private void checkAllEndTradeRequests() {
        int totalPlayers = gameState.getPlayers().size();
        if (endTradeRequests.size() >= totalPlayers) {
            mainFrame.showMessage("全員が取引終了を選択しました。");
            endTradePhase();
        } else {
            mainFrame.showMessage("待機中: " + endTradeRequests.size() + "/" + totalPlayers + " 人が終了を選択");
        }
    }
    
    /**
     * 取引提案が送信されたとき
     */
    private void onProposalSubmitted(TradeProposal proposal) {
        int proposerId = proposal.getFrom().getId();
        
        // 事前チェック: 取引が成立可能かどうか確認
        if (!canTradeBeCompleted(proposal)) {
            mainFrame.showMessage("× 取引不可 - 在庫またはお金が不足しています。");
            // ダイアログを表示せず、取引作成UIに戻る
            showTradeDialogForPlayer(proposerId);
            return;
        }
        
        this.currentProposal = proposal;
        this.isWaitingForResponse = true;
        
        // Logに取引内容を共有
        String tradeMessage = formatTradeMessage(proposal);
        mainFrame.showMessage(tradeMessage);
        
        // 相手側にも表示（マルチプレイの場合は先に送り先に通知）
        Player target = proposal.getTo();
        
        if (mainFrame.isMultiPlayerMode()) {
            // マルチプレイの場合：相手のビューに通知を送る（BroadcastMainFrameが処理）
            mainFrame.notifyTradeProposal(proposal, accepted -> {
                handleTradeResponse(proposal, accepted);
            });
        } else {
            // シングルプレイの場合：送り先がCPUなら自動応答、自分なら受信UIを表示
            if (!isMyPlayer(target)) {
                // CPUプレイヤーの場合は自動応答（簡易実装：ランダムで承諾/拒否）
                Timer responseTimer = new Timer(2000, e -> {
                    boolean accept = Math.random() > 0.3; // 70%で承諾
                    handleTradeResponse(proposal, accept);
                });
                responseTimer.setRepeats(false);
                responseTimer.start();
            } else {
                // 送り先が自分の場合（他プレイヤーからの取引）
                mainFrame.showReceivedTrade(proposal, accepted -> {
                    handleTradeResponse(proposal, accepted);
                });
            }
        }
        
        // 送り主側にダイアログを表示（破談ボタンのみ）- 参照を保持
        sentTradeDialog = mainFrame.showSentTrade(proposal, action -> {
            if ("cancel".equals(action)) {
                // 破談（送り主がキャンセル）
                mainFrame.showMessage(proposal.getFrom().getName() + " が取引を取り消しました。");
                // 送り先のダイアログを閉じる
                mainFrame.cancelTradeProposal(proposal);
                currentProposal = null;
                isWaitingForResponse = false;
                sentTradeDialog = null;
                showTradeDialogForPlayer(proposerId);
            }
        });
    }
    
    /**
     * 取引が成立可能かどうかを事前チェック
     */
    private boolean canTradeBeCompleted(TradeProposal proposal) {
        Player from = proposal.getFrom();
        Player to = proposal.getTo();
        
        // 提案者の酒が足りるか
        for (Map.Entry<AlcoholType, Integer> entry : proposal.getAlcoholOffered().entrySet()) {
            int have = from.getInventory().getOrDefault(entry.getKey(), 0);
            if (have < entry.getValue()) {
                return false;
            }
        }
        // 提案者のお金が足りるか
        if (from.getMoney() < proposal.getMoneyOffered()) {
            return false;
        }
        
        // 相手の酒が足りるか
        for (Map.Entry<AlcoholType, Integer> entry : proposal.getAlcoholRequested().entrySet()) {
            int have = to.getInventory().getOrDefault(entry.getKey(), 0);
            if (have < entry.getValue()) {
                return false;
            }
        }
        // 相手のお金が足りるか
        if (to.getMoney() < proposal.getMoneyRequested()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * ダイアログがキャンセルされたとき
     */
    private void onDialogCancelled() {
        // 取引フェーズを終了
        endTradePhase();
    }
    
    /**
     * 取引内容をログ用にフォーマット
     */
    private String formatTradeMessage(TradeProposal proposal) {
        StringBuilder sb = new StringBuilder();
        sb.append("【取引提案】 ");
        sb.append(proposal.getFrom().getName());
        sb.append(" → ");
        sb.append(proposal.getTo().getName());
        sb.append("\n");
        
        // 渡すもの
        if (!proposal.getAlcoholOffered().isEmpty() || proposal.getMoneyOffered() > 0) {
            sb.append("  渡す: ");
            boolean first = true;
            for (Map.Entry<AlcoholType, Integer> entry : proposal.getAlcoholOffered().entrySet()) {
                if (!first) sb.append(", ");
                sb.append(getAlcoholDisplayName(entry.getKey())).append("x").append(entry.getValue());
                first = false;
            }
            if (proposal.getMoneyOffered() > 0) {
                if (!first) sb.append(", ");
                sb.append(proposal.getMoneyOffered()).append("円");
            }
            sb.append("\n");
        }
        
        // 受け取るもの
        if (!proposal.getAlcoholRequested().isEmpty() || proposal.getMoneyRequested() > 0) {
            sb.append("  受取: ");
            boolean first = true;
            for (Map.Entry<AlcoholType, Integer> entry : proposal.getAlcoholRequested().entrySet()) {
                if (!first) sb.append(", ");
                sb.append(getAlcoholDisplayName(entry.getKey())).append("x").append(entry.getValue());
                first = false;
            }
            if (proposal.getMoneyRequested() > 0) {
                if (!first) sb.append(", ");
                sb.append(proposal.getMoneyRequested()).append("円");
            }
        }
        
        return sb.toString();
    }
    
    private String getAlcoholDisplayName(AlcoholType type) {
        switch (type) {
            case BEER: return "ビール";
            case RUM: return "ラム";
            case VODKA: return "ウォッカ";
            case GIN: return "ジン";
            default: return type.name();
        }
    }

    /**
     * 取引提案を処理する（旧API互換）
     */
    public void handleTradeProposed(Player from, Player to, TradeProposal proposal) {
        onProposalSubmitted(proposal);
    }

    /**
     * 相手プレイヤーが取引に応答したときに呼び出す。
     */
    public void handleTradeResponse(TradeProposal proposal, boolean accepted) {
        isWaitingForResponse = false;
        
        if (accepted) {
            // 取引を実行
            boolean success = gameState.applyTrade(proposal);
            if (success) {
                mainFrame.updateAllPlayersState(gameState.getPlayers());
                mainFrame.showMessage("★ 取引成立！ " + proposal.getFrom().getName() + " と " + proposal.getTo().getName() + " の取引が成立しました。");
                
                // プレイヤーのインベントリを更新
                mainFrame.updatePlayerInventory(proposal.getFrom());
                mainFrame.updatePlayerInventory(proposal.getTo());
                
                // 送り主側ダイアログに成立メッセージを表示して閉じる
                if (sentTradeDialog != null) {
                    mainFrame.showTradeResultAndClose(sentTradeDialog, 
                        "★ 取引成立！", 
                        new Color(100, 200, 100), 1500);
                    sentTradeDialog = null;
                }
            } else {
                mainFrame.showMessage("取引に失敗しました（在庫またはお金が不足）。");
                if (sentTradeDialog != null) {
                    mainFrame.showTradeResultAndClose(sentTradeDialog, 
                        "取引失敗\n在庫またはお金が不足", 
                        new Color(255, 150, 150), 1500);
                    sentTradeDialog = null;
                }
            }
        } else {
            String rejectMessage = "取引破談\n" + proposal.getTo().getName() + " が取引を拒否しました。";
            mainFrame.showMessage("× 取引破談 - " + proposal.getTo().getName() + " が取引を拒否しました。");
            
            // 送り主側ダイアログに破談メッセージを表示して1.5秒後に閉じる
            if (sentTradeDialog != null) {
                mainFrame.showTradeResultAndClose(sentTradeDialog, rejectMessage, new Color(255, 100, 100), 1500);
                sentTradeDialog = null;
            }
        }
        
        // 提案者のIDを保存（currentProposalをnullにする前に）
        int proposerId = proposal.getFrom().getId();
        currentProposal = null;
        
        // 取引フェーズを継続（次の取引作成UIを表示）
        if (tradePhaseActive) {
            // 少し待ってから取引作成UIに戻る
            Timer timer = new Timer(1800, e -> {
                if (tradePhaseActive) {
                    // 提案者のビューに取引ダイアログを再表示
                    showTradeDialogForPlayer(proposerId);
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    /**
     * View から「取引フェーズを終了する」操作があったときに呼ぶ。
     */
    public void endTradePhase() {
        if (!tradePhaseActive) return;
        
        tradePhaseActive = false;
        mainFrame.enableTradeUI(false);
        mainFrame.showMessage("=== 取引フェーズ終了 ===");
        
        // 取引終了表示を2秒間表示してから次へ
        mainFrame.showTradePhaseComplete();
        javax.swing.Timer timer = new javax.swing.Timer(2000, e -> {
            gameManager.onTradePhaseFinished();
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * このビューの所有者プレイヤー（自分）を取得
     */
    private Player getMyPlayer() {
        int myPlayerId = mainFrame.getMyPlayerId();
        for (Player p : gameState.getPlayers()) {
            if (p.getId() == myPlayerId) {
                return p;
            }
        }
        return null;
    }
    
    /**
     * 指定されたプレイヤーが「自分」かどうか判定
     */
    private boolean isMyPlayer(Player player) {
        return player != null && player.getId() == mainFrame.getMyPlayerId();
    }
}
