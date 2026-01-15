// メイン画面の親パネル
package game.view;

import java.awt.*;
import javax.swing.*;
import game.view.components.*;


public class GameBoardPanel extends JPanel {
    public GameBoardPanel() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.BLACK);
        
        MarketPanel marketPanel = new MarketPanel();
        LogPanel logPanel = new LogPanel();
        CustomerPanel customerPanel = new CustomerPanel();
        InfoPanel infoPanel = new InfoPanel();
        ToolPanel toolPanel = new ToolPanel();
        
        // 各パネルをラップしてマージンを追加（パネル間の距離を広げる）
        JPanel marketWrapper = new JPanel(new BorderLayout());
        marketWrapper.setOpaque(false);
        marketWrapper.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8)); // 8pxのマージン
        marketWrapper.add(marketPanel, BorderLayout.CENTER);
        
        JPanel logWrapper = new JPanel(new BorderLayout());
        logWrapper.setOpaque(false);
        logWrapper.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8)); // 8pxのマージン
        logWrapper.add(logPanel, BorderLayout.CENTER);
        
        JPanel customerWrapper = new JPanel(new BorderLayout());
        customerWrapper.setOpaque(false);
        customerWrapper.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8)); // 8pxのマージン
        customerWrapper.add(customerPanel, BorderLayout.CENTER);
        
        JPanel infoWrapper = new JPanel(new BorderLayout());
        infoWrapper.setOpaque(false);
        infoWrapper.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8)); // 8pxのマージン
        infoWrapper.add(infoPanel, BorderLayout.CENTER);
        
        JPanel toolWrapper = new JPanel(new BorderLayout());
        toolWrapper.setOpaque(false);
        toolWrapper.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8)); // 8pxのマージン
        toolWrapper.add(toolPanel, BorderLayout.CENTER);
        
        this.add(marketWrapper, BorderLayout.CENTER);
        this.add(logWrapper, BorderLayout.EAST);
        this.add(customerWrapper, BorderLayout.WEST);
        this.add(infoWrapper, BorderLayout.SOUTH);
        this.add(toolWrapper, BorderLayout.NORTH);
    }
}