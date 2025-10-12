# Kafka

Kafka

## Clarification

- Imagine a library (Kafka topic) that stores books (messages). The library has several shelves (partitions), and each book needs to be placed on a shelf. Every message in a partition is assigned a unique ID known as an offset.

- Kafka guarantees ordering within a partition, but not across partitions. If message order matters, must ensure those messages go to the same partition by using a message key

- Offset is an index pointing to the latest consumed message, used to keep a track of which messages have already been consumed. So if a consumer were to go down, this offset value would help us know exactly from where the consumer has to start consuming events

- A topic will default have 1 partition and a consumer must under 1 consumer group

- If two consumers have subscribed to the same topic and are present in the same consumer group, then these two consumers would be assigned a different set of partitions and none of these two consumers would receive the same messages.

![](images/kafka.webp)

## List of common Q&A’s

- https://medium.com/nerd-for-tech/a-basic-introduction-to-kafka-a7d10a7776e6

- https://medium.com/javarevisited/kafka-partitions-and-consumer-groups-in-6-mins-9e0e336c6c00