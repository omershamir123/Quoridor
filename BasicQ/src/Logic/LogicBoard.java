/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import GUI.Cell;
import GUI.BoardPanel;
import GUI.HorizontalWall;
import GUI.VerticalWall;
import java.awt.Font;
import java.util.HashSet;
import javafx.util.Pair;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author Omer
 */
public class LogicBoard
{
    public enum Paths
    {
        TOP, BOTTOM, LEFT, RIGHT
    };

    // The gameboard 
    public GUI.Cell[][] cells;
    // The board panel
    public BoardPanel panel;
    // Array of players in the game
    public Logic.Player[] players;
    // The board size
    public int BSize = 9;
    // The number of walls allowed for each player
    private final int MaxWalls = 10;
    // The index of the current player in the player array
    public int currentPlayer = 0;
    // the current turn of the game
    public int turnNumber = 0;
    // Is the game over
    public boolean gameOver = false;
    // The game mode - 0 (1VS1) OR 1(1VSComp)
    private int gameMode;
    private final int COMPUTER_GAME = 1;
    // set of intesections available for vertical walls on the board
    public HashSet<Pair<Integer, Integer>> VerticalIntersections;
    // set of intesections available for horizontal walls on the board
    public HashSet<Pair<Integer, Integer>> HorizontalIntersections;

    /**
     * Constructor of the LogicBoard, sets up all of the cell and the players
     *
     * @param panel - the boardPanel of the board
     */
    public LogicBoard(BoardPanel panel)
    {
        this.panel = panel;
        this.gameMode = chooseGameMode();
        if (this.gameMode == -1)
            System.exit(0);
        setWalls();
        setCells();
        setIntersections();
        setPlayers();
    }
    
    private int chooseGameMode()
    {
        String[] choices = { "Player Vs. Player", "Player Vs. Computer" };
        String input = (String) JOptionPane.showInputDialog(null, "Choose the game Mode you wish to select",
        "QUORIDOR", JOptionPane.QUESTION_MESSAGE, null, choices, choices[1]);
        for (int i = 0; i < choices.length; i++)
            if (choices[i].equals(input))
                return i;
        return -1;
    }

    // setting up the walls for this game
    private void setWalls()
    {
        for (int i = 0; i < 2 * this.MaxWalls; i++)
        {
            VerticalWall vw = new VerticalWall(this);
            vw.SetCoordinates(this.BSize * Cell.CELL_WIDTH + 75, (this.BSize / 2 - 1) * Cell.CELL_WIDTH + 25);
            //vw.setSize(10, 110);
            panel.add(vw);

            HorizontalWall hw = new HorizontalWall(this);
            hw.SetCoordinates(this.BSize * Cell.CELL_WIDTH + 25, (this.BSize / 2 - 1) * Cell.CELL_WIDTH + 5);
            //hw.setSize(110, 10);
            panel.add(hw);
        }
    }

    /**
     * Sets up the cells of the board, adding he buttons and their neighbors
     */
    private void setCells()
    {
        this.cells = new Cell[this.BSize][this.BSize];
        for (int i = 0; i < BSize; i++)
        {
            for (int j = 0; j < BSize; j++)
            {
                cells[i][j] = new Cell(i, j, this);
                panel.add(cells[i][j]);
            }
        }
        for (int i = 0; i < this.BSize - 1; i++)
        {
            for (int j = 0; j < this.BSize - 1; j++)
            {
                cells[i][j].addNeighbor(cells[i + 1][j], Paths.BOTTOM);
                cells[i][j].addNeighbor(cells[i][j + 1], Paths.RIGHT);
            }
        }
        // add for the right and bottom boundary only their bottom and right neighbors accrodingly
        for (int i = 0; i < this.BSize - 1; i++)
        {
            cells[this.BSize - 1][i].addNeighbor(cells[BSize - 1][i + 1], Paths.RIGHT);
            cells[i][this.BSize - 1].addNeighbor(cells[i + 1][BSize - 1], Paths.BOTTOM);
        }
    }

    // Sets the players of the game, their location and their icons
    private void setPlayers()
    {
        this.players = new Player[2];
        this.players[0] = new Player(1, BSize - 1, -1, this.MaxWalls);
        if (this.gameMode == COMPUTER_GAME)
            this.players[1] = new AI(2, 0, -1, this.MaxWalls, this);
        else
            this.players[1] = new Player(2, 0, -1, this.MaxWalls);
        this.currentPlayer = 0;
        players[0].setPlace(cells[0][4]);
        players[1].setPlace(cells[8][4]);
        for (Player player : players)
        {
            JLabel wallsInfo = new JLabel("Player "+player.playerNo+" walls Left: "+player.getWallsLeft());
            wallsInfo.setSize(500,30);
            wallsInfo.setFont(new Font("ComicSans", 1, 16));
            wallsInfo.setLocation(10+(player.playerNo-1)*250, BSize * Cell.CELL_WIDTH + 15);
            wallsInfo.setForeground(player.playerColor);
            panel.add(wallsInfo);
            player.setWallsInfo(wallsInfo);
        }
        players[0].place.SetOptionsForCurrentPlayer(true);
    }

    // sets all of the available intersections for walls in the game
    private void setIntersections()
    {
        this.VerticalIntersections = new HashSet<>();
        this.HorizontalIntersections = new HashSet<>();
//        for (int i = 1; i < this.BSize; i++)
//        {
//            for (int j = 1; j <= this.BSize; j++)
//            {
//                this.VerticalIntersections.add(new Pair(i, j));
//                this.HorizontalIntersections.add(new Pair(i, j));
//            }
//        }
    }

    /**
     * This function ends the turn of the current player: Sets all of its
     * current neighbors to unclickable Sets its place to the cell given Changes
     * the current player, and sets its neighbors to clickable If it is not a
     * wall move - the function updates the current cells' neighbors to not
     * clickable. Otherwise, it has already been done in the mouse Released
     * function of the walls. If it is a wall drag, updates the number of walls
     * of the current player
     */
    public void endTurn(Cell cell, boolean isWallMove)
    {
        this.players[this.currentPlayer].place.SetOptionsForCurrentPlayer(false);
        this.players[this.currentPlayer].place.setIcon(null);
        this.players[this.currentPlayer].setPlace(cell);
        this.turnNumber++;
        // only disables current cells' neighbors if it is a move of pieces
        if (!isWallMove)
        {
            // Check whether the current player has won
            if (this.players[this.currentPlayer].getEndingRow() == cell.getRow() || 
                    this.players[this.currentPlayer].getEndingCol() == cell.getCol())
            {
                endGame();
                return;
            }
        } else
        {
            this.players[this.currentPlayer].setWallsLeft(this.players[currentPlayer].getWallsLeft() - 1);
        }
        this.currentPlayer = (this.currentPlayer + 1) % this.players.length;
        this.panel.info.setText("Player " + (this.currentPlayer + 1) + "'s Turn");
        if (this.players[this.currentPlayer] instanceof AI)
            ((AI)this.players[this.currentPlayer]).computerMove();
        else
            this.players[this.currentPlayer].place.SetOptionsForCurrentPlayer(true);
    }
    
    // The game has ended and the current player has won
    private void endGame()
    {
        this.gameOver = true;
        this.panel.info.setText("Player " + (this.currentPlayer + 1) + " Won the game");
        this.panel.info.setForeground(this.players[this.currentPlayer].playerColor);
        String[] choices = { "Yes", "No" };
        String input = (String)JOptionPane.showInputDialog(null, "Player " + (this.currentPlayer + 1) + " won the game \n Would you like to replay?", 
                "WINNER", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
        if (input.equals("Yes"))
        {
            this.panel.removeAll();
            this.panel.setPanel();
            LogicBoard l = new LogicBoard(panel);
            this.panel.board = l;
            this.panel.revalidate();
            this.panel.repaint();
        }
    }
}
