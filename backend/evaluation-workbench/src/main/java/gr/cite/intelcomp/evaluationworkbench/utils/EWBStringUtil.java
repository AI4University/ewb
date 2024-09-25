package gr.cite.intelcomp.evaluationworkbench.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EWBStringUtil {

    /**
     *Constant for characters that separate words. It's a constant variable in case of expansion
     */
    private static final String WORD_DELIMS = "\\s|\\.|,";

    public static Long countWordsUsed(String text, List<String> words) {
        Long count = 0L;
        List<String> textWords = List.of(text.split(WORD_DELIMS));
        for (String word: words) {
            count += Collections.frequency(textWords, word);
        }
        return count;
    }
}
