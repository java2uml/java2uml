package com.github.java2uml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;

import net.sourceforge.plantuml.SourceStringReader;

public class DataExtractor {	
    /**
     * Извлечение данных из множества классов для построения uml диаграмм в формате plantuml
     * @param classes - ножество загруженный классов
     * @return
     */
    public static String extract(final Set<Class> classes) {

        // текст в формате plantuml - начало сборки
        StringBuilder source = new StringBuilder();
        source.append("@startuml\n");
        source.append("skinparam classAttributeIconSize 0\n");
        
        // таблица пакетов для всех входящих классов
        Hashtable<String, String> packages = new Hashtable<>();
        for (Class clazz : classes) {
        	packages.put(getPackageName(clazz.getCanonicalName()), "");
        }
        
        // множество связей: исключение <.. класс
        Set<String> throwLinks = new HashSet<String>();
        
        // объявление классов...
        for (Class clazz : classes) {
        	// анонимные классы пока игнорируем
        	if (clazz.isAnonymousClass()) {
        		continue;
        	}
        	
        	// получение информации о классе
            // String className 	= (clazz.getSimpleName().isEmpty()) ? clazz.getCanonicalName() : clazz.getSimpleName();
        	String className 	= clazz.getCanonicalName();
            String classPack	= getPackageName(clazz.getCanonicalName());
            
            // объявляем класс и его содержимое
            StringBuilder res = new StringBuilder();
            res.append(getClassModifiers(classes, clazz));
            res.append(" {\n"); 
            
            // получение информации о полях
            Field[] fields = clazz.getDeclaredFields();
            res.append(".. Fields ..\n");
            for (Field field : fields) {
            	res.append(getMemberModifiers(field.getModifiers()));
            	res.append(field.getName());
            	res.append(" : ");
            	res.append(field.getType().getSimpleName());
            	res.append("\n");
            }

            // получение информации методах
            Method[] methods = clazz.getDeclaredMethods();
            res.append(".. Methods ..\n");
            for (Method method : methods) {
                res.append(getMemberModifiers(method.getModifiers()));
                res.append(method.getName());
                res.append("()");
                res.append(" : ");
                res.append(method.getReturnType().getSimpleName() + "\n");
                
                // бросает ли метод исключение
                for (Class exception : method.getExceptionTypes()) {
                	if (classes.contains(exception)) {
                		// добавляем связь о брошенном исключении
                		String link = exception.getCanonicalName();
                		link += " <.. ";
                		link += className;
                		link += "\n";
                		throwLinks.add(link);
                	}
                }
            }
            // закрываем класс
            res.append("}\n");
            
            // добавляем класс в таблицу пакетов
            packages.put(classPack, packages.get(classPack) + res.toString());
        }
        
        // вывод объявленных классов
		for (Entry<String, String> entry : packages.entrySet()) {
			if (entry.getKey().equals("<default_pack>")) {
				// связи меж пакетом
				source.append(entry.getValue());
				source.append("\n");
				continue;
			}
			// связи внутри пакетов
			source.append("package ");
			source.append(entry.getKey());
			source.append(" {\n");
			source.append(entry.getValue());
			source.append(" }\n");
		}
        
        // определение межклассовых связей
        for (Class clazz : classes) {
        	if ( clazz.getSimpleName().isEmpty() ) {
            	continue;
            }
        	        	
        	//имя класса
        	String className = clazz.getCanonicalName();
        	
        	// получение супер класса и реализованных интерфейсов
            Class superClass = clazz.getSuperclass();
            Class[] interfaces = clazz.getInterfaces();
            
            // множество структур, объявленных внутри класса
            Set<Class> declaredClasses = new HashSet<Class>();
            declaredClasses.addAll(Arrays.asList(clazz.getDeclaredClasses()));
            
            // пакет текущего класса
            String classPack = getPackageName(clazz.getCanonicalName());
            
            // объявление связей...
            if (classes.contains(superClass)) {
                // супер класс доступен во множестве - добавим связь
            	source.append(superClass.getCanonicalName());
                source.append(" <|-- ");
                source.append(className);
                source.append("\n");
            }
            for (Class interfc : interfaces) {
                if (classes.contains(interfc)) {
                    // интерфейс доступен во множестве - добавим связь
                	source.append(interfc.getCanonicalName());
                    source.append(" <|.. ");
                    source.append(className);
                    source.append("\n");
                }
            }

            // получение внешних классов, являющихся полями clazz, объявленных вне clazz
            Field[] fieldClasses = clazz.getDeclaredFields();
            for (Field fieldClass : fieldClasses) {
                if (fieldClass.getType() instanceof Object) {
                    if (classes.contains(fieldClass.getType())) {
                    	if (className.equals(fieldClass.getType().getCanonicalName())) {
                    		// связь на самого себя не учитываем
                    		continue;
                    	}
                    	if ( isDeclared(fieldClass.getType(), clazz) ) {
                    		// clazz объявлен внутри fieldClass - связь не учитываем
                    		continue;
                    	}
                        // поле есть внешний класс - добавляем связь агрегирование
                        source.append(className);
                        source.append(" o-- ");
                        source.append(fieldClass.getType().getCanonicalName());
                        source.append("\n");
                    }
                }
            }
            
            // получение внутренних классов, объявленных внутри clazz
            for ( Class declaredClass : declaredClasses ) {
            	if (classes.contains(declaredClass)) {
            		// связь через композицию
            		source.append(className);
                    source.append(" *-- ");
                    source.append(declaredClass.getCanonicalName());
                    source.append("\n");
            	}
            }
        }
        
        // брошенные исключения
        for (String link : throwLinks) {
        	source.append(link);
        }
                
        // конец сборки
        source.append("@enduml\n");
        return source.toString();
    }
    
    /**
     * Генерация диаграммы классов
     * @param source - исходный текст классов на языке plantuml
     */
    public static void generate(final String source) {
        try {
            File file = new File("diagrams\\test.png");
            if (!file.exists()) {
                file.createNewFile();
            }

            // поток вывода для диаграммы
            OutputStream png = new FileOutputStream(file);

            // генератор диаграмм
            SourceStringReader reader = new SourceStringReader(source);

            // генерация жиаграммы
            String desc = reader.generateImage(png);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        
    /**
     * Получение строки с именем пакета
     * @param className - полное имя класса
     * @return
     */
    private static String getPackageName(final String className) {
    	if ( className == null ) {
    		return "<default_pack>";
    	}
    	String[] pack = className.split("\\."); 
    	if ( pack.length <= 1 ) {
    		return "<default_pack>";
    	}
    	String packName = "";
    	for (int i=0; i < pack.length; ++i) {
    		if (Character.isUpperCase(pack[i].charAt(0))) {
    			// перешли с пакета на класс - пакет получен, выходим
    			break;
    		}
    		packName += pack[i] + ".";
    	}
    	// уберем лишнюю точку
    	return (packName.isEmpty()) ? "<default_pack>" : packName.substring(0, packName.length()-1);
    }
    
    /**
     * Проверка объявления класса child в теле класса parent 
     */
    private static boolean isDeclared(final Class parent, final Class child) {
    	if (parent == null || child == null ) {
    		return false;
    	}
    	Set<Class> classes = new HashSet<Class>();
    	classes.addAll(Arrays.asList(parent.getDeclaredClasses()));
    	if (classes.contains(child)) {
    		return true;
    	}
    	return false;
    }
    
    /**
     * Получение модификаторов членов класса
     * @param mod
     * @return
     */
    private static String getMemberModifiers(final int mod) {
        // значение по умолчанию - package private
        String modStr = "~";
        if (Modifier.isPrivate(mod)) {
            modStr = "-";
        }
        if (Modifier.isProtected(mod)) {
            modStr = "#";
        }
        if (Modifier.isPublic(mod)) {
            modStr = "+";
        }
        if (Modifier.isAbstract(mod)) {
            modStr = "{abstract} " + modStr;
        }
        if (Modifier.isStatic(mod)) {
            modStr = "{static} " + modStr;
        }
        return modStr;
    }

    /**
     * Получение модификаторов класса
     * @param classes - множество всех классов
     * @param clazz - текущий класс для построения
     * @return
     */
    private static String getClassModifiers(final Set<Class> classes, final Class clazz) {
    	String className 	= (clazz.getSimpleName().isEmpty()) ? "<Unknown>" : clazz.getCanonicalName();    	
        String modStr 		= "class " + className;
        if (Modifier.isAbstract(clazz.getModifiers())) {
            modStr = "abstract class " + className + " ";
        }
        if (clazz.isInterface()) {
            modStr = "interface " + className + " ";
        }
        if (clazz.isEnum()) {
        	modStr = "enum " + className + " ";
        }
        if (className.toLowerCase().contains("exception")) {
        	// исключения получают особый вид
        	modStr = "class " + className + " << (E,yellow) >> ";
        }
        return modStr;
    }
}
