package com.github.java2uml.core.reflection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;
import net.sourceforge.plantuml.SourceStringReader;

import com.github.java2uml.core.Options;

public class DataExtractor {
	
	/**
     * Извлечение данных из множества классов для построения uml диаграмм в формате plantuml
     * @author Balyschev Alexey - alexbalu-alpha7@mail.ru
     * @param classes - ножество загруженный классов
     * @return boolean
     */
    public static String extract(final Set<Class> classes) {
        // текст в формате plantuml - начало сборки
        StringBuilder source = new StringBuilder();
        source.append("@startuml\n");
        //source.append("' Split into 2 pages\n");
        //source.append("page 2x1\n");
        //source.append("skinparam backgroundColor Snow\n");
        //source.append("skinparam monochrome true\n");
        source.append("skinparam classAttributeIconSize 0\n");
        //source.append("skinparam classBorderColor MediumSeaGreen\n");
        //source.append("skinparam classBackgroundColor Lavender\n");
        //source.append("skinparam classFontSize 20\n");
        //source.append("skinparam classAttributeFontSize 18\n");
        //source.append("skinparam packageBorderColor DarkSlateGray\n");
        //source.append("skinparam packageBackgroundColor GhostWhite\n");
        //source.append("skinparam packageFontColor Black\n");
        //source.append("skinparam packageFontStyle italic\n");
        //source.append("skinparam packageFontSize 18\n");
        //source.append("skinparam classArrowColor DarkBlue\n");
        //source.append("skinparam classArrowFontSize 24\n");
        //source.append("skinparam classArrowFontStyle bold\n");
        source.append("scale 1.0\n");
        
        // список связей между классами
        List<String> links = new ArrayList<String>();
        
        // таблица пакетов для всех входящих классов
        Map<String, String> packages = new TreeMap<>();
        Set<String> classNames = new HashSet<>();
        for (Class clazz : classes) {
        	packages.put(getPackageName(clazz.getCanonicalName()), "");
        	classNames.add(clazz.getCanonicalName());
        }
        
        // множество связей для бросаемых исключений: исключение <.. класс
        Set<String> throwLinks = new HashSet<String>();
        
        // хэш точек входа: определяем по public static методу с именем main
        Map<String, String> entryPoints = new HashMap<>();
        
        // объявление классов...
        for (Class clazz : classes) {
        	// анонимные классы и классы, доюавленные компилятором, пока игнорируем
        	if (clazz.isAnonymousClass() || clazz.isSynthetic()) {
        		continue;
        	}
        	
        	// получение информации о классе
        	String className 	= clazz.getCanonicalName();
            String classPack	= getPackageName(clazz.getCanonicalName());
            
            // объявляем класс и его содержимое
            StringBuilder res = new StringBuilder();
            res.append(getClassModifiers(classes, clazz));
            
            // реализуемые интерфейсы, не попавшие во входное множество
            if (Options.isShowLollipop()) {
            	StringBuilder outerInterfaces = new StringBuilder();
                for (Class inter : clazz.getInterfaces()) {
                	if (!classes.contains(inter)) {
                		// внешний интерфейс - укажем
                		outerInterfaces.append(inter.getSimpleName());
                		outerInterfaces.append(", ");
                	}
                }
                if (!outerInterfaces.toString().isEmpty()) {
                	String impl = outerInterfaces.toString(); 
                	res.append(" <<");
                	res.append(impl.substring(0, impl.length()-2));
                	res.append(">> ");
                }
                res.append(" {\n"); 
            }
            
            // буфер статических членов класса
            StringBuilder staticMembers = new StringBuilder();
            
            // получение информации о полях
            Field[] fields = clazz.getDeclaredFields();
            if(fields.length > 0) {
            	res.append(".. Fields  ..\n");
                for (Field field : fields) {
                	if (field.isSynthetic()) {
                    	// выводим только объявленные структуры
                    	continue;
                    }
                	if (Modifier.isStatic(field.getModifiers())) {
                		// статические члены в конец объявления
                		staticMembers.append(getMemberModifiers(field.getModifiers()));
                		staticMembers.append(field.getName());
                		staticMembers.append(" : ");
                		staticMembers.append(field.getType().getSimpleName());
                		staticMembers.append("\n");
                		continue;
                	}
                	res.append(getMemberModifiers(field.getModifiers()));
                	res.append(field.getName());
                	res.append(" : ");
                	res.append(field.getType().getSimpleName());
                	res.append("\n");
                }
            }
            
            // получение информации методах
            Method[] methods = clazz.getDeclaredMethods();
            final boolean showMethodArgs = Options.isShowMethodArgs();
            if (methods.length > 0) {
            	res.append(".. Methods ..\n");
            }
            for (Method method : methods) {
            	if (method.isSynthetic()) {
            		// выводим только объявленные структуры
            		continue;
            	}
            	
            	if (Modifier.isStatic(method.getModifiers())) {
            		// статические члены в конец объявления
            		staticMembers.append(getMemberModifiers(method.getModifiers()));
            		staticMembers.append(method.getName());
            		staticMembers.append("()");
            		staticMembers.append(" : ");
            		staticMembers.append(method.getReturnType().getSimpleName());
            		staticMembers.append("\n");
            		
            		// определяем точку входа
            		if (Modifier.isPublic(method.getModifiers()) && method.getName().equals("main")) {
            			entryPoints.put(clazz.getCanonicalName(), "");
            		}
            		continue;
            	}
            	res.append(getMemberModifiers(method.getModifiers()));
                res.append(method.getName());
                res.append("(");
                // аргументы функций
                String args = "";
                if (showMethodArgs) {
                	for (Class arg : method.getParameterTypes()) {
                		if (!arg.getSimpleName().isEmpty()) {
                			args += arg.getSimpleName() + ", "; 
                		}
                	}
                	if (args.contains(",")) {
                		args = args.substring(0, args.lastIndexOf(","));
                	}
                }
                res.append(args);
                res.append(")");
                res.append(" : ");
                res.append(method.getReturnType().getSimpleName());
                res.append("\n");
                
                // бросает ли метод исключение
                for (Class exception : method.getExceptionTypes()) {
                	if (classes.contains(exception)) {
                		// добавляем связь о брошенном исключении
                		if (Options.isShowAssociation()) {
                			String link = exception.getCanonicalName();
                    		link += " <.. ";
                    		link += className;
                    		link += "\n";
                    		throwLinks.add(link);
                		}
                	}
                }
            }
            
            // инфорация о статике
            if (!staticMembers.toString().isEmpty()) {
            	res.append(".. Static ..\n");
                res.append(staticMembers.toString());
            }
            
            // информация о внутренних структурах
            if (!Options.isShowClassInterior()) {
            	// множество структур, объявленных внутри класса
                Set<Class> declaredClasses = new HashSet<Class>();
                declaredClasses.addAll(Arrays.asList(clazz.getDeclaredClasses()));
                
            	if (declaredClasses.size() > 0) {
            		res.append(".. Interiors ..\n");	
                    for (Class declared : declaredClasses) {
                    	String modStr = getMemberModifiers(declared.getModifiers());
                    	String desc = "class ";
                    	if (declared.isInterface()) {
                    		desc = "interface ";
                    	}
                    	if (declared.isAnnotation()) {
                    		desc = "annotation ";
                    	}
                    	if (declared.isEnum()) {
                    		desc = "enum ";
                    	}
                    	res.append(modStr);
                    	res.append(desc);
                    	res.append(declared.getSimpleName());
                    	res.append("\n");
                    }
                }
            }
            
            // закрываем класс
            res.append("}\n");
            
            // добавляем класс в таблицу пакетов (класс с точками входа идут в отдельный пакет)
            if (entryPoints.containsKey(clazz.getCanonicalName())) {
            	entryPoints.put(clazz.getCanonicalName(), res.toString());
            } else {
            	packages.put(classPack, packages.get(classPack) + res.toString());
            }
        }
        
        // вывод объявленных классов с учетом пакетов и их вложенности 
        List<String> packList = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        for (Entry<String, String> entry : packages.entrySet()) {
        	if (entry.getValue().trim().isEmpty()) {
        		// добавлять нечего - пропускаем
        		continue;
        	}

        	if (packList.isEmpty()) {
        		// список пуст - добавим текущий пакет
        		packList.add(entry.getKey());
        		buffer.append("package ");
        		buffer.append(entry.getKey());
        		//buffer.append(" #");
        		//buffer.append(getPackageColor(0));
        		buffer.append(" {\n");		
        	} else {
        		// индекс - является ли текущий пакет пакетом из списка
        		int packNdx = -1;
        		for (int i=0; i < packList.size(); ++i) {
        			if (entry.getKey().contains(packList.get(i))) {
        				packNdx = i;
        			}
        		}
        		if (packNdx > -1) {
        			// пакет вложен - буферизуем пакеты, в которые не входит текущий
        			for (int i=packList.size()-1; i >= packNdx; --i) {
        				if (i==packNdx) {
        					// первый пакет не учитываем
        					continue;
        				}
        				String pack = packList.get(i);
                		buffer.append(packages.get(pack));
                		buffer.append("\n");
                		buffer.append("}\n");
        			}
        			
        			// удаляем буфиризированные пакеты
        			List<String> rest = new ArrayList<>();
        			rest.addAll(packList.subList(0, packNdx+1));
        			packList.clear();
        			for (String pack : rest) {
        				packList.add(pack);
        			}
        			
        			// добавляем текущий пакет
        			packList.add(entry.getKey());
        			buffer.append("package ");
            		buffer.append(entry.getKey());
            		//buffer.append(" #");
            		//buffer.append(getPackageColor(packList.size()));
            		buffer.append(" {\n");
        		} else {
        			// пакет не вложен - буферезуем весь список
        			for (int i=packList.size()-1; i >= 0; --i) {
        				String pack = packList.get(i);
                		buffer.append(packages.get(pack));
                		buffer.append("\n");
                		buffer.append("}\n");
        			}
        			
        			// очищаем список
        			packList.clear();
        			
        			// добавляем новый пакет и буферезуем его
        			packList.add(entry.getKey());
        			buffer.append("package ");
            		buffer.append(entry.getKey());
            		//buffer.append(" #");
            		//buffer.append(getPackageColor(packList.size()));
            		buffer.append(" {\n");
        		}
        	}
        }
        
        if (!packList.isEmpty()) {
        	// добавим в буфер последние пакеты
        	for (int i=packList.size()-1; i >= 0; --i) {
				String pack = packList.get(i);
        		buffer.append(packages.get(pack));
        		buffer.append("\n");
        		buffer.append("}\n");
			}
        }
        
        // очищаем список пакетов
		packList.clear();
		
		// добавляем содерфимое буфера в сборщик
		source.append(buffer.toString());
		
		// определение точек входа
		source.append("package Entry_Points <<Cloud>> {\n");
		for (Entry entry : entryPoints.entrySet()) {
			source.append(entry.getValue());
			source.append("\n");
		}
		source.append("}\n");
		        
        // определение межклассовых связей
        for (Class clazz : classes) {
        	if (clazz.getSimpleName().isEmpty()) {
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
                StringBuilder link = new StringBuilder();
                link.append(superClass.getCanonicalName());
                link.append(" <|-- ");
                link.append(className);
                link.append("\n");
                links.add(link.toString());
            }
            
            if (Options.isShowImplementation()) {
            	for (Class interfc : interfaces) {
                    if (classes.contains(interfc)) {
                        // интерфейс доступен во множестве - добавим связь
                    	StringBuilder link = new StringBuilder();
                    	link.append(interfc.getCanonicalName());
                    	link.append(" <|.. ");
                    	link.append(className);
                    	link.append("\n");
                    	links.add(link.toString());
                    } 
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
                    	if (fieldClass.getType().isEnum()) {
                    		continue;
                    	}
                    	if ( isDeclared(fieldClass.getType(), clazz) ) {
                    		// clazz объявлен внутри fieldClass - связь не учитываем
                    		continue;
                    	}
                        // поле есть внешний класс - добавляем связь агрегирование
                    	if (Options.isShowAggregation()) {
                    		StringBuilder link = new StringBuilder();
                        	link.append(className);
                        	link.append(" o-- ");
                        	link.append("\"1..1\" ");
                        	link.append(fieldClass.getType().getCanonicalName());
                        	link.append("\n");
                        	links.add(link.toString());
                            continue;
                    	}
                    }
                    
                    // проверка поля на массив и коллекцию
                	if (fieldClass.getType().isArray()) {
            			// для массивов связь композиция
            			String arrayClassName = getArrayName(fieldClass.getType().getCanonicalName());
            			if (classNames.contains(arrayClassName) && !arrayClassName.equals(className)) {
            				// есть вхождение - устанавливаем связь
            				if (Options.isShowComposition()) {
            					StringBuilder link = new StringBuilder();
                            	link.append(className);
                            	link.append(" *-- ");
                            	link.append("\"0..*\" ");
                            	link.append(getArrayName(fieldClass.getType().getCanonicalName()));
                            	link.append("\n");
                            	links.add(link.toString());
                        	}
            			}
            			// идем дальше
            			continue;
            		}
            		if (isCollection(fieldClass.getType())) {
            			// поле является коллекцией - проверим тип параметра            			
            			Type genericFieldType = fieldClass.getGenericType();
            			if(genericFieldType instanceof ParameterizedType){
            				// определили параметризированный тип
            			    ParameterizedType aType = (ParameterizedType) genericFieldType;
            			    Type[] fieldArgTypes = aType.getActualTypeArguments();
            			    for(Type fieldArgType : fieldArgTypes) {
            			    	String argType = getActualClass(classNames, fieldArgType.toString());
            			    	if (argType != null && Options.isShowComposition() && !argType.equals(className)) {
            			    		// есть вхождение - устанавливаем связь
                					StringBuilder link = new StringBuilder();
                                	link.append(className);
                                	link.append(" *-- ");
                                	link.append("\"0..*\" ");
                                	link.append(argType);
                                	link.append("\n");
                                	links.add(link.toString());
            			    	}
            			    }
            			}
            			continue;
            		}
                }
            	
            }
            
            // получение внутренних классов, объявленных внутри clazz
            if (Options.isShowClassInterior()) {
            	for ( Class declaredClass : declaredClasses ) {
                	if (classes.contains(declaredClass)) {
                		// связь через ассоциацию
                		if (Options.isShowAssociation()) {
                			StringBuilder link = new StringBuilder();
                        	link.append(className);
                        	link.append(" +--> ");
                        	link.append(declaredClass.getCanonicalName());
                        	link.append("\n");
                        	links.add(link.toString());
                		}
                	}
                }
            }
        }
        
        // обработка полученных связей - 2 этапа..
        List<String> genLinks = new ArrayList<>();  // список с новыми связями
        List<String> oldLinks = new ArrayList<>();  // список старых связей, подлежащих удалению
        
        // этап 1: группировка связей
        for (int i=0; i < links.size(); ++i) {
        	int matches = 1;
        	if (oldLinks.contains(links.get(i))) {
        		continue;
        	}
        	String linkType = null;
        	if (links.get(i).contains("o--") && links.get(i).contains("\"1..1\"")) {
        		linkType = "o--";
        	} else if (links.get(i).contains("*--") && links.get(i).contains("\"0..*\"")) {
        		linkType = "*--";
        	}
        	
        	if (linkType != null) {
        		// начинаем группировку связей
        		for (int j=0; j < links.size(); ++j) {
            		if (i==j) {
            			continue;
            		}
            		if (links.get(i).equals(links.get(j))) {
            			matches++;
            		}
            	}
        		if (matches > 1) {
            		String link;
            		String params[] = links.get(i).split("\\s");
            		if (params.length >= 4) {
            			// убеждаемся, что пар-ры правильно извлечены
            			link = params[0].trim() + " " + linkType + " ";
            			
            			// мощность отношений
            			String agrMult = "\"" + matches + ".." + matches + "\" " + params[3].trim() + "\n";
            			String cmpMult = "\"0..*(" + matches + ")\" " + params[3].trim() + "\n";
            			
            			link += (linkType.equals("o--")) ? agrMult : cmpMult;   
            	        genLinks.add(link);
            	        oldLinks.add(links.get(i));
            		}
            	}
        	}
        }
        
        // удаление откорректированных связей
        for (String link : oldLinks) {
        	while(links.contains(link)) {
        		links.remove(link);
        	}
        }
        // добавление сгруппированных связей
        links.addAll(genLinks);
        
        
        // этап 2: создание двусторонних связей
        genLinks.clear();
        oldLinks.clear();
        for (int i=0; i < links.size(); ++i) {
        	// извлечение имен классов в связи
        	String[] params  = links.get(i).split("\\s");
        	String cls1Left	 	= null;
        	String cls1Right 	= null;
        	String leftTypeLink = null; 
        	if (params.length > 2) {
        		cls1Left 		= params[0];
        		cls1Right 		= params[params.length-1]; 
        		leftTypeLink 	= params[1];
        	}
        	if (cls1Left == null || cls1Right == null || leftTypeLink == null) {
        		continue;
        	}
        	if (!leftTypeLink.contains("o") && !leftTypeLink.contains("*")) {
        		// двуторонняя связь допускается только для агрегации и композиции
        		continue;
        	}
        	
        	for (int j=0; j < links.size(); ++j) {
            	// ищем зеркально отображеннные связи
        		if (i==j || oldLinks.contains(links.get(j)) || !isSimlexLink(links.get(j))) {
        			// связь задействована или не подходит - выходим
        			continue;
        		}
        		String[] params2 = links.get(j).split("\\s");
        		String cls2Left  	 = null;
            	String cls2Right 	 = null;
            	String rightTypeLink = null; 
            	if (params2.length > 2) {
            		cls2Left 		= params2[0];
            		cls2Right 		= params2[params2.length-1]; 
            		rightTypeLink 	= params2[1];
            	}
            	if (cls2Left == null || cls2Right == null || rightTypeLink == null) {
            		continue;
            	}
            	if (!rightTypeLink.contains("o") && !rightTypeLink.contains("*")) {
            		// двуторонняя связь допускается только для агрегации и композиции
            		continue;
            	}
        		if (cls1Left.equals(cls2Right) && cls1Right.equals(cls2Left)) {
        			// нашли зеркальную связь - можно создать двустороннюю связь
        			String link = createDuplexLink(cls1Left, links.get(i), cls2Left, links.get(j));
        			genLinks.add(link);
        			oldLinks.add(links.get(i));
        			oldLinks.add(links.get(j));	
        		}
            }
        }
        // удаление откорректированных связей
        for (String link : oldLinks) {
        	while(links.contains(link)) {
        		links.remove(link);
        	}
        }
        // добавление сгруппированных связей
        links.addAll(genLinks);
        
        // вывод связей
        for (String link : links) {
        	if (link.contains("$")) {
        		// связи с $ пока игнорируем
        		continue;
        	}
        	source.append(link);
        }
        for (String link : throwLinks) {
        	if (link.contains("$")) {
        		// связи с $ пока игнорируем
        		continue;
        	}
        	source.append(link);
        }
                
        // конец сборки
        source.append("@enduml\n");
        
        // сохраняем в файл
        return source.toString();
    }
    
    /**
     * Проверка, является ли указанный тип коллекцией
     * @author Balyschev Alexey - alexbalu-alpha7@mail.ru
     * @param Class - входящий класс
     * @return boolean
     */
    private static boolean isCollection(final Class<?> clazz) {
    	if (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)) {
    		// проверка на коллекцию и мап
    		return true;
    	}
    	return false;
    }
    
    /**
     * Создание связи 
     * @author Balyschev Alexey - alexbalu-alpha7@mail.ru
     * @param link 		- строковое представление связи
     * @param nameLeft	- полное имя левого класса
     * @param nameRight - полное имя правого класса
     * @return String
     */
    private static String createLink(final String link, final String nameLeft, final String nameRight) {
    	if (link == null || nameLeft == null || nameRight == null) {
    		return "";
    	}
    	return nameLeft + " " + link + " " + nameRight + "\n";
    }
    
    /**
     * Получение класса из списка имен классов через строку
     * @author Balyschev Alexey - alexbalu-alpha7@mail.ru
     * @param clsNames
     * @param str
     * @return Srting - строка с CanonicalName найденного класса
     */
    private static String getActualClass(final Set<String> clsNames, final String str) {
    	if (str == null) {
    		return null;
    	}
    	for (String clsNm : clsNames) {
    		if (clsNm != null && str.contains(clsNm)) {
    			return clsNm;
    		}
    	}
    	return null;
    }
    
    /**
     * Построение двухсторонней связи из двух односторонних
     * @author Balyschev Alexey - alexbalu-alpha7@mail.ru
     * @param clsLeft	- полное имя левого класса
     * @param firstLink - 1-ая связь
     * @param clsRight  - полное имя правого класса
     * @param secondLink - 2-ая связь
     * @return String - код двусторонней связи или пустую строку
     */
    private static String createDuplexLink(final String clsLeft, final String firstLink, 
    		final String clsRight, final String secondLink) {
    	String[] params1 = firstLink.split("\\s");
    	String[] params2 = secondLink.split("\\s");
		if (params1.length < 3 || params2.length < 3) {
			// не хватает данных - выходим
			return "";
		}
		// текущая связь
		String leftLinkType 	= params1[1];
		String rightLinkType 	= params2[1];
		String rightMult	= (params1[2].contains("\"")) ? params1[2] : "";
		String leftMult 	= (params2[2].contains("\"")) ? params2[2] : "";
		
		// убираем линию из связи
		String[] conn = {"..", "--"};
		for (String c : conn) {
			if (leftLinkType.contains(c)) {
				leftLinkType = leftLinkType.substring(0, leftLinkType.indexOf(c));
			}
			if (rightLinkType.contains(c)) {
				rightLinkType = rightLinkType.substring(0, rightLinkType.indexOf(c));
			}
		}
		// добавим в связь	
		String link = clsLeft + " " + leftMult + " " + leftLinkType + ".." + rightLinkType + " " + rightMult + " " + clsRight + "\n";
		return link;
    }
    
    /**
     * Проврека, является ли связь односоронней
     * @author Balyschev Alexey - alexbalu-alpha7@mail.ru
     * @param link
     * @return
     */
    private static boolean isSimlexLink(final String link) {
    	if (link == null) {
    		return false;
    	}
    	String[] params = link.split("\\s");
    	return true;
    }
    
    /**
     * Генерация диаграммы классов
     * @author Balyschev Alexey - alexbalu-alpha7@mail.ru
     * @param source 	- исходный текст классов на языке plantuml
     * @param outPath 	- путь с именем выхондого файла
     * @param type 		- тип выходного файла: png|svg
     */
    public static void generateFromSrting(final String source, final String outPath, final String type) 
    		throws FileNotFoundException, IOException {
    	try(OutputStream out = new FileOutputStream(new File(outPath));
    			ByteArrayOutputStream os = new ByteArrayOutputStream();) {
            // проверка параметров
    		if (source == null || outPath == null || type == null) {
    			throw new IllegalArgumentException("DataExtractor.generateFromSrting(): Wrong method arguments!");
    		}
    		
            // генерация диаграммы
    		SourceStringReader reader = new SourceStringReader(source);
    		if (type.equalsIgnoreCase("svg")) {
    				// генерация диаграммы и сохранение в файл classes.svg
                    String desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
                    save(new String(os.toByteArray()), "classes.svg");
                    
    		} else if (type.equalsIgnoreCase("png")) {
    			// генерация png диаграммы
    			File file = new File(outPath);
                if (!file.exists()) {
                    file.createNewFile();
                }
                OutputStream png = new FileOutputStream(file);
                String desc = reader.generateImage(png);	
    		}
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
    }
    
    /**
     * Генерация диаграммы классов из файла в формате .plantuml
     * @author Balyschev Alexey - alexbalu-alpha7@mail.ru
     * @param srcPath - путь с именем файла с исходным кодом диаграмм
     * @param outPath - путь с именем выхондого файла
     * @param type - тип выходного файла: png|svg
     * @return void
     */
    public static void generateFromFile(final String srcPath, final String outPath, final String type) 
    		throws FileNotFoundException, IOException {
    	try (BufferedReader srcReader = new BufferedReader(new FileReader(srcPath));
    			ByteArrayOutputStream os = new ByteArrayOutputStream();) {
    		// проверка параметров
    		if (srcPath == null || outPath == null || type == null) {
    			throw new IllegalArgumentException("DataExtractor.generateFromSrting(): Wrong method arguments!");
    		}
    		// генерация диаграммы
    		if (type.equalsIgnoreCase("svg")) {
    			// извлечение исходников
        		String line = null;
        		StringBuilder source = new StringBuilder();
        		while((line = srcReader.readLine()) != null) {
        			source.append(line);
        			source.append("\n"); // // важно! добавить перенос строки
        		}
    			// генерация диаграммы и сохранение в файл classes.svg
        		SourceStringReader reader = new SourceStringReader(source.toString());
                String desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
                save(new String(os.toByteArray()), outPath);
                    
    		} else if (type.equalsIgnoreCase("png")) {
    			// генерация png диаграммы
    			SourceFileReader srcFileReader = new SourceFileReader(new File(srcPath));
    			List<GeneratedImage> list = srcFileReader.getGeneratedImages();
    			for (GeneratedImage img : list) {
    				File png = img.getPngFile();
    				png.createNewFile();
    			}
    		}
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
    }
    
    /**
     * Сохранение сгенерированного кода в формате plantuml
     * @author Balyschev Alexey - alexbalu-alpha7@mail.ru
     * @param source	- код описания ml диаграммы в формате plantuml
     * @param path 		- путь с именем выходного файла
     * @return boolean
     */
    public static boolean save(final String source, final String path) {
    	try(Writer writer = new BufferedWriter(new OutputStreamWriter(
    			new FileOutputStream(path), "UTF-8"))) {
    		writer.write(source);
    		return true;
    	} catch(IOException e) {
    		e.printStackTrace();
    		return false;
    	}
    }
        
    /**
     * Получение строки с именем пакета (обрезаем из canonicalName строку с именем класса)
     * @author Balyschev Alexey - alexbalu-alpha7@mail.ru
     * @param className - полное имя класса
     * @return
     */
    private static String getPackageName(final String className) {
    	if (className == null) {
    		return "default_pack";
    	}
    	int classNdx = className.lastIndexOf(".");
    	if (classNdx > -1) {
    		return className.substring(0, classNdx);
    	}
    	return "default_pack";
    }
    
    /**
     * Получучение строки с именем типа для массива
     * @author Balyschev Alexey - alexbalu-alpha7@mail.ru
     * @param arrayName - полное имя класса массива
     */
    private static String getArrayName(final String arrayName) {
    	if (arrayName == null) {
    		return "";
    	}
    	if (arrayName.contains("[")) {
    		return arrayName.substring(0, arrayName.indexOf("[")); 
    	}
    	return arrayName;
    }
    
    /**
     * Проверка объявления класса child в теле класса parent
     * @author Balyschev Alexey - alexbalu-alpha7@mail.ru
     * @param parent
     * @param child
     * @return
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
     * @author Balyschev Alexey - alexbalu-alpha7@mail.ru
     * @param mod
     * @return String
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
            modStr += " {abstract} ";
        }
        if (Modifier.isStatic(mod)) {
            modStr += " {static} ";
        }
        return modStr;
    }

    /**
     * Получение модификаторов класса
     * @author Balyschev Alexey - alexbalu-alpha7@mail.ru
     * @param classes - множество всех классов
     * @param clazz - текущий класс для построения
     * @return String
     */
    private static String getClassModifiers(final Set<Class> classes, final Class clazz) {
    	String unknown 	= getPackageName(clazz.getCanonicalName()) + ".Unknown";
    	String known	= getPackageName(clazz.getCanonicalName()) + "." + clazz.getSimpleName();
    	String className 	= (clazz.getSimpleName().isEmpty()) ? unknown : known;
    	className = clazz.getCanonicalName();
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
    
    /**
     * Получение цвета пакета в зависимости от уровня вложенности
     * @author Balyschev Alexey - alexbalu-alpha7@mail.ru
     * @param level - чем больше уровень, тем темнее цвет (но не более 10 уровней)
     * @return String
     */
    public static String getPackageColor(final int level) {
    	if (level <= 0) {
    		// по-умолчанию возвращаем белый
    		return Integer.toHexString(0xffffff);
    	}
    	int color = 0xffffff;
    	for(int i=1; i < level%11; ++i) {
    		// с каждым уровнем делаем чуть темнее
    		color -= 0x121212;
    	}
    	//return Integer.toHexString(color);
    	return Integer.toHexString(0xffffff);
    }
}
