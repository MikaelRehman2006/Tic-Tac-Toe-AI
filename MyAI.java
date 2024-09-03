import java.util.List;
import java.util.LinkedList;
import java.util.Random;

public class MyAI implements AI {
    public String getName() {
        return "MinMaxTree";
    }
    // Return the best move myPiece can make using a MinMaxTree
    public Point getNextMove(TicTacToe game, TicTacToePiece myPiece) {
        // if game is over, return null
        if (game.isGameOver())
            return null;
        
            // Generate a MinMax tree using the TicTacToe game parameter
        TNode<MinMaxNode> root = generateMinMaxTree(game, null);
        // Score the MinMax tree
        scoreMinMaxTree(root, myPiece);
        
         // Create a list of MinMaxNodes from the children of the root of the MinMax tree you just generated
        List<TNode<MinMaxNode>> children = root.getChildren();
          // if the root node has no children, return null
        if (children.isEmpty())
            return null;
        
         // Determine what the best (highest) score is among all of the MinMax nodes in the list you just created
        int bestScore = Integer.MIN_VALUE;
        for (TNode<MinMaxNode> child : children) {
            bestScore = Math.max(bestScore, child.getData().getScore());
        }

        // Create a list to hold all of the moves (Point objects) that are rated with the best score
        List<Point> bestMoves = new LinkedList<>();
          // loop through all of the children of the root of the MinMax tree and add the Point from the ones with the "best score" to the list of Points you just created
        for (TNode<MinMaxNode> child : children) {
            if (child.getData().getScore() == bestScore) {
                bestMoves.add(child.getData().getMove());
            }
        }

            // return a random point from the list of Points
        Random rand = new Random();
        return bestMoves.get(rand.nextInt(bestMoves.size()));
    }
    
    // Generate a MinMaxTree consisting of a root node containing game, and children nodes 
    // containing all possible moves the current player can make
    private TNode<MinMaxNode> generateMinMaxTree(TicTacToe game, Point move) {
          // make a copy of game (so you can modify the copy without changing game)
        TicTacToe copy = game.copy();
         // if move is not null
        // apply move to the copy (addPiece and nextPlayer)
        if (move != null) {
            copy.addPiece(move.getRow(), move.getCol());
            copy.nextPlayer();
        }
        
         // Make a MinMaxNode with the copy and move
        MinMaxNode minMaxNode = new MinMaxNode(copy, move);
        // Make a TNode with the MinMaxNode you just created
        TNode<MinMaxNode> node = new TNode<>(minMaxNode);
          
         // recursively call generateMinMaxTree for each legal move that the new current player can make on copy (every empty space is a legal move)
        if (!copy.isGameOver()) {
            for (Point emptySpace : copy.getEmptySpaces()) {
                node.addChild(generateMinMaxTree(copy, emptySpace));
            }
        }
          // return the TNode you created above
        return node;
    }

    // Generate a score for every node in the MinMaxTree (from the point of view of player)
    private void scoreMinMaxTree(TNode<MinMaxNode> node, TicTacToePiece player) {
         // get the MinMaxNode out of the root node
        MinMaxNode minMaxNode = node.getData();
        // get the game out of the MinMaxNode
        TicTacToe game = minMaxNode.getGame();
        
        
        // if the game is over
        // use the setScore method to score the MinMaxNode based on who won the game
        if (game.isGameOver()) {
            TicTacToePiece winner = game.getWinner();
            // if player is the winner -> 10 points
            if (winner == player)
                minMaxNode.setScore(10);
            // if the game is tied -> - points
            else if (winner == null)
                minMaxNode.setScore(0);
            //if player is the loser -> -10 points
            else
                minMaxNode.setScore(-10);
        } else {
             // if the game is not over
            // recursively call scoreMinMaxTree on all of the root node's children
            for (TNode<MinMaxNode> child : node.getChildren()) {
                scoreMinMaxTree(child, player);
            }
            
            //// determine the lowest and highest scores among all of the root node's children
            int bestScore = (game.getCurrentPlayer() == player) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            for (TNode<MinMaxNode> child : node.getChildren()) {
                int childScore = child.getData().getScore();
                    //  if it is player's turn
                //      set this MinMaxNode's score to the highest score
                //  if it is NOT player's turn
                //      set this MinMaxNode's score to the lowest score

                if (game.getCurrentPlayer() == player) {
                    bestScore = Math.max(bestScore, childScore);
                } else {
                    bestScore = Math.min(bestScore, childScore);
                }
            }
            minMaxNode.setScore(bestScore);
        }
    }

    
}