package game.view.components;

import java.awt.*;
import javax.swing.*;

public class ToolPanel extends JPanel {
    public ToolPanel() {
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(0, 100)); // 高さ100（InfoPanelと同じ）
        this.add(new JLabel("Tools"));
    }
}

