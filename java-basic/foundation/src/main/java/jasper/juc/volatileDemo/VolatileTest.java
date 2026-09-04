package jasper.juc.volatileDemo;

public class VolatileTest {

    int i = 0;
    volatile boolean flag = false;

    public void write() {
        i = 1;
        //storestore 禁止上面的普通写和下面的volatile写重排序
        flag = true;
        //storeload  可以禁止下面的可能的volatile 读/写与上面的写重排序
    }

    public void read() {
        if (flag) {
            //loadload   禁止下面的普通读和上面的volatile读重排序
            //loadstore  禁止下面的普通写和volatile读重排序
            System.out.println("i=" + i);
        }
    }

}