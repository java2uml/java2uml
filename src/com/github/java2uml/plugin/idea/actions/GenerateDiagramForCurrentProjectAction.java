package com.github.java2uml.plugin.idea.actions;

import com.github.java2uml.gui.About;
import com.github.java2uml.gui.UIPlugin;
import com.github.java2uml.gui.UIPluginEntry;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

/**
 * Created by Андрей on 30.01.2015.
 */
public class GenerateDiagramForCurrentProjectAction extends AnAction {
    Project project;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        project = UIPluginEntry.getProject();
        UIPluginEntry.getUi().getPath().setText(project.getBasePath());
        new GenerateAction().actionPerformed(null);
    }
}
