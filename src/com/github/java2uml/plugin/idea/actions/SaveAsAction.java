package com.github.java2uml.plugin.idea.actions;

import com.github.java2uml.gui.UIPlugin;
import com.github.java2uml.gui.UIPluginEntry;
import com.github.java2uml.plugin.idea.PluginSettings;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import javafx.stage.FileChooser;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Created by Андрей on 17.02.2015.
 */
public class SaveAsAction extends AnAction {
    private static JFileChooser fileSaver;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        if (fileSaver == null) {
            initFileSaver();
        }
        if (PluginSettings.getSettings().get("showDiagram") == 1) {
            fileSaver.setCurrentDirectory(new File(UIPluginEntry.getUi().getPath().getText()));
            if (PluginSettings.getSettings().get("diagramExtension") == PluginSettings.PNG) {
                fileSaver.setSelectedFile(new File("diagram.png"));
                fileSaver.setFileFilter(new FileNameExtensionFilter("PNG image", "png"));
            } else {
                fileSaver.setSelectedFile(new File("diagram.svg"));
                fileSaver.setFileFilter(new FileNameExtensionFilter("SVG image", "svg"));
            }

            CopyOption[] options = new CopyOption[]{
                    StandardCopyOption.REPLACE_EXISTING,
            };
            if (fileSaver.showSaveDialog(UIPluginEntry.getUi().getMainFrame()) == JFileChooser.APPROVE_OPTION) {
                File file = fileSaver.getSelectedFile();
                try {
                    if (PluginSettings.getSettings().get("diagramExtension") == PluginSettings.PNG) {
                        Files.copy(new File("diagram.png").toPath(), file.toPath(), options);
                    } else {
                        Files.copy(new File("diagram.svg").toPath(), file.toPath(), options);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(UIPluginEntry.getUi().getMainFrame(), UIPluginEntry.getUi().getLocaleLabels().getString("youMustGenerateDiagramFirst"), "Java2UML message", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void initFileSaver() {
        fileSaver = new JFileChooser();
    }
}
