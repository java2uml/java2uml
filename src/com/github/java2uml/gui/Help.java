package com.github.java2uml.gui;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by mac on 29.12.14.
 */
public class Help extends JFrame implements ActionListener {
    private final int WIDTH = 600;
    private final int HEIGHT = 400;
    private JEditorPane editorPane;
    private URL helpURL;
    private static Help help;

    public static boolean helpIsNull(){
        return help == null;
    }

    public static Help getInstance(){
        if (help == null){
            help = new Help("Java2UML Help");
        }
        return help;
    }

    private Help(String _title){
        super(_title);
        URL helpURL = null;
        File file = new File("/src/com/github/java2uml/gui/help/help.html");
        try {
            helpURL = file.toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        editorPane = new JEditorPane();
        editorPane.setEditable(false);

        try {
            editorPane.setPage(helpURL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        editorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
                    try {
                        if (!e.getURL().toString().contains("graph") && !e.getURL().toString().contains("wikipedia") &&
                                !e.getURL().toString().contains("plantuml")) {
                            editorPane.setPage(e.getURL());
                        } else {
                            try {
                                Desktop.getDesktop().browse(e.getURL().toURI());
                            } catch (URISyntaxException e1) {
                                e1.printStackTrace();
                            }
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        getContentPane().add(new JScrollPane(editorPane));
        setSize(600, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String strAction = e.getActionCommand();
        URL tempURL;
        try {
            if (strAction == "Contents") {
                tempURL = editorPane.getPage();
                editorPane.setPage(tempURL);
            }
            if (strAction == "Close"){
                processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }


}
