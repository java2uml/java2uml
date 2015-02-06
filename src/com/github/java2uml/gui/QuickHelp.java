package com.github.java2uml.gui;

import javax.swing.*;

public class QuickHelp extends JFrame {
    private static QuickHelp quickHelp;

    public static boolean quickHelpIsNull(){
        return quickHelp == null;
    }

    public static QuickHelp getInstance(){
        return QuickHelpHolder.quickHelpInstance;
    }

    private static class QuickHelpHolder {
        static final QuickHelp quickHelpInstance = new QuickHelp();
    }

    private QuickHelp(){
        super("Java2UML Quick Help");

        JLabel imageLabel = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("quickhelp.png")));
        JScrollPane scrollPane = new JScrollPane(imageLabel);

        this.add(scrollPane);
        this.setSize(670, 480);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static void main(String[] args) {
        new QuickHelp();
    }
}
