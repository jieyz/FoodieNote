package com.yaozu.listener.utils;

/**
 * Created by jieyz on 2016/2/15.
 */

import com.yaozu.listener.db.model.Person;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.text.Collator;
import java.util.Comparator;

/**
 * @Title： SortChineseName.java
 * @Description: 中文字符排序
 * @Function: 中文字符排序
 * @Copyright: Copyright (c) 2016/2/15.
 * @Author :        jieyz
 * @Version 0.1
 */
public class SortChineseName implements Comparator<Person> {
    Collator cmp = Collator.getInstance(java.util.Locale.CHINA);

    @Override
    public int compare(Person o1, Person o2) {
        String str1 = o1.getName().toLowerCase();
        String str2 = o2.getName().toLowerCase();
        for (int i = 0; i < str1.length() && i < str2.length(); i++) {
            int codePoint1 = str1.charAt(i);
            int codePoint2 = str2.charAt(i);
            if (Character.isSupplementaryCodePoint(codePoint1)
                    || Character.isSupplementaryCodePoint(codePoint2)) {
                i++;
            }
            if (codePoint1 != codePoint2) {
                if (Character.isSupplementaryCodePoint(codePoint1)
                        || Character.isSupplementaryCodePoint(codePoint2)) {
                    return codePoint1 - codePoint2;
                }
                String pinyin1 = pinyin((char) codePoint1);
                String pinyin2 = pinyin((char) codePoint2);
                if (pinyin1 != null && pinyin2 != null) { // 两个字符都是汉字
                    if (!pinyin1.equals(pinyin2)) {
                        return pinyin1.compareTo(pinyin2);
                    }
                } else {
                    return codePoint1 - codePoint2;
                }
            }
        }
        return str1.length() - str2.length();
    }

    /**
     * * 字符的拼音，多音字就得到第一个拼音。不是汉字，就return null。
     */
    private static String pinyin(char c) {
        String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c);
        if (pinyins == null) {
            return c+"";
        }
        return pinyins[0];
    }

}
