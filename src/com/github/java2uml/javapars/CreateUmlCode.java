
package com.github.java2uml.javapars;


import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

import japa.parser.ast.body.*;

import japa.parser.ast.expr.Expression;
import japa.parser.ast.type.ClassOrInterfaceType;

import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nadcukandrej on 09.12.14.
 */
public class CreateUmlCode {

    private static List<File> files;
    private static StringBuilder source;
    private static String fileUML;
    public final static String UML_TEMPLATE = "uml_templates";

    public CreateUmlCode(String folder) throws Exception {
        // Генерирование названия файла UML
        fileUML = "UML" + getClass().getSimpleName() + ".pumpl.ft";

        init(folder);
    }

    public static void init(String path) throws Exception {
        String absolutePath = path;
        File folder = new File(absolutePath);
        files = new ArrayList<File>();
        createArrayFiles(folder);
        source = new StringBuilder();
        // текст в формате plantuml - начало сборки
        source.append("@startuml\n");
        for (File fileName : files) {
            getCU(fileName);
        }
        source.append("@enduml\n");

        // Запись в файл UML в папку uml_templates
        write(source.toString());
    }

    public static void getCU(File path) throws Exception {
        // creates an input stream for the file to be parsed
        FileInputStream in = new FileInputStream(path);
//        JavaParser.setCacheParser(false);
        CompilationUnit cu;
        try {
            // parse the file
            cu = JavaParser.parse(in);
        } finally {
            in.close();
        }
        /**
         *  Вызов визитора для классов и интерфейсов
         */
        new GetClassOrInterfaceDeclaration().visit(cu, null);

        /**
         *  Вызов визитора для ENUM
         */
        new GetEnumConstantDeclaration().visit(cu, null);
    }

    /**
     * Visitor implementation for visiting ClassOrInterfaceDeclaration nodes.
     */
    private static class GetClassOrInterfaceDeclaration extends VoidVisitorAdapter {

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Object arg) {

            if (n.getImplements() != null) {
                source.append("\n");
                for (ClassOrInterfaceType type : n.getImplements()) {
                    source.append(type.getName());
                    source.append(" <|-- ");
                    source.append(n.getName() + "\n");
                }
            }
            if (n.getExtends() != null) {
                for (ClassOrInterfaceType type : n.getExtends())
                    source.append("\n" + type.getName());
                source.append(" <|-- ");
                source.append(n.getName() + "\n\n");
            }
            source.append(Modifier.toString(n.getModifiers() - 1));
            source.append(n.getModifiers() - 1 > 0 ? " " : "");
            if (n.isInterface())
                source.append(Modifier.toString(Modifier.INTERFACE) + " ");
            else
                source.append("class ");

            source.append(n.getName());

            if (n.getMembers().size() > 0) {
                source.append("{\n");

                // Вызов визитора для полей
                new GetFields().visit(n, arg);

                // Вызов визитора для методов
                new GetMethods().visit(n, arg);
                source.append("}\n");
            }
        }

    }

    /**
     * Visitor implementation for visiting EnumConstantDeclaration nodes.
     */
    private static class GetEnumConstantDeclaration extends VoidVisitorAdapter {

        @Override
        public void visit(EnumConstantDeclaration n, Object arg) {
//            System.out.println(n.getName());

            if(n.getClassBody() != null) {
                for (BodyDeclaration var : n.getClassBody()) {
                    System.out.println("getAnnotations- " + var.getAnnotations()+ "\n");
                }
            }
        }

    }

    /**
     * Visitor implementation for visiting FieldDeclaration nodes.
     */
    private static class GetFields extends VoidVisitorAdapter {

        @Override
        public void visit(FieldDeclaration n, Object arg) {

            setModifier(n.getModifiers());

            source.append(n.getType());
            for (VariableDeclarator var : n.getVariables()) {
                source.append(" " + var.getId() + "\n");
            }
        }

    }

    /**
     * Visitor implementation for visiting MethodDeclaration nodes.
     */
    private static class GetMethods extends VoidVisitorAdapter {

        @Override
        public void visit(MethodDeclaration n, Object arg) {

            setModifier(n.getModifiers());

            source.append(n.getName() + "(");
            if (n.getParameters() != null) {
                setParameters(n.getParameters());
            }
            source.append(")\n");
        }

        private void setParameters(List<Parameter> parameters) {
            for (Parameter parameter : parameters) {
                source.append(parameter.getType() + " ");
                source.append(parameter.getId());
            }
        }

    }

    private static void setModifier(int mod) {
        switch (mod) {
            case Modifier.PRIVATE:
                source.append(" -");
                break;
            case Modifier.PROTECTED:
                source.append(" #");
                break;
            default:
                source.append(" +");
        }
    }

    private static void createArrayFiles(File path) {
        File[] folder = path.listFiles();

        for (int i = 0; i < folder.length; i++) {
            if (folder[i].isDirectory())
                createArrayFiles(folder[i]);
            else if (folder[i].toString().toLowerCase().endsWith(".java")) {
                files.add(folder[i]);
            }
        }

    }

    public static void write(String text) {
        //Определяем файл
        File folder = new File(UML_TEMPLATE).getAbsoluteFile();
        File file = new File(UML_TEMPLATE + "/" + fileUML).getAbsoluteFile();

        try {
            //проверяем, что если папка не существует то создаем ее
            if(!folder.exists()){
                folder.mkdir();
            }
            //проверяем, что если файл не существует то создаем его
            if (!file.exists()) {
                file.createNewFile();
            }

            //PrintWriter обеспечит возможности записи в файл
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

            try {
                //Записываем текст в файл
                out.print(text);
            } finally {
                out.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
