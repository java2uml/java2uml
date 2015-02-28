package com.github.java2uml.plugin.idea.actions;

import com.github.java2uml.gui.UIPluginEntry;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Created by Андрей on 03.02.2015.
 */
public class ClearAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        UIPluginEntry.getUi().getGeneratedCode().setText("");
    }
}
