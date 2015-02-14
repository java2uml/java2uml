package com.github.java2uml.plugin.idea;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Андрей on 07.02.2015.
 */
public class PluginSettings {
    static Map<String, Integer> settings;

    public final static int ENGLISH = 0;
    public final static int RUSSIAN = 1;
    public final static int HORIZONTAL = 0;
    public final static int VERTICAL = 1;
    public final static int PARSING = 0;
    public final static int REFLECTION = 1;
    public final static int PNG = 0;
    public final static  int SVG = 1;
    public final static  int CLASS = 0;
    public final static  int SEQUENCE = 1;

    private PluginSettings(){

    }

    static void initSettings(){
           //инициализируем настройки по умолчанию (или восстанавливаем с прошлого сеанса)
        String[] keys = {"language", "direction", "parseType", "aggregation", "association", "composition",
                "typeOfDiagram", "diagramExtension", "header", "lollipop", "showDiagram"};
        settings = new HashMap<>();
        settings.put(keys[0], ENGLISH);
        settings.put(keys[1], VERTICAL);
        settings.put(keys[2], REFLECTION);
        settings.put(keys[3], 1);
        settings.put(keys[4], 1);
        settings.put(keys[5], 1);
        settings.put(keys[6], CLASS);
        settings.put(keys[7], PNG);
        settings.put(keys[8], 0);
        settings.put(keys[9], 1);
        settings.put(keys[10], 1);
    }

    public static Map<String, Integer> getSettings() {
        if (settings == null){
           initSettings();
        }
        return settings;
    }

    public static void setSettings(String key, int value){
        settings.put(key, value);
    }
}
