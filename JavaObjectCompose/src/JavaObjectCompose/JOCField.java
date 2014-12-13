package JavaObjectCompose;

import java.util.HashMap;

/**
 * Created by vitol_000 on 24.11.2014.
 * java2uml
 *
 * Класс объекта java-переменной
 */
public class JOCField extends JOCBase {
    //Тип переменной
    private String type;

    //Значение по-умолчанию
    private String defaultValue;

    @Override
    HashMap<JOCModifier, Boolean> createModifiersMap() {
        return JOCModifiersFactory.createModifiersFor(this);
    }

    public JOCField(String name) {
        this.name = name;
    }

    public JOCField(String name, String type) {
        this(name);
        this.type = type;
    }

    public JOCField(String name, String type, String defaultValue) {
        this(name, type);
        this.defaultValue = defaultValue;
    }

    public JOCField(JOCModifier accessModifier, String name) {
        this(name);
        this.accessModifier = accessModifier;
    }

    public JOCField(JOCModifier accessModifier, String name, String type) {
        this(name, type);
        this.accessModifier = accessModifier;
    }

    public JOCField(JOCModifier accessModifier, String name, String type, String defaultValue) {
        this(name, type, defaultValue);
        this.accessModifier = accessModifier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isStatic() {
        return modifiersMap.get(JOCModifier.STATIC);
    }

    public boolean isFinal() {
        return modifiersMap.get(JOCModifier.FINAL);
    }

    public boolean isTransient() {
        return modifiersMap.get(JOCModifier.TRANSIENT);
    }

    public boolean isVolatile() {
        return modifiersMap.get(JOCModifier.VOLATILE);
    }
}
