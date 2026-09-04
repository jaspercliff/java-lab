package jasper.basic.dataType;

public class MemoryDemo {
    // 1. 静态变量：存储在方法区 (Method Area / Metaspace)
    static int staticVar = 10;

    // 2. 成员变量（非静态）：存储在堆 (Heap) 中对象的内部
    int instanceVar = 20;
    Integer wrappedInstanceVar = Integer.valueOf(30);

    public void display() {
        // 3. 局部变量（基本类型）：存储在栈 (Stack) 的栈帧中
        int localVar = 40; // istore 栈

        long longVar = 40L;// lstore 栈

        // 4. 局部变量（引用类型）：
        // 'refVar' 这个引用变量本身在栈中，
        // 但它指向的对象 new Integer(50) 在堆中。
        // jdk9 deprecated
        Integer refVar = new Integer(50);

        System.out.println(localVar);
        System.out.println(longVar);
        System.out.println(refVar);
    }

    public static void main(String[] args) throws InterruptedException {
        MemoryDemo demoObject = new MemoryDemo();
        demoObject.display();
        Thread.sleep(500000);
    }
}
