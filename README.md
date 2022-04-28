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
| MyBenchmark.testMaxFloatFunction                                | thrpt | 25  | 69.949 ± 4.043   | ops/s |
| MyBenchmark.testMaxFloatFunctionNewFloatFieldSource             | thrpt | 25  | 112.326 ± 3.228  | ops/s |
| MyBenchmark.testNewMaxFloatFunction                             | thrpt | 25  | 93.216 ± 2.757   | ops/s |
| MyBenchmark.testNewMaxFloatFunctionNewFloatFieldSource          | thrpt | 25  | 123.364 ± 7.861  | ops/s |
| MyBenchmark.testMaxFloatFunctionRareField                       | thrpt | 25  | 257.339 ± 33.849 | ops/s |
| MyBenchmark.testMaxFloatFunctionNewFloatFieldSourceRareField    | thrpt | 25  | 287.175 ± 22.840 | ops/s |
| MyBenchmark.testNewMaxFloatFunctionRareField                    | thrpt | 25  | 235.268 ± 4.103  | ops/s |
| MyBenchmark.testNewMaxFloatFunctionNewFloatFieldSourceRareField | thrpt | 25  | 272.397 ± 8.406  | ops/s |

## References
* https://issues.apache.org/jira/browse/LUCENE-10534
* New MaxFloatFunction - https://github.com/apache/lucene/pull/837
* New FloatFieldSource - https://github.com/apache/lucene/pull/847
