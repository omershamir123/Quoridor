/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import GUI.Cell;

/**
 *
 * @author Omer
 */
public class Player
{
    private String name;
    private Cell place;
    private int WallsLeft;

    public Player(String name, int walls)
    {
        this.name = name;
        this.place = null;
        this.WallsLeft = walls;
    }

    public int getWallsLeft()
    {
        return WallsLeft;
    }

    public void setWallsLeft(int WallsLeft)
    {
        this.WallsLeft = WallsLeft;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Cell getPlace()
    {
        return place;
    }

    public void setPlace(Cell place)
    {
        this.place = place;
    }
}
