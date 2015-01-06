package com.github.java2uml.gui;

import com.github.java2uml.core.Main;
import com.github.java2uml.core.Options;
import net.sourceforge.plantuml.SourceStringReader;
import org.stathissideris.ascii2image.core.FileUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;

public class UIEntry {
    static UI ui;
    String[] args;
    String plantUMLCode;
    SwingWorkerForBackgroundGenerating swingWorker;
    static ExceptionListener exceptionListener;


    private String[] gettingParametersFromUI() {
        args = new String[9];
        for (int i = 0; i < args.length; i++) {
            args[i] = "";
        }
        ui.increaseProgressBarForTwenty();
        args[0] = ui.getParsingCheckboxItem().getState() ? "java" : "class";
        args[1] = ui.getPath().getText().toString();
        args[2] = !ui.getShowHeader().getState() ? "" : "";
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
        GenerateActionListener generateActionListener = new GenerateActionListener();
        ui = UI.getInstance();
        exceptionListener = ui;
        ui.initUI().setVisible(true);
        ui.getCancelLoading().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ui.getLabelForDiagram().setIcon(null);
                try {
                    swingWorker.cancel(true);
                } catch (Exception ex){
                    exceptionListener.handleExceptionAndShowDialog(ex);
                }
                ui.getGeneratePlantUML().setEnabled(true);
                ui.getProgressBar().setString("0%");
                ui.getProgressBar().setValue(0);
            }
        });
        ui.getGeneratePlantUML().addActionListener(generateActionListener);
        ui.getGenerateItem().addActionListener(generateActionListener);
        ui.settingStateForAllOptions();

    }

    public static void main(String[] args) {

        final UIEntry uiEntry = new UIEntry();
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    uiEntry.initUI();

                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
            exceptionListener.handleExceptionAndShowDialog(e);

        } catch (InvocationTargetException e) {
            e.printStackTrace();
            exceptionListener.handleExceptionAndShowDialog(e);
        }

        
    }

    private String generatePlantUMLAndLoadToTextArea(String outputPath) {
        plantUMLCode = null;
        try {
            plantUMLCode = FileUtils.readFile(new File(outputPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ui.getGeneratedCode().setText(plantUMLCode);
        return plantUMLCode;
    }

    public void generateDiagram(String source, String fileName) {
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



        } catch (Exception e) {
            e.printStackTrace();
            exceptionListener.handleExceptionAndShowDialog(e);
        }
    }

    public class GenerateActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                ui.getLabelForDiagram().setIcon(null);
                ui.getProgressBar().setValue(0);
                ui.getProgressBar().setString("0%");
                swingWorker = new SwingWorkerForBackgroundGenerating();
                swingWorker.execute();

            } catch (Exception e1) {
                e1.printStackTrace();
                exceptionListener.handleExceptionAndShowDialog(e1);
            }
        }
    }
    public class SwingWorkerForBackgroundGenerating extends SwingWorker<String,String> {
        @Override
        protected String doInBackground() throws Exception {

                    try {
                        while (!isCancelled()) {
                            ui.getGeneratePlantUML().setEnabled(false);
                            ui.getProgressBar().setString("Loading files...");
                            ui.increaseProgressBarForTwenty();
                            Main.main(gettingParametersFromUI());
                            ui.getProgressBar().setString("Code generation...");
                            ui.increaseProgressBarForTwenty();
                            generatePlantUMLAndLoadToTextArea(Options.getOutputFile());
                            ui.getProgressBar().setString("Loading diagram...");
                            ui.increaseProgressBarForTwenty();
                            generateDiagram(plantUMLCode, "diagram.png");
                            ui.showDiagram("diagram.png");
                            ui.setProgressBarComplete();
                            ui.getProgressBar().setString("Complete");
                            ui.getGeneratePlantUML().setEnabled(true);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        ui.getProgressBar().setString("0%");
                        ui.getProgressBar().setValue(0);
                        exceptionListener.handleExceptionAndShowDialog(e);
                        ui.getGeneratePlantUML().setEnabled(true);
                    }
            return "";
        }

    }
}
