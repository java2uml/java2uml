package com.github.java2uml.core.reflection;

import com.github.java2uml.core.Options;

import java.io.File;
import java.io.FileWriter;
import java.util.Set;

/**
 * Создание кода UML на основе рефлексии.
 *
 * Created by Игорь Акимов on 30.12.2014.
 */
public class Reflection {
    public void loadClassesAndGenerateDiagram() throws Exception {
        //UMLClassLoader ecl = new UMLClassLoader();
        //Set<Class> classes = null;

        // ui.validateProgressBarTo();

        // Пробуем загрузить классы.
        // classes = ecl.loadClasses(Options.getPath());

        // ui.validateProgressBarTo();

        // todo убрать вывод в консоль после тестирования
//		if (classes == null || classes.size() == 0) {
        if (Options.getClasses() == null || Options.getClasses().isEmpty()) {
            // Если по каким-то причинам классы оказались не загружены,
            // выбрасываем исключение.
            System.out.println("Классы не загружены.");
            throw new ClassesNotLoadedException("Classes are not loaded.");
        } else {
            System.out.println("Классы загружены, передаем на обработку.");
            System.out.println("----------------------------------------");

            // for (Class clazz : classes) {
            // System.out.println(clazz.getName());
            // ui.validateProgressBarTo();
            // }
            // Создаем код UML из загруженных классов.
            // String diagram = DataExtractor.extract(classes);
            String diagram = DataExtractor.extract(Options.getClasses());

            // Сохраняем код в заданный файл.
            File outputFile = new File(Options.getOutputFile());
            FileWriter outputWriter = new FileWriter(outputFile);
            outputWriter.write(diagram);
            outputWriter.flush();
            outputWriter.close();
            System.out.println("Данные сохранены в "
                    + outputFile.getAbsolutePath());

            // System.out.println(diagram);
            // ui.getGeneratedCode().setText(diagram);
            // DataExtractor.generate(diagram);
            //
            // ui.setProgressBarComplete();
            //
            // ui.showDiagram();
        }
    }

}
