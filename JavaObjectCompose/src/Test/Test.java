package Test;

import JavaObjectCompose.*;

import java.util.ArrayList;

/**
 * Created by vitol_000 on 07.12.2014.
 * java2uml
 */
public class Test {
    JOCClass mJOCClass = new JOCClass("Qwerty");
    JOCPackage mJOCPackage = new JOCPackage("QwertyPackage");

    void setClassAccess() {
        mJOCClass.addField(new JOCField("qwerty", "int", "12"));
        mJOCClass.addImplementedInterface(new JOCClass("interface"));
        mJOCClass.addInnerClass(new JOCClass(JOCModifier.PUBLIC,"innerClass",true));
        mJOCClass.addMethod(new JOCMethod("method","float"));
        mJOCClass.getBaseClass();
        mJOCClass.setBaseClass(new JOCClass("baseClass"));
        mJOCClass.getBaseClass();
    }

    public static void main(String[] args) {
        Test test = new Test();
        test.setClassAccess();
    }
}
