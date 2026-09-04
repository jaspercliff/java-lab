package jasper.jvm.classload.loading.pluginDemo;

/**
 * 如果你引用了别人写的库（比如 JSON 解析库、数据库驱动），或者引用了你自己写的其他文件夹里的类，javac 在编译时默认是找不到这些东西的 -cp
 * javac -cp pluginDemo RiskRule.java RuleImpl.java
 * javac -d . RiskRule.java RuleImpl.java 会生成目录结构 上边不对
 * jar cf rule.jar RuleImpl.class
 * jar cf rule.jar com/jasper/classload/loading/pluginDemo/RuleImpl.class 上边不对
 * jar tf rule.jar 验证路径
 */
public class RuleImpl implements RiskRule {

    @Override
    public boolean check(int amount) {
        return amount < 600;
    }
}