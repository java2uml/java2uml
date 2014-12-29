package com.github.java2uml.core;

/**
 * Вспомогательный класс для передачи параметров запуска.
 *
 * Created by Игорь Акимов on 28.12.2014.
 */
public class Options {
    private String path = null;
    private String outputFile = "classes.plantuml";
    private String header = null;

    private boolean isClassDiagram = true;

    private boolean showComposition = true;
    private boolean showAggregation = true;
    private boolean showAssociation = true;
    private boolean showLollipop = true;

    private boolean isVertical = true;

    public Options() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public boolean isClassDiagram() {
        return isClassDiagram;
    }

    public boolean isSequenceDiagram() {
        return !isClassDiagram;
    }

    public void setClassDiagram() {
        isClassDiagram = true;
    }

    public void resetClassDiagram() {
        isClassDiagram = false;
    }

    public boolean isShowComposition() {
        return showComposition;
    }

    public void setShowComposition(boolean showComposition) {
        this.showComposition = showComposition;
    }

    public boolean isShowAggregation() {
        return showAggregation;
    }

    public void setShowAggregation(boolean showAggregation) {
        this.showAggregation = showAggregation;
    }

    public boolean isShowAssociation() {
        return showAssociation;
    }

    public void setShowAssociation(boolean showAssociation) {
        this.showAssociation = showAssociation;
    }

    public boolean isShowLollipop() {
        return showLollipop;
    }

    public void setShowLollipop(boolean showLollipop) {
        this.showLollipop = showLollipop;
    }

    public boolean isVertical() {
        return isVertical;
    }

    public boolean isHorizontal() {
        return !isVertical;
    }

    public void setVertical() {
        isVertical = true;
    }

    public void setHorizontal() {
        isVertical = false;
    }
}
