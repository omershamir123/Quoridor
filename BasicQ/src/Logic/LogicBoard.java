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
import java.util.HashSet;
import javafx.util.Pair;

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
    private int MaxWalls = 10;
    // The index of the current player in the player array
    public int currentPlayer = 0;
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
        setWalls();
        setCells();
        setIntersections();
        setPlayers();
    }

    // setting up the walls for this game
    private void setWalls()
    {
        for (int i = 0; i < 2 * this.MaxWalls; i++)
        {
            VerticalWall vw = new VerticalWall(this);
            vw.SetCoordinates(this.BSize * 60 + 75, (this.BSize / 2 - 1) * 60 + 25);
            vw.setSize(10, 110);
            panel.add(vw);

            HorizontalWall hw = new HorizontalWall(this);
            hw.SetCoordinates(this.BSize * 60 + 25, (this.BSize / 2 - 1) * 60 + 5);
            hw.setSize(110, 10);
            panel.add(hw);
        }
    }

    /**
     * Sets up the cells of the board, adding he buttons and their neighbors
     */
    private void setCells()
    {
        this.cells = new Cell[9][9];
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
            cells[this.BSize - 1][i].addNeighbor(cells[this.BSize - 1][i + 1], Paths.RIGHT);
            cells[i][this.BSize - 1].addNeighbor(cells[i + 1][BSize - 1], Paths.BOTTOM);
        }
    }

    // Sets the players of the game, their location and their icons
    private void setPlayers()
    {
        this.players = new Player[2];
        this.players[0] = new Player(1, BSize - 1, -1, this.MaxWalls);
        this.players[1] = new Player(2, 0, -1, this.MaxWalls);
        this.currentPlayer = 0;
        players[0].setPlace(cells[0][4]);
        players[1].setPlace(cells[8][4]);
        players[0].place.SetOptionsForCurrentPlayer(true);

    }

    // sets all of the available intersections for walls in the game
    private void setIntersections()
    {
        this.VerticalIntersections = new HashSet<>();
        this.HorizontalIntersections = new HashSet<>();
        for (int i = 1; i < this.BSize; i++)
        {
            for (int j = 1; j <= this.BSize; j++)
            {
                this.VerticalIntersections.add(new Pair(i, j));
                this.HorizontalIntersections.add(new Pair(i, j));
            }
        }
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
        // only disables current cells' neighbors if it is a move of pieces
        if (!isWallMove)
        {
            // Check whether the current player has won
            this.players[this.currentPlayer].place.SetOptionsForCurrentPlayer(false);
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
        this.players[this.currentPlayer].place.setIcon(null);
        this.players[this.currentPlayer].setPlace(cell);
        this.currentPlayer = (this.currentPlayer + 1) % this.players.length;
        this.players[this.currentPlayer].place.SetOptionsForCurrentPlayer(true);
        this.panel.info.setText("Player " + (this.currentPlayer + 1) + "'s Turn");
    }
    
    // The game has ended and the current player has won
    private void endGame()
    {
        this.panel.info.setText("Player " + (this.currentPlayer + 1) + " Won the game");
    }
}