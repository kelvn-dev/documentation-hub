@Builder
@Getter
@Setter
class Beer {

    //required
    private String name;
    private double drinkSize;
    private double alcoholPercentage;
    private double price;

}

/**
 * Builder is a creational design pattern that lets you construct complex objects step by step
 * pros: 
 * - Reduces the number of parameters in the constructor
 * - provides readable method calls
 * cons:
 * - increases the number of lines of code
 */
public class Builder {
    public static void main(String[] args) {
        Beer beer = Beer.builder()
                .name("Standard Beer")
                .drinkSize(500)
                .alcoholPercentage(5.0)
                .price(5.99)
                .build();
    }
}
