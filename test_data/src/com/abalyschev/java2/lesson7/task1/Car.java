package com.abalyschev.java2.lesson7.task1;


//import javax.annotation.PostConstruct;



public class Car {


    private Engine engine;

    private Gear gear;


    public Car() {
    }

    public void init() {
        System.out.println("Post Instance");
    }

    @Override
    public String toString() {
        return "Car{" +
                "engine=" + engine +
                ", gear=" + gear +
                '}';
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Gear getGear() {
        return gear;
    }

    public void setGear(Gear gear) {
        this.gear = gear;
    }
}
