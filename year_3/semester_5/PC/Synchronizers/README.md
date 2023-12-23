# Synchronizers

Set of synchronizers for concurrent programming.

## Information

This project contains two problem sets developed for [Concurrent Programming @ ISEL](https://www.isel.pt/en/leic/concurrent-programming).

- [Problem description (set 1)](docs/problem-description1.pdf)
- [Problem description (set 2)](docs/problem-description2.pdf)

### List of synchronizers

#### Set 1
- [MessageBox](src/main/java/pt/isel/pc/problemsets/set1/MessageBox/MessageBox.java): blocks all threads until a message is sent.
- [SemaphoreWithShutdown](src/main/java/pt/isel/pc/problemsets/set1/SemaphoreWithShutdown.java): as the name implies, is a semaphore implementation with the ability to shutdown.
- [BlockingMessageQueue](src/main/java/pt/isel/pc/problemsets/set1/BlockingMessageQueue.java): communication between threads in a FIFO manner.
- [KeyedThreadPoolExecutor](src/main/java/pt/isel/pc/problemsets/set1/KeyedThreadPoolExecutor.java): a thread pool that allows submission of tasks with a key.
#### Set 2
- [Exchanger](src/main/java/pt/isel/pc/problemsets/set2/Exchanger): 3 different implementations based on the [Java Exchanger](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Exchanger.html).
  - [MonitorExchanger](src/main/java/pt/isel/pc/problemsets/set2/Exchanger/MonitorExchanger.java): implementation based on monitors.
  - [LockExchanger](src/main/java/pt/isel/pc/problemsets/set2/Exchanger/LockFreeExchanger.java): implementation based on locks.
  - [CancellableLockFreeExchanger](src/main/java/pt/isel/pc/problemsets/set2/Exchanger/CancellableLockFreeExchanger.java): a lock-free implementation which also handles interruptions.
- [MessageBox](src/main/java/pt/isel/pc/problemsets/set2/MessageBox.java): allows a message to be consumed by one or more consumer threads.
- [MessageQueue](src/main/java/pt/isel/pc/problemsets/set2/MessageQueue.java): communication between threads (using the [Michael-Scott queue algorithm](https://www.cs.rochester.edu/~scott/papers/1996_PODC_queues.pdf))

## How to run

The following instructions must be executed in the [root directory of the project](./) (isel-academic-archive/year_3/semester_5/PC/Synchronizers).

### Requirements
- Java SDK 11

You can run the tests by using: `./gradlew test`

## Authors
- João Nunes ([joaonunatings](https://github.com/joaonunatings))