package com.github.java2uml.plugin.idea;

import com.github.java2uml.gui.QuickHelp;
import com.github.java2uml.gui.UIPluginEntry;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ActionCallback;
import com.intellij.openapi.wm.*;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.xml.XmlCoreEnvironment;
import javafx.application.Application;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.util.prefs.Preferences;

/**
 * Created by Андрей on 25.01.2015.
 */

public class MainPanel implements ToolWindowFactory {
    private static Preferences config;
    private static final int ITERATOR_OF_EXECUTIONS = 1;
    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        config = Preferences.userRoot().node("firstRun");
        config.putInt("amountOfExecutions", config.getInt("amountOfExecutions", 0) + ITERATOR_OF_EXECUTIONS);
        UIPluginEntry uiPluginEntry = new UIPluginEntry(project, toolWindow);
        //Создаем фабрику контента
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        //Создаем контент(окно) с нашим GUI
        Content content = contentFactory.createContent(uiPluginEntry, "", false);
        //Добавляем в IDE
        toolWindow.getContentManager().addContent(content);
        if (config.getInt("amountOfExecutions", 0) <= 1) {
            config.putInt("amountOfExecutions", config.getInt("amountOfExectuions", 1) + ITERATOR_OF_EXECUTIONS);
            String[] options = new String[]{UIPluginEntry.getUi().getLocaleLabels().getString("noLabel"), UIPluginEntry.getUi().getLocaleLabels().getString("yesLabel")};
            int showOrNot = JOptionPane.showOptionDialog(UIPluginEntry.getUi().getMainFrame(), UIPluginEntry.getUi().getLocaleLabels().getString("wantToShowQuickHelp"), "Java2UML", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
            switch (showOrNot) {
                case 0:
                    break;
                case 1:
                    QuickHelp.getInstance().setVisible(true);
                    break;
            }
        }
    }

}
