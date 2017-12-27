/**
 * Copyright (C) Faruk Akgul 2012.
 * <p>
 * Licensed under the GNU General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/gpl.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.keepfight.tc;

import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.MaxWordSeg;
import com.chenlb.mmseg4j.Word;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Small library to calculate the most important words of given documents.
 *
 * @author Faruk Akgul
 * @version 0.1
 */

public class TfIdf {

    private final List<String> documents;

    private Dictionary dic = Dictionary.getInstance();

    /**
     * Takes a documents of words as parameter.
     *
     * @param documents List of documents.
     */
    public TfIdf(List<String> documents) {
        this.documents = documents;
    }

    /**
     * Returns the documents that contains the documents.
     *
     * @return List<String> documents
     */
    public List<String> getDocuments() {
        return this.documents;
    }

    /**
     * Generates documents of words from the provided documents which are matched by
     * the REGEX.
     *
     * @param document Given document to filter its words.
     * @return List that contains the words of the document which are matched.
     */
    List<String> build_words(String document) {
        List<String> wordList = new ArrayList<>();
        MMSeg mmSeg = new MMSeg(new StringReader(document), new MaxWordSeg(dic));
        Word word = null;
        try {
            while ((word = mmSeg.next()) != null) {
                wordList.add(word.getString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordList;
    }

    /**
     * Calculates the Tf scores of words which are built by build_words.
     *
     * @return map that contains the words and their Tf scores.
     * @see TfIdf#build_words(String)
     */
    public Map<String, Long> tf() {
        return documents.stream()
                .flatMap(document -> build_words(document).stream())
                .collect(Collectors.groupingBy(x -> x, Collectors.counting()));
    }

    /**
     * Calculates the Tf scores of words of a single document. Words are split
     * by spaces. This should fixed with a smarter REGEX.
     *
     * @param document Single document to calculate the Tf scores of words of it.
     * @return map that contains the words and Tf scores.
     */
    public Map<String, Long> tf(String document) {
        return build_words(document)
                .stream()
                .collect(Collectors.groupingBy(x -> x, Collectors.counting()));
    }

    /**
     * This is the same method as calculating the tf scores. I don't know why I've
     * written the same thing again.
     *
     * @param list List that contains the words that are split by space.
     * @return map that contains the words and their tf scores.
     * @see TfIdf#tf()
     */
    private Map<String, Long> counter(List<String> list) {
        return list.stream().collect(Collectors.groupingBy(x -> x, Collectors.counting()));
    }

    /**
     * Calculates the df scores of the words in documents.
     *
     * @return Map that contains the words and their df scores.
     */
    public Map<String, Long> df() {
        List<String> list = documents.stream()
                .map(this::build_words)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
        return counter(list);
    }

    /**
     * Calculates the df scores of the words in given documents documents.
     *
     * @param documents Document documents to calculate df scores.
     * @return Map that contains the words and their df scores.
     */
    public Map<String, Long> df(List<String> documents) {
        List<String> list = documents.stream()
                .map(this::build_words)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        return counter(list);
    }

    /**
     * Calculates the idf scores of words.
     *
     * @return Map that contains the words and their idf scores.
     */
    public Map<String, Double> idf() {
        Map<String, Long> map = df();
        return documents.stream()
                .map(this::build_words)
                .flatMap(List::stream)
                .filter(d -> map.get(d) != null)
                .map(d -> new Pair<>(d, 1.0 / (map.get(d) + 1e-100)))
                .collect(Collectors.toMap(Pair::getK, Pair::getV));
    }

    /**
     * Calculates the tfidf scores of words. Basically, calculating the tfidf scores
     * gives us the most important words of documents. This method has quite a few
     * weakness. For example; this method gives very high scores for very rare words.
     * For more accuracy, @see TfIdf#tfidf_tweak1.
     *
     * @return Map that contains the words and their tfidf scores.
     * @see TfIdf#tfidf_tweak1
     */
    public Map<String, Double> tfidf(String document) {
        Map<String, Long> df_scores = df();
        Map<String, Long> tf_scores = tf(document);
        return build_words(document).stream()
                .distinct()
                .map(d -> new Pair<>(d, tf_scores.get(d) / (df_scores.get(d) + 0.01)))
                .collect(Collectors.toMap(Pair::getK, Pair::getV));
    }

    public List<Pair<Integer, Map<Integer, Double>>> tfidf(List<Pair<Integer, String>> documents) {
        final Map<String, Long> df_scores = df();
        return documents.stream().map(document -> {
            Pair<Integer, Map<Integer, Double>> res = new Pair<>();
            Map<String, Long> tf_scores = tf(document.getV());
            res.setK(document.getK());
            res.setV(build_words(document.getV()).stream()
                    .distinct()
                    .filter(s->Objects.nonNull(DicMap.getInstance().get(s)))
                    .map(d -> new Pair<>(d, tf_scores.get(d) / (df_scores.get(d) + 0.01)))
                    .collect(Collectors.toMap(s->DicMap.getInstance().get(s.getK()), Pair::getV)));
            return res;
        }).collect(Collectors.toList());
    }

    /**
     * Calculates the tfidf scores of words. This method also finds out the most
     * important words of given documents. However, this is more intelligent than
     * then pure TfIdf#tfidf method.
     *
     * @return Map that contains the words and their tweaked tfidf scores.
     * @see TfIdf#tfidf(String)
     */
    public Map<String, Double> tfidf_tweak1(String document) {
        Map<String, Long> df_scores = df();
        final int N = documents.size();
        Map<String, Long> tf_scores = tf(document);
        return build_words(document).stream()
                .distinct()
                .map(d -> new Pair<>(d, tf_scores.get(d) * (Math.log(N / df_scores.get(d))) + 0.01))
                .collect(Collectors.toMap(Pair::getK, Pair::getV));
    }
}
