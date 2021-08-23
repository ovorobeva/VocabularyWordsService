package com.gitjub.ovorobeva.vocabularywordsservice.wordsprocessing;

import com.gitjub.ovorobeva.vocabularywordsservice.dto.generated.GeneratedWords;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordsProcessing {
    List<GeneratedWords> generatedWordsList = new LinkedList<>();
    private int id = 0;
    private int wordsCount;

    public WordsProcessing(int wordsCount) {
        this.wordsCount = wordsCount;
    }

    private static Boolean isPartOfSpeechCorrect(String word, WordsClient wordsClient) throws InterruptedException {
        WordsClient.logger.log(Level.INFO, "isPartOfSpeechCorrect: the word " + word + " is being checked");
        boolean isCorrect = true;
        List<String> partsOfSpeech = wordsClient.getPartsOfSpeech(word);

        WordsClient.logger.log(Level.INFO, "isPartOfSpeechCorrect: parts of speech for the word " + word + " are: " + partsOfSpeech);

        if (partsOfSpeech == null || partsOfSpeech.isEmpty()) {
            return false;
        }

        for (String partOfSpeech : partsOfSpeech) {
            if (!partOfSpeech.matches("(?i)noun|adjective|transitive & intransitive verb|transitive verb|intransitive verb|verb|adverb|idiom|past-participle") || partOfSpeech.isEmpty()) {
                WordsClient.logger.log(Level.INFO, "isPartOfSpeechCorrect: The word " + word + " is to be removed because of part of speech: " + partOfSpeech);
                isCorrect = false;
                break;
            }
        }
        return isCorrect;
    }

/*    private static List<String> processResponse(String entity, JSONArray response) {
        List<String> responseList = new LinkedList<>();
        try {
            //   JSONArray jsonResponse = new JSONArray(response);
            int wordsCount = response.length();
            for (int i = 0; i < wordsCount; i++) {
                responseList.add(response.getJSONObject(i).getString(entity));
            }
            WordsClient.logger.log(Level.INFO, "processResponse: response processed");
        } catch (JSONException e) {
            e.printStackTrace();
            WordsClient.logger.log(Level.SEVERE, "processResponse: Cannot parse response.Response is: " + response + " Error message is: " + e);
        }
        WordsClient.logger.log(Level.INFO, "processResponse: processed result is: " + responseList);
        return responseList;
    }*/

    public List<GeneratedWords> getWords() throws InterruptedException {

        WordsClient wordsClient = WordsClient.getWordsClient();

        List<String> words = wordsClient.getRandomWords(wordsCount);

        Iterator<String> iterator = words.iterator();
        int removedCounter = 0;


        WordsClient.logger.log(Level.INFO, "getWords: Starting removing non-matching words from the list \n" + words);

        while (iterator.hasNext()) {
            String word = iterator.next();
            Pattern pattern = Pattern.compile("[^a-zA-Z[-]]");
            Matcher matcher = pattern.matcher(word);

            if (matcher.find()) {
                removedCounter++;
                WordsClient.logger.log(Level.INFO, "getWords: Removing the word " + word + " because of containing symbol " + matcher.toMatchResult() + ". The count of deleted words is " + removedCounter);
                continue;
            }

            if (!isPartOfSpeechCorrect(word, wordsClient)) {
                removedCounter++;
                WordsClient.logger.log(Level.INFO, "getWords: Removing the word " + word + " because of the wrong part of speech. The count of deleted words is " + removedCounter);
                continue;
            }
            generatedWordsList.add(new GeneratedWords(id, word));
            id++;
        }
        if (removedCounter > 0){
            wordsCount = removedCounter;
            getWords();
        }
        WordsClient.logger.log(Level.INFO, "getWords: Words are: " + words);

        return generatedWordsList;

    }


}

