package com.github.java2uml;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

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

        UMLClassLoader ecl = new UMLClassLoader();

        Set<Class> classes = ecl.loadClasses(path);

        //todo убрать вывод в консоль после тестирования
        System.out.println("Классы загружены, передаем на обработку.");
        System.out.println("----------------------------------------");
        
        for( Class clazz : classes ) {
        	System.out.println(clazz.getName());
        }
        
        String diagram = DataExtractor.extract(classes);
        System.out.println(diagram);
        DataExtractor.generate(diagram, "diagrams\\test.png");
    }
}
