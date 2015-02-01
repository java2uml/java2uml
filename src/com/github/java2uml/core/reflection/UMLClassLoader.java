package com.github.java2uml.core.reflection;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * http://forum.vingrad.ru/articles/topic-157979.html
 * <p/>
 * Reworked by Igor Akimov on 07.12.2014.
 */
public class UMLClassLoader extends ClassLoader {

    private Hashtable<String, Class> cache;

    private ArrayList<String> paths;

    private ClassLoader currentLoader = null;

    public UMLClassLoader() {
        cache = new Hashtable<String, Class>();
        paths = new ArrayList<String>();
    }

    /**
     * @see java.lang.ClassLoader#findClass(java.lang.String)
     */
    protected synchronized Class findClass(String className) throws ClassNotFoundException {
        Class result;
        byte classData[];

        // Проверяем кэш классов. Если класса в кэше нет, тогда применяются другие средства
        result = cache.get(className);
        if (result != null) {
            return result;
        }

        // Проверяется нет ли такого класса в стандартном пути
        try {
            // если загрузчик классов текущего потока уже является расширенным,
            // используем сохраненное значение загрузчика классов
            if (Thread.currentThread().getContextClassLoader() instanceof UMLClassLoader) {
                if (this.currentLoader == null) {
                    throw new ClassNotFoundException();
                } else {
                    result = this.currentLoader.loadClass(className);
                }
            } else {
                result = Thread.currentThread().getContextClassLoader().loadClass(className);
            }
            return result;
        } catch (ClassNotFoundException e) {
        } catch (Exception e) {
        }

        // Попытка загрузить класс из добавленного пути
        classData = getClassFromAddedClassPaths(className);
        if (classData == null) {
            throw new ClassNotFoundException();
        }

        // определение класса
        result = super.defineClass(className, classData, 0, classData.length);
        if (result == null) {
            throw new ClassFormatError();
        }

        resolveClass(result);

        // Полученный класс добавляется в кэш классов
        cache.put(className, result);

        return result;
    }

    /**
     * Метод ищет класс в созданном CLASSPATH и возвращает массив байтов
     *
     * @param className Имя класса, который нужно загрузить
     * @return Массив, содержащий байт-код класса
     */
    private byte[] getClassFromAddedClassPaths(String className) {
        try {
            String fileSeparator = System.getProperty("file.separator");
            for (String path : paths) {
                File pathFile = new File(path);
                if (pathFile.isDirectory()) {
                    File f = new File(path + fileSeparator + classNameToFileName(className));
                    if (f.exists()) {
                        FileInputStream fis = new FileInputStream(f);
                        return createByteArray(fis);
                    }
                } else {
                    JarFile jarFile = new JarFile(pathFile);
                    ZipEntry entry = jarFile.getEntry(classNameToZipEntryName(className));
                    if (entry == null) {
                        continue;
                    }
                    InputStream stream = jarFile.getInputStream(entry);
                    return createByteArray(stream);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Создает массив байтов из входного потока
     *
     * @param in Входной поток
     * @return Массив байтов
     * @throws IOException
     */
    private byte[] createByteArray(InputStream in) throws IOException {
        final int bufferSize = 2048;
        byte result[] = new byte[bufferSize];

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len = 0;
        while ((len = in.read(result)) != -1) {
            out.write(result, 0, len);
        }
        return out.toByteArray();
    }

    /**
     * Преобразует имя пакета в путь к каталогу
     *
     * @param className преобразуемое имя класса
     * @return полученное имя файла
     */
    private String classNameToFileName(String className) {
        return className.replace('.', System.getProperty("file.separator").charAt(0)) + ".class";
    }

    private String classNameToZipEntryName(String className) {
        return className.replace('.', '/') + ".class";
    }

    protected URL findResource(String name) {
        URL res = ClassLoader.getSystemResource(name);
        if (res != null) {
            return res;
        }
        try {
            String fsep = System.getProperty("file.separator");
            for (String path : paths) {
                File pathFile = new File(path);
                if (pathFile.isDirectory()) {
                    File f = new File(path + fsep + name);
                    if (f.exists()) {
                        return f.toURL();
                    }
                } else {
                    JarFile jarFile = new JarFile(pathFile);
                    ZipEntry entry = jarFile.getEntry(name);
                    if (entry == null) {
                        continue;
                    }
                    String url = createJarResourceURL(pathFile, name, "/");
                    return new URL(url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String createJarResourceURL(File jarFile, String resourceName, String fileSeparator)
            throws MalformedURLException {
        String url = "jar:" + jarFile.toURL() + "!" + fileSeparator + resourceName;
        return url;
    }

    /**
     * Добавляет строку в CLASSPATH
     *
     * @param path Добавляемая строка
     */
    public void addClassPath(String path) {
        paths.add(path);
    }

    /**
     * Удаляет строку из CLASSPATH
     *
     * @param path Удаляемая строка
     */
    public void removeClassPath(String path) {
        int index = paths.indexOf(path);
        paths.remove(index);
    }

    /**
     * Загружает все классы из path.
     *
     * @param path Путь к папке с классами.
     * @return Массив загруженных классов.
     */
    public Set<Class> loadClasses(String path) throws ClassNotFoundException, IOException {
        Set<Class> classes = new HashSet<Class>();
        List<String> classNames = new ArrayList<String>();

        if (path.matches(".+\\.jar$")) {
            try {
                // прислали jar - распакуем
                JarFile jarfile = new JarFile(new File(path));
                // выгружаем классы в папку с именем jar
                String destdir = path.substring(0, path.indexOf(".jar"));
                Enumeration<JarEntry> enu = jarfile.entries();
                while (enu.hasMoreElements()) {
                    JarEntry je = enu.nextElement();
                    File fl = new File(destdir, je.getName());
                    if (je.isDirectory()) {
                        continue;
                    }
                    if (!fl.exists()) {
                        fl.getParentFile().mkdirs();
                        fl.getParentFile().createNewFile();
                        fl = new java.io.File(destdir, je.getName());
                    }
                    try (InputStream is = jarfile.getInputStream(je);
                         FileOutputStream fo = new FileOutputStream(fl);) {
                        // сохраняем файл
                        while (is.available() > 0) {
                            fo.write(is.read());
                        }
                    }
                }
                // меняем path
                path = destdir;
            } catch (IOException e) {
                throw e;
            }
        }

        // Добавляем путь к списку CLASSPATH, если такой в списке отсутствует.
        int index = paths.indexOf(path);
        if (index < 0) {
            addClassPath(path);
        }

        // Загружаем список имен классов, имеющихся в path и ее подпапках.
        classNames = classList(path, "", classNames);

        // Загружаем классы в список классов.
        // Исключения пока выбрасываются вверх.
        for (String className : classNames) {
            // TODO убрать вывод после тестирования
//            System.out.print(
//                    "Загружается " + className.substring(className.lastIndexOf(".") + 1) + "... ");
            String loadingClassName = className;
            String loadingFromPath = path;

            boolean packetFound = false;
            while (!packetFound) {
                try {
                    // Загружаем класс.
                    Class c = loadClass(loadingClassName);
                    classes.add(c);

                    // Если у класса нет пакета, определяем его.
                    if (c.getPackage() == null) {
                        int posSeparator = loadingClassName.lastIndexOf(".");
                        if (posSeparator > 0) {
                            String packageName = loadingClassName.substring(0, posSeparator);
                            definePackage(packageName, null, null, null, null, null, null, null);
                        }
                    }

                    // TODO убрать вывод после тестирования
//                    System.out.println("Загружен " + loadingClassName + " из " + loadingFromPath);
                    packetFound = true;
                } catch (NoClassDefFoundError e) {
                    // Если класс не смог загрузиться, переходим на следующую подпапку.
                    int posSeparator = loadingClassName.indexOf(".");
                    if (posSeparator == -1) {
                        // TODO убрать вывод после тестирования
//                        System.out.println("Не найден пакет для класса " + loadingClassName);
                        break;
                    }

                    loadingFromPath = loadingFromPath + System.getProperty("file.separator")
                            + loadingClassName.substring(0, posSeparator);
                    loadingClassName = loadingClassName.substring(posSeparator + 1);
                    index = paths.indexOf(loadingFromPath);
                    if (index < 0) {
                        addClassPath(loadingFromPath);
                    }
                }
            }
        }

        return classes;
    }

    /**
     * Загружает список имен классов, имеющихся в папке path.
     * Имена классов дополняются префиксом относительно исходной папки.
     *
     * @param path        Имя папки
     * @param classPrefix Префикс для имени класса.
     * @param classNames  Список, в который добавляются найденные имена классов.
     * @return Список имен классов.
     */
    private List<String> classList(String path, String classPrefix, List<String> classNames) {
        // Расширение файлов классов.
        final String CLASS_EXTENSION = ".class";

        File dir = new File(path);

        // Список файлов и подпапок.
        File[] children = dir.listFiles();

        // Список имен файлов с заданным расширением.
        String[] files = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(CLASS_EXTENSION);
            }
        });

        // Если префикс имени класса пустой, то к имени класса ничего не добавляем,
        // иначе добавляем относительный путь. Получившееся полное имя класса заносим
        // в список имен.
        classPrefix = (classPrefix.isEmpty()) ? classPrefix : classPrefix + ".";
        if (files != null) {
            for (String fileName : files) {
                classNames.add(classPrefix + fileName.split(CLASS_EXTENSION)[0]);
            }
        }

        // Ищем классы в подпапках.
        if (children != null) {
            for (File subDir : children) {
                if (subDir.isDirectory()) {
                    classNames = classList(subDir.getPath(), classPrefix + subDir.getName(),
                            classNames);
                }
            }
        }

        return classNames;
    }
}
