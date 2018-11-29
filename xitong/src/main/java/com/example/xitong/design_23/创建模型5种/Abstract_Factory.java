package com.example.xitong.design_23.创建模型5种;

public class Abstract_Factory {
    //工厂生成器：生产具体的工厂
    public static AbstractFactory getFactory(String factoryName) {
        if (factoryName.equalsIgnoreCase("animals"))
            return new AnimalsFactory();
        else if (factoryName.equalsIgnoreCase("others"))
            return new OthersFactory();
        else
            return null;
    }

    public static void main(String[] args) {
        //生产动物工厂
        AbstractFactory animalsFactory = getFactory("animals");
        //通过动物工厂创建一个cat对象
        Animals cat = animalsFactory.getAnimals("cat");
        cat.name();
    }
}

interface Animals { //动物
    void name();
}

class Cat implements Animals { //猫
    @Override
    public void name() {
        System.out.println("this is a cat");
    }
}

class AnimalsFactory extends AbstractFactory { //动物工厂
    public Animals getAnimals(String name) {
        if (name.equalsIgnoreCase("cat"))
            return new Cat();
        else
            return null;
    }

    @Override
    public Object getObject() {
        return null;
    }
}

class OthersFactory extends AbstractFactory { //其他工厂
    public Object getObject() {
        return null;
    }

    @Override
    public Animals getAnimals(String name) {
        return null;
    }
}

abstract class AbstractFactory { //抽象工厂
    abstract public Animals getAnimals(String name);

    abstract public Object getObject();
}