package io.kimmking.java8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ForeachDemo {
    
    private int x=1;
    
    public static void main(String[] args) {
    
        ForeachDemo demo = new ForeachDemo();
        
        demo.test();
    
        System.out.println(demo.x);
    }
    
    private void test() {
        List list = new ArrayList();
        list.add(1);
        int y = 1;
        //流是独一份数据
        list.forEach(e -> {
            e=2;
            System.out.println("foreach作用域内"+list.get(0));
            x=2;
//            y=2;
            // can't be compiled
        });
        System.out.println("foreach作用域外"+list.get(0));
    }
    
}
