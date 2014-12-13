package JavaObjectCompose;

import java.util.HashMap;

/**
 * Created by vitol_000 on 24.11.2014.
 * java2uml
 *
 * Класс для создания списка модификаторов кроме модификатора доступа.
 */
final class JOCModifiersFactory {
    //Здесь хранится создаваемый список модификаторов
    private static HashMap<JOCModifier, Boolean> modifiersMap;

    private JOCModifiersFactory() {}

    //Метод инициирует создание списка и возвращает сформированный список.
    //В качестве аргумента передается объект, для которого строится список модификаторов.
    public static HashMap<JOCModifier, Boolean> createModifiersFor(Object o) {
        modifiersMap = new HashMap<JOCModifier, Boolean>();
        if (o.getClass() == JOCField.class) {
            createModifiersForField();
        } else if (o.getClass() == JOCMethod.class) {
            createModifiersForMethod();
        } else if (o.getClass() == JOCClass.class) {
            createModifiersForClass();
        }
        return modifiersMap;
    }

    //Создает список модификаторов для переменной
    private static void createModifiersForField() {
        add(JOCModifier.STATIC);
        add(JOCModifier.FINAL);
        add(JOCModifier.TRANSIENT);
        add(JOCModifier.VOLATILE);
    }

    //Создает список модификаторов для метода
    private static void createModifiersForMethod() {
        add(JOCModifier.STATIC);
        add(JOCModifier.ABSTRACT);
        add(JOCModifier.FINAL);
        add(JOCModifier.NATIVE);
        add(JOCModifier.SYNCHRONIZED);
        add(JOCModifier.STRICTFP);
    }

    //Создает список модификаторов для класса
    private static void createModifiersForClass() {
        add(JOCModifier.STATIC);
        add(JOCModifier.ABSTRACT);
        add(JOCModifier.FINAL);
        add(JOCModifier.STRICTFP);
    }

    //Метод добавления модификатора.
    private static void add(JOCModifier modifier) {
        if (modifier != null) {
            modifiersMap.put(modifier, false);
        }
    }
}
