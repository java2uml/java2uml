package com.github.java2uml.javapars.core;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by nadcukandrej on 18.12.14.
 */
public class Package {
    private CompilationUnit cu;
    private PackageDeclaration pack;
    private List<Clazz> classes;
    
    public Package(CompilationUnit cu) {
        this.cu = cu;
        classes = new ArrayList<Clazz>();

        /**
         *  Вызов визитора для packages
         */
        new GetPackageDeclaration().visit(cu, null);
        
        /**
         *  Добавление класса в коллекцию
         */
        classes.add(new Clazz(cu));

    }

    public CompilationUnit getCu() {
        return cu;
    }

    public PackageDeclaration getPack() {
        return pack;
    }

    public List<Clazz> getClasses() {
        return classes;
    }

    /**
     * Visitor implementation for visiting PackageDeclaration nodes.
     */
    private class GetPackageDeclaration extends VoidVisitorAdapter {
        @Override
        public void visit(PackageDeclaration n, Object arg) {
                pack = n;
        }
    }

    public boolean nonePack(){
        return pack == null;

    }

}
