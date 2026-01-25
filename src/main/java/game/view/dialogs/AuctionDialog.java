package game.view.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import game.model.Player;
import game.model.alcohol.TruckCard;
import game.model.alcohol.AlcoholType;
import game.view.components.AlcoholPanel;

/**
 * オークションダイアログ
 */
public class AuctionDialog extends JDialog {
    private int bidAmount = 0;
    private boolean submitted = false;
    private JSpinner bidSpinner;
    
    public AuctionDialog(JFrame parent, Player player, TruckCard truck) {
        super(parent, "オークション - " + player.getName(), true);
        
        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(parent);
        
        // トラックカード情報を表示
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("トラックカードの内容:");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        
        if (truck != null) {
            // お酒のアイコンを横に並べて表示
            JPanel alcoholPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            alcoholPanel.setOpaque(false);
            
            for (AlcoholType type : AlcoholType.values()) {
                int count = truck.getCount(type);
                if (count > 0) {
                    // アイコンとラベルを縦に並べたパネル
                    JPanel itemPanel = new JPanel();
                    itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
                    itemPanel.setOpaque(false);
                    
                    AlcoholPanel icon = new AlcoholPanel(type, count);
                    icon.setAlignmentX(Component.CENTER_ALIGNMENT);
                    itemPanel.add(icon);
                    
                    JLabel label = new JLabel(type.name() + " x" + count);
                    label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
                    label.setAlignmentX(Component.CENTER_ALIGNMENT);
                    itemPanel.add(label);
                    
                    alcoholPanel.add(itemPanel);
                }
            }
            infoPanel.add(alcoholPanel);
        }
        
        infoPanel.add(Box.createVerticalStrut(20));
        
        // 入札額入力
        JPanel bidPanel = new JPanel(new FlowLayout());
        bidPanel.add(new JLabel("入札額:"));
        
        SpinnerNumberModel model = new SpinnerNumberModel(0, 0, player.getMoney(), 1);
        bidSpinner = new JSpinner(model);
        bidSpinner.setPreferredSize(new Dimension(100, 25));
        bidPanel.add(bidSpinner);
        bidPanel.add(new JLabel("円 (所持金: " + player.getMoney() + "円)"));
        
        infoPanel.add(bidPanel);
        
        add(infoPanel, BorderLayout.CENTER);
        
        // ボタンパネル
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton submitButton = new JButton("入札");
        JButton cancelButton = new JButton("キャンセル");
        
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bidAmount = (Integer) bidSpinner.getValue();
                if (bidAmount > player.getMoney()) {
                    JOptionPane.showMessageDialog(AuctionDialog.this, 
                        "所持金が足りません", "エラー", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                submitted = true;
                dispose();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitted = false;
                dispose();
            }
        });
        
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * 入札額を取得
     */
    public int getBidAmount() {
        return bidAmount;
    }
    
    /**
     * 入札が提出されたか
     */
    public boolean isSubmitted() {
        return submitted;
    }
    
    /**
     * ダイアログを表示して入札額を取得
     */
    public static int showBidDialog(JFrame parent, Player player, TruckCard truck) {
        AuctionDialog dialog = new AuctionDialog(parent, player, truck);
        dialog.setVisible(true);
        
        if (dialog.isSubmitted()) {
            return dialog.getBidAmount();
        }
        return -1; // キャンセル
    }
}
