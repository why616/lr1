package com.example.lr1.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SetToStringArray {
    public static String[] change(Set set){
        List<String> myList = new ArrayList<String>();
        String[] strings;
        int i = 0;
        for (Object o : set) {
            if (!o.equals(null)){
                myList.add((String)o);
                i++;
            }
        }
        strings = myList.toArray(new String[myList.size()]);
        return strings;
    }
}
