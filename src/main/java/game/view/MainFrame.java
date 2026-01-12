// メイン画面のJFrame
package game.view;

import javax.swing.*;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import game.model.Player;
import game.model.alcohol.TruckCard;
import game.controller.TradeProposal;
import java.util.List;

 
public class MainFrame extends JFrame {
  public MainFrame(){
    this.setTitle("CAPONE");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.add(new GameBoardPanel());
    
    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    gd.setFullScreenWindow(this);
    
    this.setVisible(true);
  }

  // ----- 最低限のスタブメソッド（実装は後で追加） -----

  public void updateRoundInfo(int round) {
    // TODO: 実装が必要
  }

  public void updateAllPlayersState(List<Player> players) {
    // TODO: 実装が必要
  }

  public void enableTradeUI(boolean enabled) {
    // TODO: 実装が必要
  }

  public void enableServeUI(boolean enabled) {
    // TODO: 実装が必要
  }

  public void showMessage(String message) {
    // TODO: 実装が必要
    System.out.println(message); // デバッグ用
  }

  public void showAuctionTruck(TruckCard truck) {
    // TODO: 実装が必要
  }

  public void showAuctionResult(Player winner) {
    // TODO: 実装が必要
  }

  public void showTradeOfferDialog(Player from, Player to, TradeProposal proposal) {
    // TODO: 実装が必要
  }

  public void showGameOverDialog(Player winner) {
    // TODO: 実装が必要
  }
}