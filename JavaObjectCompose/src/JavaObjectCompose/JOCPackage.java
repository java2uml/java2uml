package JavaObjectCompose;

import java.util.ArrayList;

/**
 * Created by vitol_000 on 10.12.2014.
 * java2uml
 */
public class JOCPackage {
    private String name;
    private ArrayList<JOCClass> classes;

    public JOCPackage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<JOCClass> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<JOCClass> classes) {
        this.classes = classes;
    }

    public void addClass(JOCClass jocClass) {
        if (classes == null) {
            classes = new ArrayList<JOCClass>();
        }
        classes.add(jocClass);
    }
}
