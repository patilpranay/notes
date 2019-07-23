# Helpful Java Notes

### A good image showing the Collections hierarchy
![Collections hierarchy](Collections.jpg "Collections hierarchy")
[Source][https://stackoverflow.com/questions/3317381/what-is-the-difference-between-collection-and-list-in-java]

### How to dynamically determine the component type of an array
```
Class ofArray = o.getClass().getComponentType();
```
[Source][https://stackoverflow.com/questions/212805/in-java-how-do-i-dynamically-determine-the-type-of-an-array]

### An explanation of what the compiler does with checked exceptions (versus unchecked exceptions)
[Stack Overflow][https://stackoverflow.com/questions/32991140/why-is-throws-part-of-the-method-signature]

### Do I need to declare thrown RuntimeExceptions in the method signature?
You don't because RuntimeExceptions (and any subclasses) are unchecked exceptions.
However, it is a good idea to declare them anyways since it makes reading APIs
and accompanying documentation easier to read.

[Source][https://stackoverflow.com/questions/22996407/java-throws-runtimeexception]

### What is DAO?
DAO refers to the Data Access Object pattern. This pattern allows us to isolate
the application/business layer from the persistence layer (usually a relational
database).

[Source][https://www.baeldung.com/java-dao-pattern]
