package com.github.java2uml.gui;

import com.apple.eawt.Application;
import com.github.java2uml.core.Main;
import com.github.java2uml.core.Options;
import com.github.java2uml.core.reflection.DataExtractor;
import net.sourceforge.plantuml.SourceStringReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.stathissideris.ascii2image.core.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

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
//        createAndShowPng(source, fileName);
        sendRequestAndShowSvg(source);

    }

    private void createAndShowPng(String source, String fileName) {
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


        } catch (Throwable e) {
            e.printStackTrace();
            exceptionListener.handleExceptionAndShowDialog(e);
        }

        try {
            DataExtractor.generateFromFile(source, fileName, "png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequestAndShowSvg(String source) {
        String result = executePost(source);

//        Using http://jsoup.org
//        Ищем id=diagram <img src="">
        Document document = Jsoup.parse(result);

        Elements els = document.select("#diagram");
        for (Element el : els) {
//            System.out.println(el.toString());
//            System.out.println(el.select("img[src]").attr("src"));

            try {
                String s = el.select("img[src]").attr("src");
                s = s.replace("/png/", "/svg/");
                URI uri = new URL(s).toURI();
                Desktop.getDesktop().browse(uri);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private String executePost(String source) {
        source = "text=" + source;

        final String url = "http://www.plantuml.com/plantuml/form";

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        String result = "";

        try {
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");

            StringEntity entity = new StringEntity(source);
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            result = EntityUtils.toString(response.getEntity());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
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
        @Override
        protected String doInBackground() throws Exception {

            try {
                ui.getGeneratePlantUML().setEnabled(false);
                ui.getProgressBar().setString(ui.getLocaleLabels().getString("loadingFilesLabel"));
                ui.increaseProgressBarForTwenty();
                if (isCancelled()) return null;

                Main.main(gettingParametersFromUI());

                if (isCancelled()) return null;
                ui.getProgressBar().setString(ui.getLocaleLabels().getString("codeGenerationLabel"));
                ui.increaseProgressBarForTwenty();
                generatePlantUMLAndLoadToTextArea(Options.getOutputFile());

                if (isCancelled()) return null;

                if (ui.getEnableDiagramItem().getState()) {
                    ui.getProgressBar().setString(ui.getLocaleLabels().getString("loadingDiagramLabel"));
                    ui.increaseProgressBarForTwenty();
                    generateDiagram(plantUMLCode, "diagram.png");
                    if (isCancelled()) return null;
                    ui.showDiagram("diagram.png");
                }

                if (isCancelled()) return null;
                ui.setProgressBarComplete();
                ui.getProgressBar().setString(ui.getLocaleLabels().getString("completeLabel"));
                ui.getGeneratePlantUML().setEnabled(true);


            } catch (Throwable e) {
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
