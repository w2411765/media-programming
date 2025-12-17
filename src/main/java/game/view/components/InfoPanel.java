package game.view.components;

import java.awt.*;
import javax.swing.*;

public class InfoPanel extends JPanel {
    public InfoPanel() {
        this.setBackground(new Color(255, 200, 255));
        this.setBorder(BorderFactory.createTitledBorder("Info Panel"));
        this.add(new JLabel("Game Info"));
    }
}

