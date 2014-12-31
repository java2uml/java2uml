package com.github.java2uml.gui;

import com.github.java2uml.core.Main;
import net.sourceforge.plantuml.SourceStringReader;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import static java.lang.String.*;

public class UIEntry {
    UI ui;
    String[] args;


    private String[] gettingParametersFromUI() {
        args = new String[9];
        for (int i = 0; i < args.length; i++) {
            args[i] = "";
        }

        args[0] = ui.getParsingCheckboxItem().getState() ? "java" : "class";
        args[1] = ui.getPath().getText().toString();
        args[2] = !ui.getShowHeader().getState() ? "noheader" : "";
        args[3] = ui.getClassDiagramCheckboxItem().getState() ? "classes_diagram" : "sequence_diagram";
        args[4] = ui.getVerticalDirectionCheckboxItem().getState() ? "vertical" : "horizontal";
        args[5] = !ui.getShowComposition().getState() ? "nocomposition" : "";
        args[6] = !ui.getShowAggregation().getState() ? "noaggregation" : "";
        args[7] = !ui.getShowAssociation().getState() ? "noassociation" : "";
        args[8] = !ui.getShowLollipops().getState() ? "nolollipop" : "";

        for (String str : args) System.out.println(str);

        return args;
    }


    public void initUI() {
        ui = UI.getInstance();
        ui.initUI().setVisible(true);
        ui.addActionListenerToChooseFile();
        ui.getGeneratePlantUML().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                        try {
                            Main.main(gettingParametersFromUI());
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                try {
                    Main.main(gettingParametersFromUI());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }

        });
        ui.settingStateForAllOptions();

    }

    public static void main(String[] args) {
        final UIEntry uiEntry = new UIEntry();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                uiEntry.initUI();
            }
        });

    }

    public static void generateDiagram(final String source, final String fileName) {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            // поток вывода для диаграммы
            OutputStream png = new FileOutputStream(file);

            // генератор диаграмм
            SourceStringReader reader = new SourceStringReader(source);

            // генерация диаграммы
            String desc = reader.generateImage(png);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
