package JavaObjectCompose;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vitol_000 on 24.11.2014.
 * java2uml
 *
 * Класс объекта java-метод
 */
public class JOCMethod extends JOCBase {
    //Тип возвращаемого значения
    private String typeOfReturn;

    //Список аргументов
    private ArrayList<JOCField> arguments;

    @Override
    HashMap<JOCModifier, Boolean> createModifiersMap() {
        return JOCModifiersFactory.createModifiersFor(this);
    }

    public JOCMethod (String name) {
        this.name = name;
        arguments = new ArrayList<JOCField>();
    }

    public JOCMethod (String name, String typeOfReturn) {
        this(name);
        this.typeOfReturn = typeOfReturn;
    }

    public JOCMethod (String name, String typeOfReturn, ArrayList<JOCField> arguments) {
        this(name, typeOfReturn);
        this.arguments = arguments;
    }

    public JOCMethod (JOCModifier accessModifier, String name) {
        this(name);
        this.accessModifier = accessModifier;
    }

    public JOCMethod (JOCModifier accessModifier, String name, String typeOfReturn) {
        this(name, typeOfReturn);
        this.accessModifier = accessModifier;
    }

    public JOCMethod (JOCModifier accessModifier, String name, String typeOfReturn, ArrayList<JOCField> arguments) {
        this(name, typeOfReturn, arguments);
        this.accessModifier = accessModifier;
    }

    public String getTypeOfReturn() {
        return typeOfReturn;
    }

    public void setTypeOfReturn(String typeOfReturn) {
        this.typeOfReturn = typeOfReturn;
    }

    public ArrayList<JOCField> getArguments() {
        return arguments;
    }

    public void setArguments(ArrayList<JOCField> arguments) {
        this.arguments = arguments;
    }

    public void addArgument(JOCField argument) {
        if (arguments == null) {
            arguments = new ArrayList<JOCField>();
        }
        arguments.add(argument);
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

    public boolean isNative() {
        return modifiersMap.get(JOCModifier.NATIVE);
    }

    public boolean isSynchronized() {
        return modifiersMap.get(JOCModifier.SYNCHRONIZED);
    }

    public boolean isStrictfp() {
        return modifiersMap.get(JOCModifier.STRICTFP);
    }
}
