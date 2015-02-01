package com.github.java2uml.gui;

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.BorderUIResource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by mac on 01.01.15.
 */
public class About extends JFrame {
    JLabel logo, version, about, copyright;
    JPanel mainAboutPanel;

    public About(String title) {
        super(title);
        version = new JLabel("<html><b>Java2UML v.0.4a</b></html>");
        logo = null;
        copyright = new JLabel(UI.getInstance().getLocaleLabels().getString("copyrightLabel"));
        mainAboutPanel = new JPanel();
        about = new JLabel(UI.getInstance().getLocaleLabels().getString("aboutLabel"));
        mainAboutPanel.setLayout(new GridBagLayout());

        TitledBorder titledBorder = new TitledBorder("<html><b>Java2UML v.0.4a</b></html>");
        titledBorder.setTitleJustification(TitledBorder.CENTER);


        this.getContentPane();
        about.setBorder(BorderFactory.createCompoundBorder(titledBorder, new EmptyBorder(4, 4, 4, 4)));

        try {
            BufferedImage bufferedImage = ImageIO.read(getClass().getClassLoader().getResource("about_logo.png"));
            bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.ULTRA_QUALITY, 70);
            logo = new JLabel(new ImageIcon(bufferedImage));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mainAboutPanel.add(logo, new GridBagConstraints(10, 0, 2, 2, 5, 3, GridBagConstraints.CENTER, GridBagConstraints.ABOVE_BASELINE_LEADING, new Insets(5, 0, 0, 0), 0, 0));
        mainAboutPanel.add(about, new GridBagConstraints(10, 3, 5, 5, 7, 5, GridBagConstraints.CENTER, GridBagConstraints.REMAINDER, new Insets(5, 0, 0, 0), 0, 0));
        mainAboutPanel.add(copyright, new GridBagConstraints(10, 25, 3, 2, 3, 2, GridBagConstraints.CENTER, GridBagConstraints.REMAINDER, new Insets(3, 0, 3, 0), 0, 0));

        add(BorderLayout.NORTH, mainAboutPanel);
        setSize(340, 600);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }
}
