package com.github.java2uml.core;

import java.util.Hashtable;

/**
 * Created by Igor on 28.12.2014.
 */
public class Options {
    private String path = null;
    private String outputFile = null;
    private String header = null;
    private Hashtable<String, Boolean> options;

    public Options() {
        options = new Hashtable<String, Boolean>();
        options.put("class_diagram", true);
        options.put("composition", true);
        options.put("aggregation", true);
        options.put("association", true);
        options.put("lollipop", true);
        options.put("vertical", true);
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
        return options.get("class_diagram");
    }

    public void setClassDiagram() {
        options.put("class_diagram", true);
    }

    public void resetClassDiagram() {
        options.put("class_diagram", false);
    }

    public boolean isComposition() {
        return options.get("composition");
    }

    public void setComposition() {
        options.put("composition", true);
    }

    public void resetComposition() {
        options.put("composition", false);
    }

    public boolean isAggregation() {
        return options.get("aggregation");
    }

    public void setAggregation() {
        options.put("aggregation", true);
    }

    public void resetAggregation() {
        options.put("aggregation", false);
    }

    public boolean isAssociation() {
        return options.get("association");
    }

    public void setAssociation() {
        options.put("association", true);
    }

    public void resetAssociation() {
        options.put("association", false);
    }

    public boolean isLollipop() {
        return options.get("lollipop");
    }

    public void setLollipop() {
        options.put("lollipop", true);
    }

    public void resetLollipop() {
        options.put("lollipop", false);
    }

    public boolean isVertical() {
        return options.get("vertical");
    }

    public void setVertical() {
        options.put("vertical", true);
    }

    public void resetVertical() {
        options.put("vertical", false);
    }
}
