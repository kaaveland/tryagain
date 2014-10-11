tryagain
=======

Micro-library to take away the tedious task of writing counting loops
for retrying actions that might fail because of unstable networks or end
systems. This is error-prone code that should always have tests -- by
using a library, it becomes much simpler to make code behave more
robustly.

The basic use looks like this:
```java
on(RuntimeException.class).maxAttempts(2).execute(new Retriable<String>() {
    @Override
    public String execute(final int attempt) throws Exception {
        if (attempt == 1) {
            throw new RuntimeException();
        }
        return "Result";
    }
});
```

Because the library must catch Exception, it also has to declare to
throw Exception even if it is possible that it will not happen.

To invoke a `void` method, implement `RetriableWithoutResult` instead of
Retriable.

Avoiding checked exceptions
-----

In order to avoid having to declare `throws Exception` everywhere, it is
possible to configure exceptions to be wrapped in WrappedException,
which is a RuntimeException:

```java
on(RuntimeException.class).maxAttempts(2).wrapExceptions().execute(new Retriable<String>() {
    @Override
    public String execute(final int attempt) throws Exception {
        if (attempt == 1) {
            throw new RuntimeException();
        }
        return "Result";
    }
```

Or if you don't want to have to worry about exception-handling further
up the callstack, you can bypass checked exceptions entirely, so now
thrown exceptions are wrapped:
```java
on(RuntimeException.class).maxAttempts(2).bypassExceptionChecking().execute(new Retriable<String>() {
    @Override
    public String execute(final int attempt) throws Exception {
        if (attempt == 1) {
            throw new RuntimeException();
        }
        return "Result";
    }
```

Use the last one responsibly, it'll be able to throw exceptions that it
does not declare. If you try to catch these, javac will helpfully tell
you that you have dead code. In order to convince javac that the code
isn't dead, you can use `.declare(IOException.class)` on the object
returned by `bypassExceptionChecking':

```java
on(IOException.class).maxAttempts(10).bypassExceptionChecking().declare(IOException.class).execute(operation);
```

Retrier.on() vs Retrier.onInstanceOf()
----

Retrier.on matches *exactly* on the class of the exception, it does not
use an instance-check and will never match on subclasses.

Adding delays between retries
----

Sometimes it is useful to have delays between attempts to invoke
services over the network. `.withDelay(millis)` will add static a delay between
retries and `.exponentialBackoff(millis)` will use exponential backoff
to calculate delays.
