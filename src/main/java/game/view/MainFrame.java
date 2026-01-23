// メイン画面のJFrame
package game.view;

import javax.swing.*;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import game.model.Player;
import game.model.alcohol.TruckCard;
import game.controller.TradeProposal;
import game.controller.GameManager;
import java.util.List;
import game.view.components.TitlePanel;
import game.view.GameBoardPanel;
import game.view.components.CenterPanel;
 
public class MainFrame extends JFrame {
  private GameBoardPanel gameBoardPanel;
  private GameManager gameManager;
  
  public MainFrame(){
    this.setTitle("CAPONE");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.add(new TitlePanel());
    
    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    gd.setFullScreenWindow(this);
    
    this.setVisible(true);
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
    // ToolPanelに提供ボタンを追加する場合はここで実装
    if (gameBoardPanel != null && gameBoardPanel.getToolPanel() != null) {
      // 実装は後で追加
    }
  }

  public void showMessage(String message) {
    if (gameBoardPanel != null && gameBoardPanel.getLogPanel() != null) {
      gameBoardPanel.getLogPanel().addMessage(message);
    } else {
      System.out.println(message); // フォールバック
    }
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

  public void showTradeOfferDialog(Player from, Player to, TradeProposal proposal) {
    // TradeDialogを表示
    game.view.dialogs.TradeDialog dialog = new game.view.dialogs.TradeDialog(this, proposal);
    dialog.setVisible(true);
    
    // 応答を処理（GameManagerに通知する必要があるが、ここでは簡易実装）
    if (dialog.isAccepted()) {
      showMessage(to.getName() + " が取引を承諾しました");
    } else {
      showMessage(to.getName() + " が取引を拒否しました");
    }
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
}