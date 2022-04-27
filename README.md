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
| MyBenchmark.testMaxFloatFunction                                | thrpt | 25  | 65.668 ±  2.724  | ops/s |
| MyBenchmark.testMaxFloatFunctionNewFloatFieldSource             | thrpt | 25  | 113.779 ±  8.229 | ops/s |
| MyBenchmark.testNewMaxFloatFunction                             | thrpt | 25  | 64.588 ±  1.154  | ops/s |
| MyBenchmark.testNewMaxFloatFunctionNewFloatFieldSource          | thrpt | 25  | 115.084 ± 12.421 | ops/s |
| MyBenchmark.testMaxFloatFunctionRareField                       | thrpt | 25  | 237.400 ±  7.981 | ops/s |
| MyBenchmark.testMaxFloatFunctionNewFloatFieldSourceRareField    | thrpt | 25  | 281.997 ± 27.575 | ops/s |
| MyBenchmark.testNewMaxFloatFunctionRareField                    | thrpt | 25  | 236.144 ±  5.528 | ops/s |
| MyBenchmark.testNewMaxFloatFunctionNewFloatFieldSourceRareField | thrpt | 25  | 269.662 ±  8.247 | ops/s |

## References
* https://issues.apache.org/jira/browse/LUCENE-10534
* New MaxFloatFunction - https://github.com/apache/lucene/pull/837
* New FloatFieldSource - https://github.com/apache/lucene/pull/840
