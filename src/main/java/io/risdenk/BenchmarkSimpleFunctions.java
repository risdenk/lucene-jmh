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

import org.apache.lucene.expressions.Expression;
import org.apache.lucene.expressions.SimpleBindings;
import org.apache.lucene.expressions.js.JavascriptCompiler;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.ConstValueSource;
import org.apache.lucene.queries.function.valuesource.DivFloatFunction;
import org.apache.lucene.queries.function.valuesource.FloatFieldSource;
import org.apache.lucene.queries.function.valuesource.NewFloatFieldSource;
import org.apache.lucene.queries.function.valuesource.ProductFloatFunction;
import org.apache.lucene.queries.function.valuesource.SumFloatFunction;
import org.apache.lucene.search.DoubleValuesSource;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

public class BenchmarkSimpleFunctions extends BenchmarkBase {
  @State(Scope.Benchmark)
  public static class MyState extends BaseState {
    public Query basicQuery1;
    public Query basicSimpleQuery1;
    public Query basicLuceneCompiler1;

    @Setup(Level.Trial)
    @Override
    public void doSetup() throws Exception {
      super.doSetup();

      // sum(product(10,10))
      basicQuery1 = new FunctionQuery(new SumFloatFunction(new ValueSource[]{new ProductFloatFunction(new ValueSource[]{new ConstValueSource(10), new ConstValueSource(10)})}));

      // product(10,10)
      basicSimpleQuery1 = new FunctionQuery(new ProductFloatFunction(new ValueSource[]{new ConstValueSource(10), new ConstValueSource(10)}));

      Expression exprSimple1 = JavascriptCompiler.compile("(10 * 10)");
      SimpleBindings bindingsSimple1 = new SimpleBindings();
      basicLuceneCompiler1 = new FunctionQuery(ValueSource.fromDoubleValuesSource(exprSimple1.getDoubleValuesSource(bindingsSimple1)));
    }
  }

  @Benchmark
  public TopDocs testBasicQuery1(MyState state) throws Exception {
    return state.indexSearcher.search(state.basicQuery1, 10);
  }

  @Benchmark
  public TopDocs testBasicSimpleQuery1(MyState state) throws Exception {
    return state.indexSearcher.search(state.basicSimpleQuery1, 10);
  }

  @Benchmark
  public TopDocs testBasicLuceneCompiler1(MyState state) throws Exception {
    return state.indexSearcher.search(state.basicLuceneCompiler1, 10);
  }
}
