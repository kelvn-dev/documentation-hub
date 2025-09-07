/**
 * Factory Classes
 */
interface ProductFactory {
    Product createProduct();
}

class ProductAFactory implements ProductFactory{
    @Override
    public Product createProduct() {
        return new ProductA();
    }
}

class ProductBFactory implements ProductFactory{
    @Override
    public Product createProduct() {
        return new ProductB();
    }
}

/**
 * Product Classes
 */
interface Product {
    String getName();
}

class ProductA implements Product {

    @Override
    public String getName() {
        return "Product A";
    }
}

class ProductB implements Product {

    @Override
    public String getName() {
        return "Product B";
    }
}

/**
 * Abstract Factory is a creational design pattern that provides a solution to create groups of related objects without specifying their concrete classes
 * 
 * Usecase: Needs to deal with different groups of related items. Types might not be known in advance or want to keep room for adding more types in the future
 */
public class AbstractFactory {
    public static void main(String[] args) {
        // Create Product A using ProductAFactory
        ProductFactory productAFactory = new ProductAFactory();
        Product productA = productAFactory.createProduct();
        System.out.println("Product A: " + productA.getName());

        // Create Product B using ProductBFactory
        ProductFactory productBFactory = new ProductBFactory();
        Product productB = productBFactory.createProduct();
        System.out.println("Product B: " + productB.getName());
    }
}