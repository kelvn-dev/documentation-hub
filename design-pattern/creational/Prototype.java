abstract class Tree implements Cloneable {
    private String type;

    public void setType(String type) {
        this.type = type;
    }

    public abstract void copy();

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

//Concrete Class-1 Pine Tree
class PineTree extends Tree {

    public PineTree() {
        setType("Pine Tree");
    }

    @Override
    public void copy() {
        //implementation
    }
}

//Concrete Class-2 Plastic Tree
class PlasticTree extends Tree {

    public PlasticTree() {
        setType("Plastic Tree");
    }

    @Override
    public void copy() {
        //implementation
    }
}

/**
 * Prototype is a creational design pattern that creates new objects by copying existing objects, rather than instantiating them from scratch
 * 
 * Usecase: useful for resource-intensive or complex object creation processes
 */
public class Prototype {
    public static void main(String[] args) {
        Tree plasticTree = new PlasticTree();
        Tree pineTree = new PineTree();
    }
}
