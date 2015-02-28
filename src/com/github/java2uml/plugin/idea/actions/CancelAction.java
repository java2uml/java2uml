package com.github.java2uml.plugin.idea.actions;

import com.github.java2uml.gui.UIPluginEntry;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Created by Андрей on 03.02.2015.
 */
public class CancelAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        UIPluginEntry.getUi().getLabelForDiagram().setIcon(null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GenerateAction.getSwingWorker().cancel(true);
                } catch (NullPointerException ex) {
                    GenerateAction.exceptionListener.handleExceptionAndShowDialog(ex);
                }
            }
        }).start();
        //ui.getGeneratePlantUML().setEnabled(true);
        UIPluginEntry.getUi().getProgressBar().setString("0%");
        UIPluginEntry.getUi().getProgressBar().setValue(0);
    }
}
