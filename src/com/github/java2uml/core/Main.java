package com.github.java2uml.core;

import com.github.java2uml.core.reflection.Reflection;

public class Main {
    // todo
    // Из класса UI эту переменную меняет JFileChooser, при выборе директории.
    // Необходимо эту переменную из Main перенести в пакет GUI.
    public static String path;
//    static String[] args;
//    UI ui;

    public static void main(String[] args) throws Exception {
        final int firstOptionalArgument = 2; // Порядковый номер первого необязательно параметра.
//        Main main = new Main();
//        main.go();

//*
        // Должны быть заданы хотя бы два параметра: тип файлов для преобразования и
        // путь к исходным файлам.
        if (args.length < 2) {
            throw new InvalidParameterException("Too few parameters.");
        }

        // Создаем новый объект с параметрами.
        Options options = new Options();

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
            throw new InvalidParameterException("Incorrect parameters.");
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
                options.setPath(paramValue);
            } else if (paramLength <= "source_path".length() && "source_path"
                    .substring(0, paramLength).equals(paramName)) {
                options.setPath(paramValue);
            } else {
                throw new InvalidParameterException("Incorrect parameters.");
            }
        } else {
            options.setPath(args[1]);
        }

        // Разбираем оставшиеся параметры.
        for (int i = firstOptionalArgument; i < args.length; i++) {
            String param = args[i];
            paramLength = param.length();
            // Длина параметра не может быть меньше 3 символов.
            if (paramLength < 3) {
                throw new InvalidParameterException("Incorrect parameters.");
            }

            // Проверяем параметры с именами, устанавливаем соответствующие значения.
            equalSignPos = param.indexOf("=");
            if (equalSignPos > 0) {
                String paramName = param.substring(0, equalSignPos).toLowerCase();
                String paramValue = param.substring(equalSignPos + 1);
                int paramNameLength = paramName.length();
                if (paramNameLength >= 3 && paramNameLength <= "output_file".length()
                        && "output_file".substring(0, paramNameLength).equals(paramName)) {
                    options.setOutputFile(paramValue);
                    continue;
                } else if (paramNameLength >= 3 && paramNameLength <= "header".length() && "header"
                        .substring(0, paramNameLength).equals(paramName)) {
                    options.setHeader(paramValue);
                    continue;
                } else {
                    throw new InvalidParameterException("Incorrect parameters.");
                }
            }

            param = param.toLowerCase();
            // Параметр, задающий вывод диаграммы классов. Параметр не может
            // быть задан совместно с "sequence_diagram"
            if (paramLength <= "classes_diagram".length() && "classes_diagram"
                    .substring(0, paramLength).equals(param)) {
                for (int j = firstOptionalArgument; j < args.length; j++) {
                    String comparedParam = args[j].toLowerCase();
                    if (comparedParam.length() <= "sequence_diagram".length() && "sequence_diagram"
                            .substring(0, comparedParam.length()).equals(comparedParam)) {
                        throw new InvalidParameterException("Incompatible parameters.");
                    }
                }
                options.setClassDiagram();
                continue;
            }

            // Параметр, задающий вывод диаграммы последовательностей. Указанный параметр не может
            // быть задан совместно с "classes_diagram", и может использоваться только при
            // парсинге файлов ".java".
            if (paramLength <= "sequence_diagram".length() && "sequence_diagram"
                    .substring(0, paramLength).equals(param)) {
                if (sourceFileType == 1) {
                    for (int j = firstOptionalArgument; j < args.length; j++) {
                        String comparedParam = args[j].toLowerCase();
                        if (comparedParam.length() <= "classes_diagram".length()
                                && "classes_diagram".substring(0, comparedParam.length())
                                .equals(comparedParam)) {
                            throw new InvalidParameterException("Incompatible parameters.");
                        }
                    }
                    options.resetClassDiagram();
                    continue;
                } else {
                    throw new InvalidParameterException("Incompatible parameters.");
                }
            }

            // Параметр, задающий вертикальное направление диаграммы. Не может использоваться
            // совместно с параметром "horizontal".
            if (paramLength <= "vertical".length() && "vertical".substring(0, paramLength)
                    .equals(param)) {
                for (int j = firstOptionalArgument; j < args.length; j++) {
                    String comparedParam = args[j].toLowerCase();
                    if (comparedParam.length() <= "horizontal".length() && "horizontal"
                            .substring(0, comparedParam.length()).equals(comparedParam)) {
                        throw new InvalidParameterException("Incompatible parameters.");
                    }
                }
                options.setVertical();
                continue;
            }

            // Параметр, задающий горизонтальное направление диаграммы. Не может использоваться
            // совместно с параметром "vertical".
            if (paramLength <= "horizontal".length() && "horizontal".substring(0, paramLength)
                    .equals(param)) {
                for (int j = firstOptionalArgument; j < args.length; j++) {
                    String comparedParam = args[j].toLowerCase();
                    if (comparedParam.length() <= "vertical".length() && "vertical"
                            .substring(0, comparedParam.length()).equals(comparedParam)) {
                        throw new InvalidParameterException("Incompatible parameters.");
                    }
                }
                options.setHorizontal();
                continue;
            }

            // Следующие параметры можно сократить до 5 символов.
            if (paramLength < 5) {
                throw new InvalidParameterException("Incorrect parameters.");
            }

            // Параметр, запрещающий вывод композиции.
            if (paramLength <= "nocomposition".length() && "nocomposition".substring(0, paramLength)
                    .equals(param)) {
                options.setShowComposition(false);
                continue;
            }

            // Параметр, запрещающий вывод агрегации.
            if (paramLength <= "noaggregation".length() && "noaggregation".substring(0, paramLength)
                    .equals(param)) {
                options.setShowAggregation(false);
                continue;
            }

            // Параметр, запрещающий вывод ассоциации.
            if (paramLength <= "noassociation".length() && "noassociation".substring(0, paramLength)
                    .equals(param)) {
                options.setShowAssociation(false);
                continue;
            }

            // Параметр, запрещающий вывод "леденцов".
            if (paramLength <= "nolollipop".length() && "nolollipop".substring(0, paramLength)
                    .equals(param)) {
                options.setShowLollipop(false);
                continue;
            }

            // Параметр, запрещающий вывод "леденцов".
            if (paramLength <= "noimplementation".length() && "noimplementation".substring(0, paramLength)
                    .equals(param)) {
                options.setShowImplementation(false);
                continue;
            }

            throw new InvalidParameterException("Incorrect parameters.");
        }

        // Запускаем соответствующую обработку.
        switch (sourceFileType) {
            case 1:
                // вызываем парсинг
                System.out.println("Парсинг");
                break;
            case 2:
                // вызываем рефлексию
                System.out.println("Рефлексия");
                Reflection reflection = new Reflection();
                reflection.loadClassesAndGenerateDiagram(options);
                break;
        }
        //*/
    }

/* Следующие методы отключены, т.к. должны быть реализованы в соответствующих местах.

    // todo
    // Метод должен быть в пакете GUI
    private void go() throws Exception {
        Method[] methods;
        Field[] fields;
        Constructor[] constructors;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initUI();
            }
        });


    }

    // todo
    // Метод должен быть в пакете GUI.
    public void initUI() {
        ui = new UI();
        ui.initUI().setVisible(true);
        ui.addActionListenerToChooseFile();
        ui.getGeneratePlantUML().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread generateDiagramThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadClassesAndGenerateDiagram(ui.getPath().getText());
                    }
                });
                generateDiagramThread.start();

            }
        });
    }
*/
    // todo
    // Метод должен быть в пакете reflection и parsing (получать путь из options), если этот
    // метод нужен в GUI, надо посмотреть, как его заменить.
    public static String getPath() {
        return path;
    }

    // todo
    // Метод должен быть в пакете reflection и parsing (устанавливать путь в options), если этот
    // метод нужен в GUI, надо посмотреть, как его заменить.
    public static void setPath(String _path) {
        path = _path;
    }
//*/
}
