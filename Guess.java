import java.awt.Graphics;

public class Guess {
    public static final byte INCOMPLETE_WORD = -1;

    public static String correctWord;
    private static byte[] correctCharacters = new byte[26]; // stores the count of each character inside the correct word. used for invalid character highlighting
    public static String shuffledWord;
    public char[] thisGuess; // | is the current word, { is anything not filled out yet
    //private byte[] guessCharacters = new byte[26]; // stores the count of each character inside the current guess. used for invalid character highlighting
    public byte currChar = 0;
    public byte status = STAT_UNREVEALED; // -1 unrevealed, 0 cursor showing, 1 incorrect, 2 correct
    // use this flag to draw status icons  on the left side of the guess
    // used to show what guess is being written to
    public static final byte STAT_UNREVEALED = -1;
    public static final byte STAT_CURSOR = 0;
    public static final byte STAT_INCORRECT = 1;
    public static final byte STAT_CORRECT = 2;
    
    public Guess() {
        thisGuess = new char[correctWord.length()];
        for (int i = 0; i < thisGuess.length; i++) {
            thisGuess[i] = '{';
        }
        //thisGuess[0] = '|';
    }
    
    public static void setWinWord(String str) {
        correctWord = str;
        for (int i = 0; i < correctCharacters.length; i++) {
            correctCharacters[i] = 0;
        }
        char[] tempstr = new char[str.length()];
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            correctCharacters[c-'a']++;
            tempstr[i] = c;
        }
        char tempchar;
        int temppoint;
        for (int i = 0; i < tempstr.length-1; i++) {
            temppoint = (int)(Math.random()*(tempstr.length-2-i))+i+1;
            tempchar = tempstr[i];
            tempstr[i] = tempstr[temppoint];
            tempstr[temppoint] = tempchar;
        }
        shuffledWord = new String(tempstr);
        //System.out.println(correctWord);
        //System.out.println(shuffledWord);
    }
    
    public boolean addChar(char c) {
        if (currChar < thisGuess.length) {
            thisGuess[currChar++] = c;
            //System.out.println("char added successfully");
            if (currChar < thisGuess.length) {
                thisGuess[currChar] = '|';
                //System.out.println("cursor still exists");
            }
            return true;
        }
        return false;
    }
    
    public boolean removeChar() {
        if (currChar < thisGuess.length && currChar != 0) {
            thisGuess[currChar] = '{';
        }
        if (currChar > 0) {
            thisGuess[--currChar] = '|';
            return true;
        }
        return false;
    }
    
    public void prepareCursor() {
        thisGuess[0] = '|';
        status = STAT_CURSOR;
    }
    
    public byte checkGuess() {
        byte correctLetters = 0;
        if (currChar == thisGuess.length) {
            for (int i = 0; i < thisGuess.length; i++) {
                if (thisGuess[i] == correctWord.charAt(i)) {
                    correctLetters++;
                }
            }
            return correctLetters;
        } else {
            return INCOMPLETE_WORD;
        }
    }
    
    public void draw(Graphics g, int x, int y, boolean centered) {
        // reset guesschar array
        //for (int i = 0; i < 26; i++) {
        //    guessCharacters[i] = 0;
        //}
        
        // centering
        // transform to screenspace
        x = x*Game.WINDOW_SCALE_FACTOR;
        if (centered) { // scoot x back by half the width of the string
            x -= (thisGuess.length*DrawingUtils.SCALED_CHAR_WIDTH)/2+Game.WINDOW_SCALE_FACTOR; // get the total pixel width of the string, divide it by 2, subtract 1
        }
        y = y*Game.WINDOW_SCALE_FACTOR;
        int offset = 0;
        
        // little icon to the left drawing
        switch (status) {
            case STAT_UNREVEALED:
                g.setColor(DrawingUtils.SUBTEXT_COLOR);
                DrawingUtils.drawChar(g, '~', x-7*Game.WINDOW_SCALE_FACTOR, y);
                break;
            case STAT_CURSOR:
                g.setColor(DrawingUtils.TEXT_COLOR);
                DrawingUtils.drawChar(g, '}', x-7*Game.WINDOW_SCALE_FACTOR, y);
                break;
            case STAT_INCORRECT:
                g.setColor(DrawingUtils.INCORRECT_COLOR);
                DrawingUtils.drawChar(g, 'x', x-7*Game.WINDOW_SCALE_FACTOR, y);
                break;
            case STAT_CORRECT:
                g.setColor(DrawingUtils.CORRECT_COLOR);
                DrawingUtils.drawChar(g, 'o', x-7*Game.WINDOW_SCALE_FACTOR, y);
                break;
        }
        
        // char drawing
        for (int i = 0; i < thisGuess.length; i++) {
            char curChar = thisGuess[i];
            //if ('a' <= curChar && curChar <= 'z') {
            //    guessCharacters[curChar-'a']++;
            //}
            if (curChar >= '{') {
                g.setColor(DrawingUtils.SUBTEXT_COLOR);
            } else {
                g.setColor(DrawingUtils.TEXT_COLOR);
            }
            if (curChar != '{' || (curChar == '{' && status != STAT_UNREVEALED)) {
                if (status == STAT_INCORRECT && correctCharacters[curChar-'a'] == 0) {
                    g.setColor(DrawingUtils.INCORRECT_COLOR);
                }
                //if (status == STAT_INCORRECT && (guessCharacters[curChar-'a'] > correctCharacters[curChar-'a'])) {
                //    g.setColor(DrawingUtils.INCORRECT_COLOR);
                //} else {
                //    g.setColor(DrawingUtils.TEXT_COLOR);
                //}
                DrawingUtils.drawChar(g, curChar, x+offset, y);
            }
            offset += DrawingUtils.SCALED_CHAR_WIDTH;
        }
        
        // pip drawing
        byte correctLetters = this.checkGuess();
        offset += DrawingUtils.CHAR_WIDTH;
        y += Game.WINDOW_SCALE_FACTOR;
        g.setColor(DrawingUtils.SUBTEXT_COLOR);
        for (int i = 0; i < thisGuess.length; i++) {
            if (status > STAT_CURSOR) {
                g.setColor(DrawingUtils.CORRECT_COLOR);
                if (i == correctLetters) {
                    break;
                }
            }
            g.fillRect(x+offset, y, Game.WINDOW_SCALE_FACTOR, 3*Game.WINDOW_SCALE_FACTOR);
            offset += 2*Game.WINDOW_SCALE_FACTOR;
        }
    }
}