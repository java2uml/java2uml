package com.github.java2uml.core;
/**
 * Вспомогательный класс для передачи параметров запуска.
 *
 * Created by Игорь Акимов on 28.12.2014.
 */
public final class Options {
    private static String path = null;
    private static String outputFile = "classes.plantuml";
    private static String header = null;

    private static boolean isClassDiagram = true;

    private static boolean showComposition = true;
    private static boolean showAggregation = true;
    private static boolean showAssociation = true;
    private static boolean showLollipop = true;
    private static boolean showImplementation = true;

    private static boolean isVertical = true;

    private Options() {
    }

    public static void init() {
        path = null;
        outputFile = "classes.plantuml";
        header = null;

        isClassDiagram = true;

        showComposition = true;
        showAggregation = true;
        showAssociation = true;
        showLollipop = true;
        showImplementation = true;

        isVertical = true;
    }

    public static String getPath() {
        return path;
    }

    public static void setPath(String path) {
        Options.path = path;
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
}
