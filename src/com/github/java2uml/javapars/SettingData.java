
package com.github.java2uml.javapars;


import com.github.java2uml.javapars.core.Package;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;


import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nadcukandrej on 09.12.14.
 */
public class SettingData {

    private List<File> files;
    private List<Package> packages;

    public SettingData(String folder) throws Exception {
        packages = new ArrayList<Package>();
        init(folder);
    }

    public void init(String path) throws Exception {
        String absolutePath = path;
        File folder = new File(absolutePath);
        files = new ArrayList<File>();
        createArrayFiles(folder);
        for (File fileName : files) {
            getCU(fileName);
        }

    }

    public void getCU(File path) throws Exception {
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
         *   Создание пакетов
         */
        
        packages.add(new Package(cu));

    }

    public List<Package> getPackages() {
        return packages;
    }

    private void createArrayFiles(File path) {
        File[] folder = path.listFiles();

        for (int i = 0; i < folder.length; i++) {
            if (folder[i].isDirectory())
                createArrayFiles(folder[i]);
            else if (folder[i].toString().toLowerCase().endsWith(".java")) {
                files.add(folder[i]);
            }
        }
    }


}
