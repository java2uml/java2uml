package com.github.java2uml;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

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


        String path = args[0];

        UMLClassLoader ecl = new UMLClassLoader();

        List<Class> classes = ecl.loadClasses(path);

        Class c = classes.get(0);

        System.out.println("Класс: " + c.getName());

        System.out.println("Конструкторы:");
        constructors = c.getConstructors();
        for (Constructor constructor: constructors) {
            System.out.println(constructor.getName() + " modifiers " + constructor.getModifiers() + " parameters types " + constructor.getParameterTypes());
        }

        System.out.println("Методы:");
        methods = c.getMethods();
        for (Method method: methods) {
            System.out.println(method.getName() + " return " + method.getReturnType() + " modifiers " + method.getModifiers() + " parameters types " + method.getParameterTypes());
        }

        System.out.println("Поля:");
        fields = c.getFields();
        for (Field field: fields) {
            System.out.println(field.getName() + " " + field.getType());
        }
    }
}
