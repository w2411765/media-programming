package game.view.components;

import java.awt.*;
import javax.swing.*;

public class CustomerPanel extends JPanel {
    private Image backgroundImage;
    
    public CustomerPanel() {
        this.setBackground(new Color(200, 255, 200));
        this.setBorder(BorderFactory.createTitledBorder("Customer Panel"));
        this.add(new JLabel("Customers"));
        this.setPreferredSize(new Dimension(440, 0));
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/ui/speakeasy.png"));
        backgroundImage = icon.getImage();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2d.dispose();
        }
    }
}

