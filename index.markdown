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

## In the Java code

Annotate any attribute that should be considered in the search with `@Filterable`.

## Class Diagram (Example)
<img class="image" src="{{ site.baseurl }}/assets/class-diagram.png">

## Filtering Collections - Examples

For examples of using the Introspector Filter, consider the class diagram above.

The tool (and the respective examples not using it) uses the 
[Apache Commons Lang](https://commons.apache.org/proper/commons-lang/){:target="_blank"}
`StringUtils` class to removes diacritics (~= accents) from strings. 

Assumptions:
- A `List<Post> postsCollection` is provided to be filtered.
- A primitive type value (`textFilter`) to be used as a filter is provided.
- An instance of `IntrospectorFilter` is provided.

### Filtering by text in Publication 

**Without** Introspector Filter
```java
var filteredList = postsCollection.stream()
    .filter(p -> Objects.isNull(p.getText()) ||
        StringUtils.stripAccents(p.getText().trim().toLowerCase())
            .contains(textFilter.trim().toLowerCase()))
    .toList();
```       

**With** Introspector Filter
```java
var filteredList = postsCollection.stream()
    .filter(p -> filter.filter(p, textFilter)).toList();
```

### Filtering by hashtags in Post 

**Without** Introspector Filter
```java
var filteredList = postsCollection.stream()
    .filter(p -> Objects.isNull(p.getHashtags()) || p.getHashtags().isEmpty() ||
        p.getHashtags().stream().anyMatch(h ->
            StringUtils.stripAccents(h.trim().toLowerCase())
                .contains(textFilter.trim().toLowerCase())))
    .toList();
```       

**With** Introspector Filter
```java
var filteredList = postsCollection.stream()
    .filter(p -> filter.filter(p, textFilter)).toList();
```

### Filtering by review in Comment

**Without** Introspector Filter
```java
var filteredList = postsCollection.stream()
    .filter(p -> Objects.isNull(p.getComments()) || p.getComments().isEmpty() ||
        p.getComments().stream().anyMatch(c ->
            StringUtils.stripAccents(c.trim().toLowerCase())
                .contains(textFilter.trim().toLowerCase())))
    .toList();
```       

**With** Introspector Filter
```java
var filteredList = postsCollection.stream()
    .filter(p -> filter.filter(p, textFilter)).toList();
```

### Filtering by relevance in Publication

**Without** Introspector Filter
```java
var filteredList = postsCollection.stream()
    .filter(p -> p.getRelevance() == textFilter)
    .toList();
```       

**With** Introspector Filter
```java
var filteredList = postsCollection.stream()
    .filter(p -> filter.filter(p, textFilter)).toList();
```

## Javadoc available at <a href="http://tnas.github.io/introspectorfilter/apidocs/index.html" target="_blank">Introspector Filter Javadoc</a>