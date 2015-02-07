package com.github.java2uml.plugin.idea.actions;

import com.github.java2uml.gui.Settings;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Created by Андрей on 03.02.2015.
 */
public class SettingsAction extends AnAction {
    private static Settings settings;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        if (!Settings.settingsAreNull()) {
            if (!settings.isVisible()) {
                settings.setVisible(true);
            } else {
                settings.toFront();
                settings.repaint();
            }
        } else settings = Settings.getInstance();
    }
}
