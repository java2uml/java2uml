package com.github.java2uml.core.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.Timer;

public class Main {
    static String[] args;

    public static void main(String[] args) throws Exception {
        System.out.println("Java2UML starting point...");
        Main.args = args;

        new Main().go();
    }

    private void go() throws Exception {
        Method[] methods;
        Field[] fields;
        Constructor[] constructors;
        
        //String path = args[0];
        String path = "C:\\Apache2_2\\htdocs\\eclipse\\SeaBattle\\bin";

        long startLoading = System.currentTimeMillis();
        UMLClassLoader ecl = new UMLClassLoader();
        long finishLoading = System.currentTimeMillis();

        Set<Class> classes = ecl.loadClasses(path);

        //todo убрать вывод в консоль после тестирования
        System.out.println("Классы загружены, передаем на обработку.");
        System.out.println("----------------------------------------");
        
        for( Class clazz : classes ) {
        	System.out.println(clazz.getName());
        }
        
        long startExtraction = System.currentTimeMillis();
        Boolean res = DataExtractor.extract(classes);
        long finishExtraction = System.currentTimeMillis();
        
        long startGeneration = System.currentTimeMillis();
        if (res) {
        	System.out.println("Source file has been generated successfully. Generate diagram...");
        	DataExtractor.generateFromFile("classes.plantuml", "diagrams/test.png");
        }
        long finishGeneration = System.currentTimeMillis();
        
        System.out.println("Loading: " + (double)(finishLoading - startLoading)/1000);
        System.out.println("Extraction: " + (double)(finishExtraction - startExtraction)/1000);
        System.out.println("Generation: " + (double)(finishGeneration - startGeneration)/1000);
    }
}
