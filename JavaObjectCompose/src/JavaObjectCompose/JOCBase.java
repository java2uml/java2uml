package JavaObjectCompose;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vitol_000 on 24.11.2014.
 * java2uml
 *
 * Базовый абстрактный класс.
 * Содержит имя и модификаторы.
 * Наследуется классами JCCClass, JCCMethod, JCCParameter.
 */
abstract class JOCBase {
    //Название. По-умолчанию аноним.
    protected String name = "";

    //Модификатор доступа. По-умолчанию package-private.
    protected JOCModifier accessModifier = JOCModifier.DEFAULT;

    //Список модификаторв кроме модификатора доступа
    protected HashMap<JOCModifier, Boolean> modifiersMap = createModifiersMap();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JOCModifier getAccessModifier() {
        return accessModifier;
    }

    //Установка модификатора по его названию
    public void setAccessModifier(String accessModifierName) {
        accessModifier = JOCModifier.valueOf(accessModifierName);
    }

    public void setAccessModifier(JOCModifier accessModifier) {
        this.accessModifier = accessModifier;
    }

    public ArrayList<JOCModifier> getEnabledModifiers() {
        ArrayList<JOCModifier> enabledModifiers = new ArrayList<JOCModifier>();
        for (JOCModifier modifier : modifiersMap.keySet()) {
            if (modifiersMap.get(modifier)) {
                enabledModifiers.add(modifier);
            }
        }
        return enabledModifiers;
    }

    public ArrayList<JOCModifier> getAvailableModifiers() {
        return new ArrayList<JOCModifier>(modifiersMap.keySet());
    }

    public void setModifiers(ArrayList<String> modifiersNames) {
        for (String modifierName : modifiersNames) {
            setModifier(modifierName);
        }
    }

    public void setModifier(JOCModifier modifier) {
        modifiersMap.replace(modifier, true);
    }

    public void setModifier(String modifierName) {
        setModifier(JOCModifier.valueOf(modifierName));
    }

    abstract HashMap<JOCModifier,Boolean> createModifiersMap();
}
