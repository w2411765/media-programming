package game.view.components;

import java.awt.*;
import javax.swing.*;

public class AlcoholPanel extends JPanel {
    public AlcoholPanel(int num) {
        this.setOpaque(false); // 背景を透明にして、親の背景画像が見えるようにする
        this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        
        Dimension size = new Dimension(50, 50);
        this.setPreferredSize(size);
        this.setMaximumSize(size);
        this.setMinimumSize(size);
        this.setLayout(new BorderLayout());
        
        JLabel label = new JLabel(String.valueOf(num), SwingConstants.CENTER);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        label.setOpaque(false); // ラベルも透明にする
        this.add(label, BorderLayout.CENTER);
    }
}

