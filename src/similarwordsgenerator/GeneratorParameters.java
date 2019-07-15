package similarwordsgenerator;

class GeneratorParameters {

    private boolean sorted = true;
    private boolean firstCharAsInInput = true;
    private boolean lastCharAsInInput = true;
    private int numberOfWords;
    private int minWordLength;
    private int maxWordLength;

    GeneratorParameters() {
    }

    boolean isSorted() {
        return sorted;
    }

    void setSorted(boolean sorted) {
        this.sorted = sorted;
    }

    boolean isFirstCharAsInInput() {
        return firstCharAsInInput;
    }

    void setFirstCharAsInInput(boolean firstCharAsInInput) {
        this.firstCharAsInInput = firstCharAsInInput;
    }

    boolean isLastCharAsInInput() {
        return lastCharAsInInput;
    }

    void setLastCharAsInInput(boolean lastCharAsInInput) {
        this.lastCharAsInInput = lastCharAsInInput;
    }

    int getNumberOfWords() {
        return numberOfWords;
    }

    void setNumberOfWords(int numberOfWords) {
        this.numberOfWords = numberOfWords;
    }

    int getMinWordLength() {
        return minWordLength;
    }

    void setMinWordLength(int minWordLength) {
        this.minWordLength = minWordLength;
    }

    int getMaxWordLength() {
        return maxWordLength;
    }

    void setMaxWordLength(int maxWordLength) {
        this.maxWordLength = maxWordLength;
    }
}