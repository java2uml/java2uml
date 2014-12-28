package com.github.java2uml.core.parsing;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.*;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nadcukandrej on 21.12.14.
 */
public class UMLDiagramClasses {
    private CompilationUnit cu;

    public UMLDiagramClasses(CompilationUnit cu) {
        this.cu = cu;
        getClasses();
        getEnums();
    }

    private void getClasses() {
        
        new VoidVisitorAdapter() {
            @Override
            public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                // Если класс имплементирует интерфейсы создаем связь
                if (n.getImplements() != null) {
                    for (ClassOrInterfaceType type : n.getImplements()) {
                            // Отделяем сторонние интерфейсы
                            if(availability(type.getName())){
                                // Если внутри пакета связь делаем короткую
                                CreateUmlCode.connections.append(nameWithPath(type.getName()));
                                // Если внутри пакета связь делаем короткую
                                if (genericPackage(cu.getImports(), cu.getPackage()))
                                    CreateUmlCode.connections.append(" <|. ");
                                else
                                    CreateUmlCode.connections.append(" <|.. ");

                                CreateUmlCode.connections.append(nameWithPath(n.getName()) + "\n");
                            }else{
                                CreateUmlCode.connections.append(type.getName());
                                CreateUmlCode.connections.append(" ()- ");
                                CreateUmlCode.connections.append(nameWithPath(n.getName()) + "\n");
                            }
                            

                        
                    }
                }
                // Если класс наследует другой класс создаем связь
                if (n.getExtends() != null) {
                    for (ClassOrInterfaceType type : n.getExtends()) {
                        // Отделяем сторонние классы
                        if(availability(type.getName())) {
                            CreateUmlCode.connections.append(nameWithPath(type.getName()));
                            if (genericPackage(cu.getImports(), cu.getPackage()))
                                CreateUmlCode.connections.append(" <|- ");
                            else
                                CreateUmlCode.connections.append(" <|-- ");
                            break;
                        }else{
                            CreateUmlCode.connections.append(type.getName());
                            CreateUmlCode.connections.append(" ()- ");
                        }


                    }


                    CreateUmlCode.connections.append(nameWithPath(n.getName()) + "\n");
                }

                if (n.isInterface())
                    CreateUmlCode.source.append(Modifier.toString(Modifier.INTERFACE) + " ");

                else
                    CreateUmlCode.source.append("class ");
                CreateUmlCode.source.append(n.getName());
                // Определяем точку входа
                if (n.getMembers().toString().contains("public static void main(String[] args)") && !n.getName().equals("UMLDiagramClasses"))
                    CreateUmlCode.source.append(" << start >> ");

                if (n.getMembers().size() > 0) {
                    CreateUmlCode.source.append("{\n");

                    // Вытягиваем поля
                    setFields(n);

                    // Вытягиваем методы
                    setMethods(n);

                    CreateUmlCode.source.append("}\n");
                    // Вытягиваем внутренние классы
                    accessClass(n);

                    // Добавляем связь внутренним enum
                    setConnectWithInnerEnums(n, 
                            nameWithPath(n.getName()), 
                            cu.getPackage() == null ? "" : cu.getPackage().getName().toString());
                    // Добавляем связь - агрегацию
                    setAggregation(n, nameWithPath(n.getName()));
                } else
                    CreateUmlCode.source.append("\n");

            }
        }.visit(cu, null);

    }
    
    private boolean availability(String string){
        for (String item : CreateUmlCode.classes) {
            if (string.equals(item)) {
                return true;
            }
        }
        return false;
        
    }

    /**
     * Добавление связи - агрегации
     *
     * @param n
     * @param nameClass
     */
    private void setAggregation(ClassOrInterfaceDeclaration n, final String nameClass) {
        StringBuilder writeClass = new StringBuilder();
        final List<String> typeFields = new ArrayList<String>();
        new VoidVisitorAdapter() {
            @Override
            public void visit(FieldDeclaration n, Object arg) {
                if (n != null)
                    typeFields.add(n.getType().toString());
            }
        }.visit(n, null);

        for (String item : typeFields) {
            for (String cl : CreateUmlCode.classes) {
                if ((item.equals(cl) || containsClass(item, cl))
                        && !nameWithPath(cl).equals(nameClass) 
                        && !writeClass.toString().contains("." + cl + ".")) {
                    CreateUmlCode.aggregation.append(nameClass);
                    CreateUmlCode.aggregation.append(" \" " + 1 + "\" ");
                    CreateUmlCode.aggregation.append(" o-- ");
                    CreateUmlCode.aggregation.append(" \"0..");
                    if (item.equals(cl))
                        CreateUmlCode.aggregation.append(quantityConnection(typeFields, cl) + "\" ");
                    else
                        CreateUmlCode.aggregation.append("*\" ");

                    String connect = nameWithPath(cl);
                    CreateUmlCode.aggregation.append(connect + "\n");
                    writeClass.append("." + cl + ".");
                }
            }
        }
    }

    private int quantityConnection(List<String> classes, String checkClass) {
        int quantity = 0;
        for (String clazz : classes) {
            if (clazz.equals(checkClass))
                quantity++;

        }
        return quantity;
    }

    private void setConnectWithInnerEnums(ClassOrInterfaceDeclaration n, final String className, final String path) {
        // Так как в ClassOrInterfaceDeclaration нет прямого метода разбиения этих данных,
        // преобразуем нужный нам кусок кода в нужный тип и получаем доступ
        new VoidVisitorAdapter() {
            @Override
            public void visit(EnumDeclaration n, Object arg) {
                CreateUmlCode.composition.append(className);
                CreateUmlCode.composition.append(" *- ");
                CreateUmlCode.composition.append(path + "." + n.getName() + "\n");
            }
        }.visit(n, null);

    }

    /**
     *  Поиск внутренних классов
     * @param n
     */
    private void accessClass(ClassOrInterfaceDeclaration n){
        new VoidVisitorAdapter() {
            @Override
            public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                
                for (BodyDeclaration body : n.getMembers()){

                    // Находим в коде признаки внутреннего класса
                    if(body.toString().contains(" class ") && isClass(body)) {

                        // Преобразуем доступа к нужным методам
                        ClassOrInterfaceDeclaration cu = (ClassOrInterfaceDeclaration) body;
                        getDataInnerClass(cu, n.getName());
                    }
                }
            }
        }.visit(n, null);
        
    }
    
    private boolean isClass(BodyDeclaration body){
        if(body.toString().startsWith("protected ")
                || body.toString().startsWith("private ")
                || body.toString().startsWith("class ")
                || body.toString().startsWith("abstract") 
                || body.toString().startsWith("static"))
            return true;
        return false;
        
    }

    /**
     * 
     * @param n
     * @param parentClass
     */
    private void getDataInnerClass(ClassOrInterfaceDeclaration n, final String parentClass){
        new VoidVisitorAdapter() {
            @Override
            public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                // Если класс имплементирует интерфейсы создаем связь
                if (n.getImplements() != null) {
                    for (ClassOrInterfaceType type : n.getImplements()) {
                        // Отделяем сторонние интерфейсы
                        if(availability(type.getName())){
                            // Если внутри пакета связь делаем короткую
                            CreateUmlCode.connections.append(nameWithPath(type.getName()));
                            // Если внутри пакета связь делаем короткую
                            if (genericPackage(cu.getImports(), cu.getPackage()))
                                CreateUmlCode.connections.append(" <|. ");
                            else
                                CreateUmlCode.connections.append(" <|.. ");

                            CreateUmlCode.connections.append(cu.getPackage().getName() + "." + n.getName() + "\n");
                        }else{
                            CreateUmlCode.connections.append(type.getName());
                            CreateUmlCode.connections.append(" ()- ");
                            CreateUmlCode.connections.append(cu.getPackage().getName() + "." + n.getName() + "\n");
                        }



                    }
                }
                // Если класс наследует другой класс создаем связь
                if (n.getExtends() != null) {
                    for (ClassOrInterfaceType type : n.getExtends()) {
                        // Отделяем сторонние классы
                        if(availability(type.getName())) {
                            CreateUmlCode.connections.append(nameWithPath(type.getName()));
                            if (genericPackage(cu.getImports(), cu.getPackage()))
                                CreateUmlCode.connections.append(" <|- ");
                            else
                                CreateUmlCode.connections.append(" <|-- ");
                            break;
                        }else{
                            CreateUmlCode.connections.append(type.getName());
                            CreateUmlCode.connections.append(" ()- ");
                        }
                    }


                    CreateUmlCode.connections.append(cu.getPackage().getName() + "." + n.getName() + "\n");
                }
                
                if (n.isInterface())
                    CreateUmlCode.source.append(Modifier.toString(Modifier.INTERFACE) + " ");
                else
                    CreateUmlCode.source.append("class ");
                CreateUmlCode.source.append(n.getName());
                CreateUmlCode.source.append(" << inner >> ");
                if (n.getMembers().size() > 0) {
                    CreateUmlCode.source.append("{\n");

                    // Вытягиваем поля
                    setFields(n);

                    // Вытягиваем методы
                    setMethods(n);

                    CreateUmlCode.source.append("}\n");

                    // Добавляем связь - композицию
                    setComposition(cu.getPackage().getName() + "." + n.getName(), parentClass);
                } else
                    CreateUmlCode.source.append("\n");
            }
        }.visit(n, null);
    }
    
    private void setComposition(String innerClass, String parentClass){

        CreateUmlCode.composition.append(innerClass);
        CreateUmlCode.composition.append(" *- ");
        CreateUmlCode.composition.append(cu.getPackage().getName() + "." + parentClass + "\n");
    }

    private void getEnums() {
        // Посещаем визитор EnumDeclaration
        new VoidVisitorAdapter() {
            @Override
            public void visit(EnumDeclaration n, Object arg) {
                if (n != null) {
                    CreateUmlCode.source.append("enum " + n.getName());
                    CreateUmlCode.source.append("{\n");
                    if (n.getMembers() != null && n.getMembers().size() > 0) {

                        // Вытягиваем константы
                        if (n.getEntries() != null) {
                            for (EnumConstantDeclaration constant : n.getEntries())
                                CreateUmlCode.source.append(constant.getName() + "\n");
                        }
                        // Вытягиваем поля и методы
                        if (n.getMembers().size() > 0) {
                            setEnumFields(n);
                            setEnumMethods(n);
                        }

                    }
                    CreateUmlCode.source.append("}\n");

                }
            }
        }.visit(cu, null);

    }

    private void setEnumFields(EnumDeclaration n) {
        CreateUmlCode.source.append(".. Fields ..\n");
        // Так как в EnumDeclaration нет прямого метода разбиения этих данных,
        // преобразуем нужный нам кусок кода в нужный тип и получаем доступ
        new VoidVisitorAdapter() {
            @Override
            public void visit(FieldDeclaration n, Object arg) {
                CreateUmlCode.source.append(CreateUmlCode.setModifier(n.getModifiers()));
                CreateUmlCode.source.append(n.getType());
                for (VariableDeclarator var : n.getVariables()) {
                    CreateUmlCode.source.append(" " + var.getId() + "\n");
                }
            }
        }.visit(n, null);

    }

    private void setEnumMethods(EnumDeclaration n) {
        CreateUmlCode.source.append(".. Methods ..\n");
        // Так как в EnumDeclaration нет прямого метода разбиения этих данных,
        // преобразуем нужный нам кусок кода в нужный тип и получаем доступ
        new VoidVisitorAdapter() {
            @Override
            public void visit(MethodDeclaration n, Object arg) {

                CreateUmlCode.source.append(CreateUmlCode.setModifier(n.getModifiers()));
                if (n.getType() != null)
                    CreateUmlCode.source.append(n.getType() + " ");
                CreateUmlCode.source.append(n.getName() + "(");
                if (n.getParameters() != null) {
                    setParameters(n.getParameters());
                }
                CreateUmlCode.source.append(")\n");
            }
        }.visit(n, null);

    }

    private void setFields(ClassOrInterfaceDeclaration clazz) {

        new VoidVisitorAdapter() {
            @Override
            public void visit(FieldDeclaration n, Object arg) {

                CreateUmlCode.source.append(CreateUmlCode.setModifier(n.getModifiers()));
                CreateUmlCode.source.append(n.getType());
                for (VariableDeclarator var : n.getVariables()) {
                    CreateUmlCode.source.append(" " + var.getId() + "\n");
                }
            }
        }.visit(clazz, null);
    }


    public void setMethods(ClassOrInterfaceDeclaration clazz) {
        new VoidVisitorAdapter() {
            @Override
            public void visit(MethodDeclaration n, Object arg) {

                CreateUmlCode.source.append(CreateUmlCode.setModifier(n.getModifiers()));
                if (n.getType() != null)
                    CreateUmlCode.source.append(n.getType() + " ");
                CreateUmlCode.source.append(n.getName() + "(");
                if (n.getParameters() != null) {
                    setParameters(n.getParameters());
                }
                CreateUmlCode.source.append(")\n");
            }
        }.visit(clazz, null);

    }

    private void setParameters(List<Parameter> parameters) {

        for (Parameter parameter : parameters) {

            CreateUmlCode.source.append(parameter.getType() + " ");
            CreateUmlCode.source.append(parameter.getId());
            if (parameters.size() > 1 && parameters.indexOf(parameter) < parameters.size() - 1)
                CreateUmlCode.source.append(", ");
        }
    }

    private String nameWithPath(String nameClass) {
        if (cu.getImports() != null && cu.getImports().size() > 0)
            for (ImportDeclaration imp : cu.getImports()) {
                if (imp.getName().toString().toLowerCase().endsWith("." + nameClass.toLowerCase()))
                    return imp.getName().toString();
            }
        return (cu.getPackage() == null ? "" : cu.getPackage().getName() + ".") + nameClass;
    }

    private boolean genericPackage(List<ImportDeclaration> imports, PackageDeclaration pack) {
        if (imports != null && imports.size() > 0)
            for (ImportDeclaration imp : imports) {
                if (imp.getName().toString().contains(pack.getName().toString()))
                    return true;
            }
        return false;
    }
    
    private boolean containsClass(String where, String what){
        if(where.contains("<" + what + ">") || where.contains(what + "["))
            return true;
        return false;
        
    }

}
