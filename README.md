# CompareString2

[![](https://jitpack.io/v/fAndreuzzi/CompareString2.svg)](https://jitpack.io/#fAndreuzzi/CompareString2)

This library is a **wrapper** of *tdebatty*'s [**java-string-similarity**](https://github.com/tdebatty/java-string-similarity). It provides many methods to perform **String** comparison with various algorithms.

## Get CompareString2

```gradle
// gradle
dependencies {
    implementation 'com.github.fAndreuzzi:CompareString2:v1.0'
}
```

```maven
// maven
<dependency>
	  <groupId>com.github.fAndreuzzi</groupId>
	  <artifactId>CompareString2</artifactId>
	  <version>v1.0</version>
</dependency>

// but remember to add jitpack
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
</repositories>
```

Otherwise you can get the last `CompareString2-*.jar` file from the `target` folder.

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
