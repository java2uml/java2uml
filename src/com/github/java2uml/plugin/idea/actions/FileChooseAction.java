package com.github.java2uml.plugin.idea.actions;

import com.github.java2uml.gui.UI;
import com.github.java2uml.gui.UIPlugin;
import com.github.java2uml.gui.UIPluginEntry;
import com.github.java2uml.plugin.idea.MainPanel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;

/**
 * Created by Андрей on 30.01.2015.
 */
public class FileChooseAction extends DumbAwareAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        if (UIPluginEntry.getUi() != null){
            UIPluginEntry.getUi().chooseItem();
        }
        else{
            MainPanel.createToolWindowContent();
        }
    }
}
