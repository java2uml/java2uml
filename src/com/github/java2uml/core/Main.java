package com.github.java2uml.core;

import com.github.java2uml.core.reflection.UMLClassLoader;
import com.github.java2uml.gui.UI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

public class Main {
    //Из класса UI эту переменную меняет JFileChooser, при выборе директории
    public static String path;
    static String[] args;
    UI ui;

    public static String getPath() {
        return path;
    }

    public static void setPath(String _path) {
        path = _path;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Java2UML starting point...");
        Main.args = args;
        Main main = new Main();
        main.go();

/*
        if (args.length < 3) {
            throw new InvalidParameterException("Too few parameters.");
        }

        Options options = new Options();

        options.setPath(args[0]);
        options.setOutputFile(args[1]);

        int sourceFileType;
        int length2Param = args[2].length();
        if (length2Param >= 3 && length2Param <= "java".length() && "java".substring(0, length2Param).equals(args[2])) {
            sourceFileType = 1;
        } else if (length2Param >= 3 && length2Param <= "class".length() && "class".substring(0, length2Param).equals(args[2])) {
            sourceFileType = 2;
        } else {
            throw new InvalidParameterException("Incorrect parameters.");
        }

        for (int i = 3; i < args.length; i++) {
            String param = args[i].toLowerCase();
            int paramLength = param.length();
            if (paramLength < 3) {
                throw new InvalidParameterException("Incorrect parameters.");
            }

            if (paramLength <= "classes_diagramm".length() && "classes_diagramm".substring(0, paramLength).equals(param)) {
                options.setClassDiagram();
                continue;
            }

            if (paramLength <= "sequence_diagram".length() && "sequence_diagram".substring(0, paramLength).equals(param)) {
                if (sourceFileType == 1) {
                    options.resetClassDiagram();
                    continue;
                } else {
                    throw new InvalidParameterException("Incompatible parameters.");
                }
            }

            if (paramLength <= "vertical".length() && "vertical".substring(0, paramLength).equals(param)) {
                for (int j = 3; j < args.length; j++) {
                    String comparedParam = args[j];
                    if (comparedParam.length() <= "horizontal".length() && "horizontal".substring(0, comparedParam.length()).equals(comparedParam)) {
                        throw new InvalidParameterException("Incompatible parameters.");
                    }
                }
                options.setVertical();
                continue;
            }

            if (paramLength <= "horizontal".length() && "horizontal".substring(0, paramLength).equals(param)) {
                for (int j = 3; j < args.length; j++) {
                    String comparedParam = args[j];
                    if (comparedParam.length() <= "vertical".length() && "vertical".substring(0, comparedParam.length()).equals(comparedParam)) {
                        throw new InvalidParameterException("Incompatible parameters.");
                    }
                }
                options.setHorizontal();
                continue;
            }

            if (paramLength < 5) {
                throw new InvalidParameterException("Incorrect parameters.");
            }

            if (paramLength <= "nocomposition".length() && "nocomposition".substring(0, paramLength).equals(param)) {
                options.resetComposition();
                continue;
            }

            if (paramLength <= "noaggregation".length() && "noaggregation".substring(0, paramLength).equals(param)) {
                options.resetAggregation();
                continue;
            }

            if (paramLength <= "noassociation".length() && "noassociation".substring(0, paramLength).equals(param)) {
                options.resetAssociation();
                continue;
            }

            if (paramLength <= "nolollipop".length() && "nolollipop".substring(0, paramLength).equals(param)) {
                options.resetLollipop();
                continue;
            }

            throw new InvalidParameterException("Incorrect parameters.");
        }

        switch (sourceFileType) {
            case 1:
                // вызываем парсинг
                System.out.println("Парсинг");
                break;
            case 2:
                // вызываем рефлексию
                System.out.println("Рефлексия");
                break;
        }
 //*/
    }

    private void go() throws Exception {
        Method[] methods;
        Field[] fields;
        Constructor[] constructors;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initUI();
            }
        });


    }

    public void initUI() {
        ui = new UI();
        ui.initUI().setVisible(true);
        ui.addActionListenerToChooseFile();
        ui.getGeneratePlantUML().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread generateDiagramThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadClassesAndGenerateDiagram(ui.getPath().getText());
                    }
                });
                generateDiagramThread.start();

            }
        });
    }

    public void loadClassesAndGenerateDiagram(String path) {
        UMLClassLoader ecl = new UMLClassLoader();
        Set<Class> classes = null;

        ui.increaseProgressBarForTwenty();

        try {
            classes = ecl.loadClasses(path);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        ui.increaseProgressBarForTwenty();

        //todo убрать вывод в консоль после тестирования
        if (classes == null || classes.size() == 0) {
            System.out.println("Классы не загружены.");
        } else {
            System.out.println("Классы загружены, передаем на обработку.");
            System.out.println("----------------------------------------");

            for (Class clazz : classes) {
                System.out.println(clazz.getName());
                ui.increaseProgressBarForTwenty();
            }

            String diagram = DataExtractor.extract(classes);
            //        System.out.println(diagram);
            ui.getGeneratedCode().setText(diagram);
            DataExtractor.generate(diagram);

            ui.setProgressBarComplete();

            ui.showDiagram();
        }
    }
}
