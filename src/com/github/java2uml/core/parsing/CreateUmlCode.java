package com.github.java2uml.core.parsing;

import com.github.java2uml.core.Options;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import java.io.*;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * 
 * * Created by Nadchuk Andrei on 09.12.14.
 */
public class CreateUmlCode {

    public static StringBuilder source;
    public static StringBuilder connections;
    public static StringBuilder aggregation;
    public static StringBuilder composition;
    public static StringBuilder association;
    private static String fileUMLDiagramClasses;
    public static final String UML_TEMPLATE = "uml_templates";
    public static List<String> classes;
    private static String projectName;
    private int typeConnections;
    private static int level = 0;
    private static String color;

    public CreateUmlCode() throws Exception {
        projectName = endAfterLastPoint(Options.getPath(), "/");
        // Генерирование названия файла UML
        fileUMLDiagramClasses = Options.getOutputFile();
        init(Options.getPath());
    }

    public void init(String path) throws Exception {
        classes = new ArrayList<String>();
        createListClasses(new File(path));
        source = new StringBuilder();
        connections = new StringBuilder();
        aggregation = new StringBuilder();
        composition = new StringBuilder();
        association = new StringBuilder();
        // текст в формате plantuml - начало сборки
        source.append("@startuml\n");
        

        // разбираем анализируемый проект
        readPackage(new File(path));
        // добавляем опции исходя из Options
        neededTypeConnections();
        // конец сборки
        source.append("@enduml\n");

    }

    /**
     *  Analysis package and the creation of nesting
     * @author - Nadchuk Andrei navikom11@mail.ru
     * @param path
     * @throws Exception
     */
    private void readPackage(File path) throws Exception {
        File[] folder = path.listFiles();


        for (int i = 0; i < folder.length; i++) {
            if (folder[i].isDirectory()) {

                if(folder[i].toString().contains(projectName + System.getProperty("file.separator") + "src") && getNamePackage(folder[i].toString()) != null) {
                    System.out.println("Reading folder... " + folder[i].toString());
                    level++;

                    color = (level == 1 ? "#FFFFFF" : "#DDDDDD");
                    source.append("namespace ");
                    source.append(getNamePackage(folder[i].toString()) + " " + color + " {\n");
                }
                readPackage(folder[i]);
                if(folder[i].toString().contains(projectName + System.getProperty("file.separator") + "src") && getNamePackage(folder[i].toString()) != null) {
                    source.append("}\n");
                    level--;
                }
            }
            else if (folder[i].toString().toLowerCase().endsWith(".java") && folder[i].toString().contains(projectName + System.getProperty("file.separator") + "src")) {
                System.out.println("Reading file... " + folder[i].toString());
                createCU(folder[i]);
            }
        }

    }

    /**
     *  Call parser file
     * @author - Nadchuk Andrei navikom11@mail.ru
     * @param path
     * @throws Exception
     */
    public void createCU(File path) throws Exception {
        // creates an input stream for the file to be parsed
        FileInputStream in = new FileInputStream(path);
        CompilationUnit cu;
        try {
            // parse the file
            cu = JavaParser.parse(in);
        } finally {
            in.close();
        }
        /**
         *   Начало анализа кода
         */
        if(Options.isClassDiagram())
            new UMLDiagramClasses(cu);
        else if(Options.isSequenceDiagram()){
            // todo создать генератор диаграммы последовательностей
            
        }
    }

    public static String setModifier(int mod) {
        switch (mod) {
            case Modifier.PUBLIC:
                return " +";
            case Modifier.PRIVATE:
                return (" -");
            case Modifier.PROTECTED:
                return (" #");
            case Modifier.STATIC:
                return (" {static}");
            case Modifier.PUBLIC | Modifier.STATIC:
                return (" +{static}");
            case Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL:
                return (" +{static}");
            case Modifier.PROTECTED | Modifier.STATIC:
                return (" #{static}");
            case Modifier.PROTECTED | Modifier.STATIC | Modifier.FINAL:
                return (" #{static}");
            case Modifier.PRIVATE | Modifier.STATIC:
                return (" -{static}");
            case Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL:
                return (" -{static}");
            case Modifier.PUBLIC | Modifier.FINAL:
                return (" +");
            case Modifier.PROTECTED | Modifier.FINAL:
                return (" #");
            case Modifier.PRIVATE | Modifier.FINAL:
                return (" -");
            case Modifier.ABSTRACT:
                return (" {abstract}");
            default:
                return "";
        }
    }

    /**
     *  Writing to a file generated string 
     * @author - Nadchuk Andrei navikom11@mail.ru 
     * @throws IOException
     */
    public static void write() throws IOException, CreateUmlCodeException {
        String text = source.toString();
        if(text.trim().length() == 0)
            throw new CreateUmlCodeException("plantUML code is not generated");
        //Определяем папку и файл для записи кода plantUml
//        File folder = new File(UML_TEMPLATE).getAbsoluteFile();
//        File file = new File(UML_TEMPLATE + "/" + fileUMLDiagramClasses).getAbsoluteFile();
        //Определяем файл для записи кода plantUml
        File file = new File(fileUMLDiagramClasses).getAbsoluteFile();

        
        //проверяем, что если папка не существует то создаем ее
//        if (!folder.exists()) {
//            folder.mkdir();
//        }
        //проверяем, что если файл не существует то создаем его
        if (!file.exists()) {
            file.createNewFile();
        }

        //PrintWriter обеспечит возможности записи в файл
        PrintWriter out = new PrintWriter(file.getAbsoluteFile());

        try {
            //Записываем текст в файл
            out.print(text);
        } catch (Exception e){
            throw new CreateUmlCodeException("Error writing to file");
        }finally {
            out.close();
        }
    }

    public static String endAfterLastPoint(String string, String separator){
        String[] str = string.split(separator);
        if(inArray(str, "src"))
            return str[getIndexProjectName(str, "src")];
        return str.length > 1 ? str[str.length-1] : string;
    }
    
    private static boolean inArray(String[] arr, String str){
        for (int i = 0; i < arr.length; i++){
            if(str.equals(arr[i]))
                return true;
            
        }
        return false;
    }
    
    private static int getIndexProjectName(String[] arr, String str){
        for (int i = 0; i < arr.length; i++){
            if(str.equals(arr[i]))
                return i - 1;

        }
        return 0;
        
    }

    private String getNamePackage(String path){
        String[] subString = path.split("src");
        if (subString.length > 1 && (subString[1].contains(".") || subString[1].contains("-")))
            return null;
        String namePackage = subString.length > 1 ? subString[1].replace(System.getProperty("file.separator"), ".").substring(1) : null;
        return namePackage;
    }

    private void createListClasses(File path) throws CreateUmlCodeException{
        if(path.exists()) {
            File[] folder = path.listFiles();

            for (int i = 0; i < folder.length; i++) {
                if (folder[i].isDirectory()) {
                    createListClasses(folder[i]);
                } else if (folder[i].toString().toLowerCase().endsWith(".java")) {
                    classes.add(getNameClass(folder[i].toString()));
                }
            }
        }else 
            throw new CreateUmlCodeException("Folder is not exist");
    }

    private String getNameClass(String file){
        String[] subString = file.split(Pattern.quote(System.getProperty("file.separator")));
        String className = subString.length > 1 ? subString[subString.length - 1].replace(".java", "") : null;
        return className;
    }

    /**
     *  Adding links and other options
     * @author - Nadchuk Andrei navikom11@mail.ru 
     */
    private void neededTypeConnections(){

        if(Options.getHeader() != null && Options.getHeader().length() > 0)
            source.append("title " + Options.getHeader() + "\n");

        source.append(connections);
        if(Options.isShowAggregation())
            source.append(aggregation);
        if(Options.isShowComposition())
            source.append(composition);
        if(Options.isShowAssociation())
            source.append(association);
        
    }
}
