package script;


import javax.swing.*;
import java.awt.*;

public class UserInterface extends JPanel {
    public final JPanel rootPanel;

    public UserInterface(SimpleChopper script) {
        // Create new JPanel
        this.rootPanel = new JPanel(new GridLayout(0, 1));

        // Add element(s) to panel
        final JLabel label = new JLabel("Click the start button!");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        rootPanel.add(label);

        // Start button
        final JButton startBtn = new JButton("Start");
        startBtn.setHorizontalAlignment(SwingConstants.CENTER);
        startBtn.setFocusPainted(false);
        rootPanel.add(startBtn);

        // Start script on button click
        startBtn.addActionListener(e -> {
            // Disable button after press
            startBtn.setEnabled(false);

            label.setText("RUNNING");

            // Signal script we're ready to run
            script.pressedStart.set(true);
        });
    }
}