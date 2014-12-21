
package com.github.java2uml.javapars;


import com.github.java2uml.javapars.core.*;
import com.github.java2uml.javapars.core.Package;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.*;

import japa.parser.ast.comments.Comment;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.Type;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nadcukandrej on 09.12.14.
 */
public class CreateUmlCode {

    private static SettingData data;
    private static StringBuilder source;
    private String fileUML;
    public final String UML_TEMPLATE = "uml_templates";


    public CreateUmlCode(String folder) throws Exception {
        // Генерирование названия файла UML
        fileUML = "UML" + getClass().getSimpleName() + ".pumpl.ft";

        init(folder);
    }

    public void init(String path) throws Exception {
        data = new SettingData(path);
        source = new StringBuilder();

        List<Package> packages = data.getPackages();
        // текст в формате plantuml - начало сборки
        source.append("@startuml\n");
//        source.append("skinparam classAttributeIconSize 0\n");
        // Перебираем пакеты
        for (Package pack : packages) {
            if (!pack.nonePack()) {
                source.append("namespace ");
                source.append(pack.getPack().getName() + " {\n");
            }
            // Вытягиваем классы
            getClasses(pack.getClasses());
            // Вытягиваем enum
            getEnums(pack.getCu());
            if (!pack.nonePack()) {
                source.append("}\n");
            }

        }
        source.append("@enduml\n");

        // Запись в файл UML в папку uml_templates
        write(source.toString());
    }

    private void getClasses(List<Clazz> classes) {
        for (Clazz clazz : classes) {
            ClassOrInterfaceDeclaration n = clazz.getClazz();
            if(n != null) {
                if (n.getImplements() != null) {
                    source.append("\n");
                    for (ClassOrInterfaceType type : n.getImplements()) {
                        // Если внутри пакета связь делаем короткую
                        source.append(nameWithPath(clazz.getCu(), type.getName()));
                        // Если внутри пакета связь делаем короткую
                        if(genericPackage(clazz.getCu().getImports(), clazz.getCu().getPackage()))
                            source.append(" <|. ");
                        else
                           source.append(" <|.. ");
                        
                        source.append(n.getName() + "\n");
                    }
                }
                if (n.getExtends() != null) {
                    for (ClassOrInterfaceType type : n.getExtends())
                        source.append("\n" + nameWithPath(clazz.getCu(), type.getName()));
                    
                    if(genericPackage(clazz.getCu().getImports(), clazz.getCu().getPackage()))
                        source.append(" <|- ");
                    else
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

                    // Вытягиваем поля
                    if (clazz.getFields() != null)
                        setFields(clazz.getFields());

                    // Вытягиваем методы
                    setMethods(clazz.getMethods());

                    source.append("}\n");
                    // todo Вытягиваем внутренние классы

                    // Добавляем связь внутренним enum
                    setConnectWithInnerEnums(n, n.getName());
                    // todo переделать логику добавления агрегации
                    setAggregation(n, clazz.getCu(), n.getName());
                }
            }
        }
    }
    // todo связь добавлять только тогда когда создается экземпляр объекта
    // экземпляры объекта в цикле
    // либо если объект передан через конструктор
    private void setAggregation(ClassOrInterfaceDeclaration n, final CompilationUnit cu, final String nameClass){
        final List<String> objects = new ArrayList<String>();
        // Анализируем конструктор
        new VoidVisitorAdapter(){
            @Override
            public void visit(ConstructorDeclaration n, Object arg) {
                if(n.getParameters() != null)
                for (Parameter parameter : n.getParameters()){
                    for (String cl : Clazz.getClasses()){
                        if(parameter.getType().toString().equals(cl)
                        && objectsWithConnection(objects, cu, parameter.getType().toString()) == null){
                            
                            source.append(nameClass);
                            source.append(" \"" + 1 + "\" ");
                            source.append(" o-- ");
                            source.append(" \"" + 1 + "\" ");
                            String connect = nameWithPath(cu, parameter.getType().toString());
                            source.append(connect + "\n");
                            objects.add(connect);
                        }
                    }
                }
            }
        }.visit(n, null);
        // Анализируем возвращаемы тип метода
        new VoidVisitorAdapter(){
            @Override
            public void visit(MethodDeclaration n, Object arg) {
                
                if(n.getParameters() != null && n.getBody() != null)
                    for (Parameter parameter : n.getParameters()){

                        // Добавляем связь только для объектов созданных в анализируемом проекте
                        // тело метода не должно быть пустым
                        // запрещаем замыкать связь на себя
                        // если Объект был в конструкторе, игнорируем его
                        for (String cl : Clazz.getClasses()) {
                            if (parameter.getType().toString().equals(cl)
                                && !n.getBody().toString().equals("{\n}")
                                && !parameter.getType().toString().equals(nameClass.toString())
                                && objectsWithConnection(objects, cu, parameter.getType().toString()) == null
                                    && !objectInComment(parameter.getAllContainedComments(), parameter.getType().toString())) {
                                
                                    source.append(nameClass);
                                    source.append(" \"" + 1 + "\" ");
                                    source.append(" o-- ");
                                    source.append(" \"" + 1 + "\" ");
                                    String connect = nameWithPath(cu, parameter.getType().toString());
                                    source.append(connect + "\n");
                                    objects.add(connect);


                            }
                        }
                    }

            }
        }.visit(n, null);
        
        // Анализируем тело метода
        new VoidVisitorAdapter(){
            @Override
            public void visit(BlockStmt n, Object arg) {
                // тело метода не должно быть пустым
                if(n.getStmts() != null)
                    for (Statement statement : n.getStmts()){
                        // Добавляем связь только для объектов созданных в анализируемом проекте
                        // запрещаем замыкать связь на себя
                        for (String clazz : Clazz.getClasses()){

                                if (statement.toString().contains("new " + clazz)
                                    && !clazz.equals(nameClass)
                                    && objectsWithConnection(objects, cu, clazz) == null
                                    && !objectInComment(statement.getAllContainedComments(), clazz)) {
                                        source.append(nameClass);
                                        source.append(" \"" + 1 + "\" ");
                                        source.append(" o-- ");
            //                            source.append(" \"" + 1 + "\" ");
                                        String connect = nameWithPath(cu, clazz);
                                    
                                        source.append(connect + "\n");
                                        objects.add(connect);
                                }
                            
                        }

                    }
            }
        }.visit(n, null);
    }
    
    private String objectsWithConnection(List<String> objects, CompilationUnit cu, String checkThis){
        
        for(String obj : objects) {
            if(obj.equals(nameWithPath(cu,checkThis))) {
                return obj;
            }
        }
        return null;
    }
    
    private boolean objectInComment(List<Comment> comments, String object){
        if(comments.size() > 0 && comments.get(0).toString().contains("new " + object))
            return true;
        return false;
    }
    
    private void setConnectWithInnerEnums(ClassOrInterfaceDeclaration n, final String className){
        // Так как в ClassOrInterfaceDeclaration нет прямого метода разбиения этих данных,
        // преобразуем нужный нам кусок кода в нужный тип и получаем доступ
        new VoidVisitorAdapter(){
            @Override
            public void visit(EnumDeclaration n, Object arg) {
                source.append(className);
                source.append(" *- ");
                source.append(n.getName() + "\n");
            }
        }.visit(n, null);
        
    }

    private void getEnums(CompilationUnit cu){
        // Посещаем визитор EnumDeclaration
        new VoidVisitorAdapter(){
            @Override
            public void visit(EnumDeclaration n, Object arg) {
                if(n != null) {
                    source.append("enum " + n.getName());
                    source.append("{\n");
                    if (n.getMembers().size() > 0) {

                        // Вытягиваем константы
                        if (n.getEntries() != null) {
                            for(EnumConstantDeclaration constant : n.getEntries())
                                source.append(constant.getName() + "\n");
                        }
                        // Вытягиваем поля и методы
                        if (n.getMembers().size() > 0) {
                            setEnumFields(n);
                            setEnumMethods(n);
                        }

                    }
                    source.append("}\n");

                }
            }
        }.visit(cu, null);

    }
    
    private void setEnumFields(EnumDeclaration n){
        source.append(".. Fields ..\n");
        // Так как в EnumDeclaration нет прямого метода разбиения этих данных,
        // преобразуем нужный нам кусок кода в нужный тип и получаем доступ
        new VoidVisitorAdapter(){
            @Override
            public void visit(FieldDeclaration n, Object arg) {
                setModifier(n.getModifiers());
                source.append(n.getType());
                for (VariableDeclarator var : n.getVariables()) {
                    source.append(" " + var.getId() + "\n");
                }
            }
        }.visit(n, null);
        
    }
    
    private void setEnumMethods(EnumDeclaration n){
        source.append(".. Methods ..\n");
        // Так как в EnumDeclaration нет прямого метода разбиения этих данных,
        // преобразуем нужный нам кусок кода в нужный тип и получаем доступ
        new VoidVisitorAdapter(){
            @Override
            public void visit(MethodDeclaration n, Object arg) {

                setModifier(n.getModifiers());
                if(n.getType() != null)
                    source.append(n.getType() + " ");
                source.append(n.getName() + "(");
                if (n.getParameters() != null) {
                    setParameters(n.getParameters());
                }
                source.append(")\n");
            }
        }.visit(n, null);
       
    }

    private void setFields(List<FieldDeclaration> fields) {
        if(fields != null && fields.size() > 0)
            for (FieldDeclaration n : fields) {
                setModifier(n.getModifiers());
                source.append(n.getType());
                for (VariableDeclarator var : n.getVariables()) {
                    source.append(" " + var.getId() + "\n");
                }
            }
    }


    public void setMethods(List<MethodDeclaration> methods) {
        if(methods != null && methods.size() > 0) {
            for(MethodDeclaration n : methods) {
                setModifier(n.getModifiers());
                if(n.getType() != null)
                    source.append(n.getType() + " ");
                source.append(n.getName() + "(");
                if (n.getParameters() != null) {
                    setParameters(n.getParameters());
                }
                    source.append(")\n");
            }
        }
    }

    private void setParameters(List<Parameter> parameters) {

        for (Parameter parameter : parameters) {

            source.append(parameter.getType() + " ");
            source.append(parameter.getId());
            if(parameters.size() > 1 && parameters.indexOf(parameter) < parameters.size() - 1)
                source.append(", ");
        }
    }

    private void setModifier(int mod) {
        switch (mod) {
            case Modifier.PUBLIC:
                source.append(" +");
                break;
            case Modifier.PRIVATE:
                source.append(" -");
                break;
            case Modifier.PROTECTED:
                source.append(" #");
                break;
            case Modifier.STATIC:
                source.append(" {static}");
                break;
            case Modifier.PUBLIC | Modifier.STATIC:
                source.append(" +{static}");
                break;
            case Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL:
                source.append(" +{static}");
                break;
            case Modifier.PROTECTED | Modifier.STATIC:
                source.append(" #{static}");
                break;
            case Modifier.PROTECTED | Modifier.STATIC | Modifier.FINAL:
                source.append(" #{static}");
                break;
            case Modifier.PRIVATE | Modifier.STATIC:
                source.append(" -{static}");
                break;
            case Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL:
                source.append(" -{static}");
                break;
            case Modifier.PUBLIC | Modifier.FINAL:
                source.append(" +");
                break;
            case Modifier.PROTECTED | Modifier.FINAL:
                source.append(" #");
                break;
            case Modifier.PRIVATE | Modifier.FINAL:
                source.append(" -");
                break;
            case Modifier.ABSTRACT:
                source.append(" {abstract}");
                break;
            default:
                source.append("");
        }
    }

    public void write(String text) {
        //Определяем файл
        File folder = new File(UML_TEMPLATE).getAbsoluteFile();
        File file = new File(UML_TEMPLATE + "/" + fileUML).getAbsoluteFile();

        try {
            //проверяем, что если папка не существует то создаем ее
            if (!folder.exists()) {
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
    
    private String nameWithPath(CompilationUnit cu, String nameClass){
        
        if(cu.getImports() != null && cu.getImports().size() > 0)
        for(ImportDeclaration imp : cu.getImports()){
            if(imp.getName().toString().toLowerCase().endsWith(nameClass.toLowerCase()))
                return imp.getName().toString();
        }
        return cu.getPackage().getName() + "." + nameClass;
    }

    private boolean genericPackage(List<ImportDeclaration> imports, PackageDeclaration pack){
        if(imports != null && imports.size() > 0)
            for(ImportDeclaration imp : imports){
                if(imp.getName().toString().contains(pack.getName().toString()))
                    return true;
            }
        return false;
    }
    
    private String endAfterLastPoint(String string){
        String[] str = string.split(".");
        return str.length > 1 ? str[str.length-1] : string;
    }
}
