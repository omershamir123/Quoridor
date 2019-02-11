/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import GUI.BoardPanel;
import GUI.Cell;
import GUI.Enums;
import GUI.HorizontalWall;
import GUI.VerticalWall;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashSet;
import javafx.util.Pair;
import javax.swing.JLabel;

/**
 *
 * @author Omer
 */
public class LogicBoard
{

    public static LogicBoard instance = null;
    // The gameboard itself
    public Cell[][] gameboard;
    // The size of the board
    public int BSize;
    // The number of walls for each player
    private int MaxWalls;
    // An arraylist of the players in the current game
    public ArrayList<Player> players;
    public static BoardPanel panel;
    // set of intesections available for vertical walls on the board
    public HashSet<Pair<Integer, Integer>> VerticalIntersections;
    // set of intesections available for horizontal walls on the board
    public HashSet<Pair<Integer, Integer>> HorizontalIntersections;
    
    // singleton of board
    public static LogicBoard getInstance()
    {
        if (instance == null)
        {
            instance = new LogicBoard();
        }
        return instance;
    }

    public void Init()
    {
        this.BSize = 9;
        this.MaxWalls = 10;
        // setting up the walls for this game
        for (int i = 0; i < 2 * this.MaxWalls; i++)
        {
            VerticalWall vw = new VerticalWall();
            vw.SetCoordinates(this.BSize * 60 + 75, (this.BSize / 2 - 1) * 60 + 25);
            vw.setSize(10, 110);
            panel.add(vw);

            HorizontalWall hw = new HorizontalWall();
            hw.SetCoordinates(this.BSize * 60 + 25, (this.BSize / 2 - 1) * 60 + 5);
            hw.setSize(110, 10);
            panel.add(hw);
        }
        setCells();
        setIntersections();
    }

    private void setCells()
    {
        this.gameboard = new Cell[this.BSize][this.BSize];
        for (int i = 0; i < this.BSize; i++)
        {
            for (int j = 0; j < this.BSize; j++)
            {
                this.gameboard[i][j] = new Cell(i, j);
                panel.add(this.gameboard[i][j]);
            }
        }
        // Creates for each Cell its neighbors
        // Doesn't go over the right and bottom border
        for (int i = 0; i < this.BSize - 1; i++)
        {
            for (int j = 0; j < this.BSize - 1; j++)
            {
                this.gameboard[i][j].addNeighbor(this.gameboard[i + 1][j], Enums.Paths.BOTTOM);
                this.gameboard[i][j].addNeighbor(this.gameboard[i][j + 1], Enums.Paths.RIGHT);
            }
        }
        // add for the right and bottom boundary only their bottom and right neighbors accrodingly
        for (int i = 0; i < this.BSize - 1; i++)
        {
            this.gameboard[this.BSize - 1][i].addNeighbor(this.gameboard[this.BSize - 1][i + 1], Enums.Paths.RIGHT);
            this.gameboard[i][this.BSize - 1].addNeighbor(this.gameboard[i + 1][this.BSize - 1], Enums.Paths.BOTTOM);
        }
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
}
