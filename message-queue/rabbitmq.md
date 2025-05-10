# RabbitMQ

RabbitMQ

## Clarification

- In the rabbitMQ universe, an exchange is like a post office, a queue is like the physical location, and the routing key is the address of that location

- When we created the queue and didn’t bound with any exchange, the default exchange automatically got bound with it and took the routing key as the queue’s name

- A producer never directly sends messages to a queue. Instead, the producer sends messages to an exchange, and the exchange is responsible for routing those messages to queues, based on bindings and optionally routing keys

- Types of exchanges:

  - Direct Exchange: Routes messages to queues with an exact matching routing key.

  - Fanout Exchange: Broadcasts messages to all bound queues, ignoring routing keys.

  - Topic Exchange: Routes based on wildcard pattern matching of the routing key.

  - Headers Exchange: Routes based on message headers instead of the routing key.

## Comparition with Kafka

- Kafka handles real-time data streams at scale and is designed as a streaming platform, while RabbitMQ is designed as a traditional message broker for messaging between applications

- For use cases that require low latency and high throughput, Kafka is a better choice, whereas for use cases where reliability and message durability are critical, RabbitMQ is a better choice.

