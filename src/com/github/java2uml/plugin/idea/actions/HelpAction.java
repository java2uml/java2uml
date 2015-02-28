package com.github.java2uml.plugin.idea.actions;

import com.github.java2uml.gui.Help;
import com.github.java2uml.gui.HelpRu;
import com.github.java2uml.plugin.idea.PluginSettings;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;

/**
 * Created by Андрей on 30.01.2015.
 */
public class HelpAction extends DumbAwareAction{
    private static Help helpWindow;
    private static HelpRu helpRu;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        if (PluginSettings.getSettings().get("language") == PluginSettings.ENGLISH) {
            if (!Help.helpIsNull()) {
                if (!helpWindow.isVisible()) {
                    helpWindow.setVisible(true);
                } else {
                    helpWindow.toFront();
                    helpWindow.repaint();
                }
            } else helpWindow = Help.getInstance();
        } else {
            if (!HelpRu.helpIsNull()) {
                if (!helpRu.isVisible()) {
                    helpRu.setVisible(true);
                } else {
                    helpRu.toFront();
                    helpRu.repaint();
                }
            } else helpRu = HelpRu.getInstance();
        }
    }
}
