package com.yaozu.listener.utils;

/**
 * Created by jieyz on 2016/2/15.
 */

import com.yaozu.listener.db.model.Person;

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
        if (cmp.compare(o1.getName(), o2.getName()) > 0) {
            return 1;
        } else if (cmp.compare(o1.getName(), o2.getName()) < 0) {
            return -1;
        }
        return 0;
    }
}
