package cn.wishhust.framework.util;


import org.apache.commons.lang3.StringUtils;

/**
 * 字符串工具类
 */
public final class StringUtil {


    /**
     * 字符串是否为空
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str != null) {
            str = str.trim();
        }
        return StringUtils.isEmpty(str);
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String[] splitString(String str, String separator) {
        return StringUtils.splitByWholeSeparator(str, separator);
    }

}
