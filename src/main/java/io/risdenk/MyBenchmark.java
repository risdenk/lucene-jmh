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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FloatDocValuesField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.FloatFieldSource;
import org.apache.lucene.queries.function.valuesource.MaxFloatFunction;
import org.apache.lucene.queries.function.valuesource.NewFloatFieldSource;
import org.apache.lucene.queries.function.valuesource.NewMaxFloatFunction;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

public class MyBenchmark {
  @State(Scope.Benchmark)
  public static class MyState {
    public ByteBuffersDirectory dir;
    public IndexReader indexReader;
    public IndexSearcher indexSearcher;
    
    public Query maxFloatFunctionQuery;
    public Query maxFloatFunctionNewFloatFieldSourceQuery;
    public Query newMaxFloatFunctionQuery;
    public Query newMaxFloatFunctionNewFloatFieldSourceQuery;

    public Query maxFloatFunctionRareFieldQuery;
    public Query maxFloatFunctionNewFloatFieldSourceRareFieldQuery;
    public Query newMaxFloatFunctionRareFieldQuery;
    public Query newMaxFloatFunctionNewFloatFieldSourceRareFieldQuery;

    @Setup(Level.Trial)
    public void doSetup() throws Exception {
      Random r = new Random(42);
      //int numDocs = Math.abs(r.nextInt());
      int numDocs = 500000;
      int numRareDocs = 100;
      System.out.println("Num Docs: " + numDocs);
      String fieldName = "field-" + r.nextInt();
      String rareFieldName = "rarefield-" + r.nextInt();
      Analyzer analyzer = new StandardAnalyzer();
      ByteBuffersDirectory dir = new ByteBuffersDirectory();
      IndexWriterConfig config = new IndexWriterConfig(analyzer);
      try (IndexWriter writer = new IndexWriter(dir, config)) {
        List<Document> docs = new ArrayList<>(numDocs / 1000);
        for(int i=0; i < numDocs; i++) {
          Document document = new Document();
          document.add(new FloatDocValuesField(fieldName, r.nextFloat()));
          if (i > numDocs - numRareDocs) {
            document.add(new FloatDocValuesField(rareFieldName, r.nextFloat()));
          }
          docs.add(document);
          if (i % 1000 == 0) {
            writer.addDocuments(docs);
            docs.clear();
          }
        }
        writer.addDocuments(docs);
        writer.forceMerge(1);
      }

      indexReader = DirectoryReader.open(dir);
      indexSearcher = new IndexSearcher(indexReader);

      maxFloatFunctionQuery = new FunctionQuery(new MaxFloatFunction(new ValueSource[]{new FloatFieldSource(fieldName)}));
      maxFloatFunctionNewFloatFieldSourceQuery = new FunctionQuery(new MaxFloatFunction(new ValueSource[]{new NewFloatFieldSource(fieldName)}));
      newMaxFloatFunctionQuery = new FunctionQuery(new NewMaxFloatFunction(new ValueSource[]{new FloatFieldSource(fieldName)}));
      newMaxFloatFunctionNewFloatFieldSourceQuery = new FunctionQuery(new NewMaxFloatFunction(new ValueSource[]{new NewFloatFieldSource(fieldName)}));

      maxFloatFunctionRareFieldQuery = new FunctionQuery(new MaxFloatFunction(new ValueSource[]{new FloatFieldSource(rareFieldName)}));
      maxFloatFunctionNewFloatFieldSourceRareFieldQuery = new FunctionQuery(new MaxFloatFunction(new ValueSource[]{new NewFloatFieldSource(rareFieldName)}));
      newMaxFloatFunctionRareFieldQuery = new FunctionQuery(new NewMaxFloatFunction(new ValueSource[]{new FloatFieldSource(rareFieldName)}));
      newMaxFloatFunctionNewFloatFieldSourceRareFieldQuery = new FunctionQuery(new NewMaxFloatFunction(new ValueSource[]{new NewFloatFieldSource(rareFieldName)}));
    }

    @TearDown(Level.Trial)
    public void doTearDown() throws Exception {
      if (dir != null) {
        dir.close();
        dir = null;
      }
      if (indexReader != null) {
        indexReader.close();
        indexReader = null;
      }
    }
  }

  @Benchmark
  public TopDocs testMaxFloatFunction(MyState state) throws Exception {
    return state.indexSearcher.search(state.maxFloatFunctionQuery, 10);
  }

  @Benchmark
  public TopDocs testMaxFloatFunctionNewFloatFieldSource(MyState state) throws Exception {
    return state.indexSearcher.search(state.maxFloatFunctionNewFloatFieldSourceQuery, 10);
  }

  @Benchmark
  public TopDocs testNewMaxFloatFunction(MyState state) throws Exception {
    return state.indexSearcher.search(state.newMaxFloatFunctionQuery, 10);
  }

  @Benchmark
  public TopDocs testNewMaxFloatFunctionNewFloatFieldSource(MyState state) throws Exception {
    return state.indexSearcher.search(state.newMaxFloatFunctionNewFloatFieldSourceQuery, 10);
  }

  @Benchmark
  public TopDocs testMaxFloatFunctionRareField(MyState state) throws Exception {
    return state.indexSearcher.search(state.maxFloatFunctionRareFieldQuery, 10);
  }

  @Benchmark
  public TopDocs testMaxFloatFunctionNewFloatFieldSourceRareField(MyState state) throws Exception {
    return state.indexSearcher.search(state.maxFloatFunctionNewFloatFieldSourceRareFieldQuery, 10);
  }

  @Benchmark
  public TopDocs testNewMaxFloatFunctionRareField(MyState state) throws Exception {
    return state.indexSearcher.search(state.newMaxFloatFunctionRareFieldQuery, 10);
  }

  @Benchmark
  public TopDocs testNewMaxFloatFunctionNewFloatFieldSourceRareField(MyState state) throws Exception {
    return state.indexSearcher.search(state.newMaxFloatFunctionNewFloatFieldSourceRareFieldQuery, 10);
  }
}
