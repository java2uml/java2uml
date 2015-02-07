package com.github.java2uml.plugin.idea.actions;

import com.github.java2uml.gui.UIPluginEntry;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

/**
 * Created by Андрей on 31.01.2015.
 */
public class GenerateAction extends DumbAwareAction{
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = UIPluginEntry.getProject();
        ToolWindow toolWindow = UIPluginEntry.getToolWindow();
        new UIPluginEntry(project, toolWindow).generateActionFromOutside();
    }
}
