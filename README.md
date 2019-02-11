# CompareString2

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
