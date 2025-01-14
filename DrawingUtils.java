import java.awt.Graphics;
import java.awt.Color;

public class DrawingUtils {
    // palette
    public static final Color BACKGROUND_COLOR = new Color(23, 10, 25);
    public static final Color TEXT_COLOR = new Color(255, 255, 255);
    public static final Color SUBTEXT_COLOR = new Color(155, 125, 180);
    public static final Color CORRECT_COLOR = Color.GREEN;
    public static final Color INCORRECT_COLOR = Color.RED;

    // drawing utilities
    // all coordinates in here are intended to be in unscaled space, which gets upscaled to screenspace
    public static final int CHAR_WIDTH = 6;
    public static final int CHAR_HEIGHT = 5;
    public static int SCALED_CHAR_WIDTH = Game.WINDOW_SCALE_FACTOR*CHAR_WIDTH;
    public static int SCALED_CHAR_HEIGHT = Game.WINDOW_SCALE_FACTOR*CHAR_HEIGHT;
    
    public static void recalculateConstants() {
        SCALED_CHAR_WIDTH = Game.WINDOW_SCALE_FACTOR*CHAR_WIDTH;
        SCALED_CHAR_HEIGHT = Game.WINDOW_SCALE_FACTOR*CHAR_HEIGHT;
    }
    
    public static void drawText(Graphics g, String str, int x, int y, boolean centered) {
        // transform to screenspace
        x = x*Game.WINDOW_SCALE_FACTOR;
        if (centered) { // scoot x back by half the width of the string
            x -= (str.length()*SCALED_CHAR_WIDTH)/2+Game.WINDOW_SCALE_FACTOR; // get the total pixel width of the string, divide it by 2, subtract 1
        }
        y = y*Game.WINDOW_SCALE_FACTOR;
        int offset = 0;
        
        for (int i = 0; i < str.length(); i++) {
            char curChar = str.charAt(i);
            drawChar(g, Character.toLowerCase(curChar), x+offset, y);
            offset += SCALED_CHAR_WIDTH;
        }
    }
    
    // font information. every lowercase char has 25 pixels, every uppercase char has 42 pixels
    // to access a specific character
    private static final int CHAR_AREA = ((CHAR_WIDTH-1)*CHAR_HEIGHT);
    public static void drawChar(Graphics g, char c, int x, int y) {
        int arrayOffset = (c - 'a')*CHAR_AREA;
        if (arrayOffset < 0 || arrayOffset > ANAFONT_LOWER.length) {
            if (c != ' ') {
                System.out.println("invalid char "+c+" fed to drawChar");
                System.out.println("char code "+(int)c);
            }
        } else {
            for (int i = 0; i < CHAR_AREA; i++) {
                if (ANAFONT_LOWER[i+arrayOffset]) {
                    g.fillRect(x+(i%(CHAR_WIDTH-1)*Game.WINDOW_SCALE_FACTOR), y+(i/CHAR_HEIGHT*Game.WINDOW_SCALE_FACTOR), Game.WINDOW_SCALE_FACTOR, Game.WINDOW_SCALE_FACTOR);
                }
            }
        }
    }
    
    private static final boolean[] ANAFONT_LOWER = new boolean[]{
        // a
        false, true,  true,  true,  false,
        true,  false, false, false, true,
        true,  true,  true,  true,  true,
        true,  false, false, false, true,
        true,  false, false, false, true,
        // b
        true,  true,  true,  true,  false,
        true,  false, false, false, true,
        true,  true,  true,  true,  false,
        true,  false, false, false, true,
        true,  true,  true,  true,  false,
        // c
        false, true,  true,  true,  false,
        true,  false, false, false, true,
        true,  false, false, false, false,
        true,  false, false, false, true,
        false, true,  true,  true,  false,
        // d
        true,  true,  true,  true,  false,
        true,  false, false, false, true,
        true,  false, false, false, true,
        true,  false, false, false, true,
        true,  true,  true,  true,  false,
        // e
        true,  true,  true,  true,  true,
        true,  false, false, false, false,
        true,  true,  true,  false, false,
        true,  false, false, false, false,
        true,  true,  true,  true,  true,
        // f
        true,  true,  true,  true,  true,
        true,  false, false, false, false,
        true,  true,  true,  false, false,
        true,  false, false, false, false,
        true,  false, false, false, false,
        // g
        false, true,  true,  true,  false,
        true,  false, false, false, true,
        true,  false, false, false, false,
        true,  false, true,  true,  true,
        false, true,  true,  false, false,
        // h
        true,  false, false, false, true,
        true,  false, false, false, true,
        true,  true,  true,  true,  true,
        true,  false, false, false, true,
        true,  false, false, false, true,
        // i
        true,  true,  true,  true,  true,
        false, false, true,  false, false,
        false, false, true,  false, false,
        false, false, true,  false, false,
        true,  true,  true,  true,  true,
        // j
        true,  true,  true,  true,  true,
        false, false, false, true,  false,
        false, false, false, true,  false,
        true,  false, false, true,  false,
        false, true,  true,  false, false,
        // k
        true,  false, false, false, true,
        true,  false, false, true,  false,
        true,  true,  true,  false, false,
        true,  false, false, true,  false,
        true,  false, false, false, true,
        // l
        true,  false, false, false, false,
        true,  false, false, false, false,
        true,  false, false, false, false,
        true,  false, false, false, false,
        true,  true,  true,  true,  true,
        // m
        true,  true,  false, true,  false,
        true,  false, true,  false, true,
        true,  false, false, false, true,
        true,  false, false, false, true,
        true,  false, false, false, true,
        // n
        true,  false, false, false, true,
        true,  true,  false, false, true,
        true,  false, true,  false, true,
        true,  false, false, true,  true,
        true,  false, false, false, true,
        // o
        false, true,  true,  true,  false,
        true,  false, false, false, true,
        true,  false, false, false, true,
        true,  false, false, false, true,
        false, true,  true,  true,  false,
        // p
        true,  true,  true,  true,  false,
        true,  false, false, false, true,
        true,  true,  true,  true,  false,
        true,  false, false, false, false,
        true,  false, false, false, false,
        // q
        false, true,  true,  true,  false,
        true,  false, false, false, true,
        true,  false, false, false, true,
        true,  false, false, true,  false,
        false, true,  true,  false, true,
        // r
        true,  true,  true,  true,  false,
        true,  false, false, false, true,
        true,  true,  true,  true,  false,
        true,  false, false, false, true,
        true,  false, false, false, true,
        // s
        false, true,  true,  true,  true,
        true,  false, false, false, false,
        false, true,  true,  true,  false,
        false, false, false, false, true,
        true,  true,  true,  true,  false,
        // t
        true,  true,  true,  true,  true,
        false, false, true,  false, false,
        false, false, true,  false, false,
        false, false, true,  false, false,
        false, false, true,  false, false,
        // u
        true,  false, false, false, true,
        true,  false, false, false, true,
        true,  false, false, false, true,
        true,  false, false, false, true,
        false, true,  true,  true,  false,
        // v
        true,  false, false, false, true,
        true,  false, false, false, true,
        false, true,  false, true,  false,
        false, true,  false, true,  false,
        false, false, true,  false, false,
        // w
        true,  false, false, false, true,
        true,  false, false, false, true,
        true,  false, true,  false, true,
        true,  false, true,  false, true,
        false, true,  false, true,  false,
        // x
        true,  false, false, false, true,
        false, true,  false, true,  false,
        false, false, true,  false, false,
        false, true,  false, true,  false,
        true,  false, false, false, true,
        // y
        true,  false, false, false, true,
        false, true,  false, true,  false,
        false, false, true,  false, false,
        false, false, true,  false, false,
        false, false, true,  false, false,
        // z
        true,  true,  true,  true,  true,
        false, false, false, true,  false,
        false, false, true,  false, false,
        false, true,  false, false, false,
        true,  true,  true,  true,  true,
        // {
        true,  true,  false, true,  true,
        true,  false, false, false, true,
        false, false, false, false, false,
        true,  false, false, false, true,
        true,  true,  false, true,  true,
        // |
        false, false, false, false, false,
        false, true,  true,  true,  false,
        false, true,  true,  true,  false,
        false, true,  true,  true,  false,
        false, false, false, false, false,
        // }
        true,  false, false, false, false,
        false, true,  false, false, false,
        false, false, true,  false, false,
        false, true,  false, false, false,
        true,  false, false, false, false,
        // ~
        false, false, false, false, false,
        false, false, false, false, false,
        false, false, true,  false, false,
        false, false, false, false, false,
        false, false, false, false, false,
    };
}