package com.yaozu.listener;

import com.yaozu.listener.db.model.Person;
import com.yaozu.listener.utils.SortChineseName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jieyz on 2016/2/15.
 */
public class TestBean {
    public static void main(String args[]) {
        List<Person> list = new ArrayList<Person>();
        Person p = new Person();
        p.setName("张三");
        list.add(p);

        p = new Person();
        p.setName("李四");
        list.add(p);

        p = new Person();
        p.setName("王五");
        list.add(p);

        p = new Person();
        p.setName("赵六");
        list.add(p);

        p = new Person();
        p.setName("a赵六");
        list.add(p);

        p = new Person();
        p.setName("A");
        list.add(p);

        p = new Person();
        p.setName("B");
        list.add(p);

        p = new Person();
        p.setName("x");
        list.add(p);

        p = new Person();
        p.setName("1");
        list.add(p);

        p = new Person();
        p.setName("13");
        list.add(p);

        p = new Person();
        p.setName("10");
        list.add(p);

        p = new Person();
        p.setName("cwe");
        list.add(p);

        p = new Person();
        p.setName("c");
        list.add(p);

        p = new Person();
        p.setName("Y");
        list.add(p);
        //正序
        Collections.sort(list, new SortChineseName());
        System.out.println("中文名称正序排列：");
        for (Person pp:list){
            System.out.println(pp.getName());
        }
    }
}
