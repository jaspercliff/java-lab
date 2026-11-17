package com.jasper.xhgen;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/** 命名 / 转义工具：把 OpenAPI 里的各种名字变成安全的文件名、函数名、shell 字面量。 */
final class Names {

    private static final Pattern NON_SHELL = Pattern.compile("[^A-Za-z0-9_]");
    private static final Pattern NON_DIR = Pattern.compile("[\\\\/:*?\"<>|\\s]+");
    private static final Set<String> SHELL_RESERVED = Set.of(
            "if", "then", "else", "elif", "fi", "for", "while", "until", "do", "done",
            "case", "esac", "function", "select", "in", "local", "return", "exit", "echo",
            "read", "set", "shift", "declare", "typeset", "export", "source", "alias",
            "eval", "exec", "true", "false", "test", "time", "trap", "unset", "wait"
    );

    private Names() {
    }

    /** 目录名：去掉文件系统非法字符，空白折叠成 '-'，保留中文。 */
    static String dir(String raw) {
        String s = NON_DIR.matcher(raw == null ? "" : raw.trim()).replaceAll("-");
        s = s.replaceAll("^-+|-+$", "");
        if (s.isEmpty()) {
            return "default";
        }
        return s;
    }

    /** 脚本文件名中的 operationId 片段。 */
    static String fileStem(String raw) {
        String s = raw == null ? "" : raw.trim();
        s = s.replaceAll("[^A-Za-z0-9_\\-\\u4e00-\\u9fff]+", "-");
        s = s.replaceAll("^-+|-+$", "");
        return s.isEmpty() ? "operation" : s;
    }

    /** bash 函数名 / 变量名：首字母不能是数字，避开关键字，组内去重。 */
    static String shellIdentifier(String raw, Set<String> used) {
        String s = NON_SHELL.matcher(raw == null ? "" : raw).replaceAll("_");
        s = s.replaceAll("^_+", "");
        if (s.isEmpty()) {
            s = "op";
        }
        if (Character.isDigit(s.charAt(0))) {
            s = "op_" + s;
        }
        String base = s;
        String candidate = base;
        int i = 2;
        while (used.contains(candidate) || SHELL_RESERVED.contains(candidate.toLowerCase(Locale.ROOT))) {
            candidate = base + "_" + i++;
        }
        used.add(candidate);
        return candidate;
    }

    /** shell 单引号字面量（含空格 / 引号 / $ 都安全）。 */
    static String shq(String value) {
        String v = value == null ? "" : value;
        return "'" + v.replace("'", "'\\''") + "'";
    }

    /**
     * 双引号字面量，但保留 {@code ${var}} 供 shell 展开。
     * 用于 xh 请求项与 URL —— 这些字符串里既有参数插值，又可能含空格。
     */
    static String dqKeepVars(String value) {
        String v = value == null ? "" : value;
        return "\"" + v.replace("\\", "\\\\").replace("\"", "\\\"").replace("`", "\\`") + "\"";
    }

    /** 直接包双引号：用于已经按双引号内规则转义好的字符串，不再二次处理反斜杠。 */
    static String dqReady(String value) {
        return "\"" + (value == null ? "" : value) + "\"";
    }

    /** 双引号内的纯字面量：所有会破坏 shell 语法的字符都转义（与 dqKeepVars 不同，不保留变量）。 */
    static String dqContent(String value) {
        String v = value == null ? "" : value;
        return v.replace("\\", "\\\\").replace("\"", "\\\"").replace("`", "\\`").replace("$", "\\$");
    }

    /** 注释文本：去掉换行、压掉多余的 '#' 前缀，避免破坏脚本注释行。 */
    static String comment(String text) {
        String s = text == null ? "" : text;
        return s.replaceAll("[\\r\\n]+", " ").replace("#", "＃").trim();
    }

    /** 一组名字按顺序去重（保留首次出现顺序）。 */
    static Set<String> orderedSet() {
        return new LinkedHashSet<>();
    }
}
