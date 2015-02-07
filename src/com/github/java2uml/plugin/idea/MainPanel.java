package com.github.java2uml.plugin.idea;

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

/**
 * Created by Андрей on 25.01.2015.
 */

public class MainPanel implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        UIPluginEntry uiPluginEntry = new UIPluginEntry(project, toolWindow);
        //Создаем фабрику контента
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        //Создаем контент(окно) с нашим GUI
        Content content = contentFactory.createContent(uiPluginEntry, "", false);
        //Добавляем в IDE
        toolWindow.getContentManager().addContent(content);
    }

}
