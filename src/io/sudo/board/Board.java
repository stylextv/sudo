package io.sudo.board;

import java.util.Random;

public class Board {
	
	private static final int SIZE = 9;
	private static final int BLOCK_SIZE = 3;
	private static final int MINIMAL_DIGIT = 1;
	private static final int MAXIMAL_DIGIT = 9;
	private static final int EMPTY_SQUARE_DIGIT = 0;
	private static final int FULL_BOARD_DIGIT_AMOUNT = SIZE * SIZE;
	
	private static final int UNIQUELY_SOLVABLE_SOLUTION_AMOUNT = 1;
	private static final int DEFAULT_COUNT_SOLUTIONS_SOLUTION_LIMIT = 1000;
	private static final int DEFAULT_COUNT_SOLUTIONS_START_X = 0;
	private static final int DEFAULT_COUNT_SOLUTIONS_START_Y = 0;
	
	private static final int SHUFFLE_ITERATION_AMOUNT = 1000;
	private static final Random SHUFFLE_RANDOM = new Random();
	
	private static final int REMOVE_DIGITS_ITERATION_AMOUNT = 100;
	private static final Random REMOVE_DIGITS_RANDOM = new Random();
	
	private static final char TO_STRING_EMPTY_SQUARE_SYMBOL = ' ';
	private static final char TO_STRING_SQUARE_SEPARATOR_SYMBOL = ' ';
	private static final char TO_STRING_BLOCK_HORIZONTAL_SEPARATOR_SYMBOL = '|';
	private static final char TO_STRING_BLOCK_VERTICAL_SEPARATOR_SYMBOL = '-';
	private static final int TO_STRING_BLOCK_VERTICAL_SEPARATOR_SYMBOL_AMOUNT = 23;
	private static final String TO_STRING_LINE_SEPARATOR_SYMBOLS = System.lineSeparator();
	
	private static final int[][] RANDOM_BOARD_INITIAL_DIGITS = new int[][] {
			{ 4, 6, 1, 8, 3, 9, 5, 2, 7 },
			{ 3, 8, 9, 2, 7, 5, 1, 4, 6 },
			{ 5, 2, 7, 6, 4, 1, 9, 8, 3 },
			{ 2, 5, 8, 1, 6, 7, 3, 9, 4 },
			{ 6, 7, 3, 9, 8, 4, 2, 5, 1 },
			{ 9, 1, 4, 5, 2, 3, 6, 7, 8 },
			{ 7, 4, 5, 3, 9, 6, 8, 1, 2 },
			{ 8, 9, 6, 4, 1, 2, 7, 3, 5 },
			{ 1, 3, 2, 7, 5, 8, 4, 6, 9 }
	};
	
	private final int[][] digits;
	private int digitAmount;
	
	public Board(int digitAmount) {
		this(RANDOM_BOARD_INITIAL_DIGITS, FULL_BOARD_DIGIT_AMOUNT);
		
		shuffle();
		removeDigits(FULL_BOARD_DIGIT_AMOUNT - digitAmount);
	}
	
	private Board(int[][] digits, int digitAmount) {
		this.digits = digits;
		this.digitAmount = digitAmount;
	}
	
	public boolean uniquelySolvable() {
		return countSolutions(UNIQUELY_SOLVABLE_SOLUTION_AMOUNT + 1) == UNIQUELY_SOLVABLE_SOLUTION_AMOUNT;
	}
	
	public int countSolutions() {
		return countSolutions(DEFAULT_COUNT_SOLUTIONS_SOLUTION_LIMIT);
	}
	
	public int countSolutions(int solutionLimit) {
		return countSolutions(solutionLimit, DEFAULT_COUNT_SOLUTIONS_START_X, DEFAULT_COUNT_SOLUTIONS_START_Y);
	}
	
	private int countSolutions(int solutionLimit, int startX, int startY) {
		if(digitAmount == FULL_BOARD_DIGIT_AMOUNT) return 1;
		
		int amount = 0;
		int x = startX;
		int y = startY;
		
		while(x < SIZE) {
			while(y < SIZE) {
				
				if(digits[x][y] != EMPTY_SQUARE_DIGIT) {
					
					y++;
					continue;
				}
				
				for(int digit = MINIMAL_DIGIT; digit <= MAXIMAL_DIGIT; digit++) {
					boolean placeable = true;
					
					for(int i = 0; i < SIZE; i++) {
						if(digits[i][y] == digit || digits[x][i] == digit) {
							
							placeable = false;
							break;
						}
					}
					
					if(!placeable) continue;
					
					int blockX = x / BLOCK_SIZE * BLOCK_SIZE;
					int blockY = y / BLOCK_SIZE * BLOCK_SIZE;
					
					for(int i = 0; i < BLOCK_SIZE; i++) {
						for(int j = 0; j < BLOCK_SIZE; j++) {
							
							if(digits[blockX + i][blockY + j] == digit) {
								
								placeable = false;
								break;
							}
						}
						
						if(!placeable) break;
					}
					
					if(!placeable) continue;
					
					digits[x][y] = digit;
					digitAmount++;
					
					amount += countSolutions(solutionLimit - amount, x, y);
					
					digits[x][y] = EMPTY_SQUARE_DIGIT;
					digitAmount--;
					
					if(amount >= solutionLimit) return amount;
				}
				
				return amount;
			}
			
			x++;
			y = 0;
		}
		
		return amount;
	}
	
	private void shuffle() {
		for(int i = 0; i < SHUFFLE_ITERATION_AMOUNT; i++) {
			if(SHUFFLE_RANDOM.nextBoolean()) {
				
				int blockX = SHUFFLE_RANDOM.nextInt(SIZE / BLOCK_SIZE) * BLOCK_SIZE;
				int x1 = blockX + SHUFFLE_RANDOM.nextInt(BLOCK_SIZE);
				int x2 = blockX + SHUFFLE_RANDOM.nextInt(BLOCK_SIZE);
				if(x1 == x2) continue;
				
				for(int y = 0; y < SIZE; y++) {
					
					int digit = digits[x1][y];
					digits[x1][y] = digits[x2][y];
					digits[x2][y] = digit;
				}
				
			} else {
				
				int blockY = SHUFFLE_RANDOM.nextInt(SIZE / BLOCK_SIZE) * BLOCK_SIZE;
				int y1 = blockY + SHUFFLE_RANDOM.nextInt(BLOCK_SIZE);
				int y2 = blockY + SHUFFLE_RANDOM.nextInt(BLOCK_SIZE);
				if(y1 == y2) continue;
				
				for(int x = 0; x < SIZE; x++) {
					
					int digit = digits[x][y1];
					digits[x][y1] = digits[x][y2];
					digits[x][y2] = digit;
				}
			}
		}
	}
	
	private boolean removeDigits(int digitAmount) {
		if(digitAmount <= 0) return true;
		
		for(int i = 0; i < REMOVE_DIGITS_ITERATION_AMOUNT; i++) {
			
			int x = REMOVE_DIGITS_RANDOM.nextInt(SIZE);
			int y = REMOVE_DIGITS_RANDOM.nextInt(SIZE);
			
			int digit = digits[x][y];
			if(digit == EMPTY_SQUARE_DIGIT) continue;
			
			digits[x][y] = EMPTY_SQUARE_DIGIT;
			this.digitAmount--;
			
			if(uniquelySolvable() && removeDigits(digitAmount - 1)) return true;
			
			digits[x][y] = digit;
			this.digitAmount++;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for(int y = 0; y < SIZE; y++) {
			if(y != 0 && y % BLOCK_SIZE == 0) {
				
				for(int i = 0; i < TO_STRING_BLOCK_VERTICAL_SEPARATOR_SYMBOL_AMOUNT; i++) {
					builder.append(TO_STRING_BLOCK_VERTICAL_SEPARATOR_SYMBOL);
				}
				
				builder.append(TO_STRING_LINE_SEPARATOR_SYMBOLS);
			}
			
			for(int x = 0; x < SIZE; x++) {
				
				int digit = digits[x][y];
				char symbol = digit == EMPTY_SQUARE_DIGIT ? TO_STRING_EMPTY_SQUARE_SYMBOL : (char) ('0' + digit);
				
				if(x != 0 && x % BLOCK_SIZE == 0) {
					builder.append(TO_STRING_SQUARE_SEPARATOR_SYMBOL);
					builder.append(TO_STRING_BLOCK_HORIZONTAL_SEPARATOR_SYMBOL);
				}
				
				builder.append(TO_STRING_SQUARE_SEPARATOR_SYMBOL);
				builder.append(symbol);
			}
			
			builder.append(TO_STRING_LINE_SEPARATOR_SYMBOLS);
		}
		
		return builder.toString();
	}
	
}
