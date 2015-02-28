package com.github.java2uml.gui;

import com.github.java2uml.core.Options;
import com.github.java2uml.plugin.idea.PluginSettings;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.stathissideris.ascii2image.core.FileUtils;

import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.util.List;

/**
 * Created by Андрей on 17.02.2015.
 */
public class SwingWorkerForBackgroundGenerating extends SwingWorker<String, String> {
    private final String dpng = "diagram.png";
    private final String dsvg = "diagram.svg";

    private String path;

    public static String getPlantUMLCode() {
        return plantUMLCode;
    }

    static String plantUMLCode;
    private boolean isEnableDiagramItem;
    private boolean isPngExtensionItem;
    static ExceptionListener exceptionListener;

    public SwingWorkerForBackgroundGenerating() {
        isEnableDiagramItem = PluginSettings.getSettings().get("showDiagram") == 0 ? false : true;
        isPngExtensionItem = PluginSettings.getSettings().get("diagramExtension") == 0 ? false : true;

        File currentDir = new File(UIPluginEntry.getUi().getPath().getText());
        if (currentDir.isFile()) {
            path = isPngExtensionItem ? currentDir.getParentFile() + FileSystems.getDefault().getSeparator() + dpng : currentDir.getParentFile() + FileSystems.getDefault().getSeparator() + dsvg;
        } else {
            path = isPngExtensionItem ? currentDir + FileSystems.getDefault().getSeparator() + dpng : currentDir + FileSystems.getDefault().getSeparator() + dsvg;
        }
    }

    @Override
    protected String doInBackground() throws Exception {

//            deletePreviousVersionsOfDiagrams();
        setProgress(2);
        publish("loadingFilesLabel");
        if (isCancelled()) return null;

        setProgress(3);
        publish("codeGenerationLabel");
        plantUMLCode = generatePlantUMLAndLoadToTextArea(Options.getOutputFile());
        publish("showCodeString");

        if (isCancelled()) return null;

        if (isEnableDiagramItem) {
            setProgress(4);
            publish("loadingDiagramLabel");
            generateDiagram(plantUMLCode, path);
        }

        if (isCancelled()) return null;
        setProgress(5);
        publish("completeLabel");

        return "";
    }

    @Override
    protected void process(List<String> chunks) {
//            super.process(chunks);
        for (String chunk : chunks) {
            if (chunk.equals("showCodeString")) {
                UIPluginEntry.getUi().getGeneratedCode().setText(plantUMLCode);
            } else {
                UIPluginEntry.getUi().getProgressBar().setString(UIPluginEntry.getUi().getLocaleLabels().getString(chunk));
            }
        }
        UIPluginEntry.getUi().validateProgressBarTo(getProgress());
    }

    @Override
    protected void done() {
        if (isCancelled()) {
            UIPluginEntry.getUi().getProgressBar().setString("0%");
            UIPluginEntry.getUi().getProgressBar().setValue(0);
//            ui.getGeneratePlantUML().setEnabled(true);
        }
        //ui.getOpenOnPlantUMLServer().setEnabled(true);
        UIPluginEntry.getUi().setProgressBarComplete();
        //ui.getGeneratePlantUML().setEnabled(true);

        if (PluginSettings.getSettings().get("showDiagram") == 1) {
            if (PluginSettings.getSettings().get("diagramExtension") == PluginSettings.PNG) {
                try {
                    File file = new File(UIPluginEntry.getUi().getPath().getText());
                    if (file.isFile()) {
                        file = file.getParentFile();
                        System.out.println(file);
                    }
                    UIPluginEntry.getUi().showDiagram(new File(file.toString() + FileSystems.getDefault().getSeparator() + "diagram.png").toURI().toURL());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                if (PluginSettings.getSettings().get("language") == PluginSettings.ENGLISH)
                    UIPluginEntry.getUi().showDiagram(getClass().getClassLoader().getResource("doesnt_support_svg_en.png"));
                else
                    UIPluginEntry.getUi().showDiagram(getClass().getClassLoader().getResource("doesnt_support_svg_ru.png"));
            }
        }
    }

    public void generateDiagram(String source, String fileName) {
        try {
            UIPluginEntry.getUi().setPathOfCurrentDiagram(fileName);

            File path = new File(fileName);

            if (!path.exists()) {
                path.createNewFile();
            }

            // поток вывода для диаграммы
            OutputStream image = new FileOutputStream(path);

            // генератор диаграмм
            SourceStringReader reader = new SourceStringReader(source);

            // генерация диаграммы
            if (PluginSettings.getSettings().get("diagramExtension") == PluginSettings.PNG) {
                String desc = reader.generateImage(image);
            } else {
                final ByteArrayOutputStream os = new ByteArrayOutputStream();
// Write the first image to "os"
                String desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
                os.close();

// The XML is stored into svg
                final String svg = new String(os.toByteArray());
//                System.out.println(svg);

                try {
                    FileWriter fw = new FileWriter(path);
                    fw.write(svg);
                    fw.close();

                } catch (IOException iox) {
                    //do stuff with exception
                    iox.printStackTrace();
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
            exceptionListener = UIPluginEntry.getExceptionListener();
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
        return plantUMLCode;
    }

}

