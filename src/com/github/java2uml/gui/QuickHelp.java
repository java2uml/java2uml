package com.github.java2uml.gui;

import javax.swing.*;

/**
 * Created by mac on 28.01.15.
 */
public class QuickHelp extends JFrame {
    private JLabel imageLabel;
    private JScrollPane scrollPane;
    private static QuickHelp quickHelp;

    public static boolean quickHelpIsNull(){
        return quickHelp == null;
    }

    public static QuickHelp getInstance(){
        if (quickHelp == null) {
            quickHelp = new QuickHelp();
        }
        return quickHelp;
    }

    private QuickHelp(){
        super("Java2UML Quick Help");

        imageLabel = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("quickhelp.png")));
        scrollPane = new JScrollPane(imageLabel);

        this.add(scrollPane);
        this.setSize(670, 480);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static void main(String[] args) {
        new QuickHelp();
    }
}
