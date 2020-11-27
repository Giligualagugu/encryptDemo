package com.kele.enc.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DemoTest {
    public static void main(String[] args) {


        List<String> list1 = Arrays.asList("a", "b", "c");

        List<String> a=new ArrayList<>(list1);

        List<String> list2 = Arrays.asList("b", "c", "d");

        List<String> b=new ArrayList<>(list2);

        a.removeAll(b);

        System.out.println(a);
    }
}
