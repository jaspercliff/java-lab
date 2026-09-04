package jasper.jvm.classload.initialization.passiveReference;

    /**
     * 通过数组定义来引用类 不会触发初始化
     */
    public class NotInitialization {
        public static void main(String[] args) {
            final SuperClass[] superClasses = new SuperClass[10];
            System.out.println("数组的类名: " + superClasses.getClass().getName());
            //数组的类名: [Lcom.jasper.classload.initialization.passiveReference.SuperClass;
        }
    }
