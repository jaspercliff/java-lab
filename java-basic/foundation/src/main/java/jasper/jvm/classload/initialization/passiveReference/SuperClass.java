package jasper.jvm.classload.initialization.passiveReference;

class SuperClass {
    static { System.out.println("SuperClass init!"); }
    public static int value = 123;
}