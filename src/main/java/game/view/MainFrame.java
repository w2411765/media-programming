// メイン画面のJFrame
package game.view;

import javax.swing.*;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

 
public class MainFrame extends JFrame {
  public MainFrame(){
    this.setTitle("CAPONE");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.add(new GameBoardPanel());
    
    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    gd.setFullScreenWindow(this);
    
    this.setVisible(true);
  }
}