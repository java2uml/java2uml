package com.github.java2uml.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by mac on 06.02.15.
 */
public class WantToShowQuickHelpDialog extends JFrame {
    JPanel panelForButtons;
    JButton ok, cancel;
    JLabel label;
    UI ui;

    public WantToShowQuickHelpDialog() {
        panelForButtons = new JPanel();
        ui = UI.getInstance();
        ok = new JButton("Ok");
        cancel = new JButton(ui.getLocaleLabels().getString("cancelLabel"));
        label = new JLabel(ui.getLocaleLabels().getString("wantToShowQuickHelp"));
        panelForButtons.setLayout(new BoxLayout(panelForButtons, BoxLayout.X_AXIS));

        panelForButtons.add(cancel);
        panelForButtons.add(ok);

        add(BorderLayout.NORTH, label);
        add(BorderLayout.SOUTH, panelForButtons);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new WantToShowQuickHelpDialog();
    }
}
