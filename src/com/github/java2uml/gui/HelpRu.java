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

public class HelpRu extends JFrame implements ActionListener {

    private final int WIDTH = 600;
    private final int HEIGHT = 400;
    private JEditorPane editorPane;
    private URL helpURL;
    private static HelpRu helpRu;

    public static boolean helpIsNull() {
        return helpRu == null;
    }

    public static HelpRu getInstance() {
        if (helpRu == null) {
            helpRu = new HelpRu("Java2UML Помощь");
        }
        return helpRu;
    }

    private HelpRu(String _title) {
        super(_title);

        URL helpURL = getClass().getClassLoader().getResource("ruHelp/help.html");

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
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        if (!e.getURL().toString().contains("graph") && !e.getURL().toString().contains("wikipedia") &&
                                !e.getURL().toString().contains("plantuml") && !e.getURL().toString().contains("referal")) {
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
            if (strAction == "Close") {
                processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}


