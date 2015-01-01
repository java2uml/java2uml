package com.github.java2uml.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by mac on 01.01.15.
 */
public class ExceptionDialog extends JDialog {
    public ExceptionDialog(JFrame parent, String title, String message){
        super(parent, title);


        setSize(350,170);
        setVisible(true);
        setLocationRelativeTo(null);
    }
}
