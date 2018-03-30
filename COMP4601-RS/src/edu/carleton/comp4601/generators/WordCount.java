package edu.carleton.comp4601.generators;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordCount {


    public static void countWordsInString(String s) {
        String sentence = s;
        Stream<String> wordStream = Pattern.compile("\\W").splitAsStream(sentence);
        HashMap<String,Integer> unsortedMap = new HashMap<String,Integer>();
        // foreach word count how many the word occurs in the wordstream
        wordStream.forEach((wordReal) -> {
            String word = wordReal.toLowerCase();
            if (!word.equals("")) {
                if (unsortedMap.get(word) == null) {
                    unsortedMap.put(word, 0);
                }
                unsortedMap.put(word, unsortedMap.get(word) + 1);
            }
        });
        // sort hashmap after value desc
        Map<String, Integer> sortedMap =
             unsortedMap.entrySet().stream()
            .sorted(Map.Entry.comparingByValue((v1,v2)->v2.compareTo(v1)))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
                                      (e1, e2) -> e1, LinkedHashMap::new));

        // just println word and wordcount, here you can limit to 25 (just delete)
        int i = 0;
        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
        	i++;
            System.out.println(entry.getKey());
            if (i == 200) break;
        }
    }

}