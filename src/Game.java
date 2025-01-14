import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class Game extends JPanel { //implements KeyListener
    public static final String DICTIONARY_LOCATION = "dictionary.txt";
    public static final String GAME_NAME = "ANAGRAMMATICA";
    public static int WINDOW_SCALE_FACTOR;  // 15 is fullscreen for 1920x1080 screens
                                                      // Defines the edge length in pixels of a single text pixel
    private static final byte MAIN_MENU = 0;
    private static final byte GAME_MENU = 1;
    private static byte screen = MAIN_MENU;
    private static byte gameState = 0; // 0 false, 1 loss, 2 win
    private static final byte GAME_RUNNING = 0;
    private static final byte GAME_WIN = 1;
    private static final byte GAME_LOSS = 2;
    
    private static String[] dictionary;
    private static Guess[] guesses;
    private static byte currentGuess;
    
    
    // runs the game    
    public static void main(String[] args) {
        // prepare constants
        boolean fullscreened = false;
        try {
            WINDOW_SCALE_FACTOR = Math.min(Math.max(Integer.parseInt(args[0]), 1), 15);
        } catch (Exception e) {
            // experimental fullscreen option
            if (args.length > 0 && "FULLSCREEN".equals(args[0])) {
                WINDOW_SCALE_FACTOR = 15;
                fullscreened = true;
            } else {
                WINDOW_SCALE_FACTOR = 10;
            }
        }
        DrawingUtils.recalculateConstants();
        
        // load guess dictionary
        dictionary = loadDictionary(DICTIONARY_LOCATION);
        
        // load window
        JFrame mainframe = new JFrame(GAME_NAME);
        //mainframe.setUndecorated(true);
        mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainframe.setSize(128*WINDOW_SCALE_FACTOR, 72*WINDOW_SCALE_FACTOR+(fullscreened?0:30)); // 40 is added to just about every draw y value because of the title bar
        mainframe.setResizable(false);
        mainframe.setFocusable(true);
        Game gameframe = prepareFrame(mainframe);
        mainframe.addKeyListener(new KeyAdapter() { // shove a keylistener into the mainframe
            // anonymous class?
            public void keyPressed(KeyEvent e) {
                boolean update = false;
                int key = e.getKeyCode();
                //System.out.println(key);
                if (key == KeyEvent.VK_ESCAPE) {
                    //System.out.println("ESCAPE");
                    mainframe.dispatchEvent(new WindowEvent(mainframe, WindowEvent.WINDOW_CLOSING)); // the closing method apparently
                } else if (key == KeyEvent.VK_SPACE) {
                    // yes, this is long. no, i dont care
                    JOptionPane.showMessageDialog(null, "This is ANAGRAMMATICA, a game about guessing anagrams\nPress SPACE for help - you already know this\nPress ESCAPE to instantly close the game\nPress ENTER to:\n- Go to the game from the main menu\n- Confirm guesses\n- Restart the game after losing or winning\nPress BACKSPACE to delete characters in your current guess\n\nThe letters present in the correct word are listed at the top of the screen\nThe correct word will be revealed at the end of the round, whether you win or lose\nA guess must have the same number of letters as the correct word\nThe GREEN lines to the right of a guess is the number of characters in the correct position\nRED letters are extraneous! If you see one, you put too many of that letter in your guess\nOnly standard english letters are used and supported in this game\n                                                                                                                                                                                    \nMade by Darren Seim, 2024\nNot a WORDLE ripoff", "ANAGRAMMATICA HELP", 0);
                } else if (screen == GAME_MENU) { // game menu is open
                    if (gameState == GAME_RUNNING) { // playing as normal
                        if ('A' <= key && key <= 'Z') {
                            char pressedKey = (char)(key+'a'-'A'); // convert from A-Z to a-z
                            //System.out.println(pressedKey);
                            update = guesses[currentGuess].addChar(pressedKey);
                        } else {
                            switch (key) {
                                case KeyEvent.VK_BACK_SPACE:
                                    //System.out.println("BACKSPACE");
                                    update = guesses[currentGuess].removeChar();
                                    break;
                                case KeyEvent.VK_ENTER:
                                    byte guessResponse = guesses[currentGuess].checkGuess(); // -1 if too short, otherwise returns number of matching chars between guess and correctword
                                    //System.out.print("Guess ");
                                    if (guessResponse == Guess.correctWord.length()) { // correct guess
                                        //System.out.println("correct");
                                        guesses[currentGuess].status = Guess.STAT_CORRECT;
                                        gameState = GAME_WIN;
                                        update = true;
                                    } else if (guessResponse == Guess.INCOMPLETE_WORD) { // incomplete guess
                                        //System.out.println("not long enough!");
                                    } else { // guess incorrect
                                        //System.out.println("incorrect, "+guessResponse+"/"+Guess.correctWord.length());
                                        guesses[currentGuess].status = Guess.STAT_INCORRECT;
                                        update = true;
                                        currentGuess++;
                                        if (currentGuess == guesses.length) {
                                            gameState = GAME_LOSS;
                                            //System.out.println("GAME LOST.");
                                        } else {
                                            guesses[currentGuess].prepareCursor();
                                        }
                                    }
                                    break;
                            }
                        }
                    } else { // end state, win/loss
                        if (key == KeyEvent.VK_ENTER) {
                            //System.out.println("ENTER");
                            update = true;
                            gameState = GAME_RUNNING;
                            prepareGame();
                        }
                    }
                } else { // screen is main menu
                    // swap to game, reset relevant stats
                    if (key == KeyEvent.VK_ENTER) {
                        //System.out.println("ENTER");
                        screen = GAME_MENU;
                        //System.out.println("Screen -> Game");
                        update = true;
                        prepareGame();
                    }
                }
                
                if (update) {
                    //System.out.println("Redrawing frame...");
                    mainframe.repaint();
                }
            }
        });
        
        if (fullscreened) {
            mainframe.setExtendedState(JFrame.MAXIMIZED_BOTH); 
            mainframe.setUndecorated(true);
        }
        
        //mainframe.repaint(); // redraws the mainframe once
        mainframe.setVisible(true); // draws twice for some reason
    }
    
    private static String[] loadDictionary(String location) {
        Scanner dictscanner = null;
        try {
            dictscanner = new Scanner(new File(location));
        } catch (Exception e) {
            //System.out.println(e);
            System.exit(0);
        }
        ArrayList<String> dictlist = new ArrayList<String>();
        while (dictscanner.hasNext()) {
            dictlist.add(dictscanner.next().strip());
        }
        dictscanner.close();
        return dictlist.toArray(new String[0]);
    }
    
    private static Game prepareFrame(JFrame mainframe) {
        Game gamepanel = new Game(); // changed this line
        mainframe.add(gamepanel);
        return gamepanel;
    }
    
    // drawing logic starts here
    
    @Override
    public void paintComponent(Graphics g) { // exposes the graphics context used to draw to the screen. all logic must occur in here!
        super.paintComponent(g);
        long time = System.nanoTime();
        g.setColor(DrawingUtils.BACKGROUND_COLOR); // wipe the board
        g.fillRect(0, 0, 128*Game.WINDOW_SCALE_FACTOR, 72*Game.WINDOW_SCALE_FACTOR+1);
        // redraw according to state
        if (screen == MAIN_MENU) {
            g.setColor(DrawingUtils.TEXT_COLOR);
            DrawingUtils.drawText(g, "anagrammatica", 64, 20, true);
            g.setColor(DrawingUtils.SUBTEXT_COLOR);
            DrawingUtils.drawText(g, "enter to start", 64, 35, true);
            DrawingUtils.drawText(g, "space for help", 64, 41, true);
        } else if (screen == GAME_MENU) {
            g.setColor(DrawingUtils.TEXT_COLOR);
            if (gameState == GAME_RUNNING) {
                DrawingUtils.drawText(g, Guess.shuffledWord, 64, 3, true);
            } else {
                if (gameState == GAME_LOSS) {
                    g.setColor(DrawingUtils.INCORRECT_COLOR);
                } else {
                    g.setColor(DrawingUtils.CORRECT_COLOR);
                }
                DrawingUtils.drawText(g, Guess.correctWord, 64, 3, true);
            }
            int i = 0;
            for (; i < guesses.length; i++) {
                // color is automatically managed by the guesses class
                guesses[i].draw(g, 64, 9+6*i, true);
            }
            switch (gameState) {
                case GAME_WIN:
                    g.setColor(DrawingUtils.CORRECT_COLOR);
                    DrawingUtils.drawText(g, "you win", 64, 52, true);
                    break;
                case GAME_LOSS:
                    g.setColor(DrawingUtils.INCORRECT_COLOR);
                    DrawingUtils.drawText(g, "you lost", 64, 52, true);
                    break;
            }
            if (gameState != GAME_RUNNING) {
                g.setColor(DrawingUtils.SUBTEXT_COLOR);
                DrawingUtils.drawText(g, "enter to restart", 64, 58, true);
                DrawingUtils.drawText(g, "escape to exit", 64, 64, true);
            } else {
                g.setColor(DrawingUtils.SUBTEXT_COLOR);
                DrawingUtils.drawText(g, "space for help", 64, 61, true);
            }
        }
        //System.out.println((System.nanoTime()-time)/1000000.+" ms of drawing time");
    }
    
    private static void prepareGame() {
        gameState = GAME_RUNNING;
        currentGuess = 0;
        Guess.setWinWord(dictionary[(int)(Math.random()*dictionary.length)]); // sets the victory word to a random word from the dictionary
        guesses = new Guess[Guess.correctWord.length()]; // gives the player winword length+1 guesses
        for (int i = 0; i < guesses.length; i++) {
            guesses[i] = new Guess();
        }
        guesses[0].prepareCursor();
    }
}
