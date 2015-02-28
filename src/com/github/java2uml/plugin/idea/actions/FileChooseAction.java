package com.github.java2uml.plugin.idea.actions;

import com.github.java2uml.gui.UI;
import com.github.java2uml.gui.UIPlugin;
import com.github.java2uml.gui.UIPluginEntry;
import com.github.java2uml.plugin.idea.MainPanel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * Created by Андрей on 30.01.2015.
 */
public class FileChooseAction extends DumbAwareAction {
    private static JFileChooser fileChooser;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        if (fileChooser == null) {
            initFileChooser();
        }
        if (new File(UIPluginEntry.getUi().getPath().getText()).exists() && !UIPluginEntry.getUi().getPath().getText().equals("")) {
            fileChooser.setCurrentDirectory(new File(UIPluginEntry.getUi().getPath().getText()));
        }
        UIPluginEntry.getUi().getProgressBar().setString("0%");
        UIPluginEntry.getUi().getProgressBar().setValue(0);
        int resultOfChoice = fileChooser.showOpenDialog(UIPluginEntry.getUi().getMainFrame());
        if (resultOfChoice == JFileChooser.APPROVE_OPTION) {
            File chosenDirectory = new File(fileChooser.getSelectedFile().getPath());
            UIPluginEntry.getUi().getPath().setText(chosenDirectory.toString());
        }
    }

    private void initFileChooser(){
        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Java archive (.jar)", "jar"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Java project directory", "."));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    }
}
