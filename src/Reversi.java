import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Dimension;

/**Reversi
  * @author James Timotiwu
  */
public class Reversi extends JFrame {
    //stores the current board
    private JPanel board;
    //stores the user interface
    private JPanel userI;
    //stores info about how many pieces black has on the board
    private static JTextField blackInfo = new JTextField("Black points: 0");
    //stores info about how many pieces white has on the board
    private static JTextField whiteInfo = new JTextField("White points: 0");
    //stores all the JButtons on the board
    private JButton[][] pieces;
    //determines the player who needs to play a move
    private boolean currentPlayer = true;
    //determines the color of the board
    private Color boardColor = new Color(82, 168, 113);
    //toggle to display the legal moves for each player
    private JCheckBox showLegalMoves = new JCheckBox("Show all legal moves", false);
    //determines if any legal moves are displayed/shown on the board
    private boolean legalMovesDisplayed = false;
    
    /**
     * Creates a 8 x 8 Reversi board
     */
    public Reversi() {
        this(8, 8);
    }
    
    /**
     * Creates a square board of any size with the same width and height
     * @param boardsize The width and height of the board
     */
    public Reversi(int boardsize) {
        this(boardsize, boardsize);
    }
    
    /**
     * Creates a board with a specified width and height
     * @param height the height of the board to be played on
     * @param width the width of the board to be played on
     */
    public Reversi(int height, int width) {
        pieces = new JButton[height][width];
        board = new JPanel(new GridLayout(height, width));
        userI = new JPanel();
        userI.setLayout(new BoxLayout(userI, BoxLayout.X_AXIS));
        whiteInfo.setEditable(false);
        blackInfo.setEditable(false);
        whiteInfo.setBackground(Color.YELLOW);
        setTitle("Reversi   -   Current Player: " + (currentPlayer ? "White" : "Black"));
        showLegalMoves.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                legalMovesDisplayed = !legalMovesDisplayed;
                getLegalMoves();
                if(legalMovesDisplayed == false)
                    clearGray();
            }
        });
        
        /** Creates a JButton array for the board */
        for(int i = 0; pieces.length > i; i++) {
            for(int j = 0; pieces[i].length > j; j++) {
                pieces[i][j] = new JButton();
                pieces[i][j].setBackground(boardColor);
                
                /**Action listener to check for button clicks*/
                pieces[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JButton b = (JButton)e.getSource();
                        if(isLegal(b) && getLegalMoves() != 0){
                            flipSides(b);
                            clearGray();
                            updateInfo();
                            if(getLegalMoves() == 0){
                                currentPlayer = !currentPlayer;
                                //if both players have no legal moves, the game is effectively over
                                if(getLegalMoves() == 0){
                                    if(getSideTotal(Color.WHITE) == getSideTotal(Color.BLACK))
                                        JOptionPane.showMessageDialog(board, "The game has ended in a draw!");
                                    else
                                        JOptionPane.showMessageDialog(board, (isWinner() ? "White" : "Black") + " wins! \nWhite total: " + getSideTotal(Color.WHITE) + " Black total: " + getSideTotal(Color.BLACK));
                                }
                            }
                        }
                    }
                });
                
                board.add(pieces[i][j]);
            }
        }
        
        /** set up initial pieces */
        pieces[pieces.length / 2][pieces[pieces.length / 2].length / 2].setBackground(Color.WHITE);
        pieces[(pieces.length / 2) - 1][(pieces[(pieces.length / 2) - 1].length / 2) - 1].setBackground(Color.WHITE);
        pieces[pieces.length / 2][(pieces[(pieces.length / 2) - 1].length / 2) - 1].setBackground(Color.BLACK);
        pieces[(pieces.length / 2) - 1][pieces[pieces.length / 2].length / 2].setBackground(Color.BLACK);
        
        getLegalMoves();
        
        this.userI.add(blackInfo);
        this.userI.add(whiteInfo);
        this.getContentPane().add(this.showLegalMoves, "South");
        this.getContentPane().add(this.userI, "North");
        this.getContentPane().add(this.board, "Center");
        board.setPreferredSize(new Dimension(width * 80, height * 80));
        this.pack();
        this.setVisible(true);
        this.setResizable(false);
    }
    
    /**
     * Checks if the piece is still within the bounds of the board
     * @param x the x coordinate of the piece on the board
     * @param y the y coordinate of the piece on the board
     * @return true if within bounds, false if not
     */
    public boolean inBounds(int x, int y) {
        return (y >= 0 && y < (this.pieces.length) && x >= 0 && x < (this.pieces[0].length));
    }
    
    /**
     * Checks if the current button does not have a piece on top of it
     * @param b the JButton that needs to be checked
     * @return true if the board has no pieces on top
     */
    public boolean isEmpty(JButton b) {
        return ((b.getBackground() != Color.WHITE) && (b.getBackground() != Color.BLACK));
    }
    
    /**
     * Retrieves the location of the button on the board
     * @param b the JButton that needs to be checked
     * @return Integer array of the x and y location of the button
     */
    public int[] getArrayPosition(JButton b) {
        for(int i = 0; pieces.length > i; i++) {
            for(int j = 0; pieces[i].length > j; j++) {
                if(pieces[i][j] == b){
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }
    
    /**
     * Counts the number of pieces that a color has on the board
     * @param c the color of the piece it refers to
     * @return The number of pieces of the same color on the board
     */
    public int getSideTotal(Color c) {
        int sides = 0;
        for(int i = 0; pieces.length > i; i++) {
            for(int j = 0; pieces[i].length > j; j++) {
                if(pieces[i][j].getBackground() == c)
                    sides++;
            }
        }
        return sides;
    }
    
    /**
     * Displays the legal moves allowed on the board and returns the number of legal moves
     * @return number of legal moves
     */
    public int getLegalMoves() {
        int legalMoves = 0;
        for(int i = 0; pieces.length > i; i++) {
            for(int j = 0; pieces[i].length > j; j++) {
                if(isLegal(pieces[i][j])){
                    if(legalMovesDisplayed)
                        pieces[i][j].setBackground(Color.GRAY);
                    legalMoves++;
                }
            }
        }
        return legalMoves;
    }
    
    /**
     * Clears the list of legal moves provided on the board
     */
    public void clearGray() {
        for(int i = 0; pieces.length > i; i++) {
            for(int j = 0; pieces[i].length > j; j++) {
                if(pieces[i][j].getBackground() == Color.GRAY)
                    pieces[i][j].setBackground(boardColor);
            }
        }
    }
    
    /**
     * Updates game information
     */
    public void updateInfo() {
        currentPlayer = !currentPlayer;
        setTitle("Reversi   -   Current Player: " + (currentPlayer ? "White" : "Black"));
        blackInfo.setText("Black points: " + getSideTotal(Color.BLACK));
        whiteInfo.setText("White points: " + getSideTotal(Color.WHITE));
        whiteInfo.setBackground(currentPlayer ? Color.YELLOW : null);
        blackInfo.setBackground(currentPlayer ? null : Color.YELLOW);
    }
    
    /**
     * Checks if button can be played and is a legal move
     * @param b the button that needs to be checked
     * @return true if piece is playable and legal
     */
    public boolean isLegal(JButton b){
        
        if(!isEmpty(b))
            return false;
        
        //loop to traverse across the board in all directions
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                
                if (dy != 0 || dx != 0) {
                    int x = getArrayPosition(b)[1] + dx;
                    int y = getArrayPosition(b)[0] + dy;
                    //if x, y iwithin bounds of the array, continue checking the next piece ahead as long as the background piece is not the current player or an empty button
                    if(inBounds(x, y) && ((currentPlayer ? Color.WHITE : Color.BLACK) != pieces[y][x].getBackground()) && (boardColor != pieces[y][x].getBackground()) && (Color.GRAY != pieces[y][x].getBackground())) {
                        //increments through the board in a certain direction until the next piece is no longer the opposing piece that the player is playing against
                        while(inBounds(x, y) && ((currentPlayer ? Color.WHITE : Color.BLACK) != pieces[y][x].getBackground()) && (boardColor != pieces[y][x].getBackground()) && (Color.GRAY != pieces[y][x].getBackground())) {
                            x += dx;
                            y += dy;
                        }
                        //if same color piece is found, the move is legal and the pieces can be skipped
                        if (inBounds(x, y) && ((currentPlayer ? Color.WHITE : Color.BLACK) == pieces[y][x].getBackground()))
                            return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Flips all the pieces needed when a button is played
     * @param b the button being played
     */
    public void flipSides(JButton b){
        int posX = getArrayPosition(b)[1];
        int posY = getArrayPosition(b)[0];
        
        //loop to traverse across the board in all directions
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                
                if (dy != 0 || dx != 0) {
                    int x = posX + dx;
                    int y = posY + dy;
                    
                    //checks certain direction for opposing player pieces until the current players piece is reached
                    while(inBounds(x, y) && ((currentPlayer ? Color.WHITE : Color.BLACK) != pieces[y][x].getBackground()) && (boardColor != pieces[y][x].getBackground()) && (Color.GRAY != pieces[y][x].getBackground())) {
                        x += dx;
                        y += dy;
                    }
                    
                    if(inBounds(x, y) && ((currentPlayer ? Color.WHITE : Color.BLACK) == pieces[y][x].getBackground())) {
                        x = posX + dx;
                        y = posY + dy;
                        //flips all pieces on the direction that is confirmed to work properly
                        while(inBounds(x, y) && ((currentPlayer ? Color.WHITE : Color.BLACK) != pieces[y][x].getBackground()) && (boardColor != pieces[y][x].getBackground()) && (Color.GRAY != pieces[y][x].getBackground())){
                            pieces[y][x].setBackground(currentPlayer ? Color.WHITE : Color.BLACK);
                            x += dx;
                            y += dy;
                        }
                    }
                }
            }
        }
        b.setBackground(currentPlayer ? Color.WHITE : Color.BLACK);
    }
    
    /**
     * Checks which color wins of the game
     * @return true if white wins, false if black wins
     */
    public boolean isWinner() {
        return (getSideTotal(Color.WHITE) > getSideTotal(Color.BLACK));
    }
    
    /**
     * Gets the button of the specified location
     * @param x the x coordinate of the button
     * @param y the y coordinate of the button
     * @return the button at the specified location
     */
    public JButton getButton(int x, int y) {
        return pieces[x][y];
    }
    
    /**
     * Launches the game
     * @param args[] the height and width of the board
     */
    public static void main (String args[]) {
        if(args.length == 2 && (Integer.parseInt(args[0]) != 0 || Integer.parseInt(args[1]) != 0)){
            try {
                new Reversi(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
            }
            catch (ArrayIndexOutOfBoundsException e) {
                new Reversi();
            }
        }
        else if (args.length == 1 && Integer.parseInt(args[0]) != 0){
            try {
                new Reversi(Integer.parseInt(args[0]));
            }
            catch (ArrayIndexOutOfBoundsException e) {
                new Reversi();
            }
        }
        else
            new Reversi();
    }
}