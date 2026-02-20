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

- when multiple consumers subscribe to the same queue, messages are distributed among them so that each message is processed by only one consumer

![](images/rabbitmq.png)

## Comparition with Kafka

Both are message broker but designed for different usecases
Kafka is optimized for high-throughput by using partitions and consumer groups to scales horizontally. It’s ideal for event streaming and systems that require message replay.
RabbitMQ is a traditional message broker that's good for a system that require complex message routing by using different exchange types and bindings. It’s better suited for task queues and routing-heavy use cases

