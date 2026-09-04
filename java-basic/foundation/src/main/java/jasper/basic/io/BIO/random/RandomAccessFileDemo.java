package jasper.basic.io.BIO.random;

import java.io.RandomAccessFile;

public class RandomAccessFileDemo {
    public static void main(String[] args){
        try (RandomAccessFile randomAccessFile = new RandomAccessFile("/Users/jasper/IdeaProjects/person/javaLearn/javaBasic/src/main/java/com/jasper/io/00doc/source.txt",
                "rws")) {
//            read 读取一个字节的数据从当前位置
            System.out.println(" 读取之前的位置 "+ randomAccessFile.getFilePointer() +
                    " 读取的内容：" + (char)randomAccessFile.read()+ " 读取之后的位置 " + randomAccessFile.getFilePointer());
            randomAccessFile.seek(4);
            System.out.println(" 读取之前的位置 "+ randomAccessFile.getFilePointer() + " 读取的内容：" + (char)randomAccessFile.read()+
                    " 读取之后的位置 " + randomAccessFile.getFilePointer());
            randomAccessFile.write("world".getBytes());
            randomAccessFile.seek(0);
            int content;
            while ((content = randomAccessFile.read()) != -1){

                System.out.println(" 读取之前的位置 "+ randomAccessFile.getFilePointer() + " 读取的内容：" + (char)content+
                        " 读取之后的位置 " + randomAccessFile.getFilePointer());
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
