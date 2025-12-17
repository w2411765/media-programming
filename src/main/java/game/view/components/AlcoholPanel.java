package game.view.components;

import java.awt.*;
import javax.swing.*;

public class AlcoholPanel extends JPanel {
    public AlcoholPanel(int num) {
        this.setBackground(new Color(255, 255, 255));

        this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        Dimension size = new Dimension(50, 50);
        this.setPreferredSize(size);
        this.setMaximumSize(size);
        this.setMinimumSize(size);
        this.setLayout(new BorderLayout());
        
        JLabel label = new JLabel(String.valueOf(num), SwingConstants.CENTER);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        this.add(label, BorderLayout.CENTER);
    }
}

