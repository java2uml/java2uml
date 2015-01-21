package com.github.java2uml.core.parsing;

import com.github.java2uml.core.Options;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.*;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.ThrowStmt;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nadchuk Andrei on 21.12.14.
 */
public class UMLDiagramClasses {
    private CompilationUnit cu;

    public UMLDiagramClasses(CompilationUnit cu) {
        this.cu = cu;
        getClasses();
        getEnums();
    }

    /**
     * Code generation for the classes and interfaces
     *
     * @author - Nadchuk Andrei navikom11@mail.ru
     */
    private void getClasses() {

        new VoidVisitorAdapter() {
            @Override
            public void visit(ClassOrInterfaceDeclaration n, Object arg) {


                if (n.isInterface())
                    CreateUmlCode.source.append(Modifier.toString(Modifier.INTERFACE) + " ");

                else
                    CreateUmlCode.source.append("class ");

                CreateUmlCode.source.append(n.getName());

                // Если класс имплементирует интерфейсы создаем связь
                if (n.getImplements() != null) {
                    for (ClassOrInterfaceType type : n.getImplements()) {
                        // Отделяем сторонние интерфейсы
                        if (availability(type.getName())) {
                            CreateUmlCode.connections.append(nameWithPath(type.getName()));
                            // Если внутри пакета связь делаем короткую
                            if (genericPackage(cu.getImports(), cu.getPackage()))
                                CreateUmlCode.connections.append(" <|. ");
                            else
                                CreateUmlCode.connections.append(" <|.. ");

                            CreateUmlCode.connections.append(nameWithPath(n.getName()) + "\n");
                        } else if (Options.isShowLollipop()) {
                            CreateUmlCode.source.append(" <<");
                            CreateUmlCode.source.append(type.getName());
                            if (n.getImplements().size() > 1)
                                CreateUmlCode.source.append(",");
                            CreateUmlCode.source.append(">> ");
                        }
                    }
                }
                // Если класс наследует другой класс создаем связь
                if (n.getExtends() != null && !n.getName().toLowerCase().contains("exception")) {
                    for (ClassOrInterfaceType type : n.getExtends()) {
                        // Отделяем сторонние классы
                        if (availability(type.getName())) {
                            CreateUmlCode.connections.append(nameWithPath(type.getName()));
                            if (genericPackage(cu.getImports(), cu.getPackage()))
                                CreateUmlCode.connections.append(" <|- ");
                            else
                                CreateUmlCode.connections.append(" <|-- ");
                            break;
                        } else if (!n.getName().toLowerCase().contains("exception")) {
                            CreateUmlCode.connections.append(type.getName());
                            if (Options.isShowLollipop()) {
                                CreateUmlCode.connections.append(" ()- ");
                            } else
                                CreateUmlCode.connections.append(" <|- ");
                        }

                    }


                    CreateUmlCode.connections.append(nameWithPath(n.getName()) + "\n");
                }

                // Определяем класс наследуемый exceptions
                if (n.getName().toLowerCase().contains("exception"))
                    CreateUmlCode.source.append(" << (E,yellow) >> ");
                // Определяем точку входа
                if (n.getMembers().toString().contains("public static void main(String[] args)") && !n.getName().equals("UMLDiagramClasses"))
                    CreateUmlCode.source.append(" << start >> ");

                if (n.getMembers().size() > 0) {
                    CreateUmlCode.source.append("{\n");

                    // Вытягиваем поля
                    setFields(n);
                    List<String> allThrows = new ArrayList<String>();
                    // Вытягиваем конструкторы
                    setConstructors(n, nameWithPath(n.getName()), allThrows);

                    // Вытягиваем методы
                    setMethods(n, allThrows);

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

    private void setConstructors(ClassOrInterfaceDeclaration n, final String nameClass, final List allThrows) {
        new VoidVisitorAdapter() {
            @Override
            public void visit(ConstructorDeclaration n, Object arg) {
                if (n.getThrows() != null)
                    for (NameExpr expr : n.getThrows()) {
                        String throwName = (expr.getComment() != null
                                ?
                                expr.toString().replace(expr.getComment().toString(), "")
                                :
                                expr.toString());
                        if (allThrows != null && availability(throwName) && quantityConnection(allThrows, throwName) == 0) {

                            CreateUmlCode.association.append(nameWithPath(throwName));
                            CreateUmlCode.association.append(" <.. ");
                            CreateUmlCode.association.append(nameClass + "\n");
                            allThrows.add(throwName);
                        }
                    }

            }
        }.visit(n, null);

    }

    /**
     * The isolation of classes created in the analyzed project
     *
     * @param clazz - class name
     * @return
     * @author - Nadchuk Andrei navikom11@mail.ru
     */
    private boolean availability(String clazz) {
        for (String item : CreateUmlCode.classes) {
            if (clazz.equals(item)) {
                return true;
            }
        }
        return false;

    }

    /**
     * Generation Communications - aggregation
     *
     * @param n         - piece of code
     * @param nameClass - class name
     * @author - Nadchuk Andrei navikom11@mail.ru
     */
    private void setAggregation(ClassOrInterfaceDeclaration n, final String nameClass) {
        StringBuilder writeClass = new StringBuilder();
        final List<String> typeFields = new ArrayList<String>();
        new VoidVisitorAdapter() {
            @Override
            public void visit(FieldDeclaration n, Object arg) {
                if (n != null && !Modifier.toString(n.getModifiers()).contains("static"))
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

    /**
     * Determination of the number of connections
     *
     * @param classes    - classes are already trapped in buffer
     * @param checkClass
     * @return
     * @author - Nadchuk Andrei navikom11@mail.ru
     */
    private int quantityConnection(List<String> classes, String checkClass) {
        int quantity = 0;
        for (String clazz : classes) {
            if (clazz.equals(checkClass))
                quantity++;
        }
        return quantity;
    }

    /**
     * Generation of communication for internal enum
     *
     * @param n         - piece of code
     * @param className
     * @param path
     * @author - Nadchuk Andrei navikom11@mail.ru
     */
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
     * Search inner classes
     *
     * @param n - piece of code
     * @author - Nadchuk Andrei navikom11@mail.ru
     */
    private void accessClass(ClassOrInterfaceDeclaration n) {
        new VoidVisitorAdapter() {
            @Override
            public void visit(ClassOrInterfaceDeclaration n, Object arg) {

                for (BodyDeclaration body : n.getMembers()) {

                    // Находим в коде признаки внутреннего класса
                    if (body.toString().contains(" class ") && isClass(body)) {

                        // Преобразуем доступа к нужным методам
                        ClassOrInterfaceDeclaration cu = (ClassOrInterfaceDeclaration) body;
                        getDataInnerClass(cu, n.getName());
                    }
                }
            }
        }.visit(n, null);

    }

    private boolean isClass(BodyDeclaration body) {
        String lines = body.getComment() != null ? body.toString().replace(body.getComment().toString(), "") : body.toString();
        String[] strings = lines.split(System.getProperty("line.separator"));
        String string = strings[0];
        if ((string.startsWith("protected ")
                || string.startsWith("private ")
                || string.startsWith("class ")
                || string.startsWith("abstract")
                || string.startsWith("static"))
                && string.contains(" class "))
            return true;
        return false;

    }

    /**
     * Generation of communication for inner classes
     *
     * @param n           - piece of code
     * @param parentClass
     * @author - Nadchuk Andrei navikom11@mail.rua
     */
    private void getDataInnerClass(ClassOrInterfaceDeclaration n, final String parentClass) {
        new VoidVisitorAdapter() {
            @Override
            public void visit(ClassOrInterfaceDeclaration n, Object arg) {

                if (n.isInterface())
                    CreateUmlCode.source.append(Modifier.toString(Modifier.INTERFACE) + " ");
                else
                    CreateUmlCode.source.append("class ");
                CreateUmlCode.source.append(n.getName() + "_" + parentClass);

                // Если класс имплементирует интерфейсы создаем связь
                if (n.getImplements() != null) {
                    for (ClassOrInterfaceType type : n.getImplements()) {
                        // Отделяем сторонние интерфейсы
                        if (availability(type.getName())) {
                            // Если внутри пакета связь делаем короткую
                            CreateUmlCode.connections.append(nameWithPath(type.getName()));
                            // Если внутри пакета связь делаем короткую
                            if (genericPackage(cu.getImports(), cu.getPackage()))
                                CreateUmlCode.connections.append(" <|. ");
                            else
                                CreateUmlCode.connections.append(" <|.. ");

                            CreateUmlCode.connections.append(nameWithPath(n.getName()) + "_");
                            CreateUmlCode.connections.append(parentClass + "\n");
                        } else if (Options.isShowLollipop()) {
                            CreateUmlCode.source.append(" <<");
                            CreateUmlCode.source.append(type.getName());
                            if (n.getImplements().size() > 1)
                                CreateUmlCode.source.append(",");
                            CreateUmlCode.source.append(">> ");
                        }
                    }
                }
                // Если класс наследует другой класс создаем связь
                if (n.getExtends() != null && !n.getName().toLowerCase().contains("exception")) {
                    for (ClassOrInterfaceType type : n.getExtends()) {
                        // Отделяем сторонние классы
                        if (availability(type.getName())) {
                            CreateUmlCode.connections.append(nameWithPath(type.getName()));
                            if (genericPackage(cu.getImports(), cu.getPackage()))
                                CreateUmlCode.connections.append(" <|- ");
                            else
                                CreateUmlCode.connections.append(" <|-- ");
                            break;
                        } else if (!n.getName().toLowerCase().contains("exception")) {
                            CreateUmlCode.connections.append(type.getName());
                            if (Options.isShowLollipop()) {
                                CreateUmlCode.connections.append(" ()- ");
                            } else
                                CreateUmlCode.connections.append(" <|- ");
                        }

                    }


                    CreateUmlCode.connections.append(nameWithPath(n.getName()) + "_");
                    CreateUmlCode.connections.append(parentClass + "\n");
                }

                // Определяем класс наследуемый exceptions
                if (n.getName().toLowerCase().contains("exception"))
                    CreateUmlCode.source.append(" << (E,yellow) >> ");

                if (n.getMembers().size() > 0) {
                    CreateUmlCode.source.append("{\n");

                    // Вытягиваем поля
                    setFields(n);

                    // Вытягиваем методы
                    setMethods(n, null);

                    CreateUmlCode.source.append("}\n");

                    // Добавляем связь - композицию
                    setComposition(getPackage() + n.getName() + "_" + parentClass, parentClass);
                } else
                    CreateUmlCode.source.append("\n");
            }
        }.visit(n, null);
    }

    /**
     * Generation Communications - composition
     *
     * @param innerClass
     * @param parentClass
     * @author - Nadchuk Andrei navikom11@mail.ru
     */
    private void setComposition(String innerClass, String parentClass) {

        CreateUmlCode.composition.append(innerClass);
        CreateUmlCode.composition.append(" *- ");
        CreateUmlCode.composition.append(getPackage() + parentClass + "\n");
    }

    /**
     * Code generation for enum
     *
     * @author - Nadchuk Andrei navikom11@mail.ru
     */
    private void getEnums() {
        // Посещаем визитор EnumDeclaration
        new VoidVisitorAdapter() {
            @Override
            public void visit(EnumDeclaration n, Object arg) {
                if (n != null) {
                    CreateUmlCode.source.append("enum " + n.getName());
                    CreateUmlCode.source.append("{\n");
                    if (n.getMembers() != null || n.getEntries() != null) {

                        // Вытягиваем константы
                        if (n.getEntries() != null) {
                            for (EnumConstantDeclaration constant : n.getEntries())
                                CreateUmlCode.source.append(constant.getName() + "\n");
                        }
                        // Вытягиваем поля и методы
                        if (n.getMembers() != null && n.getMembers().size() > 0) {
                            setEnumFields(n);
                            setEnumMethods(n);
                        }

                    }
                    CreateUmlCode.source.append("}\n");

                }
            }
        }.visit(cu, null);

    }

    /**
     * Code generation enum fields
     *
     * @param n - piece of code
     * @author - Nadchuk Andrei navikom11@mail.ru
     */
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

    /**
     * Code generation enum methods
     *
     * @param n - piece of code
     * @author - Nadchuk Andrei navikom11@mail.ru
     */
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

    /**
     * Code generation for class fields
     *
     * @param clazz
     * @author - Nadchuk Andrei navikom11@mail.ru
     */
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

    /**
     * Code generation for class methods
     *
     * @param clazz
     * @author - Nadchuk Andrei navikom11@mail.ru
     */
    public void setMethods(final ClassOrInterfaceDeclaration clazz, final List allThrows) {

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

                if (n.getThrows() != null) {

                    for (NameExpr expr : n.getThrows()) {
                        String throwName = (expr.getComment() != null
                                ?
                                expr.toString().replace(expr.getComment().toString(), "")
                                :
                                expr.toString());
                        if (allThrows != null && availability(throwName) && quantityConnection(allThrows, throwName) == 0) {

                            CreateUmlCode.association.append(nameWithPath(throwName));
                            CreateUmlCode.association.append(" <.. ");
                            CreateUmlCode.association.append(nameWithPath(clazz.getName()) + "\n");
                            allThrows.add(throwName);
                        }
                    }

                }
            }
        }.visit(clazz, null);

    }

    /**
     * Code generation for method parameters
     *
     * @param parameters - collection parameters
     * @author - Nadchuk Andrei navikom11@mail.ru
     */
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

                StringBuilder builder = new StringBuilder();
                builder.append(Options.getPath());
                builder.append(System.getProperty("file.separator"));
                builder.append("src");
                builder.append(System.getProperty("file.separator"));
                builder.append(imp.getName().toString().replace(".", System.getProperty("file.separator")));
                builder.append(System.getProperty("file.separator"));
                builder.append(nameClass);
                builder.append(".java");
                File file = new File(builder.toString());

                if (file.exists())
                    return imp.getName().toString() + "." + nameClass;


            }
        return getPackage() + nameClass;
    }

    private boolean genericPackage(List<ImportDeclaration> imports, PackageDeclaration pack) {
        if (imports != null && imports.size() > 0 && pack != null)
            for (ImportDeclaration imp : imports) {
                if (imp.getName().toString().contains(pack.getName().toString()))
                    return true;
            }
        return false;
    }

    private boolean containsClass(String where, String what) {
        if (where.contains("<" + what + ">") || where.contains(what + "["))
            return true;
        return false;

    }

    private String getPackage() {

        return (cu.getPackage() == null ? "" : cu.getPackage().getName().toString() + ".");

    }

}
