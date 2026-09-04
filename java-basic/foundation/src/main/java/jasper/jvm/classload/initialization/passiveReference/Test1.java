package jasper.jvm.classload.initialization.passiveReference;

/**
 * 通过子类引用父类中的静态变量，不会导致子类初始化
 * vm : -Xlog:class+load=info
 * <br/>
 *[0.016s][info][class,load] com.jasper.classload.initialization.passiveReference.SuperClass
 * source: file:/Users/jasper/code/java/person/learnJava/basic/jvm/build/classes/java/main/
 * [0.016s][info][class,load] com.jasper.classload.initialization.passiveReference.SubClass
 * source: file:/Users/jasper/code/java/person/learnJava/basic/jvm/build/classes/java/main/
 * <br/>
 * source: shared objects file。
 * 这意味着什么：这说明这些类是从 CDS (Class Data Sharing) 归档文件中加载的，而不是直接从普通的 .jar 或 .class 文件中读取。
 * 好处：CDS 是 JVM 的一项优化技术。它将这些常用的核心类预先处理并保存在一个内存映射文件中。
 * 这样 JVM 启动时可以直接“映射”这些类，而不需要重新解析，大大加快了 Java 的启动速度并减少了内存占用。
 */
public class Test1 {
    public static void main(String[] args) {
        // 输出：SuperClass init! 
        // 注意：并没有输出 SubClass init!
        System.out.println(SubClass.value); 
    }
}