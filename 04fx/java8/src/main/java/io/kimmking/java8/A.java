package io.kimmking.java8;

import com.google.common.eventbus.Subscribe;
import lombok.Data;

@Data
public class A {
    
    private int age;

    @Subscribe
    public void handle(GuavaDemo.AEvent ae){
        System.out.println(this.getClass().toString() + ",msg:" + ae + " is running.");
    }
    
}
