package com.github.java2uml.plugin.idea.actions;

import com.github.java2uml.gui.QuickHelp;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;

/**
 * Created by Андрей on 31.01.2015.
 */
public class QuickHelpAction extends DumbAwareAction{
    private static QuickHelp quickHelp;
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        if (!QuickHelp.quickHelpIsNull()) {
            if (!quickHelp.isVisible()) {
                quickHelp.setVisible(true);
            } else {
                quickHelp.toFront();
                quickHelp.repaint();
            }
        } else {
            quickHelp = QuickHelp.getInstance();
            quickHelp.setVisible(true);
        }
    }
}
