// メイン画面のJFrame
package game.view;

import javax.swing.*;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.function.Consumer;
import game.model.Player;
import game.model.alcohol.TruckCard;
import game.controller.TradeProposal;
import game.controller.GameManager;
import java.util.List;
import game.view.components.TitlePanel;
import game.view.GameBoardPanel;
import game.view.components.CenterPanel;
import game.model.order.OrderCard;
 
public class MainFrame extends JFrame {
  private GameBoardPanel gameBoardPanel;
  private GameManager gameManager;
  
  /** このビューの所有者のプレイヤーID（マルチプレイ用） */
  protected int myPlayerId = 0;
  
  /**
   * デフォルトコンストラクタ（フルスクリーンモード）
   */
  public MainFrame(){
    this(true);
  }
  
  /**
   * フルスクリーンモードを指定できるコンストラクタ
   * @param fullscreen trueでフルスクリーン、falseでウィンドウモード
   */
  public MainFrame(boolean fullscreen){
    this(fullscreen, true);
  }
  
  /**
   * 詳細設定可能なコンストラクタ
   * @param fullscreen trueでフルスクリーン、falseでウィンドウモード
   * @param visible trueでウィンドウを表示、falseで非表示（テスト用）
   */
  public MainFrame(boolean fullscreen, boolean visible){
    this.setTitle("CAPONE");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    if (visible) {
      this.add(new TitlePanel());
    }
    
    if (fullscreen && visible) {
      GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
      gd.setFullScreenWindow(this);
    } else if (visible) {
      // ウィンドウモード
      this.setSize(1280, 720);
      this.setLocationRelativeTo(null);
    }
    
    if (visible) {
      this.setVisible(true);
    }
  }
  
  /**
   * タイトル画面からゲーム画面に切り替える
   */
  public void switchToGameBoard() {
    this.getContentPane().removeAll();
    gameBoardPanel = new GameBoardPanel();
    this.add(gameBoardPanel);
    this.revalidate();
    this.repaint();
  }

  // ----- ゲーム状態更新メソッド -----

  public void updateRoundInfo(int round) {
    if (gameBoardPanel != null && gameBoardPanel.getInfoPanel() != null) {
      gameBoardPanel.getInfoPanel().updateRoundInfo(round);
    }
  }

  public void updateAllPlayersState(List<Player> players) {
    // プレイヤー状態の更新（PlayerPanelへの反映は後で実装）
    // 現時点ではログに出力
    if (gameBoardPanel != null && gameBoardPanel.getLogPanel() != null) {
      for (Player player : players) {
        gameBoardPanel.getLogPanel().addMessage(
          player.getName() + " - 所持金: " + player.getMoney() + "円"
        );
      }
    }
  }

  public void enableTradeUI(boolean enabled) {
    // ToolPanelに取引ボタンを追加する場合はここで実装
    if (gameBoardPanel != null && gameBoardPanel.getToolPanel() != null) {
      // 実装は後で追加
    }
  }

  public void enableServeUI(boolean enabled) {
    // CustomerPanelの提供機能を有効化
    if (gameBoardPanel != null && gameBoardPanel.getCustomerPanel() != null) {
      if (enabled) {
        // 自分のプレイヤー情報を取得して渡す
        Player myPlayer = getMyPlayerForServe();
        gameBoardPanel.getCustomerPanel().startServePhase(myPlayer, this::onServeCardClicked);
      } else {
        gameBoardPanel.getCustomerPanel().endServePhase();
      }
    }
  }
  
  /**
   * 提供フェーズ用に自分のプレイヤー情報を取得
   * GameManagerから取得する
   */
  protected Player getMyPlayerForServe() {
    if (gameManager != null) {
      return gameManager.getMyPlayerForUI();
    }
    return null;
  }
  
  /**
   * カードクリック時の処理（サブクラスでオーバーライド可能）
   */
  protected void onServeCardClicked(game.model.order.OrderCard card) {
    // デフォルト実装なし（GameManagerから呼ばれる）
  }
  
  /**
   * 提供フェーズ開始表示
   */
  public void showServePhaseStart() {
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      gameBoardPanel.getCenterPanel().showServePhaseStart();
    }
  }
  
  /**
   * 提供フェーズUIを表示
   */
  public void showServePhaseUI(Runnable onEndServe) {
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      gameBoardPanel.getCenterPanel().showServePhaseUI(onEndServe);
    }
  }
  
  /**
   * 提供終了待ち表示
   */
  public void showServeEndWaiting(int completedCount, int totalCount) {
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      gameBoardPanel.getCenterPanel().showServeEndWaiting(completedCount, totalCount);
    }
  }
  
  /**
   * 特定プレイヤーの提供終了待ち表示（マルチプレイ用）
   */
  public void showServeEndWaitingForPlayer(int playerId, int completedCount, int totalCount) {
    // デフォルト実装：自分のビューに表示
    if (playerId == myPlayerId) {
      showServeEndWaiting(completedCount, totalCount);
    }
  }
  
  /**
   * 提供フェーズ終了表示
   */
  public void showServePhaseComplete() {
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      gameBoardPanel.getCenterPanel().showServePhaseComplete();
    }
  }
  
  /**
   * 提供済みカードを移動
   */
  public void moveCardToServed(game.model.order.OrderCard card) {
    if (gameBoardPanel != null && gameBoardPanel.getCustomerPanel() != null) {
      gameBoardPanel.getCustomerPanel().moveToServed(card);
      // 提供後に提供可能なカードの状態を更新
      gameBoardPanel.getCustomerPanel().updateCanServeStatus();
    }
  }

  public void showMessage(String message) {
    if (gameBoardPanel != null && gameBoardPanel.getLogPanel() != null) {
      gameBoardPanel.getLogPanel().addMessage(message);
    } else {
      System.out.println(message); // フォールバック
    }
  }
  
  /**
   * チャットメッセージを追加
   */
  public void addChatMessage(String sender, String message) {
    if (gameBoardPanel != null && gameBoardPanel.getLogPanel() != null) {
      gameBoardPanel.getLogPanel().addChatMessage(sender, message);
    }
  }
  
  /**
   * チャット送信コールバックを設定
   */
  public void setOnChatSend(java.util.function.BiConsumer<String, String> callback) {
    if (gameBoardPanel != null && gameBoardPanel.getLogPanel() != null) {
      gameBoardPanel.getLogPanel().setOnChatSend(callback);
    }
  }
  
  /**
   * チャットメッセージを全ビューに送信（マルチプレイヤー用）
   * サブクラス（BroadcastMainFrame）でオーバーライド
   */
  public void broadcastChatMessage(String sender, String message) {
    // デフォルト実装：ローカルに追加
    addChatMessage(sender, message);
  }

  public void showAuctionTruck(TruckCard truck) {
    // CenterPanelにトラックカードを表示
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      showMessage("オークション: " + truck.toString());
    }
  }

  public void showAuctionResult(Player winner) {
    if (winner != null) {
      showMessage(winner.getName() + " がオークションに勝利しました！");
    }
  }
  
  /**
   * CenterPanelにオークション表示を開始（旧API - 互換性のため残す）
   */
  public void showAuctionInCenterPanel(TruckCard truck, Player currentPlayer, Consumer<Integer> onBidSubmit) {
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      gameBoardPanel.getCenterPanel().showAuction(truck, currentPlayer, onBidSubmit);
    }
  }
  
  /**
   * CenterPanelにオークション表示を開始（マルチプレイヤー対応）
   * コールバックは (playerId, amount) の形式
   * サブクラス（BroadcastMainFrame）でオーバーライドして使用
   */
  public void showAuctionInCenterPanel(TruckCard truck, java.util.function.BiConsumer<Integer, Integer> onBidSubmit) {
    // デフォルト実装：シングルプレイヤー用（旧APIにフォールバック）
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      gameBoardPanel.getCenterPanel().showAuction(truck, null, amount -> {
        if (onBidSubmit != null) {
          onBidSubmit.accept(myPlayerId, amount);
        }
      });
    }
  }
  
  /**
   * CenterPanelの入札パネルを次のプレイヤーに更新
   */
  public void showBidForPlayer(Player player, Consumer<Integer> onBidSubmit) {
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      gameBoardPanel.getCenterPanel().showBidForPlayer(player, onBidSubmit);
    }
  }
  
  /**
   * 入札完了を表示（同時入札用）
   */
  public void showBidComplete(Player player) {
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      gameBoardPanel.getCenterPanel().showBidComplete(player);
    }
  }
  
  /**
   * 取引終了待機表示
   */
  public void showTradeEndWaiting(Player player, int completedCount, int totalCount) {
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      gameBoardPanel.getCenterPanel().showTradeEndWaiting(player, completedCount, totalCount);
    }
  }
  
  /**
   * 特定プレイヤーのビューに取引終了待機表示（マルチプレイヤー用）
   */
  public void showTradeEndWaitingForPlayer(int playerId, Player player, int completedCount, int totalCount) {
    // デフォルト実装：シングルプレイヤー用
    showTradeEndWaiting(player, completedCount, totalCount);
  }
  
  /**
   * CenterPanelにゲーム開始を表示
   */
  public void showGameStart() {
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      gameBoardPanel.getCenterPanel().showGameStart();
    }
  }
  
  /**
   * CenterPanelにラウンド開始を表示
   */
  public void showRoundStart(int roundNumber) {
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      gameBoardPanel.getCenterPanel().showRoundStart(roundNumber);
    }
  }
  
  /**
   * CenterPanelに全員の入札額を表示
   */
  public void showAllBidsInCenterPanel(java.util.Map<String, Integer> bidResults) {
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      gameBoardPanel.getCenterPanel().showAllBids(bidResults);
    }
  }
  
  /**
   * CenterPanelにオークション結果を表示
   */
  public void showAuctionResultInCenterPanel(Player winner, int winningBid) {
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      gameBoardPanel.getCenterPanel().showAuctionResult(winner, winningBid);
    }
  }
  
  /**
   * CenterPanelに取引終了を表示
   */
  public void showTradePhaseComplete() {
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      gameBoardPanel.getCenterPanel().showTradePhaseComplete();
    }
  }
  
  /**
   * プレイヤーのインベントリを更新
   */
  public void updatePlayerInventory(Player player) {
    if (gameBoardPanel != null && gameBoardPanel.getMarketPanel() != null) {
      gameBoardPanel.getMarketPanel().updatePlayerInventory(player);
    }
    // 自分の場合、InfoPanelの所持金も更新
    if (isMyPlayer(player)) {
      updateInfoPanelMoney(player.getMoney());
      // CustomerPanelのプレイヤー情報も更新（提供可能なカードの状態を再評価）
      if (gameBoardPanel != null && gameBoardPanel.getCustomerPanel() != null) {
        gameBoardPanel.getCustomerPanel().updatePlayer(player);
        // インベントリが変わったので、提供可能なカードの状態を更新
        gameBoardPanel.getCustomerPanel().updateCanServeStatus();
      }
    }
  }
  
  /**
   * InfoPanelの所持金を更新（南プレイヤー=自分）
   */
  public void updateInfoPanelMoney(int money) {
    if (gameBoardPanel != null && gameBoardPanel.getInfoPanel() != null) {
      gameBoardPanel.getInfoPanel().updateMoney(money);
    }
  }
  
  /**
   * CenterPanelのフェーズを切り替える
   */
  public void setCenterPanelPhase(CenterPanel.PhaseType phase) {
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      gameBoardPanel.getCenterPanel().setPhase(phase);
    }
  }
  
  /**
   * CenterPanelに取引フェーズの表示を開始
   */
  public void showTradePhase() {
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      gameBoardPanel.getCenterPanel().showTradePhase();
    }
  }
  
  /**
   * CenterPanelにプレイヤー名を設定
   */
  public void setPlayerNames(List<Player> players) {
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      String north = "", east = "", south = "", west = "";
      for (Player p : players) {
        switch (p.getId()) {
          case 0: south = p.getName(); break;
          case 1: east = p.getName(); break;
          case 2: north = p.getName(); break;
          case 3: west = p.getName(); break;
        }
      }
      gameBoardPanel.getCenterPanel().setPlayerNames(north, east, south, west);
      
      // LogPanelに南プレイヤーの名前を設定（チャット用）
      if (gameBoardPanel.getLogPanel() != null && !south.isEmpty()) {
        gameBoardPanel.getLogPanel().setPlayerName(south);
      }
    }
  }

  public void showTradeOfferDialog(Player from, Player to, TradeProposal proposal) {
    // 旧API - 互換性のため残す
    showMessage(from.getName() + " から " + to.getName() + " への取引提案");
  }
  
  /**
   * 取引作成UIをCenterPanelに表示（新API）
   */
  public void showTradeDialog(Player currentPlayer, List<Player> otherPlayers,
                              Consumer<TradeProposal> onSubmit, Runnable onCancel,
                              Runnable onEndTrade) {
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      gameBoardPanel.getCenterPanel().showTradeCreationUI(currentPlayer, otherPlayers, onSubmit, onEndTrade);
    }
  }
  
  /**
   * 取引作成UIをCenterPanelに表示（旧API互換）
   */
  public void showTradeDialog(Player currentPlayer, List<Player> otherPlayers,
                              Consumer<TradeProposal> onSubmit, Runnable onCancel) {
    showTradeDialog(currentPlayer, otherPlayers, onSubmit, onCancel, onCancel);
  }
  
  /**
   * 取引フェーズ開始（マルチプレイヤー対応）
   * 各ビューで自分の取引ダイアログを表示
   * @param onProposalSubmit (playerId, proposal)
   * @param onEndTradeRequest (playerId)
   */
  public void startTradePhaseForAllPlayers(
          java.util.function.BiConsumer<Integer, TradeProposal> onProposalSubmit,
          Consumer<Integer> onEndTradeRequest) {
    // デフォルト実装：シングルプレイヤー用（BroadcastMainFrameでオーバーライド）
  }
  
  /**
   * 特定のプレイヤーに取引ダイアログを再表示（マルチプレイヤー用）
   */
  public void showTradeDialogForPlayer(int playerId,
          java.util.function.BiConsumer<Integer, TradeProposal> onProposalSubmit,
          Consumer<Integer> onEndTradeRequest) {
    // デフォルト実装：シングルプレイヤー用（BroadcastMainFrameでオーバーライド）
  }
  
  /**
   * 受信した取引をダイアログで表示（送り先側）
   * 「交渉成立」「破談」ボタン
   */
  public void showReceivedTrade(TradeProposal proposal, Consumer<Boolean> onResponse) {
    game.view.dialogs.TradeDialog.showReceivedTradeDialog(this, proposal, onResponse);
  }
  
  /**
   * 取引提案を通知（マルチプレイ用、オーバーライドして使用）
   * @param proposal 取引提案
   * @param onResponse 応答コールバック（true: 承諾, false: 拒否）
   */
  public void notifyTradeProposal(TradeProposal proposal, Consumer<Boolean> onResponse) {
    // デフォルト実装：何もしない（シングルプレイでは使用しない）
  }
  
  /**
   * 取引提案を通知（旧API互換）
   */
  public void notifyTradeProposal(TradeProposal proposal) {
    // デフォルト実装：何もしない
  }
  
  /**
   * 取引提案をキャンセル（送り先のダイアログを閉じる）
   */
  public void cancelTradeProposal(TradeProposal proposal) {
    // デフォルト実装：何もしない
  }
  
  /**
   * 取引提案を更新（送り主が編集した場合）
   */
  public void updateTradeProposal(TradeProposal oldProposal, TradeProposal newProposal) {
    // デフォルト実装：何もしない
  }
  
  /**
   * 送信した取引をダイアログで表示（送り主側）
   * 「編集」「破談」ボタン
   * @return 表示されたダイアログ（後でメッセージ表示などに使用）
   */
  public javax.swing.JDialog showSentTrade(TradeProposal proposal, Consumer<String> onAction) {
    return game.view.dialogs.TradeDialog.showSentTradeDialog(this, proposal, onAction);
  }
  
  /**
   * 送信済み取引ダイアログに結果メッセージを表示して閉じる
   */
  public void showTradeResultAndClose(javax.swing.JDialog dialog, String message, java.awt.Color color, int delayMs) {
    game.view.dialogs.TradeDialog.showResultAndClose(dialog, message, color, delayMs);
  }

  public void showGameOverDialog(Player winner) {
    if (winner != null) {
      String message = "ゲーム終了！\n勝者: " + winner.getName() + "\n所持金: " + winner.getMoney() + "円";
      JOptionPane.showMessageDialog(this, message, "ゲーム終了", JOptionPane.INFORMATION_MESSAGE);
    }
  }
  
  /**
   * GameManagerを設定
   */
  public void setGameManager(GameManager gameManager) {
    this.gameManager = gameManager;
  }
  
  /**
   * GameManagerを取得
   */
  public GameManager getGameManager() {
    return gameManager;
  }
  
  /**
   * このビューの所有者のプレイヤーIDを設定（マルチプレイ用）
   */
  public void setMyPlayerId(int playerId) {
    this.myPlayerId = playerId;
  }
  
  /**
   * このビューの所有者のプレイヤーIDを取得
   */
  public int getMyPlayerId() {
    return myPlayerId;
  }
  
  /**
   * 指定されたプレイヤーが「自分」かどうか判定
   */
  public boolean isMyPlayer(Player player) {
    return player != null && player.getId() == myPlayerId;
  }
  
  /**
   * マルチプレイモードかどうか（オーバーライド用）
   * デフォルトはfalse（シングルプレイ/CPUあり）
   */
  public boolean isMultiPlayerMode() {
    return false;
  }
  
  /**
   * プレイヤーの注文カードをCustomerPanelに表示
   * （南プレイヤー=自分の手札を表示）
   */
  public void updatePlayerOrders(Player player) {
    if (gameBoardPanel != null && gameBoardPanel.getCustomerPanel() != null) {
      if (player != null) {
        gameBoardPanel.getCustomerPanel().setOrderCards(player.getOrders());
      }
    }
  }
  
  /**
   * CustomerPanelのカードをクリア
   */
  public void clearCustomerPanel() {
    if (gameBoardPanel != null && gameBoardPanel.getCustomerPanel() != null) {
      gameBoardPanel.getCustomerPanel().clearCards();
    }
  }
  
  /**
   * CustomerPanelで選択されたカードを取得
   */
  public List<OrderCard> getSelectedOrderCards() {
    if (gameBoardPanel != null && gameBoardPanel.getCustomerPanel() != null) {
      return gameBoardPanel.getCustomerPanel().getSelectedCards();
    }
    return new java.util.ArrayList<>();
  }
  
  // ========== ペナルティ関連 ==========
  
  /**
   * ペナルティフェーズの表示を開始
   */
  public void showPenaltyPhase(String title, String description) {
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      gameBoardPanel.getCenterPanel().showPenaltyPhase(title, description);
    }
  }
  
  /**
   * ペナルティメッセージを表示
   */
  public void showPenaltyMessage(String title, String message) {
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      gameBoardPanel.getCenterPanel().showPenaltyMessage(title, message);
    }
  }
  
  /**
   * コイントスUIを表示
   * @param culprit 戦犯プレイヤー
   * @param canToss このプレイヤーがコインを投げられるか（自分が戦犯の場合true）
   * @param onResult コイントス結果のコールバック（true: お酒の面, false: お金の面）
   */
  public void showCoinToss(Player culprit, boolean canToss, Consumer<Boolean> onResult) {
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      gameBoardPanel.getCenterPanel().showCoinToss(culprit, canToss, onResult);
    }
  }
  
  /**
   * コイントスを実行（CPUが戦犯の場合に自動実行）
   */
  public void executeCoinToss(boolean result) {
    if (gameBoardPanel != null && gameBoardPanel.getCenterPanel() != null) {
      gameBoardPanel.getCenterPanel().executeCoinToss(result);
    }
  }
}