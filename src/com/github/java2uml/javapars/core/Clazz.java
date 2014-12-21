package com.github.java2uml.javapars.core;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.*;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nadcukandrej on 18.12.14.
 */
public class Clazz{
    private CompilationUnit cu;
    
    private ClassOrInterfaceDeclaration clazz;
    private List<FieldDeclaration> fields;
    private List<MethodDeclaration> methods;
    private EnumConstantDeclaration constants;
    private static List<String> classes;

    public Clazz(CompilationUnit cu) {
        this.cu = cu;
        if(classes == null)
            classes = new ArrayList<String>();
        methods = new ArrayList<MethodDeclaration>();
        fields = new ArrayList<FieldDeclaration>();
        init();
    }
    
    private void init(){
        /**
         *  Вызов визитора для классов и интерфейсов
         */
        new VoidVisitorAdapter<Object>(){
            @Override
            public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                clazz = n;
                if(n != null)
                    classes.add(n.getName());

                if (n.getMembers().size() > 0) {
                    
                    // Вызов визитора для полей
                    new GetFields().visit(n, arg);

                    // Вызов визитора для методов
                    new GetMethods().visit(n, arg);

                }
            }
        }.visit(cu, null);
        
    }

    public CompilationUnit getCu() {
        return cu;
    }

    public static List<String> getClasses() {
        return classes;
    }

    public ClassOrInterfaceDeclaration getClazz() {
        return clazz;
    }

    public List<FieldDeclaration> getFields() {
        return fields;
    }

    public List<MethodDeclaration> getMethods() {
        return methods;
    }

    public EnumConstantDeclaration getConstants() {
        return constants;
    }

    /**
     * Visitor implementation for visiting EnumConstantDeclaration nodes.
     */
    private class GetEnumConstantDeclaration extends VoidVisitorAdapter {
        @Override
        public void visit(EnumConstantDeclaration n, Object arg) {
              constants = n;
        }
    }

    /**
     * Visitor implementation for visiting FieldDeclaration nodes.
     */
    private class GetFields extends VoidVisitorAdapter {
        @Override
        public void visit(FieldDeclaration n, Object arg) {
              fields.add(n);
        }
    }

    /**
     * Visitor implementation for visiting MethodDeclaration nodes.
     */
    private class GetMethods extends VoidVisitorAdapter {
        @Override
        public void visit(MethodDeclaration n, Object arg) {
              methods.add(n);
        }
    }
    
}
