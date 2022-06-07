/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.risdenk;

import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.ConstValueSource;
import org.apache.lucene.queries.function.valuesource.DivFloatFunction;
import org.apache.lucene.queries.function.valuesource.FloatFieldSource;
import org.apache.lucene.queries.function.valuesource.NewFloatFieldSource;
import org.apache.lucene.queries.function.valuesource.ProductFloatFunction;
import org.apache.lucene.queries.function.valuesource.SumFloatFunction;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

public class BenchmarkConstantFunctions extends BenchmarkBase {
  @State(Scope.Benchmark)
  public static class MyState extends BaseState {
    public Query basicMathQuery1;
    public Query basicNoMathQuery1;

    public Query basicMathQuery2;
    public Query basicNoMathQuery2;

    public Query basicMathQuery3;
    public Query basicNoMathQuery3;

    @Setup(Level.Trial)
    @Override
    public void doSetup() throws Exception {
      super.doSetup();

      // sum(product(10,10),fieldA)
      basicMathQuery1 = new FunctionQuery(new SumFloatFunction(new ValueSource[]{new ProductFloatFunction(new ValueSource[]{new ConstValueSource(10), new ConstValueSource(10)}), new NewFloatFieldSource(fieldName)}));
      basicNoMathQuery1 = new FunctionQuery(new SumFloatFunction(new ValueSource[]{new ConstValueSource((float) (10*10)), new NewFloatFieldSource(fieldName)}));

      // product(product(div(sum(product(1,0.1),dwn),sum(product(250,0.1),adp)),1000),1000)
      basicMathQuery2 = new FunctionQuery(
          new ProductFloatFunction(new ValueSource[]{
              new ProductFloatFunction(new ValueSource[]{
                  new DivFloatFunction(
                      new SumFloatFunction(new ValueSource[]{
                          new ProductFloatFunction(new ValueSource[]{
                              new ConstValueSource(1),
                              new ConstValueSource((float) 0.1)
                          }),
                          new FloatFieldSource(fieldName)
                      }),
                      new SumFloatFunction(new ValueSource[]{
                          new ProductFloatFunction(new ValueSource[]{
                              new ConstValueSource(250),
                              new ConstValueSource((float) 0.1)
                          }),
                          new FloatFieldSource(fieldName)
                      })
                  ),
                  new ConstValueSource(1000)
              }),
              new ConstValueSource(1000)
          })
      );
      basicNoMathQuery2 = new FunctionQuery(
          new ProductFloatFunction(new ValueSource[]{
              new ProductFloatFunction(new ValueSource[]{
                  new DivFloatFunction(
                      new SumFloatFunction(new ValueSource[]{
                          new ConstValueSource((float) (1*.1)),
                          new FloatFieldSource(fieldName)
                      }),
                      new SumFloatFunction(new ValueSource[]{
                          new ConstValueSource((float) (250*.1)),
                          new FloatFieldSource(fieldName)
                      })
                  ),
                  new ConstValueSource(1000)
              }),
              new ConstValueSource(1000)
          })
      );

      // div(sum(product(-1,product(1.99,1.99)),1.99), fieldA)
      basicMathQuery3 = new FunctionQuery(
          new DivFloatFunction(
              new SumFloatFunction(new ValueSource[] {
                  new ProductFloatFunction(new ValueSource[] {
                      new ConstValueSource(-1),
                      new ProductFloatFunction(new ValueSource[] {
                          new ConstValueSource((float) 1.99),
                          new ConstValueSource((float) 1.99)
                      })
                  }),
                  new ConstValueSource((float) 1.99)
              }),
              new FloatFieldSource(fieldName)
          )
      );
      basicNoMathQuery3 = new FunctionQuery(
          new DivFloatFunction(
              new ConstValueSource((float) ((-1*(1.99*1.99))+1.99)),
              new FloatFieldSource(fieldName)
          )
      );
    }
  }

  @Benchmark
  public TopDocs testBasicMath1(MyState state) throws Exception {
      return state.indexSearcher.search(state.basicMathQuery1, 10);
  }

  @Benchmark
  public TopDocs testBasicNoMath1(MyState state) throws Exception {
    return state.indexSearcher.search(state.basicNoMathQuery1, 10);
  }

  @Benchmark
  public TopDocs testBasicMath2(MyState state) throws Exception {
    return state.indexSearcher.search(state.basicMathQuery2, 10);
  }

  @Benchmark
  public TopDocs testBasicNoMath2(MyState state) throws Exception {
    return state.indexSearcher.search(state.basicNoMathQuery2, 10);
  }

  @Benchmark
  public TopDocs testBasicMath3(MyState state) throws Exception {
    return state.indexSearcher.search(state.basicMathQuery3, 10);
  }

  @Benchmark
  public TopDocs testBasicNoMath3(MyState state) throws Exception {
    return state.indexSearcher.search(state.basicNoMathQuery3, 10);
  }
}
