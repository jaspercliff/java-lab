package jasper.basic.io.BIO;

import java.io.*;
import java.util.Properties;

/**
 * 底层持久化属于 BIO
 */
public class PropertiesDemo {
    public static void main(String[] args) throws FileNotFoundException {
        final Properties properties = new Properties();
        final File file = new File("/Users/jasper/IdeaProjects/person/javaLearn/javaBasic/src/main/java/com/jasper/io/test.properties");
        try( FileInputStream fileInputStream = new FileInputStream(file);) {
//            用于从输入流中加载属性配置
            properties.load(fileInputStream);
            final String property = properties.getProperty("name");
            System.out.println("property = " + property);

            for (final String key : properties.stringPropertyNames()) {
                final String property1 = properties.getProperty(key);
                System.out.println("property1 = " + property1);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
