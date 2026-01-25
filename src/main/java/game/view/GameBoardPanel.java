// メイン画面の親パネル
package game.view;

import java.awt.*;
import javax.swing.*;
import game.view.components.*;


public class GameBoardPanel extends JPanel {
    private LogPanel logPanel;
    private InfoPanel infoPanel;
    private CenterPanel centerPanel;
    private ToolPanel toolPanel;
    private MarketPanel marketPanel;
    
    public GameBoardPanel() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.BLACK);
        
        marketPanel = new MarketPanel();
        logPanel = new LogPanel();
        CustomerPanel customerPanel = new CustomerPanel();
        infoPanel = new InfoPanel();
        toolPanel = new ToolPanel();
        
        // CenterPanelを取得（MarketPanelから）
        centerPanel = marketPanel.getCenterPanel();
        
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
    
    public LogPanel getLogPanel() {
        return logPanel;
    }
    
    public InfoPanel getInfoPanel() {
        return infoPanel;
    }
    
    public CenterPanel getCenterPanel() {
        return centerPanel;
    }
    
    public ToolPanel getToolPanel() {
        return toolPanel;
    }
    
    public MarketPanel getMarketPanel() {
        return marketPanel;
    }
}