package com.github.java2uml.core;

import java.util.Hashtable;

/**
 * Created by Igor on 28.12.2014.
 */
public class Options {
    private String path = null;
    private String outputFile = null;
    private String header = null;

    private boolean isClassDiagram = true;
    private boolean isComposition = true;
    private boolean isAggregation = true;
    private boolean isAssociation = true;
    private boolean isLollipop = true;

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

    public void setClassDiagram() {
        isClassDiagram = true;
    }

    public void resetClassDiagram() {
        isClassDiagram = false;
    }

    public boolean isComposition() {
        return isComposition;
    }

    public void setComposition() {
        isComposition = true;
    }

    public void resetComposition() {
        isComposition = false;
    }

    public boolean isAggregation() {
        return isAggregation;
    }

    public void setAggregation() {
        isAggregation = true;
    }

    public void resetAggregation() {
        isAggregation = false;
    }

    public boolean isAssociation() {
        return isAssociation;
    }

    public void setAssociation() {
        isAssociation = true;
    }

    public void resetAssociation() {
        isAssociation = false;
    }

    public boolean isLollipop() {
        return isLollipop;
    }

    public void setLollipop() {
        isLollipop = true;
    }

    public void resetLollipop() {
        isLollipop = false;
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
