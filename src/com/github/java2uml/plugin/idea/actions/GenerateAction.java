package com.github.java2uml.plugin.idea.actions;

import com.github.java2uml.core.Main;
import com.github.java2uml.gui.ExceptionListener;
import com.github.java2uml.gui.SwingWorkerForBackgroundGenerating;
import com.github.java2uml.gui.UIPlugin;
import com.github.java2uml.gui.UIPluginEntry;
import com.github.java2uml.plugin.idea.PluginSettings;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import java.io.File;
import java.nio.file.FileSystems;

/**
 * Created by Андрей on 31.01.2015.
 */
public class GenerateAction extends DumbAwareAction{
    static SwingWorkerForBackgroundGenerating swingWorker;
    static ExceptionListener exceptionListener;

    public static ExceptionListener getExceptionListener() {
        return exceptionListener;
    }

    public static SwingWorkerForBackgroundGenerating getSwingWorker() {
        return swingWorker;
    }
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
//        Project project = UIPluginEntry.getProject();
//        ToolWindow toolWindow = UIPluginEntry.getToolWindow();
//        new UIPluginEntry(project, toolWindow).generateActionFromOutside();
        try {
            UIPluginEntry.getUi().getLabelForDiagram().setIcon(null);
            UIPluginEntry.getUi().getProgressBar().setValue(0);
            UIPluginEntry.getUi().getProgressBar().setString("0%");
//            ui.getGeneratePlantUML().setEnabled(false);
//            ui.getOpenOnPlantUMLServer().setEnabled(false);
            exceptionListener = UIPluginEntry.getExceptionListener();
            Main.main(gettingParametersFromSettings());

            swingWorker = new SwingWorkerForBackgroundGenerating();
            swingWorker.execute();

        } catch (Throwable e1) {
            e1.printStackTrace();
            exceptionListener.handleExceptionAndShowDialog(e1);
        }
    }

    private String[] gettingParametersFromSettings() {
        String[] args = new String[10];
        for (int i = 0; i < args.length; i++) {
            args[i] = "";
        }
        UIPlugin.getInstance().validateProgressBarTo(1);
        args[0] = PluginSettings.getSettings().get("parseType") == PluginSettings.PARSING ? "java" : "class";
        args[1] = UIPlugin.getInstance().getPath().getText().toString();
        args[2] = PluginSettings.getSettings().get("header") == 0 ? "" : "";
        args[3] = PluginSettings.getSettings().get("typeOfDiagram") == PluginSettings.CLASS ? "classes_diagram" : "sequence_diagram";
        args[4] = PluginSettings.getSettings().get("direction") == PluginSettings.VERTICAL ? "vertical" : "horizontal";
        args[5] = PluginSettings.getSettings().get("composition") == 0 ? "nocomposition" : "";
        args[6] = PluginSettings.getSettings().get("aggregation") == 0 ? "noaggregation" : "";
        args[7] = PluginSettings.getSettings().get("association") == 0 ? "noassociation" : "";
        args[8] = PluginSettings.getSettings().get("lollipop") == 0 ? "nolollipop" : "";
        if (!UIPlugin.getInstance().getPath().getText().isEmpty() && new File(UIPlugin.getInstance().getPath().getText()).exists()) {
            File outputPath = new File(UIPlugin.getInstance().getPath().getText());
            if (outputPath.isFile()) {
                args[9] = "output=" + outputPath.getParent() + FileSystems.getDefault().getSeparator() + "classes.plantuml";
            } else {
                args[9] = "output=" + outputPath + FileSystems.getDefault().getSeparator() + "classes.plantuml";
            }

        }
        for (String str : args) System.out.println(str);

        return args;
    }
}
