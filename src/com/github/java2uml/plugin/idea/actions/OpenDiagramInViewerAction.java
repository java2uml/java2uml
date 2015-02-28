package com.github.java2uml.plugin.idea.actions;

import com.github.java2uml.gui.UIPluginEntry;
import com.github.java2uml.plugin.idea.PluginSettings;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Андрей on 28.02.2015.
 */
public class OpenDiagramInViewerAction extends AnAction {
    // процесс просмоторщика диаграмм
    private Process viewerProc = null;
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
                if (PluginSettings.getSettings().get("showDiagram") == 1) {
                SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (System.getProperty("os.name").contains("Windows")) {
                        try {
                            if (viewerProc != null) {
                                viewerProc.destroy();
                                viewerProc = null;
                            }
                            viewerProc = Runtime.getRuntime().exec("java -jar lib/diagram_viewer.jar " + UIPluginEntry.getUi().getPathOfCurrentDiagram());
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    } else {
                        try {
                            Desktop.getDesktop().open(new File(UIPluginEntry.getUi().getPathOfCurrentDiagram()));
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
        } else {
            JOptionPane.showMessageDialog(UIPluginEntry.getUi().getMainFrame(), UIPluginEntry.getUi().getLocaleLabels().getString("youMustGenerateDiagramFirst"), "Java2UML message", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
