package com.github.java2uml.core.parsing;

public class Main {
    public static void main(String[] args) throws Exception {
        String absolutePath = "/Users/nadcukandrej/Downloads/SeaBattle-master";
        /**
         * * Класс вызовающий генератор PlantUML кода для файлов ".java"
         * * 1 param - абсолютный путь к анализируемому проекту 
         * * 2 param - type связей
         * * ALL = 1
         * * AGGREGATION = 2
         * * COMPOSITION = 3
         * * ASSOCIATION = 4
         * * возвращаемый результат
         * * 1 - текстовый файл с PlantUML кодом создан
         * * 0 - не создан
         */
        new CreateUmlCode().write();
    }
}
