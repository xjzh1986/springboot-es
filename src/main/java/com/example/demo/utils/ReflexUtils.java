package com.example.demo.utils;

import com.example.demo.entity.ReflexEntity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReflexUtils {
    public static List<ReflexEntity> getEntityNameAndVal(Object obj) {
        List<ReflexEntity> list = new ArrayList<ReflexEntity>();
        //Java反射 通过DeclaredFields();
        Field[] fields = obj.getClass().getDeclaredFields();
        //循环反射接收的数组 
        for (int i = 0; i < fields.length; i++) {
            ReflexEntity reflex = new ReflexEntity();
            //将对象内的私有属性转换为公共可获取的(PS:私有的获取不到)
            fields[i].setAccessible(true);
            try {
                //输出每一个属性跟值;

                reflex.setName(fields[i].getName());
                if(fields[i].getType().toString().contains("Date")){
                    reflex.setValue(((Date)fields[i].get(obj)).getTime());
                }else{
                    reflex.setValue(fields[i].get(obj));
                }
                System.out.println(reflex.getName() + "----" + reflex.getValue());
                list.add(reflex);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return list;
    }
}
