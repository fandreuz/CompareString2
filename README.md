# CompareString2

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/it.andreuzzi/CompareString2/badge.svg)](https://maven-badges.herokuapp.com/maven-central/it.andreuzzi/CompareString2) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) [![Javadocs](http://javadoc.io/badge/it.andreuzzi/CompareString2.svg?color=red)](http://javadoc.io/doc/it.andreuzzi/CompareString2) ![](https://img.shields.io/github/languages/code-size/fAndreuzzi/CompareString2.svg?style=flat)

This library is a **wrapper** of *tdebatty*'s [**java-string-similarity**](https://github.com/tdebatty/java-string-similarity). It provides many methods to perform **String** comparison with various algorithms.

## Get CompareString2

### Gradle
```gradle
dependencies {
    implementation 'it.andreuzzi:CompareString2:1.0.8'
}
```

### Maven
```xml
<dependency>
  <groupId>it.andreuzzi</groupId>
  <artifactId>CompareString2</artifactId>
  <version>1.0.8</version>
</dependency>
```

## How to

### Single comparison
```java
String s1 = "mystring";
String s2 = "muswrinh";
float result = Utils.compare(s1, s2, AlgMap.NormDistAlg.NGRAM);
```

However, when you need to perform many comparisons with the same algorithm, it's recommended to get an instance of that algorithm and passing it to the method compare:
```java
Algorithm ngram = AlgMap.NormDistAlg.NGRAM.buildArg();
float result = Utils.compare(s1, s2, ngram, AlgMap.NormDistAlg.NGRAM);
```

Some algorithms need/allow one or more parameters in order to be built properly. These are usually values that depends on the use cases. For instance, the algorithm `NGRAM` allows you to pass an `int` value:
```java
int n = 3;
Algorithm ngram = AlgMap.NormDistAlg.NGRAM.buildArg(n);
```
You can check the [**Javadoc**](https://fandreuzzi.github.io/CompareString2-Javadoc/) page for the algorithm [NGRAM](https://fandreuzzi.github.io/CompareString2-Javadoc/ohi/andre/comparestring2/algs/NGram.html).

### List comparison

#### Sorting order
Every array returned by CompareString2 is sorted by **descendent** order. The order is based on the **result** got by the entry during the comparison with `s1` using the given algorithm.<br>
Note that the sorting order isn't always the same. There are some cases where the result `0` means that the strings are totally **different** (*distance* algorithms), while in other cases the result `0` means **equal** (*similarity* algorithms).<br>
If you want to get information about this topic, check [this](https://github.com/tdebatty/java-string-similarity#normalized-metric-similarity-and-distance) section of the GitHub page of [java-string-similarity](https://github.com/tdebatty/java-string-similarity).

```java
String s1 = "wahssapp";
String[] ss = new String[] {"Facebook", "Instagram", "Snapchat", "Twitter", "WhatsApp", "Reddit"};
```

#### Best match
```java
String bM = CompareStrings.bestMatch(s1, ss, AlgMap.NormSimAlg.JAROWRINKLER);
```

#### Top n matches
This method returns an array of `min(n, ss.length)` elements.
```java
String[] topN = CompareStrings.topNmatches(s1, ss, AlgMap.NormSimAlg.JACCARD, 4);
// '4' is an optional argument of the algorithm "Jaccard"
```

<br>

Let's redefine `s1`and `ss`. You will notice that, while `s1` **needs** to be a `String` object, `ss` can be **any** `Iterable<? extends StringableObject>`. `StringableObject` is an interface which comes with **CompareString2**. We use the method `getLowercaseString()` to obtain a comparable `String`. Moreover, you need to implement the method `getString()` for testing purposes (you can return `null` if you don't need it).
Let's see an example:

```java
String s1 = "jonn";
List<Contact> ss = Arrays.asList(new Contact[] {new Contact("John", "Doe"),
    new Contact("Mario", "Rossi"), new Contact("Santa", "Claus")});

.
.
.

class Contact implements StringableObject {
  .
  .
  .
  @Override
  public String getString() {
    return name + " " + surname;
  }

  public String getLowercaseString() {
    return getString().toLowerCase();
  }
}
```

#### With deadline
```java
float deadline = 0.55;
Algorithm sordice = AlgMap.NormSimAlg.SORENSENDICE.buildAlg();
Contact[] aboveDeadline = CompareObjects.withDeadline(Contact.class, s1, ss.size(), ss, deadline, sordice, AlgMap.NormSimAlg.SORENSENDICE);
```

Since `AlgMap.NormSimAlg.SORENSENDICE` is in the category *normalized similarity*, `0` means totally **different**, and `1` means **equal**. So a bigger result means an higher similarity, and this gives the sorting order of the returned array. Check [here](https://github.com/fAndreuzzi/CompareString2#sorting-order) for more details.

#### Splitter

Let's redefine one more time `s1`, `ss`, and a new `String[]` object called `splitter`:
```java
// let's suppose that MyFile implements StringableObject
String[] splitter = new String[] {"-", "_"};
String s1 = "values";
Set<MyFile> files = new HashSet<>(Arrays.asList(new MyFile[] {new MyFile("xml-entries.xml"),
    new MyFile("json_elements.json"), new MyFile("csv-values.xml"), new MyFile("JSON_values.xml")}));
```

If you use any **CompareString2** passing `s1` and `files` you will likely get an unwanted value. We're interested in `csv-values.xml`, but since every String comparison algorithm is quite *linear*, the `csv-` part will be considered **before** the `values.xml` part; the algorithm will try to compare the two strings linearly, and the outcome will be worse than we would.<br>
You can avoid this problem passing a `String[]` object as a `splitter`. The following is a **pseudo** version of the process:
```java
double result = veryBadResult;
for(Object obj : files) {
  for(String spl : splitter) {
    for(String s : files.toString().split(spl)) {
      // keep the better result
      result = keepBetter(result, Compare.compare(s, s1, alg));
    }
  }
}
return result;
```
You can use the **splitter** feature with every method in the [List comparison](https://github.com/fAndreuzzi/CompareString2#list-comparison).

#### Deadline + Top N
This method returns a `MyFile[]` object which contains **only** `MyFile` objects whose comparison result with `s1` is greater than or equal to `deadline`. The length of the array will be between `0` and `n`.
```java
float deadline = 5;
int n = 2;
Algorithm lcs = AlgMap.NormSimAlg.LCS.buildAlg();
MyFile[] objs = CompareObjects.topMatchesWithDeadline(MyFile.class, s1, files.size(), files, n, deadline, splitter, damerau, AlgMap.NormSimAlg.SORENSENDICE);
```

## Algorithms
Please refer to [tdebatty/java-string-similarity](https://github.com/tdebatty/java-string-similarity) for a detailed description for each **algorithm**.<br>
Some algorithms are listed two or three times. This means that they comes in more than one version (*Normalized distance*, *Normalized similarity*, ...).

**Category** | **Algorithm** | **Needed args** | **Optional args**
:--- | :--- | :---: | :---:
*Distance* | LCS | / | /
*Distance* | OSA | / | /
*Distance* | QGRAM | / | int
*Distance* | WLEVENSHTEIN | CharacterSubstitutionInterface | CharacterInsDelInterface
*Normalized distance* | COSINE | / | int
*Normalized distance* | JACCARD | / | int
*Normalized distance* | JAROWRINKLER | / | int
*Normalized distance* | METRICLCS | / | /
*Normalized distance* | NGRAM | / | int
*Normalized distance* | NLEVENSHTEIN | / | /
*Normalized distance* | SORENSENDICE | / | int
*Normalized similarity* | COSINE | / | int
*Normalized similarity* | JACCARD | / | int
*Normalized similarity* | JAROWRINKLER | / | int
*Normalized similarity* | NLEVENSHTEIN | / | /
*Normalized similarity* | SORENSENDICE | / | int
*Metric distance* | DAMERAU | / | /
*Metric distance* | JACCARD | / | int
*Metric distance* | LEVENSHTEIN | / | /
*Metric distance* | METRICLCS | / | /

### Result ranges

**Category** | **Equals** | **Different**
:--- | :---: | :---:
Distance | 0 | +Infinity
Normalized distance | 0 | 1
Normalized similarity | 1 | 0
Metric distance | 0 | +Infinity

## Known users
* [**Linux CLI Launcher**](https://github.com/fAndreuzzi/TUI-ConsoleLauncher)

Please, let me know if you used **CompareString2** in your project :D
