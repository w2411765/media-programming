package game.view.components;

import java.awt.*;
import javax.swing.*;

public class LogPanel extends JPanel {
    public LogPanel() {
        this.setBackground(new Color(255, 255, 200));
        this.setBorder(BorderFactory.createTitledBorder("Log Panel"));
        this.add(new JLabel("Game Log"));
        this.setPreferredSize(new Dimension(440, 0));
    }
}
