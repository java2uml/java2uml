package JavaObjectCompose;

/**
 * Created by vitol_000 on 24.11.2014.
 * java2uml
 */
public enum JOCModifier {
    PRIVATE("private"),
    PROTECTED("protected"),
    PUBLIC("public"),
    DEFAULT("package-private"),
    STATIC("static"),
    ABSTRACT("abstract"),
    FINAL("final"),
    NATIVE("native"),
    TRANSIENT("transient"),
    VOLATILE("volatile"),
    SYNCHRONIZED("synchronized"),
    STRICTFP("strictfp");

    private String name;

    JOCModifier(String s) {
        name = s;
    }

    @Override
    public String toString() {
        return name;
    }
}
