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

    private ToolWindow toolWindow;
//    private AncestorListener java2umlAncestorListener = new Java2UmlAncestorListener();
    private Project project;
//    private JScrollPane scrollPane;

    static UIPlugin ui;
    String[] args;
    String plantUMLCode;
    SwingWorkerForBackgroundGenerating swingWorker;
    static ExceptionListener exceptionListener;

    public UIPluginEntry(Project project, ToolWindow toolWindow) {
        super(new BorderLayout());
        this.project = project;
        this.toolWindow = toolWindow;
        initUI();
        this.setSize(600, 650);
//        this.toolWindow.getComponent().addAncestorListener(java2umlAncestorListener);
    }

    private String[] gettingParametersFromUI() {
        args = new String[9];
        for (int i = 0; i < args.length; i++) {
            args[i] = "";
        }
        ui.validateProgressBarTo(1);
        args[0] = "java";
        args[1] = ui.getPath().getText().toString();
        args[2] = "";
        args[3] = "classes_diagram";
        args[4] = "vertical";
        //args[5] = !ui.getShowComposition().getState() ? "nocomposition" : "";
        //args[6] = !ui.getShowAggregation().getState() ? "noaggregation" : "";
        //args[7] = !ui.getShowAssociation().getState() ? "noassociation" : "";
        //args[8] = !ui.getShowLollipops().getState() ? "nolollipop" : "";

        for (String str : args) System.out.println(str);

        return args;
    }

    private void initUI() {
        // creating actions toolbar instead JMenu
        ActionGroup group = (ActionGroup) ActionManager.getInstance().getAction("Java2UML.Toolbar");
        final ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, group, true);
        actionToolbar.setTargetComponent(this);
        add(actionToolbar.getComponent(), BorderLayout.PAGE_START);
        // creating main panel content
        GenerateActionListener generateActionListener = new GenerateActionListener();
        ui = UIPlugin.getInstance();
        exceptionListener = ui;
        ui.initUI().setVisible(true);
        add(ui.getMainFrame());
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
            //ui.settingStateForAllOptions();
    }

    private String generatePlantUMLAndLoadToTextArea(String outputPath) {
        plantUMLCode = null;
        try {
            plantUMLCode = FileUtils.readFile(new File(outputPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        ui.getGeneratedCode().setText(plantUMLCode);
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
                String desc = reader.generateImage(image);

        } catch (Throwable e) {
            e.printStackTrace();
            exceptionListener.handleExceptionAndShowDialog(e);
        }
    }
//    public void renderLater() {
//        logger.debug("renderLater ", project.getName());
//        ApplicationManager.getApplication().invokeLater(new Runnable() {
//            @Override
//            public void run() {
//            }
//        });
//    }

    @Override
    public void dispose() {

    }

//    class Java2UmlAncestorListener extends AncestorListenerAdapter {
//        private Logger logger = Logger.getInstance(Java2UmlAncestorListener.class);
//
//        @Override
//        public void ancestorAdded(AncestorEvent ancestorEvent) {
//            logger.debug("ancestorAdded ", project.getName());
//            renderLater();
//        }
//
//    }

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
            isEnableDiagramItem = true;
            isPngExtensionItem = true;
            path = isPngExtensionItem ? dpng : dsvg;
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

                    try {
                        ui.showDiagram(new File("diagram.png").toURI().toURL());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

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

//    private void addScrollBarListeners(JComponent panel) {
//        panel.addMouseWheelListener(new MouseWheelListener() {
//            public void mouseWheelMoved(MouseWheelEvent e) {
//                if (e.isControlDown()) {
//                    setZoom(Math.max(getZoom() - e.getWheelRotation() * 10, 1));
//                } else {
//                    scrollPane.dispatchEvent(e);
//                }
//            }
//        });
//
//        panel.addMouseMotionListener(new MouseMotionListener() {
//            private int x, y;
//
//            public void mouseDragged(MouseEvent e) {
//                JScrollBar h = scrollPane.getHorizontalScrollBar();
//                JScrollBar v = scrollPane.getVerticalScrollBar();
//
//                int dx = x - e.getXOnScreen();
//                int dy = y - e.getYOnScreen();
//
//                h.setValue(h.getValue() + dx);
//                v.setValue(v.getValue() + dy);
//
//                x = e.getXOnScreen();
//                y = e.getYOnScreen();
//            }
//
//            public void mouseMoved(MouseEvent e) {
//                x = e.getXOnScreen();
//                y = e.getYOnScreen();
//            }
//        });
//    }
}