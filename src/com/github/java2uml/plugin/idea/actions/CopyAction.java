package com.github.java2uml.plugin.idea.actions;

import com.github.java2uml.gui.UIPluginEntry;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * Created by Андрей on 03.02.2015.
 */
public class CopyAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        try {
            UIPluginEntry.getUi().getGeneratedCode().setFocusable(true);
            UIPluginEntry.getUi().getGeneratedCode().selectAll();
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(UIPluginEntry.getUi().getGeneratedCode().getText()), null);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
