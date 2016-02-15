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
        p.setId(1+"");
        p.setName("张三");
        list.add(p);
        p = new Person();
        p.setId(2 + "");
        p.setName("李四");
        list.add(p);
        p = new Person();
        p.setId(3 + "");
        p.setName("王五");
        list.add(p);
        p = new Person();
        p.setId(4 + "");
        p.setName("赵六");
        list.add(p);
        p = new Person();
        p.setId(5 + "");
        p.setName("a赵六");
        list.add(p);

        p = new Person();
        p.setId(5 + "");
        p.setName("A");
        list.add(p);

        p = new Person();
        p.setId(5 + "");
        p.setName("B");
        list.add(p);

        p = new Person();
        p.setId(5 + "");
        p.setName("x");
        list.add(p);
        //正序
        Collections.sort(list, new SortChineseName());
        System.out.println("中文名称正序排列：");
        for (Person pp:list){
            System.out.println(pp.getId()+","+pp.getName());
        }
    }
}
