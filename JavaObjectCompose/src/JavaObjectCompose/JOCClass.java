package JavaObjectCompose;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vitol_000 on 24.11.2014.
 * java2uml
 *
 * Класс объекта java-класс
 */
public class JOCClass extends JOCBase {
    //Список параметров класса
    private ArrayList<JOCField> fields;

    //Список методов класса
    private ArrayList<JOCMethod> methods;

    //Внутренние классы
    private ArrayList<JOCClass> innerClasses;

    //Класс-родитель
    private JOCClass baseClass;

    //Список реализованных интерфейсов
    private ArrayList<JOCClass> implementationOf;

    private boolean isInterface = false;

    @Override
    HashMap<JOCModifier, Boolean> createModifiersMap() {
        return JOCModifiersFactory.createModifiersFor(this);
    }
    
    public JOCClass(String name) {
        this.name = name;
    }

    public JOCClass(String name, boolean isInterface) {
        this.name = name;
        this.isInterface = isInterface;
    }

    public JOCClass(JOCModifier accessModifier, String name) {
        this(name);
        this.accessModifier = accessModifier;

    }

    public JOCClass(JOCModifier accessModifier, String name, boolean isInterface) {
        this(name, isInterface);
        this.accessModifier = accessModifier;
    }

    public ArrayList<JOCField> getFields() {
        return fields;
    }

    public void setFields(ArrayList<JOCField> fields) {
        this.fields = fields;
    }

    public void addField(JOCField field) {
        if (fields == null) {
            fields = new ArrayList<JOCField>();
        }
        fields.add(field);
    }

    public ArrayList<JOCMethod> getMethods() {
        return methods;
    }

    public void setMethods(ArrayList<JOCMethod> methods) {
        this.methods = methods;
    }

    public void addMethod(JOCMethod method) {
        if (methods == null) {
            methods = new ArrayList<JOCMethod>();
        }
        methods.add(method);
    }

    public ArrayList<JOCClass> getInnerClasses() {
        return innerClasses;
    }

    public void setInnerClasses(ArrayList<JOCClass> innerClasses) {
        this.innerClasses = innerClasses;
    }

    public void addInnerClass(JOCClass innerClass) {
        if (innerClasses == null) {
            innerClasses = new ArrayList<JOCClass>();
        }
        innerClasses.add(innerClass);
    }

    public JOCClass getBaseClass() {
        return baseClass;
    }

    public void setBaseClass(JOCClass baseClass) {
        this.baseClass = baseClass;
    }

    public ArrayList<JOCClass> getImplementedInterfaces() {
        return implementationOf;
    }

    public void setImolementedInterfaces(ArrayList<JOCClass> implementationOf) {
        this.implementationOf = implementationOf;
    }

    public void addImplementedInterface(JOCClass implementedInterface) {
        if (implementationOf == null) {
            implementationOf = new ArrayList<JOCClass>();
        }
        implementationOf.add(implementedInterface);
    }

    public boolean isInterface() {
        return isInterface;
    }

    public void setAsInterface(boolean isInterface) {
        this.isInterface = isInterface;
    }

    public boolean isStatic() {
        return modifiersMap.get(JOCModifier.STATIC);
    }

    public boolean isAbstract() {
        return modifiersMap.get(JOCModifier.ABSTRACT);
    }

    public boolean isFinal() {
        return modifiersMap.get(JOCModifier.FINAL);
    }

    public boolean isStrictfp() {
        return modifiersMap.get(JOCModifier.STRICTFP);
    }
}
