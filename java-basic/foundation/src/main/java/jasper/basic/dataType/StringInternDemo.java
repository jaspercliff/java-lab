package jasper.basic.dataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StringInternDemo {
    public static void main(String[] args) {
        // 模拟 30 个固定的城市名
        String[] cities = {"北京市", "上海市", "广州市", "深圳市", "杭州市", /* ...省略其余25个 */ "成都市"};
        List<String> resultList = new ArrayList<>();

        int count = 1000000; // 100 万行数据
        Random random = new Random();

        System.out.println("开始加载数据...");

        for (int i = 0; i < count; i++) {
            // 模拟从数据库/文件读取：每次产生的都是一个“新”字符串对象
            // 虽然内容相同，但在堆中的地址不同
            // 这里不知道上一个城市是什么 没有cities 数组 是查询出来的
            String rawCity = new String(cities[random.nextInt(cities.length)]);

            // --- 关键对比点 ---
            // 方案 A：直接添加（不使用 intern）
            // resultList.add(rawCity);

            // 方案 B：使用 intern（大获全胜）
            resultList.add(rawCity.intern());
            // 堆内存里只有 30 个 String 对象 被 resultList 引用。剩下的 999,970 个临时产生的 rawCity 对象因为没有被引用，会被
            // GC 迅速回收
        }
    }
}
