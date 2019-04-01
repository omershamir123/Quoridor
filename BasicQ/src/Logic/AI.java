/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import GUI.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import javafx.util.Pair;

/**
 *
 * @author Omer
 */
public class AI extends Player
{
    private final LogicBoard board;

    public AI(int playerNo, int endingRow, int endingCol, int MaxWalls, LogicBoard board)
    {
        super(playerNo, endingRow, endingCol, MaxWalls);
        this.board = board;
    }

    @SuppressWarnings("empty-statement")
    public void computerMove()
    {
        ArrayList<Cell[]> moveOptions = this.place.getMoveOptions();
        ArrayList<Cell> shortestPath = BFS(moveOptions, this, null);
        ArrayList<Cell[]> opMoveOptions = this.board.players[(this.board.currentPlayer + 1) % 2].place.getMoveOptions();
        ArrayList<Cell> opponentPath = BFS(opMoveOptions, this.board.players[(this.board.currentPlayer + 1) % 2], null);
        // If the opponent's shortest path is shorter than the computer by more than one
        // Or the path length of the opponent is only one square, place a wall to block the opponent
        if (shortestPath.size() > 2 && ( shortestPath.size() - 1 > opponentPath.size() || (opponentPath.size() < 3 )))
        {
            if (this.wallsLeft == 0 || !blockOpponent(opponentPath, opMoveOptions, false))
                tryToMove(moveOptions, shortestPath, opMoveOptions, opponentPath);
        }
        else
            tryToMove(moveOptions, shortestPath, opMoveOptions, opponentPath);
            //board.endTurn(shortestPath.get(1), false);
    }

    // static so that all of the classes could access the method (to chek if a wall placement is possible)
    // The BFS works wither until it reaches a certain cell (if it is not null)
    // or until it reaches the ending point of the current player
    public static ArrayList<Cell> BFS(ArrayList<Cell[]> moveOptions, Player player, Cell destination)
    {
        boolean pathFound = false;
        Queue<Cell> queue = new LinkedList<>();
        HashSet<Cell> visited = new HashSet<>();
        HashMap<Cell, Cell> parentNodes = new HashMap<>(); // Key - node, value - parent
        Cell[] neighbors;
        Cell nextNode = null;

        queue.add(player.place);
        parentNodes.put(player.place, null);

        // this loop will stop when:
        // we have gone over all of the reachable nodes and did not reach the end line
        // we have reached a possible path and its start will be in the variable c
        while (!queue.isEmpty() && !pathFound)
        {
            nextNode = queue.remove();
            if ((destination == null && nextNode.getRow() == player.getEndingRow() || nextNode.getCol() == player.getEndingCol())
                    || (destination != null && nextNode.equals(destination)))
            {
                pathFound = true;
            }
            visited.add(nextNode);
            if (nextNode.equals(player.place))
            {
                neighbors = ConvertOptionsToArray(moveOptions);
            } else
            {
                neighbors = nextNode.neighbors;
            }
            for (Cell neighbor : neighbors)
            {
                if (!visited.contains(neighbor) && neighbor != null)
                {
                    parentNodes.put(neighbor, nextNode);
                    queue.add(neighbor);
                }
            }
        }
        if (!pathFound)
            return null;
        else
        {
            ArrayList<Cell> shortestPath = new ArrayList<>();
            while (nextNode != null)
            {
                shortestPath.add(nextNode);
                nextNode = parentNodes.get(nextNode);
            }
            Collections.reverse(shortestPath);
            return shortestPath;
        }
    }

    public static Cell[] ConvertOptionsToArray(ArrayList<Cell[]> moveOptions)
    {
        ArrayList<Cell> neighbors = new ArrayList<>();
        for (Cell[] next : moveOptions)
        {
            for (Cell next1 : next)
            {
                if (next1 != null)
                {
                    neighbors.add(next1);
                }
            }
        }
        return (Cell[]) neighbors.toArray(new Cell[4]);
    }

    /**
     * This function determines the row and col of the wall based on the
     * neighborIndex. It creates the instance of the appropriate wall
     *
     * @param row - the row of the origin of the move
     * @param col - the column of the origin of the move
     * @param neighborIndex - the index in the neighbor array of the relation to
     * the next cell in the path
     * @return a Pair of the new coordinates of the top left corner of the wall,
     * as well as the appropriate wall
     */
    private Pair<Wall, Pair<Integer, Integer>> determineRowAndColOfWall(int row, int col, int neighborIndex)
    {
        Pair<Integer, Integer> coordinate;
        Wall wall;
        switch (neighborIndex)
        {
            case 0:
                wall = new HorizontalWall(board);
                coordinate = new Pair(row, col);
                break;
            case 1:
                wall = new HorizontalWall(board);
                coordinate = new Pair(row + 1, col);
                break;
            case 2:
                wall = new VerticalWall(board);
                coordinate = new Pair(row, col);
                break;
            case 3:
                wall = new VerticalWall(board);
                coordinate = new Pair(row, col + 1);
                break;
            default:
                coordinate = new Pair(-1, -1);
                wall = null;
                break;
        }
        return new Pair(wall, coordinate);
    }
    
    // This function determines the direction in which the first move of the opponent goes 
    private int determineDirectionOfFirstMove(int pathIndex, ArrayList<Cell> opponentPath, ArrayList<Cell[]> opMoveOptions)
    {
        int i = -1;
        for (Iterator<Cell[]> iterator = opMoveOptions.iterator(); iterator.hasNext();)
        {
            i++;
            Cell[] next = iterator.next();
            if (next[0] != null)
            {
                for (Cell cell : next)
                {
                    if (cell != null && cell.equals(opponentPath.get(pathIndex + 1)))
                    {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    
    // This function determines the direction in which the path of the opponent goes.
    private int determineDirection(int pathIndex, ArrayList<Cell> opponentPath)
    {
        Cell origin = opponentPath.get(pathIndex);
        Cell destination = opponentPath.get(pathIndex+1);
        for (int i = 0; i < origin.neighbors.length; i++)
        {
            if (origin.neighbors[i] != null && origin.neighbors[i].equals(destination))
            {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * This function determines whether or not a wall can be placed that is beneficial to the AI
     * If so, it places it.
     * It receives the opponent path and move options, and for each cell in its path tries to place
     * a wall to block it. Then it compares the difference in path lengths to the min difference, and chooses the best one
     * The path difference is MyBFS - OpBFS, so the AI's goal is to get the lowest difference (best if negative)
     * @param opponentPath - the cell path of the opponent
     * @param opMoveOptions - the current move options of the opponent
     * @param fastLaneClosure - indicates whether or not this blocking move is to block a fast lane
     * @return true if wall has been placed, false if otherwise
     */
    private boolean blockOpponent(ArrayList<Cell> opponentPath, ArrayList<Cell[]> opMoveOptions, boolean fastLaneClosure)
    {
        System.out.println("BLOCKING OPPONENT");
        int row = opponentPath.get(0).getRow();
        int col = opponentPath.get(0).getCol();
        int bestRow = -1, bestCol = -1, maxDifferenceInPath = Integer.MAX_VALUE;
        // neighborIndex - the direction of the shortest path
        // pathIndex - the cell in the path we are currently trying to block
        int pathIndex = 0, neighborIndex;
        Wall wall, bestWall = null;
        boolean triedBothOptions = false;
        neighborIndex = determineDirectionOfFirstMove(pathIndex, opponentPath, opMoveOptions);
        Pair<Wall, Pair<Integer, Integer>> coordinate = determineRowAndColOfWall(row, col, neighborIndex);
        wall = coordinate.getKey();
        row = coordinate.getValue().getKey();
        col = coordinate.getValue().getValue();
        Point p = new Point(col * 60, row * 60);
        while (pathIndex < opponentPath.size() - 1)
        {
            // check if the wall proposed is an okay wall...
            if (wall.isLocationValid(p) && !wall.checkIntersections(row, col) && !wall.isPlayerBlocked(row, col))
            {
                // delete the neighbors of this wall in order to mimic an actual board with this wall
                wall.deleteNeighbors(row, col);
                // Now check the BFS of me and my opponent
                ArrayList<Cell> myCurrentBFSPath = BFS(this.place.getMoveOptions(), this, null);
                int myBFS = myCurrentBFSPath.size();
                ArrayList<Cell[]> opCurrentMoveOptions = this.board.players[(this.board.currentPlayer + 1) % 2].place.getMoveOptions();
                ArrayList<Cell> myCurrentOpBFSPath =  BFS(opCurrentMoveOptions, this.board.players[(this.board.currentPlayer + 1) % 2], null);
                int OpBFS = myCurrentOpBFSPath.size();
                // I want to get the minimal difference. If the opponent's path is longer, thedifference ill be negative
                if (maxDifferenceInPath > myBFS - OpBFS)
                {
                    // if you need the placement to close up a fast lane
                    if (fastLaneClosure)
                    {
                        // check whether the path to the next cell is now possible or taking much longer!!
                        ArrayList<Cell> newPathToNextCell = BFS(opMoveOptions, this.board.players[(this.board.currentPlayer + 1) % 2], opponentPath.get(pathIndex+1));
                        if (newPathToNextCell == null || newPathToNextCell.size() > pathIndex + 4)
                        {
                            bestCol = col;
                            bestRow = row;
                            bestWall = wall;
                            maxDifferenceInPath = myBFS - OpBFS;
                        }
                    }
                    else
                    {
                        bestCol = col;
                        bestRow = row;
                        bestWall = wall;
                        maxDifferenceInPath = myBFS - OpBFS;
                    }
                }
                wall.resetNeighbors(row, col);
            }
            // Each wall has two different positions possible to block a square.
            // If only one has been tried, try the other option
            if (!triedBothOptions)
            {
                if (wall instanceof VerticalWall)
                {
                    row--;
                } else
                {
                    col--;
                }
                p = new Point(col * 60, row * 60);
                triedBothOptions = true;
            } else
            {
                pathIndex++;
                if (pathIndex < opponentPath.size() - 1)
                {
                    neighborIndex = determineDirection(pathIndex, opponentPath);
                    row = opponentPath.get(pathIndex).getRow();
                    col = opponentPath.get(pathIndex).getCol();
                    coordinate = determineRowAndColOfWall(row, col, neighborIndex);
                    wall = coordinate.getKey();
                    row = coordinate.getValue().getKey();
                    col = coordinate.getValue().getValue();
                    p = new Point(col * 60, row * 60);
                    triedBothOptions = false;
                }
            }
        }
        // By this point either we have found a wall to build or there is no wall placement possible
        if (bestWall == null)
        {
            return false;
        }
        board.panel.add(bestWall);
        bestWall.placeWall(bestRow, bestCol);
        return true;
    }

    private void tryToMove(ArrayList<Cell[]> moveOptions, ArrayList<Cell> shortestPath, ArrayList<Cell[]> opMoveOptions, ArrayList<Cell> opponentPath)
    {
        if (board.players[(board.currentPlayer+1)%2].getWallsLeft() == 0 || this.wallsLeft == 0)
        {
            board.endTurn(shortestPath.get(1), false);
            return;
        }
        if (!isThereFastLane(opponentPath, opMoveOptions, shortestPath))
        {
             // Try to see your path width
             board.endTurn(shortestPath.get(1), false);
        }
    }

    private boolean isThereFastLane(ArrayList<Cell> opponentPath, ArrayList<Cell[]> opMoveOptions, ArrayList<Cell> myPath)
    {
        int currentPathWidth;
        int lengthOfFastLane = 0;
        boolean laneExists = true;
        for (int i = 0; i < opponentPath.size()-1 && laneExists; i++)
        {
            int neighborIndex = (i == 0)? determineDirectionOfFirstMove(0, opponentPath, opMoveOptions):determineDirection(i, opponentPath);
            currentPathWidth = calculatePathWidth(opponentPath.get(i), neighborIndex, opponentPath.get(i+1));
            if (currentPathWidth <= 2)
                lengthOfFastLane++;
            else
                laneExists = false;
            System.out.println(opponentPath.get(i).getRow()+" "+opponentPath.get(i).getCol());
        }
        // If the line length is less than 6 OR less than the opponent path length
        if (lengthOfFastLane < opponentPath.size() - 2 && lengthOfFastLane < 6)
            return false;
        // By now, we fully understand there is a fast lane in build, and we try to block it
        // now check if the shortest path of the computer does not intertwine with the fast lane found
        for (int i = 0; i < lengthOfFastLane; i++)
        {
            if(myPath.contains(opponentPath.get(i)))
                return false;
        }
        // Try To Block the opponent NOW
        return blockOpponent(opponentPath, opMoveOptions, true);
    }
    
    // This function returns the path width of the current move, based on the origin cell and neighborIndex
    private int calculatePathWidth(Cell origin, int neighborIndex, Cell destination)
    {
        // if the neighbor direction is top or bottom, you need to scan to the right and to the left
        // if the neighbor direction is left or right, you need to scan to the top and to the bottom
        int scanningDirection = (neighborIndex < 2)?2:0;
        int pathWidth = 1;
        Cell current = origin.neighbors[scanningDirection];
        // If the move is not a direct move, but a diagonal move, ASSUME its path width is problematic
        if (!origin.neighbors[neighborIndex].equals(destination))
            return pathWidth;
        // checks whether or not there is a path width in the direction of scanningDirection
        // If the board looked like the board below, the while loop should not have entered the loop of the left scan
        if (origin.neighbors[neighborIndex].neighbors[scanningDirection] == null)
            current = null;
        while (current != null && current.neighbors[neighborIndex] != null)
        {
            pathWidth++;
            // make sure there isn't a wall in the scanning direction of the next cell
            // *   * | *   *   *
            // *   * | B   *   *
            // *   *   A   *   *
            // The path width from A to B should be 3 and not 5, because of the wall to the left of B
            if (current.neighbors[neighborIndex].neighbors[scanningDirection] != null)
                //&& current.neighbors[neighborIndex].neighbors[neighborIndex] != null
                current = current.neighbors[scanningDirection];
            else
                current = null;
        }
        // GO TO THE OTHER DIRECTION AND REPEAT THE PROCESS
        scanningDirection++;
        current = origin.neighbors[scanningDirection];
        if (origin.neighbors[neighborIndex].neighbors[scanningDirection] == null)
            current = null;
        while (current != null && current.neighbors[neighborIndex] != null)
        {
            pathWidth++;
            if (current.neighbors[neighborIndex].neighbors[scanningDirection] != null)
                   // current.neighbors[neighborIndex].neighbors[neighborIndex] != null)
                current = current.neighbors[scanningDirection];
            else
                current = null;
        }
        return pathWidth;
    }  
}
