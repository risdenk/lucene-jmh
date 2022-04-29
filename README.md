# lucene-jmh

## Build
```
mvn clean verify
```

## Usage
```
# Standard
java -jar target/benchmarks.jar

# Output to json
java -jar target/benchmarks.jar -rf json

# With async-profiler
java -jar target/benchmarks.jar -prof async:libPath=/Users/risdenk/Downloads/async-profiler-2.7-macos/build/libasyncProfiler.so\;output=jfr
```

## Initial Results from 500k docs

Testing against all fields having float value and separate field only small handful having float values:
* Original MaxFloatFunction with original FloatFieldSource
* Original MaxFloatFunction with new FloatFieldSource
* New MaxFloatFunction with original FloatFieldSource
* New MaxFloatFunction with new FloatFieldSource

| Benchmark                                                       | Mode  | Cnt | Score and Error  | Units |
|-----------------------------------------------------------------|-------|-----|------------------|-------|
| MyBenchmark.testMaxFloatFunction                                | thrpt | 25  | 64.159  ±  2.031 | ops/s |
| MyBenchmark.testNewMaxFloatFunction                             | thrpt | 25  | 94.997  ±  2.365 | ops/s |
| MyBenchmark.testMaxFloatFunctionNewFloatFieldSource             | thrpt | 25  | 123.191 ±  9.291 | ops/s |
| MyBenchmark.testNewMaxFloatFunctionNewFloatFieldSource          | thrpt | 25  | 123.817 ±  6.191 | ops/s |
| MyBenchmark.testMaxFloatFunctionRareField                       | thrpt | 25  | 244.921 ±  6.439 | ops/s |
| MyBenchmark.testNewMaxFloatFunctionRareField                    | thrpt | 25  | 239.288 ±  5.136 | ops/s |
| MyBenchmark.testMaxFloatFunctionNewFloatFieldSourceRareField    | thrpt | 25  | 271.521 ±  3.870 | ops/s |
| MyBenchmark.testNewMaxFloatFunctionNewFloatFieldSourceRareField | thrpt | 25  | 279.334 ± 10.511 | ops/s |

## References
* https://issues.apache.org/jira/browse/LUCENE-10534
* New MaxFloatFunction - https://github.com/apache/lucene/pull/837
* New FloatFieldSource - https://github.com/apache/lucene/pull/847
