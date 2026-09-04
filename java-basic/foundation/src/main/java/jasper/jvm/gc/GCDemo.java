package jasper.jvm.gc;

public class GCDemo {
    public static void main(String[] args) throws InterruptedException {
        // 第一步：创建两个互相引用的对象
        Node a = new Node("对象A");
        Node b = new Node("对象B");
        a.next = b;
        b.next = a;

        // 第二步：此时 a 和 b 都在栈帧中，是 GC Roots，它们是“活”的
        System.out.println("运行中，此时对象是可达的...");

        // 第三步：切断外部引用
        a = null;
        b = null;

        // 此时，虽然对象A和对象B依然互相引用，但它们到 GC Roots 的路径断了
        // 由于从栈、静态变量、JNI 指针等任何一个根节点出发都找不到了，它们就被标记为不可达
        System.gc(); // 提示 JVM 进行垃圾回收
        Thread.sleep(1000);
    }
}

class Node {
    String name;
    Node next;
    byte[] memory = new byte[1024 * 1024 * 10]; // 占用10MB内存方便观察

    Node(String name) {
        this.name = name;
    }
}
