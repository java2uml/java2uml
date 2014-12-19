
package com.github.java2uml.javapars;


import com.github.java2uml.javapars.core.*;
import com.github.java2uml.javapars.core.Package;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.*;

import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.*;
import java.lang.reflect.Modifier;
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
            getEnums(pack.getEnumDeclaration(), pack.getCu());
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
                        source.append(nameWithPath(clazz.getCu().getImports(), type.getName()));
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
                        source.append("\n" + nameWithPath(clazz.getCu().getImports(), type.getName()));
                    
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
                    
                    // todo Вытягиваем константы и внутренние классы
                    source.append("}\n");
                    setAggregation(clazz.getCu().getImports(), clazz.getCu().getPackage(), n.getName());

                }
            }
        }

    }
    
    private void getEnums(EnumDeclaration n, CompilationUnit cu){
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
                    // Так как в EnumDeclaration нет прямого метода разбиения этих данных,
                    // преобразуем нужный нам кусок кода в нужный тип и получаем доступ
                    new VoidVisitorAdapter(){
                        @Override
                        public void visit(FieldDeclaration n, Object arg) {
                            source.append(".. Fields ..\n");
                            setModifier(n.getModifiers());
                            source.append(n.getType());
                            for (VariableDeclarator var : n.getVariables()) {
                                source.append(" " + var.getId() + "\n");
                            }
                        }
                    }.visit(n, null);
                    source.append(".. Methods ..\n");
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

            }
            source.append("}\n");
        }
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
        }
    }

    private void setAggregation(List<ImportDeclaration> imports, PackageDeclaration pack, String nameClass){

        if(imports != null && imports.size() > 0)
            for(ImportDeclaration imp : imports){
                for (String cl : Clazz.getClasses())
                    if(imp.getName().toString().toLowerCase().endsWith(cl.toLowerCase())){
                        source.append(nameClass);
//                        source.append(" \"" + imp.getName() + "\" ");
                        // Если в одном пакете делаем связь короткой, для exception отдельная стрелка
                        if(pack != null && imp.getName().toString().contains(pack.getName().toString()))
                            source.append(" o- ");
                        else if(imp.getName().toString().toLowerCase().contains("exception"))
                            source.append(" ..> ");
                        else
                            source.append(" o-- ");
//                        source.append(" \"" + nameClass + "\" ");
                        source.append(imp.getName() + "\n");
                    }
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
    
    private String nameWithPath(List<ImportDeclaration> imports, String nameClass){
        String path = nameClass;
        if(imports != null && imports.size() > 0)
        for(ImportDeclaration imp : imports){

            if(imp.getName().toString().toLowerCase().endsWith(nameClass.toLowerCase()))
                path = "" + imp.getName();
        }
        return path;
    }

    private boolean genericPackage(List<ImportDeclaration> imports, PackageDeclaration pack){
        String connection = "";
        if(imports != null && imports.size() > 0)
            for(ImportDeclaration imp : imports){
                if(imp.getName().toString().contains(pack.getName().toString()))
                    return true;
            }
        return false;
    }
}
