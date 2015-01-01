package com.github.java2uml.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by mac on 01.01.15.
 */
public class About extends JFrame {
    JLabel logo, version, team;
    JPanel versionPanel, logoPanel, teamPanel, logoAndVersionPanel;

    public About(String title){
        super(title);




        version = new JLabel("Java2UML \n v.1.0");
        team = new JLabel();

        versionPanel = new JPanel();
        logoPanel = new JPanel();
        teamPanel = new JPanel();

        logoAndVersionPanel = new JPanel();

        logoAndVersionPanel.setLayout(new BoxLayout(logoAndVersionPanel ,BoxLayout.X_AXIS));
        versionPanel.add(version);
        logoPanel.add(logo);
        logoAndVersionPanel.add(logoPanel);
        logoAndVersionPanel.add(versionPanel);
        add(logoAndVersionPanel);
        setSize(300, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}
