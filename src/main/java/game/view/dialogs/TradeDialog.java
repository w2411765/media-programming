package game.view.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import game.model.Player;
import game.model.alcohol.AlcoholType;
import game.controller.TradeProposal;
import java.util.Map;

/**
 * 取引ダイアログ
 */
public class TradeDialog extends JDialog {
    private boolean accepted = false;
    private TradeProposal proposal;
    private boolean isProposer; // 提案者側かどうか
    
    /**
     * 取引提案を作成するダイアログ（提案者側）
     */
    public TradeDialog(JFrame parent, Player from, Player to) {
        super(parent, "取引提案 - " + from.getName(), true);
        this.isProposer = true;
        
        setLayout(new BorderLayout());
        setSize(500, 400);
        setLocationRelativeTo(parent);
        
        // 取引内容入力パネル
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        inputPanel.add(new JLabel("相手: " + to.getName()));
        inputPanel.add(Box.createVerticalStrut(10));
        
        // 簡易版：お金のみの取引
        JPanel moneyPanel = new JPanel(new FlowLayout());
        moneyPanel.add(new JLabel("渡す金額:"));
        JSpinner moneyOfferedSpinner = new JSpinner(new SpinnerNumberModel(0, 0, from.getMoney(), 1));
        moneyPanel.add(moneyOfferedSpinner);
        moneyPanel.add(new JLabel("円"));
        
        inputPanel.add(moneyPanel);
        
        moneyPanel = new JPanel(new FlowLayout());
        moneyPanel.add(new JLabel("受け取る金額:"));
        JSpinner moneyRequestedSpinner = new JSpinner(new SpinnerNumberModel(0, 0, to.getMoney(), 1));
        moneyPanel.add(moneyRequestedSpinner);
        moneyPanel.add(new JLabel("円"));
        
        inputPanel.add(moneyPanel);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(new JLabel("※ 酒の交換機能は今後実装予定"));
        
        add(inputPanel, BorderLayout.CENTER);
        
        // ボタンパネル
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton proposeButton = new JButton("提案");
        JButton cancelButton = new JButton("キャンセル");
        
        proposeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Map<AlcoholType, Integer> emptyAlcohol = new java.util.HashMap<>();
                proposal = new TradeProposal(
                    from, to,
                    emptyAlcohol, emptyAlcohol,
                    (Integer) moneyOfferedSpinner.getValue(),
                    (Integer) moneyRequestedSpinner.getValue()
                );
                accepted = true;
                dispose();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accepted = false;
                dispose();
            }
        });
        
        buttonPanel.add(proposeButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * 取引提案を表示するダイアログ（相手側）
     */
    public TradeDialog(JFrame parent, TradeProposal proposal) {
        super(parent, "取引提案", true);
        this.proposal = proposal;
        this.isProposer = false;
        
        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(parent);
        
        // 取引内容表示
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        infoPanel.add(new JLabel(proposal.getFrom().getName() + " からの取引提案"));
        infoPanel.add(Box.createVerticalStrut(10));
        
        if (proposal.getMoneyOffered() > 0) {
            infoPanel.add(new JLabel("受け取る金額: " + proposal.getMoneyOffered() + "円"));
        }
        if (proposal.getMoneyRequested() > 0) {
            infoPanel.add(new JLabel("支払う金額: " + proposal.getMoneyRequested() + "円"));
        }
        
        add(infoPanel, BorderLayout.CENTER);
        
        // ボタンパネル
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton acceptButton = new JButton("承諾");
        JButton rejectButton = new JButton("拒否");
        
        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accepted = true;
                dispose();
            }
        });
        
        rejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accepted = false;
                dispose();
            }
        });
        
        buttonPanel.add(acceptButton);
        buttonPanel.add(rejectButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * 取引が承諾されたか
     */
    public boolean isAccepted() {
        return accepted;
    }
    
    /**
     * 取引提案を取得
     */
    public TradeProposal getProposal() {
        return proposal;
    }
    
    /**
     * 取引提案を作成するダイアログを表示
     */
    public static TradeProposal showProposeDialog(JFrame parent, Player from, Player to) {
        TradeDialog dialog = new TradeDialog(parent, from, to);
        dialog.setVisible(true);
        
        if (dialog.isAccepted()) {
            return dialog.getProposal();
        }
        return null;
    }
    
    /**
     * 取引提案を表示して応答を取得
     */
    public static boolean showResponseDialog(JFrame parent, TradeProposal proposal) {
        TradeDialog dialog = new TradeDialog(parent, proposal);
        dialog.setVisible(true);
        return dialog.isAccepted();
    }
}
