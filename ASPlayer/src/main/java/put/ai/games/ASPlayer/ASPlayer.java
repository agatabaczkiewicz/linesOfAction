package put.ai.games.ASPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;
import put.ai.games.game.TypicalBoard;



public class ASPlayer extends Player {
	public static final int inf = Integer.MAX_VALUE;
    public static int depthmax = 3;
    private Random random = new Random(0xdeadbeef);
    
  
    private static int weight[][];
    
    @Override
    public void setTime(long time) {
    	if ((time >= 15000)&&(time < 75000)) {
    		depthmax = 4;
    	} 
    	if (time >= 75000) {
    		depthmax = 5;
    	}
    	System.out.println(depthmax);
    	super.setTime(time);
    }
    
    @Override
    public String getName() {
        return "Szymon Dzięgielewski 141030 Agata Bączkiewicz 141186";
    }

    public static void main(String[] args) {}


    @Override
    public Move nextMove(Board b) {
       
    	 int alpha = -inf;
          int beta = inf;

          int bestScore = -inf;

          List<Move> theBestMo = new ArrayList<Move>();
          List<Move> moves = b.getMovesFor(getColor());
          HelpBoard board = new HelpBoard((TypicalBoard) b);
          List<Move> past_moves = new ArrayList<Move>();
    	
    	
        if (weight == null) {
        	weight = new int[b.getSize()][b.getSize()];
            final int center = b.getSize() / 2;
            for (int i = 0; i < b.getSize(); i++) {
            	for (int j = 0; j < b.getSize(); j++) {
            		if (Math.max(i, j) >= center ) {
            			  weight[i][j] = Math.min(i, Math.min(j, Math.min(b.getSize()-1-i, b.getSize()-1-j)));
            	  	}
            		else {
            			weight[i][j]=Math.min(i, j);
            	    }
            	}
            }
        }
         

        	for (Move move : moves) {
        	  past_moves.clear();
        		past_moves.add(move);

        		int score = -pvs(board, past_moves, alpha, beta, 0, getOpponent(getColor()));

        		if (score == bestScore) {
        			theBestMo.add(move);
        		} else
        		if (score > bestScore) {
        			theBestMo.clear();
        			theBestMo.add(move);
              bestScore = score;
        		}

        	}

        	return theBestMo.get(random.nextInt(theBestMo.size()));
        
        
    }
    
    private int score(final Board board, final Color player) {
        int score_player1 = 0;
        int score_player2 = 0;
        int count_player1 = 0;
        int count_player2 = 0;

        for (int x = 0; x < board.getSize(); x++) {
        	for (int y = 0; y < board.getSize(); y++) {
        		if (board.getState(x, y) == Color.PLAYER1) {
        			score_player1 += weight[x][y];
        			count_player1++;
        		}
        		else {
        			if (board.getState(x, y) == Color.PLAYER2) {
        				score_player2 += weight[x][y];
        				count_player2++;
        			}
        		}
        	}
        }
        if(player == Color.PLAYER1) {
        	if(count_player1 == 1) {
        		return score_player1 - score_player2 +10;
        	}
        	else {
        		if(count_player2 < 6 ) {
        			return score_player1 - score_player2 -2;
        			}
        		else {
        			return score_player1 - score_player2;
        		}
        	}
        }
        else {
            if(count_player2 == 1) {
        		return score_player2 - score_player1 +10;
        	}
        	else {
        		if(count_player1 < 6 ) {
        			return score_player2 - score_player1 -2;
        		}
        		else {
        			return score_player2 - score_player1;
        		}
        	}
           }
                
      }
    
    private int pvs(final HelpBoard board, final List<Move> past_moves, int alpha, int beta, int d, final Color player ) {

          int a, b;

          TypicalBoard now_board = board.applyMoves(past_moves);

          if (d == depthmax)
            return score(now_board, player);

          a = alpha;
    	    b = beta;

    	    for (Move move : now_board.getMovesFor(player)) {
    	        past_moves.add(move);

    	        int t = -pvs(board, past_moves,-b, -a, d+1, getOpponent(player));

    	        if ( (t > a) && (t < beta) && (d < depthmax - 1))
    	          a = -pvs(board, past_moves,-beta, -t, d+1, getOpponent(player));

    	        past_moves.remove(past_moves.size() - 1);

    	        a = Math.max(a,t);
    	        // beta cut-off
    	        if (a >= beta)
    	          return a;

    	        b = a + 1;
    	    }

         return a;
       }
    
    
    
    private class HelpBoard {
        private TypicalBoard typicalboard;

        public HelpBoard(TypicalBoard board) {
          typicalboard = board;
        }

        public TypicalBoard applyMoves(List<Move> moves) {
          TypicalBoard board = typicalboard.clone();

          for (Move move : moves) {
            board.doMove(move);
          }

          return board;
        }
      }

}
