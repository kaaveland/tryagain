tryagain
=======

Micro-library to take away the tedious task of writing counting loops
for retrying actions that might fail because of unstable networks or end
systems. This is error-prone code that often has to be added in a rush.

Using tryagain, it looks like this:
```java
String result = TryAgain.on(RuntimeException.class)
    .maxAttempts(2).execute(new Retriable<String>() {
    @Override
    public String execute(final int attempt) throws Exception {
        if (attempt == 1) {
            throw new RuntimeException();
        }
        return "Result";
    }
});
```

The library throws `Exception` so that `Retriab` doesn't have to rethrow
checked exceptions as something else. `on()` accepts multiple
exceptions:

```java
TryAgain.on(HttpResponseException.class, SocketTimeoutException.class)
    .maxAttempts(10)
    .withDelay(1000)
    .execute(saveDocument);
```

To invoke a `void` method, implement `RetriableWithoutResult` instead of
Retriable.

TryAgain.on() vs TryAgain.onInstanceOf()
----

`TryAgain.on` matches *exactly* on the class of the exception, it does not
use an instance-check and will never match on subclasses. If you need to
retry *all* subclasses of `IOException`, you need to use
`TryAgain.onInstanceOf`.


Avoiding checked exceptions
-----

In order to avoid having to declare `throws Exception` everywhere, it is
possible to configure exceptions to be wrapped in `WrappedException`,
which is a `RuntimeException`:

```java
TryAgain.on(IOException.class)
    .maxAttempts(2).wrapExceptions().execute(throwsIOException);
```

Or if you don't want to have to worry about breaking exception-handling
further up the callstack, you can bypass checked exceptions entirely, so
thrown exceptions are rethrown with no wrapping:

```java
TryAgain.on(IOException.class)
    .maxAttempts(2).bypassExceptionChecking().execute(throwsIOException);
```

This allows you to surgically insert retries into code without knowing
too much about the code that is calling down to you.

Use the last one responsibly, it'll be able to throw exceptions that it
does not declare. If you try to catch these, javac will helpfully tell
you that you have dead code. In order to convince javac that the code
isn't dead, you can use `.declare(IOException.class)` on the object
returned by `bypassExceptionChecking()`:

```java
TryAgain.on(IOException.class, RuntimeException.class)
    .maxAttempts(10).bypassExceptionChecking().declare(IOException.class)
    .execute(operation);
```

Adding delays between retries
----

Sometimes it is useful to have delays between attempts to invoke
services over the network. `.withDelay(millis)` will add static a delay between
retries and `.exponentialBackoff(millis)` will use exponential backoff
to calculate delays.

Other considerations
-----

All classes exposed through the library are immutable and should be
threadsafe. Retriers can be new()ed safely and shared between objects
that should have the same settings. Or they can be created using the
factory-methods on Retrier.
