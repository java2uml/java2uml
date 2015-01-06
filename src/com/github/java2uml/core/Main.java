package com.github.java2uml.core;

import com.github.java2uml.core.parsing.CreateUmlCode;
import com.github.java2uml.core.reflection.Reflection;

public class Main {
    public static void main(String[] args) throws Exception {
        final int FIRST_OPTIONAL_ARGUMENT = 2; // Порядковый номер первого необязательно параметра.

        // Должны быть заданы хотя бы два параметра: тип файлов для преобразования и
        // путь к исходным файлам.
        if (args.length < 2) {
            throw new InvalidParameterException("Too few parameters (" + args.length + ").");
        }

        // Инициализация класса Options значениями по умолчанию.
        Options.init();

        // Проверяем первый параметр - тип исходных файлов. Параметр обязательный.
        int sourceFileType;
        int length2Param = args[0].length();
        if (length2Param >= 3 && length2Param <= "java".length() && "java"
                .substring(0, length2Param).equals(args[0].toLowerCase())) {
            sourceFileType = 1;
        } else if (length2Param >= 3 && length2Param <= "class".length() && "class"
                .substring(0, length2Param).equals(args[0].toLowerCase())) {
            sourceFileType = 2;
        } else {
            throw new InvalidParameterException(
                    "Incorrect parameters. The first parameter must be a \"java\" or \"class\"");
        }

        // Проверяем второй параметр - путь к файлам. Если присутствует знак '=', то указано
        // имя параметра, в параметры помещаем только значение параметра - заданный путь.
        // Если имя параметра задано неверно, то выбрасывается исключение.
        // Параметр обязательный, имя параметра может быть опущено.
        int equalSignPos = args[1].indexOf("=");
        int paramLength;
        if (equalSignPos > 0) {
            String paramName = args[1].substring(0, equalSignPos).toLowerCase();
            String paramValue = args[1].substring(equalSignPos + 1);
            paramLength = paramName.length();
            if ("src".equals(paramName)) {
                Options.setPath(paramValue);
            } else if (paramLength >= 3 && paramLength <= "source_path".length() && "source_path"
                    .substring(0, paramLength).equals(paramName)) {
                Options.setPath(paramValue);
            } else {
                throw new InvalidParameterException(
                        "Incorrect second parameter: \"" + args[1] + "\".");
            }
        } else {
            Options.setPath(args[1]);
        }

        // Разбираем оставшиеся параметры.
        for (int i = FIRST_OPTIONAL_ARGUMENT; i < args.length; i++) {
            String param = args[i];

            // Пустые параметры пропускаем.
            if (param.trim().isEmpty()) {
                continue;
            }

            paramLength = param.length();
            // Длина параметра не может быть меньше 3 символов.
            if (paramLength < 3) {
                throw new InvalidParameterException("Incorrect parameter " + (i + 1)
                        + ". Parameter can not be shorter than 3 characters.");
            }

            // Проверяем параметры с именами, устанавливаем соответствующие значения.
            equalSignPos = param.indexOf("=");
            if (equalSignPos > 0) {
                String paramName = param.substring(0, equalSignPos).toLowerCase();
                String paramValue = param.substring(equalSignPos + 1);
                int paramNameLength = paramName.length();
                if (paramNameLength >= 3 && paramNameLength <= "output_file".length()
                        && "output_file".substring(0, paramNameLength).equals(paramName)) {
                    Options.setOutputFile(paramValue);
                    continue;
                } else if (paramNameLength >= 3 && paramNameLength <= "header".length() && "header"
                        .substring(0, paramNameLength).equals(paramName)) {
                    Options.setHeader(paramValue);
                    continue;
                } else {
                    throw new InvalidParameterException(
                            "Incorrect parameter " + (i + 1) + ": \"" + param + "\".");
                }
            }

            param = param.toLowerCase();
            // Параметр, задающий вывод диаграммы классов. Параметр не может
            // быть задан совместно с "sequence_diagram"
            if (paramLength <= "classes_diagram".length() && "classes_diagram"
                    .substring(0, paramLength).equals(param)) {
                for (int j = FIRST_OPTIONAL_ARGUMENT; j < args.length; j++) {
                    String comparedParam = args[j].toLowerCase();
                    if (comparedParam.trim().isEmpty()) {
                        continue;
                    }
                    if (comparedParam.length() <= "sequence_diagram".length() && "sequence_diagram"
                            .substring(0, comparedParam.length()).equals(comparedParam)) {
                        throw new InvalidParameterException(
                                "Incompatible parameters " + (i + 1) + " and " + (j + 1) + ": \""
                                        + param + "\" and \"" + comparedParam + "\".");
                    }
                }
                Options.setClassDiagram();
                continue;
            }

            // Параметр, задающий вывод диаграммы последовательностей. Указанный параметр не может
            // быть задан совместно с "classes_diagram", и может использоваться только при
            // парсинге файлов ".java".
            if (paramLength <= "sequence_diagram".length() && "sequence_diagram"
                    .substring(0, paramLength).equals(param)) {
                if (sourceFileType == 1) {
                    for (int j = FIRST_OPTIONAL_ARGUMENT; j < args.length; j++) {
                        String comparedParam = args[j].toLowerCase();
                        if (comparedParam.trim().isEmpty()) {
                            continue;
                        }
                        if (comparedParam.length() <= "classes_diagram".length()
                                && "classes_diagram".substring(0, comparedParam.length())
                                .equals(comparedParam)) {
                            throw new InvalidParameterException(
                                    "Incompatible parameters " + (i + 1) + " and " + (j + 1)
                                            + ": \"" + param + "\" and \"" + comparedParam + "\".");
                        }
                    }
                    Options.resetClassDiagram();
                    continue;
                } else {
                    throw new InvalidParameterException(
                            "Incompatible parameters 1 and " + (i + 1) + ": \"" + args[0] + "\" and \""
                                    + param + "\".");
                }
            }

            // Параметр, задающий вертикальное направление диаграммы. Не может использоваться
            // совместно с параметром "horizontal".
            if (paramLength <= "vertical".length() && "vertical".substring(0, paramLength)
                    .equals(param)) {
                for (int j = FIRST_OPTIONAL_ARGUMENT; j < args.length; j++) {
                    String comparedParam = args[j].toLowerCase();
                    if (comparedParam.trim().isEmpty()) {
                        continue;
                    }
                    if (comparedParam.length() <= "horizontal".length() && "horizontal"
                            .substring(0, comparedParam.length()).equals(comparedParam)) {
                        throw new InvalidParameterException(
                                "Incompatible parameters " + (i + 1) + " and " + (j + 1) + ": \"" + param
                                        + "\" and \"" + comparedParam + "\".");
                    }
                }
                Options.setVertical();
                continue;
            }

            // Параметр, задающий горизонтальное направление диаграммы. Не может использоваться
            // совместно с параметром "vertical".
            if (paramLength <= "horizontal".length() && "horizontal".substring(0, paramLength)
                    .equals(param)) {
                for (int j = FIRST_OPTIONAL_ARGUMENT; j < args.length; j++) {
                    String comparedParam = args[j].toLowerCase();
                    if (comparedParam.trim().isEmpty()) {
                        continue;
                    }
                    if (comparedParam.length() <= "vertical".length() && "vertical"
                            .substring(0, comparedParam.length()).equals(comparedParam)) {
                        throw new InvalidParameterException(
                                "Incompatible parameters " + (i + 1) + " and " + (j + 1) + ": \"" + param
                                        + "\" and \"" + comparedParam + "\".");
                    }
                }
                Options.setHorizontal();
                continue;
            }

            // Следующие параметры можно сократить до 5 символов.
            if (paramLength < 5) {
                throw new InvalidParameterException("Incorrect parameters " + (i + 1)
                        + ". Parameter can not be shorter than 5 characters.");
            }

            // Параметр, запрещающий вывод композиции.
            if (paramLength <= "nocomposition".length() && "nocomposition".substring(0, paramLength)
                    .equals(param)) {
                Options.setShowComposition(false);
                continue;
            }

            // Параметр, запрещающий вывод агрегации.
            if (paramLength <= "noaggregation".length() && "noaggregation".substring(0, paramLength)
                    .equals(param)) {
                Options.setShowAggregation(false);
                continue;
            }

            // Параметр, запрещающий вывод ассоциации.
            if (paramLength <= "noassociation".length() && "noassociation".substring(0, paramLength)
                    .equals(param)) {
                Options.setShowAssociation(false);
                continue;
            }

            // Параметр, запрещающий вывод "леденцов".
            if (paramLength <= "nolollipop".length() && "nolollipop".substring(0, paramLength)
                    .equals(param)) {
                Options.setShowLollipop(false);
                continue;
            }

            // Параметр, запрещающий вывод "леденцов".
            if (paramLength <= "noimplementation".length() && "noimplementation"
                    .substring(0, paramLength).equals(param)) {
                Options.setShowImplementation(false);
                continue;
            }

            throw new InvalidParameterException(
                    "Incorrect parameter " + (i + 1) + ": \"" + param + "\".");
        }

        // Запускаем соответствующую обработку.
        switch (sourceFileType) {
            case 1:
                // вызываем парсинг
                System.out.println("Парсинг");
                new CreateUmlCode().write();
                break;
            case 2:
                // вызываем рефлексию
                System.out.println("Рефлексия");
                Reflection reflection = new Reflection();
                reflection.loadClassesAndGenerateDiagram();
                break;
        }
    }
}
