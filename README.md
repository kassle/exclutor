# Exclutor

Implementation of exclusive-able asynchronous process

example case:
we need multiple asynchronous process to read data from database,
but only one asynchronouse process for write data to database
and the next read process will on hold till the write process finish.

# Status

![](https://github.com/kassle/exclutor/workflows/Build/badge.svg)

## Usage:

```java
String scope = "db.table.users";
Executor executor = ExclusiveExecutorFactory.create(Runtime.getRuntime().availableProcessors());
executor.execute(new AbstractExclusiveRunnable(scope, true) {
    @Override
    public void run() {
        // insert to database
    }
});
executor.execute(new AbstractExclusiveRunnable(scope, false) {
    @Override
    public void run() {
        // select from database
    }
});
```