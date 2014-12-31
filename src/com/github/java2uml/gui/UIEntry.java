package com.github.java2uml.gui;

import com.github.java2uml.core.Main;
import net.sourceforge.plantuml.SourceStringReader;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

/**
 * Created by mac on 28.12.14.
 */
public class UIEntry {
    UI ui;
    String[] args;


    private String[] gettingParametersFromUI(){
        args = new String[9];
        for (int i = 0; i < args.length; i++) {
            args[i] = "";
        }
        if (ui.getParsingCheckboxItem().getState()){
            args[0] = "java";
        } else args[0] = "class";

        args[1] = ui.getPath().getText().toString();

        if (!ui.getShowHeader().getState()){
            args[2] = "noheader";
        }


        if (ui.getClassDiagramCheckboxItem().getState()) {
            args[3] = "classes_diagram";
        } else {
            args[3] = "sequence_diagram";
        }
        if (ui.getVerticalDirectionCheckboxItem().getState()) {
            args[4] = "vertical";
        } else args[4] = "horizontal";
        if (!ui.getShowComposition().getState()){
            args[5] = "nocomposition";
        }
        if (!ui.getShowAggregation().getState()){
            args[6] = "noaggregation";
        }
        if (!ui.getShowAssociation().getState()){
            args[7] = "noassociation";
        }
        if (!ui.getShowLollipops().getState()){
            args[8] = "nolollipop";
        }
        for (int i = 0; i < args.length; i++){
            System.out.println(args[i]);
        }
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

            // генерация жиаграммы
            String desc = reader.generateImage(png);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
