/*
 * Singleton is a creational design pattern that lets you ensure that a class has only one instance while providing a global access point to this instance
 * 
 * Usecase: want to ensure that only one instance of a class exists, for example, a single database object
 */
public class Singleton {

    private static Singleton instance;

    private Singleton() {
        // private constructor to prevent instantiation from outside
    }

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
