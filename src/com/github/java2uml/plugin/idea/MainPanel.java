package com.github.java2uml.plugin.idea;

import com.github.java2uml.gui.UIPluginEntry;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

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

    public static void createToolWindowContent() {

    }
}
