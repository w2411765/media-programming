// メイン画面の親パネル
package game.view;

import java.awt.*;
import javax.swing.*;
import game.view.components.*;


public class GameBoardPanel extends JPanel {
    public GameBoardPanel() {
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(250, 250, 250));
        
        MarketPanel marketPanel = new MarketPanel();
        LogPanel logPanel = new LogPanel();
        CustomerPanel customerPanel = new CustomerPanel();
        InfoPanel infoPanel = new InfoPanel();
        
        this.add(marketPanel, BorderLayout.CENTER);
        this.add(logPanel, BorderLayout.EAST);
        this.add(customerPanel, BorderLayout.WEST);
        this.add(infoPanel, BorderLayout.SOUTH);
    }
}