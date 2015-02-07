package com.github.java2uml.gui;

import com.github.java2uml.core.Main;
import com.github.java2uml.core.Options;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderUtil;
import org.stathissideris.ascii2image.core.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.List;

public class UIEntry {
    static UI ui;
    String[] args;
    String plantUMLCode;
    SwingWorkerForBackgroundGenerating swingWorker;
    static ExceptionListener exceptionListener;


    private String[] gettingParametersFromUI() {
        args = new String[10];
        for (int i = 0; i < args.length; i++) {
            args[i] = "";
        }
        ui.validateProgressBarTo(1);
        args[0] = ui.getParsingCheckboxItem().getState() ? "java" : "class";
        args[1] = ui.getPath().getText().toString();
        args[2] = !ui.getShowHeader().getState() ? "" : "";
        args[3] = ui.getClassDiagramCheckboxItem().getState() ? "classes_diagram" : "sequence_diagram";
        args[4] = ui.getVerticalDirectionCheckboxItem().getState() ? "vertical" : "horizontal";
        args[5] = !ui.getShowComposition().getState() ? "nocomposition" : "";
        args[6] = !ui.getShowAggregation().getState() ? "noaggregation" : "";
        args[7] = !ui.getShowAssociation().getState() ? "noassociation" : "";
        args[8] = !ui.getShowLollipops().getState() ? "nolollipop" : "";
        if (!ui.getPath().getText().isEmpty() && new File(ui.getPath().getText()).exists()) {
            File outputPath = new File(ui.getPath().getText());
            if (outputPath.isFile()) {
                args[9] = "output=" + outputPath.getParent() + FileSystems.getDefault().getSeparator() + "classes.plantuml";
            } else {
                args[9] = "output=" + outputPath + FileSystems.getDefault().getSeparator() + "classes.plantuml";
            }

        }

        for (String str : args) System.out.println(str);

        return args;
    }


    public void initUI() {
        if (System.getProperty("os.name").equals("Mac OS X")) {
            settingDockIcon();
        }

        GenerateActionListener generateActionListener = new GenerateActionListener();
        ui = UI.getInstance();
        exceptionListener = ui;
        ui.initUI().setVisible(true);

        if (System.getProperty("os.name").contains("Windows")){
            ui.getMainFrame().setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("about_logo.png")));
        }

        ui.getCancelLoading().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ui.getLabelForDiagram().setIcon(null);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            swingWorker.cancel(true);
                        } catch (NullPointerException ex) {
                            exceptionListener.handleExceptionAndShowDialog(ex);
                        }
                    }
                }).start();
                ui.getGeneratePlantUML().setEnabled(true);
                ui.getProgressBar().setString("0%");
                ui.getProgressBar().setValue(0);
            }
        });
        ui.getGeneratePlantUML().addActionListener(generateActionListener);
        ui.getGenerateItem().addActionListener(generateActionListener);
        ui.getOpenOnPlantUMLServer().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (plantUMLCode != null && !plantUMLCode.equals("") && !ui.getGeneratedCode().getText().equals(""))
                    sendRequestAndShowSvg(plantUMLCode);
                else {
                    JOptionPane.showMessageDialog(ui.getMainFrame(), ui.getLocaleLabels().getString("umlCodeMustNotBeEmpty"), "Java2UML message", JOptionPane.INFORMATION_MESSAGE);
                }

            }
        });
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
        } catch (Throwable e) {
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
        return plantUMLCode;
    }

    public void generateDiagram(String source, String fileName) {
        try {
            File path = new File(fileName);

            if (!path.exists()){
                path.createNewFile();
            }

            // поток вывода для диаграммы
            OutputStream image = new FileOutputStream(path);

            // генератор диаграмм
            SourceStringReader reader = new SourceStringReader(source);

            // генерация диаграммы
            if (ui.getPngExtensionItem().getState()) {
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
                ui.getGeneratePlantUML().setEnabled(false);
                ui.getOpenOnPlantUMLServer().setEnabled(false);

                Main.main(gettingParametersFromUI());

                swingWorker = new SwingWorkerForBackgroundGenerating();
                swingWorker.execute();

            } catch (Throwable e1) {
                e1.printStackTrace();
                exceptionListener.handleExceptionAndShowDialog(e1);
            }
        }
    }

    public class SwingWorkerForBackgroundGenerating extends SwingWorker<String, String> {
        private final String dpng = "diagram.png";
        private final String dsvg = "diagram.svg";

        private String path;
        private boolean isEnableDiagramItem;
        private boolean isPngExtensionItem;

        public SwingWorkerForBackgroundGenerating() {
            isEnableDiagramItem = ui.getEnableDiagramItem().getState();
            isPngExtensionItem = ui.getPngExtensionItem().getState();

            File currentDir = new File(ui.getPath().getText());
            if (currentDir.isFile()){
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
                    ui.getGeneratedCode().setText(plantUMLCode);
                } else {
                    ui.getProgressBar().setString(ui.getLocaleLabels().getString(chunk));
                }
            }
            ui.validateProgressBarTo(getProgress());
        }

        @Override
        protected void done() {
            if (isCancelled()) {
                ui.getProgressBar().setString("0%");
                ui.getProgressBar().setValue(0);
                ui.getGeneratePlantUML().setEnabled(true);
            }
            ui.getOpenOnPlantUMLServer().setEnabled(true);
            ui.setProgressBarComplete();
            ui.getGeneratePlantUML().setEnabled(true);

            if (ui.getEnableDiagramItem().getState()) {
                if (ui.getPngExtensionItem().getState()) {
                    try {
                        File file = new File(ui.getPath().getText());
                        if (file.isFile()) {
                            file = file.getParentFile();
                            System.out.println(file);
                        }
                        ui.showDiagram(new File(file.toString() + FileSystems.getDefault().getSeparator() + "diagram.png").toURI().toURL());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (ui.getEnglishLangItem().getState())
                        ui.showDiagram(getClass().getClassLoader().getResource("doesnt_support_svg_en.png"));
                    else
                        ui.showDiagram(getClass().getClassLoader().getResource("doesnt_support_svg_ru.png"));
                }
            }
        }
    }

    public void settingDockIcon() {
        try {
            Class c = Class.forName("com.apple.eawt.Application");
            Method m = c.getMethod("getApplication");
            Image image = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("about_logo.png"));
            Object applicationInstance = m.invoke(null);
            m = applicationInstance.getClass().getMethod("setDockIconImage", java.awt.Image.class);
            m.invoke(applicationInstance, image);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void sendRequestAndShowSvg(String source) {

        Transcoder t = TranscoderUtil.getDefaultTranscoder();
        String url = null;
        try {
            url = t.encode(source);
            url = "http://www.plantuml.com/plantuml/svg/" + url;
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public boolean deletePreviousVersionsOfDiagrams(){
        boolean success = false;

        if (new File("diagram.svg").exists()) {
            success = new File("diagram.svg").delete();
        }
        if (new File("diagram.png").exists()) {
            success = new File("diagram.png").delete();
        }
        return success;
    }
}
