package com.github.java2uml.plugin.idea.actions;

import com.github.java2uml.gui.About;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;

/**
 * Created by Андрей on 30.01.2015.
 */
public class AboutAction extends DumbAwareAction {
    private About about;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        if (about == null) {
            about = new About("Java2UML About");
            about.setVisible(true);
        } else {
            about.setVisible(true);
            about.repaint();
        }
    }
}
