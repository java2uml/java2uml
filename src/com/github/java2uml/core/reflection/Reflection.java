package com.github.java2uml.core.reflection;

import com.github.java2uml.core.Options;

import java.io.File;
import java.io.FileWriter;

/**
 * Создание кода UML на основе рефлексии.
 *
 * Created by Игорь Акимов on 30.12.2014.
 */
public class Reflection {
    public void loadClassesAndGenerateDiagram() throws Exception {
    	try {
    		if (Options.getClasses() == null || Options.getClasses().isEmpty()) {
                // Если по каким-то причинам классы оказались не загружены,
                // выбрасываем исключение.
                System.out.println("Классы не загружены.");
                throw new ClassesNotLoadedException("Classes are not loaded.");
            } else {
                System.out.println("Классы загружены, передаем на обработку.");
                System.out.println("----------------------------------------");
                
                // Создаем код UML из загруженных классов.
                String diagram = DataExtractor.extract(Options.getClasses());

                // Сохраняем код в заданный файл.
                File outputFile = new File(Options.getOutputFile());
                FileWriter outputWriter = new FileWriter(outputFile);
                outputWriter.write(diagram);
                outputWriter.flush();
                outputWriter.close();
                System.out.println("Данные сохранены в "
                        + outputFile.getAbsolutePath());
            }
    	} catch(Exception e) {
    		throw new Exception(e.getMessage());
    	}
        
    }
}
