package com.github.java2uml.gui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import java.awt.*;

import com.github.java2uml.core.Main;
import com.github.java2uml.core.Options;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderUtil;
import org.stathissideris.ascii2image.core.FileUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class UIPluginEntry extends JPanel implements Disposable {
    private static Logger logger = Logger.getInstance(UIPluginEntry.class);

    public static ToolWindow getToolWindow() {
        return toolWindow;
    }

    public static Project getProject() {
        return project;
    }

    private static ToolWindow toolWindow;
//    private AncestorListener java2umlAncestorListener = new Java2UmlAncestorListener();
    private static Project project;
//    private JScrollPane scrollPane;

    public static UIPlugin getUi() {
        return ui;
    }

    static UIPlugin ui;
    String[] args;
    String plantUMLCode;
    SwingWorkerForBackgroundGenerating swingWorker;

    public static ExceptionListener getExceptionListener() {
        return exceptionListener;
    }

    static ExceptionListener exceptionListener;

    public UIPluginEntry(Project project, ToolWindow toolWindow) {
        super(new BorderLayout());
        this.project = project;
        this.toolWindow = toolWindow;
        initUI();
        this.setSize(600, 650);
//        this.toolWindow.getComponent().addAncestorListener(java2umlAncestorListener);
    }


    private void initUI() {
        // creating actions toolbar instead JMenu
        ActionGroup group = (ActionGroup) ActionManager.getInstance().getAction("Java2UML.Toolbar");
        final ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, group, true);
        actionToolbar.setTargetComponent(this);
        add(actionToolbar.getComponent(), BorderLayout.PAGE_START);
        // creating main panel content
        //GenerateActionListener generateActionListener = new GenerateActionListener();
        ui = UIPlugin.getInstance();
        exceptionListener = ui;
        ui.initUI().setVisible(true);
        add(ui.getMainFrame());
    }

//    private String generatePlantUMLAndLoadToTextArea(String outputPath) {
//        plantUMLCode = null;
//        try {
//            plantUMLCode = FileUtils.readFile(new File(outputPath));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return plantUMLCode;
//    }

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
                String desc = reader.generateImage(image);
        } catch (Throwable e) {
            e.printStackTrace();
            exceptionListener.handleExceptionAndShowDialog(e);
        }
    }

    @Override
    public void dispose() {

    }

//    public class GenerateActionListener implements ActionListener {
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            try {
//                ui.getLabelForDiagram().setIcon(null);
//                ui.getProgressBar().setValue(0);
//                ui.getProgressBar().setString("0%");
////                ui.getGeneratePlantUML().setEnabled(false);
////                ui.getOpenOnPlantUMLServer().setEnabled(false);
//
//                Main.main(gettingParametersFromSettings());
//
//                swingWorker = new SwingWorkerForBackgroundGenerating();
//                swingWorker.execute();
//
//            } catch (Throwable e1) {
//                e1.printStackTrace();
//                exceptionListener.handleExceptionAndShowDialog(e1);
//            }
//        }
//    }

//    public class SwingWorkerForBackgroundGenerating extends SwingWorker<String, String> {
//        private final String dpng = "diagram.png";
//        private final String dsvg = "diagram.svg";
//
//        private String path;
//        private boolean isEnableDiagramItem;
//        private boolean isPngExtensionItem;
//
//        public SwingWorkerForBackgroundGenerating() {
//            isEnableDiagramItem = true;
//            isPngExtensionItem = true;
//            path = isPngExtensionItem ? dpng : dsvg;
//        }
//
//        @Override
//        protected String doInBackground() throws Exception {
//
////            deletePreviousVersionsOfDiagrams();
//            setProgress(2);
//            publish("loadingFilesLabel");
//            if (isCancelled()) return null;
//
//            setProgress(3);
//            publish("codeGenerationLabel");
//            plantUMLCode = generatePlantUMLAndLoadToTextArea(Options.getOutputFile());
//            publish("showCodeString");
//
//            if (isCancelled()) return null;
//
//            if (isEnableDiagramItem) {
//                setProgress(4);
//                publish("loadingDiagramLabel");
//                generateDiagram(plantUMLCode, path);
//            }
//
//            if (isCancelled()) return null;
//            setProgress(5);
//            publish("completeLabel");
//
//            return "";
//        }
//
//        @Override
//        protected void process(List<String> chunks) {
////            super.process(chunks);
//            for (String chunk : chunks) {
//                if (chunk.equals("showCodeString")) {
//                    ui.getGeneratedCode().setText(plantUMLCode);
//                } else {
//                    ui.getProgressBar().setString(ui.localeLabels.getString(chunk));
//                }
//            }
//            ui.validateProgressBarTo(getProgress());
//        }
//
//        @Override
//        protected void done() {
//            if (isCancelled()) {
//                ui.getProgressBar().setString("0%");
//                ui.getProgressBar().setValue(0);
////                ui.getGeneratePlantUML().setEnabled(true);
//            }
////            ui.getOpenOnPlantUMLServer().setEnabled(true);
//            ui.setProgressBarComplete();
////            ui.getGeneratePlantUML().setEnabled(true);
//
//                    try {
//                        ui.showDiagram(new File("diagram.png").toURI().toURL());
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    }
//
//        }
//    }

//    private void sendRequestAndShowSvg(String source) {
//
//        Transcoder t = TranscoderUtil.getDefaultTranscoder();
//        String url = null;
//        try {
//            url = t.encode(source);
//            url = "http://www.plantuml.com/plantuml/svg/" + url;
//            Desktop.getDesktop().browse(new URL(url).toURI());
//        } catch (IOException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//    }

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