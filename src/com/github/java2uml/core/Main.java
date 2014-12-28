package com.github.java2uml.core;

import com.github.java2uml.core.reflection.UMLClassLoader;

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
