# Design pattern

Decision tree

![](./images/decision-tree.webp)

## Creational patterns

Are you choosing implementations based on context? 
- Factory
- Abstract factory

Comparision: Both used to create objects without specifying concrete class. Factory pattern creates a single object while abstract factory creates a family of related objects to ensure compatible groups of objects are created together

## Behavioral patterns

If-else base on type violate open/close principal. Replace it with strategy pattern with factory or enum with behaviors

Enum with behaviors: For simple types, declare abstract methods in an Enum and have each enum constant implement its own logic

## Structural patterns

DTO is just a simple object that carry data and DTO pattern is a structural design pattern used to transfer data between layers to decouple internal model, hide sensitive fields, ...