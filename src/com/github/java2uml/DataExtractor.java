package com.github.java2uml;

import net.sourceforge.plantuml.SourceStringReader;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;


public class DataExtractor {

    /**
     * Извлечение данных из множества классов для построения uml диаграмм в формате plantuml
     *
     * @param classes
     *
     * @return
     */
    public static String extract(final Set<Class> classes) {

        // текст в формате plantuml - начало сборки
        StringBuilder source = new StringBuilder();
        source.append("@startuml\n");
        source.append("skinparam classAttributeIconSize 0\n");

        // определение межклассовых связей
        for (Class clazz : classes) {
            // получение супер класса и реализованных интерфейсов
            Class superClass = clazz.getSuperclass();
            Class[] interfaces = clazz.getInterfaces();

            if (classes.contains(superClass)) {
                // супер класс доступен во множестве - добавим связь
                source.append(superClass.getSimpleName());
                source.append(" <|-- ");
                source.append(clazz.getSimpleName());
                source.append("\n");
            }
            for (Class interfc : interfaces) {
                if (classes.contains(interfc)) {
                    // интерфейс доступен во множестве - добавим связь
                    source.append(interfc.getSimpleName());
                    source.append(" <|-- ");
                    source.append(clazz.getSimpleName());
                    source.append("\n");
                }
            }

            // получение классов, являющихся полями текущего класса
            Field[] fieldClasses = clazz.getDeclaredFields();
            for (Field fieldClass : fieldClasses) {
                if (fieldClass.getType() instanceof Object) {
                    if (classes.contains(fieldClass.getType())) {
                        // поле доступно во множестве - добавим связь
                        source.append(clazz.getSimpleName());
                        source.append(" <-- ");
                        source.append(fieldClass.getType().getSimpleName());
                        source.append("\n");

                    }
                }

            }
        }

        for (Class clazz : classes) {
            // получение информации о класса
            String className = clazz.getSimpleName();

            // объявляем класс и его содержимое
            source.append(getClassModifiers(clazz));
            source.append(className);
            source.append(" {\n");

            // получение информации о полях
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                source.append(getMemberModifiers(field.getModifiers()));
                source.append(field.getName());
                source.append(" : ");
                source.append(field.getType().getSimpleName());
                source.append("\n");
            }

            // получение информации методах
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                source.append(getMemberModifiers(method.getModifiers()));
                source.append(method.getName());
                source.append("()");
                source.append(" : ");
                source.append(method.getReturnType().getSimpleName() + "\n");
            }
            source.append("}\n");
        }

        // конец сборки
        source.append("@enduml\n");
        return source.toString();
    }

    /**
     * Генерация диаграммы классов
     *
     * @param source
     */
    public static void generate(final String source) {
        try {
            File file = new File("d:\\test.png");
            if (!file.exists()) {
                file.createNewFile();
            }

            // поток вывода для диаграммы
            OutputStream png = new FileOutputStream(file);

            // генератор диаграмм
            SourceStringReader reader = new SourceStringReader(source);

            // генерация жиаграммы
            String desc = reader.generateImage(png);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получение модификаторов членов класса
     *
     * @param mod
     *
     * @return
     */
    private static String getMemberModifiers(final int mod) {
        // значение по умолчанию - package private
        String modStr = "~";
        if (Modifier.isPrivate(mod)) {
            modStr = "-";
        }
        if (Modifier.isProtected(mod)) {
            modStr = "#";
        }
        if (Modifier.isPublic(mod)) {
            modStr = "+";
        }
        if (Modifier.isAbstract(mod)) {
            modStr = "{abstract} " + modStr;
        }
        if (Modifier.isStatic(mod)) {
            modStr = "{static} " + modStr;
        }
        return modStr;
    }

    /**
     * Получение модификаторов класса
     *
     * @param clazz
     *
     * @return
     */
    private static String getClassModifiers(final Class clazz) {
        String modStr = "class ";
        if (Modifier.isAbstract(clazz.getModifiers())) {
            modStr = "abstract class ";
        }
        if (clazz.isInterface()) {
            modStr = "interface ";
        }
        return modStr;
    }
}
