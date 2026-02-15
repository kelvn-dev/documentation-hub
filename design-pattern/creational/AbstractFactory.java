/**
 * Abstract Factory
 */
interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
}

/**
 * Concrete Factories
 */
class WindowsFactory implements GUIFactory {
    public Button createButton() {
        return new WindowsButton();
    }
    public Checkbox createCheckbox() {
        return new WindowsCheckbox();
    }
}

/**
 * Interfaces
 */
interface Button {}
interface Checkbox {}

/**
 * Concrete implementations
 */
class WindowsButton implements Button {}
class WindowsCheckbox implements Checkbox {}

class MacButton implements Button {}
class MacCheckbox implements Checkbox {}


/**
 * Abstract Factory is a creational design pattern that provides a solution to create groups of related objects without specifying their concrete classes
 * 
 * Usecase: Needs to deal with different groups of related items. Types might not be known in advance or want to keep room for adding more types in the future
 */
public class AbstractFactory {
    public static void main(String[] args) {
        GUIFactory windowsFactory = new WindowsFactory();
        Button windowsButton = windowsFactory.createButton();
        Checkbox windowsCheckbox = windowsFactory.createCheckbox();
    }
}