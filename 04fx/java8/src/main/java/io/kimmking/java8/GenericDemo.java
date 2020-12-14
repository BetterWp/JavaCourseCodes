package io.kimmking.java8;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericDemo {

    static EventBus eventBus = new EventBus("test");
    static {
        eventBus.register(new A());
    }

    public static void main(String[] args) {
        Demo demo = new Demo();
        Class clazz = demo.getClass();
        //getSuperclass()获得该类的父类
        System.out.println(clazz.getSuperclass());
        //getGenericSuperclass()获得带有泛型的父类
        //Type是 Java 编程语言中所有类型的公共高级接口。它们包括原始类型、参数化类型、数组类型、类型变量和基本类型。
        Type type = clazz.getGenericSuperclass();
        System.out.println(type);
        //ParameterizedType参数化类型，即泛型
        ParameterizedType p = (ParameterizedType) type;
        //getActualTypeArguments获取参数化类型的数组，泛型可能有多个
        Class c = (Class) p.getActualTypeArguments()[0];
        System.out.println(c);
    }
    
    public static class Person<T> {
        
    }
    
    public static class Demo extends Person<GenericDemo> {
        
    }

    @Subscribe
    public void handle(GuavaDemo.AEvent ae){
        System.out.println(this.getClass().toString() + ",msg:" + ae + " is running.");
        ae.setState(1);
        eventBus.post(ae);
    }
}
