package com.github.java2uml.plugin.idea;

import com.github.java2uml.gui.UIPluginEntry;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Андрей on 07.02.2015.
 */
public class Java2UMLPluginRegistration implements com.intellij.openapi.components.ApplicationComponent {
    private UIPluginEntry uiPluginEntry;


    @Override
    public void initComponent() {
        PluginSettings.initSettings();
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "Java2UMLPluginRegistration";
    }
}
