package jasper.basic.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射
 * jdbc  annotation dynamic proxy
 * @see com.jasper.jdbc.StatementExample
 */
public class ReflectDemo {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        //Class.forName Person.class  person.getClass() 获取反射类
        Class<?> clazz = Class.forName("com.jasper.Person");
        String name1 = clazz.getName();// com.jasper.Person
        System.out.println("name1 = " + name1);
        Constructor<?> constructor = clazz.getConstructor();
        Object o = constructor.newInstance();
        Method setName = clazz.getMethod("setName", String.class);
        setName.invoke(o, "com/jasper");
        Method getName = clazz.getMethod("getName");
        System.out.println(getName.invoke(o));
        System.out.println(o);
//        获取字段 私有字段使用declare获取 同时setAccessible
        Field name = clazz.getDeclaredField("name");
        name.setAccessible(true);
        Object o1 = name.get(o);
        System.out.println("o1 = " + o1);
    }
}
//jasper
//Person(id=0, name=jasper, age=0)
