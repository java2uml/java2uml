package com.github.java2uml.gui;

import com.github.java2uml.core.Main;
import com.github.java2uml.core.Options;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderUtil;
import org.stathissideris.ascii2image.core.FileUtils;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

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
        if (System.getProperty("os.name").equals("Mac OS X")) {
            settingDockIcon();
        }
        GenerateActionListener generateActionListener = new GenerateActionListener();
        ui = UI.getInstance();
        exceptionListener = ui;
        ui.initUI().setVisible(true);
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
                if (plantUMLCode != null && !plantUMLCode.equals(""))
                    sendRequestAndShowSvg(plantUMLCode);

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
            OutputStream image = new FileOutputStream(file);

            // генератор диаграмм
            SourceStringReader reader = new SourceStringReader(source);

            // генерация диаграммы
            if (ui.getPngExtensionItem().getState()) {
                String desc = reader.generateImage(image);
            } else {
                System.out.println("Your code is so bad, you don't deserve to see this diagram");
                final ByteArrayOutputStream os = new ByteArrayOutputStream();
// Write the first image to "os"
                String desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
                os.close();

// The XML is stored into svg
                final String svg = new String(os.toByteArray());
                System.out.println(svg);

                try {
                    FileWriter fw = new FileWriter(file);
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
            ui.getGeneratePlantUML().setEnabled(false);
            try {
                Main.main(gettingParametersFromUI());
            } catch (Exception e) {
                e.printStackTrace();
            }
            isEnableDiagramItem = ui.getEnableDiagramItem().getState();
            isPngExtensionItem = ui.getPngExtensionItem().getState();
            path = isPngExtensionItem ? dpng : dsvg;
        }

        @Override
        protected String doInBackground() throws Exception {

            publish("loadingFilesLabel");
            if (isCancelled()) return null;

            publish("codeGenerationLabel");
            generatePlantUMLAndLoadToTextArea(Options.getOutputFile());


                if (isCancelled()) return null;
                ui.getProgressBar().setString(ui.getLocaleLabels().getString("codeGenerationLabel"));
                ui.increaseProgressBarForTwenty();
                generatePlantUMLAndLoadToTextArea(Options.getOutputFile());

                if (isCancelled()) return null;

                if (ui.getEnableDiagramItem().getState()) {
                    ui.getProgressBar().setString(ui.getLocaleLabels().getString("loadingDiagramLabel"));
                    ui.increaseProgressBarForTwenty();

                    if (ui.getPngExtensionItem().getState()) {
                        generateDiagram(plantUMLCode, "diagram.png");
                        if (isCancelled()) return null;
                        ui.showDiagram("diagram.png");
                    } else {
                        generateDiagram(plantUMLCode, "diagram.svg");
                        if (isCancelled()) return null;
//                        TODO
//                        отобразить svg
                        ui.showDiagram(null);
                    }
                }

            if (isCancelled()) return null;

            if (isEnableDiagramItem) {
                publish("loadingDiagramLabel");
                generateDiagram(plantUMLCode, path);
            }

            if (isCancelled()) return null;
            publish("completeLabel");

            return "";
        }

        @Override
        protected void process(List<String> chunks) {
//            super.process(chunks);
            for (String chunk : chunks) {
                ui.getProgressBar().setString(ui.getLocaleLabels().getString(chunk));
            }
            ui.increaseProgressBarForTwenty();
        }

        @Override
        protected void done() {
//            super.done();
            if (isCancelled()) {
                ui.getProgressBar().setString("0%");
                ui.getProgressBar().setValue(0);
                ui.getGeneratePlantUML().setEnabled(true);
            }

            ui.setProgressBarComplete();
            ui.getGeneratePlantUML().setEnabled(true);

            if (isPngExtensionItem) {
                ui.showDiagram(dpng);
            } else {
//                Desktop.getDesktop().open(new File(dsvg));
                String userDir = System.getProperty("user.dir", "unknown");
                try {
                    Desktop.getDesktop().browse(new URI("file://" + userDir + "/" + dsvg));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getClassLoader().getResource("generated.wav"));) {
                        Clip clip = AudioSystem.getClip();
                        clip.open(audioInputStream);
                        clip.start();
                    } catch (LineUnavailableException e1) {
                        e1.printStackTrace();
                    } catch (UnsupportedAudioFileException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }).start();

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
}
