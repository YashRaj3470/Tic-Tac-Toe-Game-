import java.util.*;

public class TicTacToe {
    public static void main(String[] args) {
        char[][] board = new char[3][3];
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = ' ';

            }
        }
        char player = 'X';
        boolean gameOver = false;
        Scanner sc = new Scanner(System.in);

        while (!gameOver) {
            printBoard(board);
            System.out.print(" player " + player + " enter: ");
            int row = sc.nextInt();
            int col = sc.nextInt();

            if (board[row][col] == ' ') {
                board[row][col] = player; //Place the element
                gameOver = haveWon(board, player);

                if (gameOver) {
                    System.out.println(" player " + player + " has Won : ");
                } else if (isBoardFull(board)) {
                    System.out.println("It's a draw!");
                    break; // Exits the while loop, ending the game
                }
                else{
                    if(player =='X'){
                        player ='O';
                    } else {
                        player='X';
                    }
                }

                } else {
                System.out.println("Invalid move, Try Again! ");
            }
        }
        printBoard(board);

    }
    public static boolean haveWon(char[][] board, char player){
        // check for Rows
        for (int row = 0; row < board.length; row++) {
     if(board[row][0]==player && board[row][1]==player &&board[row][2]==player ){

         return true;
     }

        }
        //check for Columns
        for (int col = 0; col < board[0].length; col++) {
            if (board[0][col] == player && board[1][col] == player && board[2][col] == player) {

                return true;
            }
        }
        //check for Diagonals

            if(board[0][0]==player && board[1][1]==player &&board[2][2]==player ) {

                return true;
            }

        if(board[0][2]==player && board[1][1]==player &&board[2][0]==player ) {

            return true;
        }
        return  false;

    }

    public static void printBoard(char[][] board){
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                System.out.print(board[row][col] + " | ");

            }
            System.out.println();
        }
    }
    public static boolean isBoardFull(char[][] board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    return false; // Found an empty cell
                }
            }
        }
        return true; // No empty cells found, all boxes are filled
    }

}

   
