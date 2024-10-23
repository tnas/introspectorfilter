---
layout: default
---

# Introspector Filter

This library provides an effortless way to filter collections in Java based on attributes annotation.

## Usage

In the **pom.xml** file, add the following dependency:

```xml
<dependency>
    <groupId>io.github.tnas</groupId>
    <artifactId>introspectorfilter</artifactId>
    <version>1.0.0</version>
</dependency>
```
## Class Diagram (Example)
<img class="image" src="{{ site.baseurl }}/assets/class-diagram.png">

## Filtering Collections

```java
// Javascript code with syntax highlighting.
List<Post> postsCollection; // Populate the list
var IntrospectorFilter filter = IntrospectorFilter();
var filteredList = postsCollection.stream()
                      .filter(p -> filter.filter(p, "vitae")).toList();
```

## Javadoc available at <a href="http://tnas.github.io/introspectorfilter/apidocs/index.html" target="_blank">Introspector Filter Javadoc</a>