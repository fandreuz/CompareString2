# CompareString2

[![](https://jitpack.io/v/fAndreuzzi/CompareString2.svg)](https://jitpack.io/#fAndreuzzi/CompareString2)   [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

This library is a **wrapper** of *tdebatty*'s [**java-string-similarity**](https://github.com/tdebatty/java-string-similarity). It provides many methods to perform **String** comparison with various algorithms.

## Get CompareString2

### Gradle
```gradle
dependencies {
    implementation 'com.github.fAndreuzzi:CompareString2:v1.0'
}
```

### Maven
```xml
<dependency>
  <groupId>com.github.fAndreuzzi</groupId>
  <artifactId>CompareString2</artifactId>
  <version>v1.0</version>
</dependency>
```

### JAR
You can get the last `CompareString2-*.jar` file from the `target` folder.

## How to

### Single comparison
```java
String s1 = "mystring";
String s2 = "muswrinh";
float result = Compare.compare(s1, s2, AlgMap.NormDistAlg.NGRAM);
```

However, when you need to perform many comparisons with the same algorithm, it's recommended to get an instance of that algorithm and passing it to the method compare:
```java
Algorithm ngram = AlgMap.NormDistAlg.NGRAM.buildArg();
float result = Compare.compare(s1, s2, ngram, AlgMap.NormDistAlg.NGRAM);
```

Some algorithms need/allow one or more parameters in order to be built properly. These are usually values that depends on the use cases. For instance, the algorithm `NGRAM` allows you to pass an `int` value:
```java
int n = 3;
Algorithm ngram = AlgMap.NormDistAlg.NGRAM.buildArg(n);
```
You can check the [**Javadoc**](https://fandreuzzi.github.io/CompareString2-Javadoc/) page for the algorithm [NGRAM](https://fandreuzzi.github.io/CompareString2-Javadoc/ohi/andre/comparestring2/algs/NGram.html).

### List comparisons

#### Sorting order
Every array returned by CompareString2 is sorted by **descendent** order. The order is based on the **result** got by the entry during the comparison with `s1` using the given algorithm.<br>
Note that the sorting order isn't always the same. There are some cases where the result `0` means that the strings are totally **different** (*distance* algorithms), while in other cases the result `0` means **equals** (*similarity* algorithms).<br>
If you want to get information about this topic, check [this](https://github.com/tdebatty/java-string-similarity#normalized-metric-similarity-and-distance) section of the GitHub page of [java-string-similarity](https://github.com/tdebatty/java-string-similarity).

```java
String s1 = "wahssapp";
String[] ss = new String[] {"Facebook", "Instagram", "Snapchat", "Twitter", "WhatsApp", "Reddit"};
```

#### Best match
```java
String bM = Compare.bestMatch(s1, ss, AlgMap.NormSimAlg.JAROWRINKLER);
```

#### Top n matches
This method returns an array of `min(n, ss.length)` elements.
```java
String[] topN = Compare.topNmatches(s1, ss, AlgMap.NormSimAlg.JACCARD, 4);
// '4' is an optional argument of the algorithm "Jaccard"
```

<br>

Let's redefine `s1`and `ss`. You will notice that, while `s1` **needs** to be a `String` object, `ss` can be **any** `Iterable<? extends Object>`. We use the method `toString()` to get a comparable `String`.

```java
String s1 = "jonn";
List<Contact> ss = Arrays.asList(new Contact[] {new Contact("John", "Doe"),
    new Contact("Mario", "Rossi"), new Contact("Santa", "Claus")});

.
.
.

class Contact {
  .
  .
  .
  public String toString() {
    return name + " " + surname;
  }
}
```



## Distance

**Algorithm** | **ID**
--- | ---
LCS | 0
OSA | 1
QGRAM | 2

## Normalized distance

**Algorithm** | **ID**
--- | ---
COSINE | 4
JACCARD | 5
JAROWRINKLER | 6
METRICLCS | 7
NGRAM | 8
NLEVENSHTEIN | 9
SORENSENDICE | 10

## Normalized similarity

**Algorithm** | **ID**
--- | ---
COSINE | 11
JACCARD | 12
JAROWRINKLER | 13
NLEVENSHTEIN | 14
SORENSENDICE | 15

## Metric distance

**Algorithm** | **ID**
--- | ---
DAMERAU | 16
JACCARD | 17
LEVENSHTEIN | 18
METRICLCS | 19

# Deadline

**Algorithm Type** | **Equal** | **Different**
--- | --- | ---
Distance | 0 | +Infinity
Normalized distance | 0 | 1
Normalized similarity | 1 | 0
Metric distance | 0 | +Infinity

Please refer to [tdebatty/java-string-similarity](https://github.com/tdebatty/java-string-similarity) for a deatiled description of every **algorithm**.
