package com.github.java2uml.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.java2uml.util.SomeUtils;

/**
 * Вспомогательный класс для передачи параметров запуска.
 *
 * Created by Игорь Акимов on 28.12.2014.
 */
public final class Options {
	
	private static Logger LOG = LoggerFactory.getLogger(Options.class);
	
    private static String path = null;
    private static String outputFile = "classes.plantuml";
    private static String header = null;
    private static int headerSize = 30;

    private static boolean isClassDiagram = true;

    private static boolean showComposition = true;
    private static boolean showAggregation = true;
    private static boolean showAssociation = true;
    private static boolean showLollipop = true;
    private static boolean showImplementation = true;

    private static boolean isVertical = true;

    private static boolean showClassInterior = false;
    private static boolean showMethodArgs = true;
    
    public static final String DOT_JAVA = ".java";
    public static final String DOT_CLASS = ".class";

    private static Set<Class> classes;
    private static Set<String> packages;
    static {
        classes		= new HashSet<Class>();
        packages 	= new HashSet<String>();
    }

    private Options() {
    }

    /**
     * Поцедура инициализации полей начальными значениями.
     * Необходимость процедуры вызвана решением использовать статические
     * поля в классе. Без нее поля создаются и инициализируются однократно
     * во время загрузки класса. А т.к. при вызове программы создания
     * кода PlantUML количество параметров переменное, то приходится
     * каждый раз инцициализировать поля класса начальными значениями.
     *
     * При добавлении новых параметров ОБЯЗАТЕЛЬНО включать в данный метод
     * строку инициализации нового параметра.
     */
    public static void init() {
        path = null;
        outputFile = "classes.plantuml";
        header = null;
        headerSize = 30;
        isClassDiagram = true;
        showComposition = true;
        showAggregation = true;
        showAssociation = true;
        showLollipop = true;
        showImplementation = true;
        isVertical = true;
        showClassInterior	= false;
        showMethodArgs 		= true;
    }

    public static String getPath() {
        return path;
    }

    public static void setPath(String path) {
        Options.path = path;
    }

    public static Set<Class> getClasses() {
        return classes;
    }

    public static void setClasses(Set<Class> classes) {
    	LOG.info("setClasses...");
    	Options.classes = classes;
        for (Class cls : Options.classes) {
        	Options.addPackage(SomeUtils.getPackageName(cls.getCanonicalName(), "."));
        }
    }

    public static void clearClassesAndPackages() {
        if (packages != null) {
            packages.clear();
        }
        if (classes != null) {
            classes.clear();
        }
    }

    public static boolean isClassesLoaded() {
        if (classes == null) {
            return false;
        }
        if (classes.isEmpty()) {
            return false;
        }
        return true;
    }

    public static Set<String> getPackages() {
        return packages;
    }

    public static void addPackage(String pack) {
    	if (pack == null) 
    		return;
    	Options.packages.add(pack);
    }
    
    public static void setPackages(Set<String> packages) {
        Options.packages = packages;
    }
    
    public static void setPackages(String format) {
    	LOG.info("setPackages...");
        packages.clear();
    	switch(format) {
        	case Options.DOT_CLASS:
        		// извлекаем пакеты из классов
        		for (Class cls : Options.classes) {
                	Options.addPackage(SomeUtils.getPackageName(cls.getCanonicalName(), "."));
                }
        		break;
        		
        	case Options.DOT_JAVA:
        		// извлекаем пакеты из пути
        		File f = new File(path);
        		if (f.exists() && f.isDirectory()) {
        			for (File sub : f.listFiles()) {
        				packages.addAll(findPackages(f, 1));
        			}
        		}
        		break;
        }
    }
    
    private static Set<String> findPackages(final File dir, final int depth) {
    	Set<String> set = new HashSet<>();
    	if (!dir.exists() || dir.isFile()) return set;
    	String pack;
    	if ((pack = getDirPackage(dir)) != null) {
    		set.add(pack);
    	}
    	for (File sub : dir.listFiles()) {
    		if (sub.exists() && sub.isDirectory()) {
    			set.addAll(findPackages(sub, depth+1));
    		}			
		}
    	return set;
    }
    
    private static String getDirPackage(File dir) {
    	FileFilter filter = new FileFilter() {
			public boolean accept(File file) {
				return file.getName().matches(".+\\.java$");
			}
		};
		if ((dir.listFiles(filter).length < 1)) {
			return null;
			
		}
    	for (File clsJava : dir.listFiles(filter)) {
			// извлекаем пакет из 1-ого класса
			try (BufferedReader reader = new BufferedReader(new FileReader(clsJava))) {
				String line = null;
				while((line = reader.readLine()) != null) {
					String packageRe = "[a-zA-Z_\\$][\\w\\$]*(?:\\.[a-zA-Z_\\$][\\w\\$]*)*";
					if (line.matches("^\\s*(package)\\s+(" + packageRe + ")\\s*(;)$")) {
						// нашли строку с именем пакета - извлечем
						String packName = line.replaceFirst("package", "").replaceAll(";", "");
						return packName;
					}
				}
			} catch(IOException e) {
				continue;
			}
		}
    	// ничего не нашли - пакет по умолчанию
    	return "";
    }

    public static String getOutputFile() {
        return outputFile;
    }

    public static void setOutputFile(String outputFile) {
        Options.outputFile = outputFile;
    }

    public static String getHeader() {
        return header;
    }

    public static void setHeader(String header) {
        Options.header = header;
    }

    public static int getHeaderSize() {
        return headerSize;
    }

    public static void setHeaderSize(int headerSize) {
        Options.headerSize = headerSize;
    }

    public static boolean isClassDiagram() {
        return isClassDiagram;
    }

    public static boolean isSequenceDiagram() {
        return !isClassDiagram;
    }

    public static void setClassDiagram() {
        isClassDiagram = true;
    }

    public static void resetClassDiagram() {
        isClassDiagram = false;
    }

    public static boolean isShowComposition() {
        return showComposition;
    }

    public static void setShowComposition(boolean showComposition) {
        Options.showComposition = showComposition;
    }

    public static boolean isShowAggregation() {
        return showAggregation;
    }

    public static void setShowAggregation(boolean showAggregation) {
        Options.showAggregation = showAggregation;
    }

    public static boolean isShowAssociation() {
        return showAssociation;
    }

    public static void setShowAssociation(boolean showAssociation) {
        Options.showAssociation = showAssociation;
    }

    public static boolean isShowLollipop() {
        return showLollipop;
    }

    public static void setShowLollipop(boolean showLollipop) {
        Options.showLollipop = showLollipop;
    }

    public static boolean isShowImplementation() {
        return showImplementation;
    }

    public static void setShowImplementation(boolean showImplementation) {
        Options.showImplementation = showImplementation;
    }

    public static boolean isVertical() {
        return isVertical;
    }

    public static boolean isHorizontal() {
        return !isVertical;
    }

    public static void setVertical() {
        isVertical = true;
    }

    public static void setHorizontal() {
        isVertical = false;
    }

    public static boolean isShowClassInterior() {
        return showClassInterior;
    }

    public static void setShowClassInterior(boolean showClassInterior) {
        Options.showClassInterior = showClassInterior;
    }

    public static boolean isShowMethodArgs() {
        return showMethodArgs;
    }

    public static void setShowMethodArgs(boolean showMethodArgs) {
        Options.showMethodArgs = showMethodArgs;
    }
}
