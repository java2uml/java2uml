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

    public void initUI() {
        ui = UI.getInstance();
        ui.initUI().setVisible(true);
        ui.addActionListenerToChooseFile();
        ui.getGeneratePlantUML().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread generateDiagramThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        Main.main();
//                        loadClassesAndGenerateDiagram(ui.getPath().getText());
                    }
                });
                generateDiagramThread.start();

            }
        });
        ui.disablingNonWorkingOptions();

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
