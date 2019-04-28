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
    private final int PATH_LENGTH_TO_WIN = 2;
    // in the beginning, until the 10th turn, the path difference allowed
    // between the two players is 1. After the 10th turn, the path difference 
    // allowed is 0
    private int PATH_DIFFERENCE_ALLOWED = 1;
    private final int TURNS_ALLOWED_UNTIL_DIFF_CHANGED;
    private final LogicBoard board;
    // This string will contain the wall that was found to block a weak spot
    // It will be "" if there is no wall
    // If there is one, it will contain the string as follows:
    // 12233
    // 1 - H/V - the wall tyep
    // 2 - the row of the wall (two digits max)
    // 3 - the col of the wall (two digits max)
    private String wallBlockingWeakSpot;

    public AI(int playerNo, int endingRow, int endingCol, int MaxWalls, LogicBoard board)
    {
        super(playerNo, endingRow, endingCol, MaxWalls);
        this.board = board;
        this.TURNS_ALLOWED_UNTIL_DIFF_CHANGED = board.BSize + 1;
    }

    public void computerMove()
    {
        // reset the wall Blocking the weak Spot
        wallBlockingWeakSpot = "";
        // Check the turn number and switch accordingly the PATH_DIFFERENCE_ALLOWED
        PATH_DIFFERENCE_ALLOWED = (this.board.turnNumber >= TURNS_ALLOWED_UNTIL_DIFF_CHANGED)?0:1;
        ArrayList<Cell[]> moveOptions = this.place.getMoveOptions();
        ArrayList<Cell> shortestPath = BFS(moveOptions, this, null);
        ArrayList<Cell[]> opMoveOptions = this.board.players[(board.currentPlayer + 1) % 2].place.getMoveOptions();
        ArrayList<Cell> opponentPath = BFS(opMoveOptions, board.players[(board.currentPlayer + 1) % 2], null);
        // If the opponent's shortest path is shorter than the computer by more than one
        // depending on the turn number
        // Only try to place a wall if the next move is not a winning move
        if ( shortestPath.size() > PATH_LENGTH_TO_WIN && 
            shortestPath.size() - PATH_DIFFERENCE_ALLOWED > opponentPath.size())
        {
            if (this.wallsLeft == 0 || !blockOpponent(opponentPath, opMoveOptions, false))
                tryToMove(moveOptions, shortestPath, opMoveOptions, opponentPath);
        } else
            tryToMove(moveOptions, shortestPath, opMoveOptions, opponentPath);
    }

    /**
     *static so that all of the classes could access the method (to check if a wall placement is possible).
     * The BFS works wither until it reaches a certain cell (if it is not null)
     * or until it reaches the ending point of the current player
     * 
     * @param moveOptions  the current move Options of the player
     * @param player  the current player to start from
     * @param destination  if you wish to have the BFS to a specific cell, include it in this param
     * @return the ArrayList of the shortest path either to the finish line or to the specific cell 
     */
    public static ArrayList<Cell> BFS(ArrayList<Cell[]> moveOptions, Player player, Cell destination)
    {
        boolean pathFound = false;
        Queue<Cell> queue = new LinkedList<>();
        HashSet<Cell> visited = new HashSet<>();
        HashMap<Cell, Cell> parentNodes = new HashMap<>(); // Key - node, value - parent
        Cell[] neighbors;
        Cell nextNode;
        ArrayList<Cell> shortestPathsEnds = new ArrayList<>();
        queue.add(player.place);
        parentNodes.put(player.place, null);

        // this loop will stop when:
        // we have gone over all of the reachable nodes and did not reach the end line
        // we have reached all possible paths and its start will be in the arraylist ShortestPathEnds
        while (!queue.isEmpty())
        {
            nextNode = queue.remove();
            if ((destination == null && nextNode.getRow() == player.getEndingRow() || nextNode.getCol() == player.getEndingCol())
                    || (destination != null && nextNode.equals(destination)))
                shortestPathsEnds.add(nextNode);
            visited.add(nextNode);
            if (nextNode.equals(player.place))
                neighbors = ConvertOptionsToArray(moveOptions);
            else
                neighbors = nextNode.neighbors;
            for (Cell neighbor : neighbors)
                if (neighbor != null && !visited.contains(neighbor))
                {
                    parentNodes.put(neighbor, nextNode);
                    queue.add(neighbor);
                }
        }
        if (shortestPathsEnds.isEmpty())
            return null;
        else
            return determineShortestPath(shortestPathsEnds, parentNodes);
    }
    
    /**
     * This function gets the parentNodes from the BFS and the shortest paths' possible ends.
     * It determines which path is indeed the shortest one.
     * @param shortestPathsEnds  the shortest paths possible ends
     * @param parentNodes  the parent Nodes HashMap created by the BFS
     * @return an ArrayList of the shortest path
     */
    public static ArrayList<Cell> determineShortestPath(ArrayList<Cell> shortestPathsEnds,  HashMap<Cell, Cell> parentNodes)
    {
        ArrayList<Cell> shortestPath = new ArrayList<>();
        ArrayList<Cell> currentPath = new ArrayList<>();
        for (Cell shortestPathsEnd : shortestPathsEnds)
        {
            currentPath.clear();
            while (shortestPathsEnd != null)
            {
                currentPath.add(shortestPathsEnd);
                shortestPathsEnd = parentNodes.get(shortestPathsEnd);
            }
            if (shortestPath.isEmpty() || shortestPath.size() > currentPath.size())
            {
                shortestPath.clear();
                shortestPath.addAll(currentPath);
            }
        }
        Collections.reverse(shortestPath);
        return shortestPath;
    }
    
    /** 
     * Converts the Moving Options given to a single dimensional array
     * @param moveOptions - the move options ArrayList
     * @return the array of the moving options
     */
    public static Cell[] ConvertOptionsToArray(ArrayList<Cell[]> moveOptions)
    {
        ArrayList<Cell> neighbors = new ArrayList<>();
        int neighborsNum = 0;
        for (Cell[] next : moveOptions)
            for (Cell next1 : next)
                if (next1 != null)
                {
                    neighbors.add(next1);
                    neighborsNum++;
                }
        return (Cell[]) neighbors.toArray(new Cell[neighborsNum]);
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
     * as well as the appropriate wallS
     */
    private Pair<Wall, Pair<Integer, Integer>> determineRowAndColOfWall(int row, int col, int neighborIndex)
    {
        Pair<Integer, Integer> coordinate;
        Wall wall;
        // If NeighborIndex Top or Bottom, place horizontal wall
        wall = (neighborIndex < 2)?new HorizontalWall(board):new VerticalWall(board);
        // Only if the neighborIndex is Bottom, add 1 to the row
        row = row + ((neighborIndex == 1)?1:0);
        // Only if the neighborIndex is right, add 1 to the column
        col = col + ((neighborIndex == 3)?1:0);
        coordinate = new Pair(row, col);
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
                for (Cell cell : next)
                    if (cell != null && cell.equals(opponentPath.get(pathIndex + 1)))
                        return i;
        }
        return -1;
        
    }

    // This function determines the direction in which the path of the opponent goes.
    private int determineDirection(int pathIndex, ArrayList<Cell> opponentPath)
    {
        Cell origin = opponentPath.get(pathIndex);
        Cell destination = opponentPath.get(pathIndex + 1);
        for (int i = 0; i < origin.neighbors.length; i++)
            if (origin.neighbors[i] != null && origin.neighbors[i].equals(destination))
                return i;
        return -1;
    }

    /**
     * This function determines whether or not a wall can be placed that is
     * beneficial to the AI If so, it places it. It receives the opponent path
     * and move options, and for each cell in its path tries to place a wall to
     * block it. Then it compares the difference in path lengths to the min
     * difference, and chooses the best one The path difference is MyBFS -
     * OpBFS, so the AI's goal is to get the lowest difference (best if
     * negative)
     *
     * @param opponentPath - the cell path of the opponent
     * @param opMoveOptions - the current move options of the opponent
     * @param fastLaneClosure - indicates whether or not this blocking move is
     * to block a fast lane
     * @return true if wall has been placed, false if otherwise
     */
    private boolean blockOpponent(ArrayList<Cell> opponentPath, ArrayList<Cell[]> opMoveOptions, boolean fastLaneClosure)
    {
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
        while (pathIndex < opponentPath.size() - 1)
        {
            Point p = new Point(col * Cell.CELL_WIDTH, row * Cell.CELL_WIDTH);
            // check if the wall proposed is an okay wall...
            if (wall.isLocationValid(p) && !wall.checkIntersections(row, col) && !wall.isPlayerBlocked(row, col))
            {
                // delete the neighbors of this wall in order to mimic an actual board with this wall
                wall.deleteNeighbors(row, col);
                // Now check the BFS of me and my opponent
                int myBFS = BFS(this.place.getMoveOptions(), this, null).size();
                ArrayList<Cell[]> opCurrentMoveOptions = this.board.players[(this.board.currentPlayer + 1) % 2].place.getMoveOptions();
                int OpBFS = BFS(opCurrentMoveOptions, this.board.players[(this.board.currentPlayer + 1) % 2], null).size();
                // I want to get the minimal difference. If the opponent's path is longer, the difference ill be negative
                if (maxDifferenceInPath > myBFS - OpBFS)
                {
                    // check whether the path to the next cell is now possible or taking much longer!!
                    // USED ONLY WHEN THERE IS A NEED FOR A FAST LANE CLOSURE
                    ArrayList<Cell> newPathToNextCell = BFS(opMoveOptions, this.board.players[(this.board.currentPlayer + 1) % 2], opponentPath.get(pathIndex + 1));
                    // if you need the placement to close up a fast lane
                    if ((!fastLaneClosure) || (fastLaneClosure && (newPathToNextCell == null || newPathToNextCell.size() > pathIndex + 4)))
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
                    row--;
                else
                    col--;
                triedBothOptions = true;
            } else
            {
                pathIndex++;
                if (pathIndex < opponentPath.size() - 1)
                {
                    neighborIndex = determineDirection(pathIndex, opponentPath);
                    coordinate = determineRowAndColOfWall(opponentPath.get(pathIndex).getRow(), opponentPath.get(pathIndex).getCol(), neighborIndex);
                    wall = coordinate.getKey();
                    row = coordinate.getValue().getKey();
                    col = coordinate.getValue().getValue();
                    triedBothOptions = false;
                }
            }
        }
        // By this point either we have found a wall to build or there is no wall placement possible
        if (bestWall == null)
            return false;
        board.panel.add(bestWall);
        bestWall.placeWall(bestRow, bestCol);
        return true;
    }
    
    /**
     * This function is called when I am in the lead in terms of moving options.
     * @param moveOptions my current move options
     * @param shortestPath my current shortestPath
     * @param opMoveOptions the current move options of my opponent
     * @param opponentPath the current shortestPath of my opponent
     */
    private void tryToMove(ArrayList<Cell[]> moveOptions, ArrayList<Cell> shortestPath, ArrayList<Cell[]> opMoveOptions, ArrayList<Cell> opponentPath)
    {
        if (board.players[(board.currentPlayer + 1) % 2].getWallsLeft() == 0 || this.wallsLeft == 0)
        {
            board.endTurn(shortestPath.get(1), false);
            return;
        }
        if (!isThereFastLane(opponentPath, opMoveOptions, shortestPath))
            checkMyPathWidth(shortestPath);
    }

    /**
     * This function checks whether or not there is fast lane in the shortest
     * path of the opponent, and if so, it tries to block the fast lane found.
     * What is a fast lane? A fast lane is a path made of 6 or more squares (or
     * the entire path length), that its path width is 2 or less squares A fast
     * lane can have one mistake, meaning exactly one step in the path that has
     * more than 2 in its width, but it cannot be the first step
     *
     * @param opponentPath - the shortest path of the opponent
     * @param opMoveOptions - the first move options of the opponent
     * @param myPath - my shortest path
     * @return true if a fast lane was detected and was blocked. False
     * otherwise.
     */
    private boolean isThereFastLane(ArrayList<Cell> opponentPath, ArrayList<Cell[]> opMoveOptions, ArrayList<Cell> myPath)
    {
        int currentPathWidth;
        int lengthOfFastLane = 0;
        boolean laneExists = true, firstOffsetMade = false;
        for (int i = 0; i < opponentPath.size() - 1 && laneExists; i++)
        {
            int neighborIndex = (i == 0) ? determineDirectionOfFirstMove(0, opponentPath, opMoveOptions) : determineDirection(i, opponentPath);
            currentPathWidth = calculatePathWidth(opponentPath.get(i), neighborIndex, opponentPath.get(i + 1));
            if (currentPathWidth <= 2)
                lengthOfFastLane++;
            else
                if (!firstOffsetMade && i != 0)
                    firstOffsetMade = true;
                else
                    laneExists = false;
        }
        // If the line length has more than two places in the path that are not in it
        // and the line length is less than 6
        if (lengthOfFastLane < opponentPath.size() - 2 && lengthOfFastLane < 6)
            return false;
        // By now, we fully understand there is a fast lane in build, and we try to block it
        // now check if the shortest path of the computer does not intertwine with the fast lane found
        for (int i = 0; i < lengthOfFastLane; i++)
            if (myPath.contains(opponentPath.get(i)))
                return false;
        // Try To Block the opponent NOW
        return blockOpponent(opponentPath, opMoveOptions, true);
    }
    
    /**
     * This function scans the path width from the origin cell in one direction
     * @param origin - the original cell
     * @param neighborIndex - the neighborIndex of the destination
     * @param scanningDirection - the scanning direction to go
     * @return the path width in that direction
     */
    private int ScanPathInOneDirection(Cell origin, int neighborIndex, int scanningDirection)
    {
        int pathWidth = 0;
        Cell current = origin.neighbors[scanningDirection];
        // checks whether or not there is a path width in the direction of scanningDirection
        // If the board looked like the board below, the while loop should not have entered the loop of the left scan
        if (origin.neighbors[neighborIndex] != null && origin.neighbors[neighborIndex].neighbors[scanningDirection] == null)
            current = null;
        while (current != null && current.neighbors[neighborIndex] != null)
        {
            pathWidth++;
            // make sure there isn't a wall in the scanning direction of the next cell
            // *   * | *   *   *
            // *   * | B   *   *
            // *   *   A   *   *
            // The path width from A to B should be 3 and not 5, because of the wall to the left of B
            if (current.neighbors[neighborIndex].neighbors[scanningDirection] != null) //&& current.neighbors[neighborIndex].neighbors[neighborIndex] != null
            
                current = current.neighbors[scanningDirection];
            else
                current = null;
        }
        return pathWidth;
    }
    
    // This function returns the path width of the current move, based on the origin cell and neighborIndex
    private int calculatePathWidth(Cell origin, int neighborIndex, Cell destination)
    {
        // if the neighbor direction is top or bottom, you need to scan to the right and to the left
        // if the neighbor direction is left or right, you need to scan to the top and to the bottom
        int scanningDirection = (neighborIndex < 2) ? 2 : 0;
        int pathWidth = 0;
        if (origin.neighbors[neighborIndex] != null)
            pathWidth++;
        Cell current = origin.neighbors[scanningDirection];
        // If the move is not a direct move, but a diagonal move, ASSUME its path width is problematic
        if (origin.neighbors[neighborIndex] != null && origin.neighbors[neighborIndex].equals(destination) == false)
            return pathWidth;
        pathWidth += ScanPathInOneDirection(origin, neighborIndex, scanningDirection);
        // GO TO THE OTHER DIRECTION AND REPEAT THE PROCESS
        pathWidth += ScanPathInOneDirection(origin, neighborIndex, scanningDirection + 1);
        return pathWidth;
    }

    private void checkMyPathWidth(ArrayList<Cell> shortestPath)
    {
        int neighborIndex, currentPathWidth;
        boolean decisionMade = false;
        // there is no need to check the path width of the first move, since we can just pass it
        for (int i = 1; i < shortestPath.size() - 1 && !decisionMade; i++)
        {
            neighborIndex = determineDirection(i, shortestPath);
            currentPathWidth = calculatePathWidth(shortestPath.get(i), neighborIndex, shortestPath.get(i + 1));
            if (currentPathWidth <= 2)
                decisionMade = handleWeakSpot(shortestPath, i, neighborIndex);
        }
        makeAfinalDecision(decisionMade, shortestPath);
    }
    
    /**
     * This function makes a final decision based on the weak spots found
     * @param decisionMade whether the computer has already chosen to go on the alternate path
     * @param shortestPath the shortest path of the computer
     */
    private void makeAfinalDecision(boolean decisionMade, ArrayList<Cell> shortestPath)
    {
        // we reach this point with 3 different options, and their solutions are:
        // 1. there is no weak spot - continue with your BFS
        // 2. there is a weak spot and there is a wall that was virtually placed that covers it - place that wall on the board
        // 3. There is a weak spot that can't be covered or there are several weak spots - a move has already been made

        if (!decisionMade)
            if (this.wallBlockingWeakSpot.equals(""))
                board.endTurn(shortestPath.get(1), false);
            else
            {
                Wall wall = (this.wallBlockingWeakSpot.charAt(0) == 'H') ? new HorizontalWall(board) : new VerticalWall(board);
                int row = Integer.parseInt(this.wallBlockingWeakSpot.substring(1, 3));
                int col = Integer.parseInt(this.wallBlockingWeakSpot.substring(3, 5));
                this.board.panel.add(wall);
                wall.placeWall(row, col);
            }
    }
    
    /**
     * This function handles a weak spot detected in my path width check.
     * It looks for a possible wall an opponent can put and block completely the path
     * If there is one, it checks for a wall to prevent the opponent from putting the dangerous wall
     * @param shortestPath the shortest path of the computer
     * @param originIndex the index of the weak spot
     * @param neighborIndex the neighborIndex of the next cell in the path
     * @return whether a move has been made or not
     */
    private boolean handleWeakSpot(ArrayList<Cell> shortestPath, int originIndex, int neighborIndex)
    {
        int bestRow = -1, bestCol = -1, maxPathDifference;
        int bestOpBFS = 0, bestMyBFS = 0;
        // Find the first wall possible to block completely this weak spot
        Pair<Wall, Pair<Integer, Integer>> coordinate = determineRowAndColOfWall(shortestPath.get(originIndex).getRow(), shortestPath.get(originIndex).getCol(), neighborIndex);
        Wall wall = coordinate.getKey();
        int row = coordinate.getValue().getKey();
        int col = coordinate.getValue().getValue();
        
        // check both options of the wall placement
        for (int i = 0; i < 2; i++)
        {
            Point p = new Point(col * Cell.CELL_WIDTH, row * Cell.CELL_WIDTH);
            // If the wall is possible to place, then check if it blocks the path completely
            if (wall.isLocationValid(p) && !wall.checkIntersections(row, col) && !wall.isPlayerBlocked(row, col))
            {
                wall.deleteNeighbors(row, col);
                // Now check the path width after the virtual placement of the wall
                int pathWidth = calculatePathWidth(shortestPath.get(originIndex), neighborIndex, shortestPath.get(originIndex + 1));
                // if the wall completely blocks my path - it is a danger
                if (pathWidth == 0)
                {
                    // IMPORTANT - the opponent may have two options of walls that can block me at the weak spot
                    // My ASSUMPTION is that the opponent will choose the wall which has least damage to them...
                    ArrayList<Cell[]> opCurrentMoveOptions = this.board.players[(this.board.currentPlayer + 1) % 2].place.getMoveOptions();
                    ArrayList<Cell> myCurrentOpBFSPath = BFS(opCurrentMoveOptions, this.board.players[(this.board.currentPlayer + 1) % 2], null);
                    int myBFS = BFS(this.place.getMoveOptions(), this, null).size();
                    int OpBFS = myCurrentOpBFSPath.size();
                    if (bestRow == -1 || OpBFS < bestOpBFS)
                    {
                        bestRow = row;
                        bestCol = col;
                        bestOpBFS = OpBFS;
                        bestMyBFS = myBFS;
                    } 
                }
                wall.resetNeighbors(row, col);
            }
            if (wall instanceof VerticalWall)
                row--;
            else
                col--;
        }
        
        //  FIND ACCORDNGLY THE PATH DIFFERENCE 
        maxPathDifference = bestMyBFS - bestOpBFS;

        // if there was no wall possible, or the maxPathDifference is 0 or less, meaning I am in the lead
        // return there is no weak spot
        if (bestRow == -1 || maxPathDifference <= 0)
            return false;
        // if the opponent is leading, the path difference is less than two and the OpBFS is more than 5 - 
        // there is no weak spot
        if (maxPathDifference <= 2 && bestOpBFS > 5)
            return false;

        // BY NOW WE KNOW FOR SURE THERE IS A WEAK SPOT
        // WE NOW TRY TO BLOCK THE WEAK SPOT
        return blockTheWeakSpot(bestRow, bestCol, wall);
        
    }

   /**
    * This function receives the row and col of the wall that can block the weak spot
    * @param bestRow row of wall
    * @param bestCol column of wall
    * @param wall the wall itself
    * @return a boolean value telling us whether a move has been made or not
    */
    private boolean blockTheWeakSpot(int bestRow, int bestCol, Wall wall)
    {
        // BY NOW WE KNOW FOR SURE THERE IS WALL THAT BLOCKS THE WEAK SPOT THAT IS DANGEROUS
        // only if it is the first weak spot detected, try to find places to block it
        if (this.wallBlockingWeakSpot.equals("") && findPlaceToStopFutureWall(bestRow, bestCol, wall))
        {
            // The function findPlaceToStopFutureWall does not reset the neighbors of the problematic wall 
            wall.resetNeighbors(bestRow, bestCol);
            //return false because a decision has not been made. Only a virtual wall was placed
            return false;
        }
        else
        {
            // we either reach this when there was no wall that could block the weak spot, or when a new wall
            // that is problematic was found
            // if there was an attempt to find a wall but it was not successful, go to the alternate path
            // In order to find the alternate path - first delete the neighbors of the problematic wall
            wall.deleteNeighbors(bestRow, bestCol);
            if (!this.wallBlockingWeakSpot.equals(""))
            {
                // a wall has already been placed, but now there is another problematic wall
                // If so, we must also go to the alternate path, but first we have to retreive the blocking wall
                // from the property of the AI, and reset its neighbors
                Wall blockingWall = (this.wallBlockingWeakSpot.charAt(0) == 'H') ? new HorizontalWall(board) : new VerticalWall(board);
                int blockingRow = Integer.parseInt(this.wallBlockingWeakSpot.substring(1, 3));
                int blockingCol = Integer.parseInt(this.wallBlockingWeakSpot.substring(3, 5));
                blockingWall.resetNeighbors(blockingRow, blockingCol);
            }
            // find the current alternate path
            ArrayList<Cell> alternatePath = BFS(this.place.getMoveOptions(), this, null);
            wall.resetNeighbors(bestRow, bestCol);
            board.endTurn(alternatePath.get(1), false);
            // a move has been made, so return true
            return true;
        }    
    }
       
    /* This function virtually places the wall that blocks the current weak spot and checks if there is anything to do
       in order to prevent it
       If it finds such wall, it returns true, and places the wall's info in the property: wallBlocking */
    private boolean findPlaceToStopFutureWall(int bestRow, int bestCol, Wall wall) 
    {
        // virtually delete the neighbors of the wall in question
        wall.deleteNeighbors(bestRow, bestCol);

        ArrayList<Cell> alternatePath = BFS(this.place.getMoveOptions(), this, null);
        int wallRow = -1, wallCol = -1;
        int pathIndex = 0, neighborIndex;
        Wall blockingWall;
        boolean triedBothOptions = false, wallNotFound = true;
        neighborIndex = determineDirectionOfFirstMove(pathIndex, alternatePath, this.place.getMoveOptions());
        Pair<Wall, Pair<Integer, Integer>> coordinate = determineRowAndColOfWall(alternatePath.get(0).getRow(), alternatePath.get(0).getCol(), neighborIndex);
        blockingWall = coordinate.getKey();
        int row = coordinate.getValue().getKey();
        int col = coordinate.getValue().getValue();
        while (pathIndex < alternatePath.size() - 1 && wallNotFound)
        {
            Point p = new Point(col * Cell.CELL_WIDTH, row * Cell.CELL_WIDTH);
            // check if the wall proposed is an okay wall...
            if (blockingWall.isLocationValid(p) && !blockingWall.checkIntersections(row, col))
                if (blockingWall.isPlayerBlocked(row, col))
                {
                    wall.resetNeighbors(bestRow, bestCol);
                    if (!blockingWall.isPlayerBlocked(row, col))
                    {
                        wallNotFound = false;
                        wallRow = row;
                        wallCol = col;
                    }
                    wall.deleteNeighbors(bestRow, bestCol);
                }
            // Each wall has two different positions possible to block a square.
            // If only one has been tried, try the other option
            if (!triedBothOptions)
            {
                if (blockingWall instanceof VerticalWall)
                    row--;
                else
                    col--;
                triedBothOptions = true;
            } else
            {
                pathIndex++;
                if (pathIndex < alternatePath.size() - 1 && wallNotFound)
                {
                    neighborIndex = determineDirection(pathIndex, alternatePath);
                    coordinate = determineRowAndColOfWall(alternatePath.get(pathIndex).getRow(), alternatePath.get(pathIndex).getCol(), neighborIndex);
                    blockingWall = coordinate.getKey();
                    row = coordinate.getValue().getKey();
                    col = coordinate.getValue().getValue();
                    triedBothOptions = false;
                }
            }
        }

        //wall.resetNeighbors(bestRow, bestCol);
        // By this point either we have found a wall to build or there is no wall placement possible
        if (wallNotFound)
            return false;
        this.wallBlockingWeakSpot = (blockingWall instanceof HorizontalWall) ? "H" : "V";
        this.wallBlockingWeakSpot += (wallRow < 10) ? "0" + wallRow : wallRow;
        this.wallBlockingWeakSpot += (wallCol < 10) ? "0" + wallCol : wallCol;
        blockingWall.deleteNeighbors(wallRow, wallCol);
        return true;
        // DONT FORGET TO RESET NEIGHBORS OF THE WALL
    }
}
