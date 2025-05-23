/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opennlp.dl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

import opennlp.tools.tokenize.Tokenizer;

/**
 * Base class for OpenNLP deep-learning classes using ONNX Runtime.
 */
public abstract class AbstractDL implements AutoCloseable {

  public static final String INPUT_IDS = "input_ids";
  public static final String ATTENTION_MASK = "attention_mask";
  public static final String TOKEN_TYPE_IDS = "token_type_ids";

  protected OrtEnvironment env;
  protected OrtSession session;
  protected Tokenizer tokenizer;
  protected Map<String, Integer> vocab;

  /**
   * Loads a vocabulary {@link File} from disk.
   *
   * @param vocabFile The vocabulary file.
   * @return A map of vocabulary words to integer IDs.
   * @throws IOException Thrown if the vocabulary file cannot be opened or read.
   */
  public Map<String, Integer> loadVocab(final File vocabFile) throws IOException {

    final Map<String, Integer> vocab = new HashMap<>();
    final AtomicInteger counter = new AtomicInteger(0);

    try (Stream<String> lines = Files.lines(Path.of(vocabFile.getPath()))) {

      lines.forEach(line -> vocab.put(line, counter.getAndIncrement()));
    }

    return vocab;
  }

  /**
   * Closes this resource, relinquishing any underlying resources.
   *
   * @throws OrtException Thrown if it failed to close Ort resources.
   * @throws IllegalStateException Thrown if the underlying resources were already closed.
   */
  @Override
  public void close() throws OrtException, IllegalStateException {
    if (session != null) {
      session.close();
    }
    if (env != null) {
      env.close();
    }
  }

}
