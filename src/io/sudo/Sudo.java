package io.sudo;

import io.sudo.board.Board;

public class Sudo {
	
	public static void main(String[] args) {
		Board board = new Board(30);
		
		System.out.println(board);
		System.out.println(board.uniquelySolvable());
	}
	
}
