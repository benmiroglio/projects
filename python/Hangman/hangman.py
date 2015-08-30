
# Hangman Game
# author Ben Miroglio
import sys
import os
from Color import *
from string import ascii_letters



#initialize game
def initialize():
	"""
	reads in player one's entry
	returns that entry for gobal assignment
	"""
	print "-------------- HANGMAN ---------------\n"
	print "Player 1: please enter a word, make sure player 2 cannot see the word"
	print "..."
	while True:
		word = sys.stdin.readline()
		return word



def reveal_progress(guessed, word, left):
	"""
	takes the already guessed letters and player 1's word as parameters

	prints out the blank and correctly guessed letters thus far
	i.e: if the word was 'hangman', and player 2 had guessed 'h', 'a', and 'g'
	the function would print:

	h a _ g _ a _

	"""
	print "Progress:"
	print "------------------------------" 
	count = 0
	for i in range(len(word)-1):
		if len(word[i]) == 1:
			if word[i] in guessed:
				print Color.BOLD + Color.YELLOW + word[i] + " " + Color.STOP, #use comma for same-line printing
				count += 1
			else:
				print "_ ",
	print "" + Color.STOP # end same-line printing
	print "------------------------------\n"
	print "Turns Remaining: " + str(left)
	print "Guess: \n"
	return count

def valid_guess(string):
	""" 
	takes in a guess, and ensures that it is one letter only, and not a special character or a space
	"""
	if string == " " or string not in ascii_letters:
		return False
	return True


#main program
if __name__ == '__main__':
	word = initialize()
	if " " in word:
		already_guessed = [" "] #automatically adds spaces as a correct guess before the game starts
	else:						#for word phrases
		already_guessed = []
	os.system('cls' if os.name == 'nt' else 'clear')
	print "LETS BEGIN! Player 2: Please enter your first letter to try to guess player 1's word\n"
	print "Only ascii letters are accepted, and if your guess is more than one letter, "
	print "the first letter will count as your guess"
	reveal_progress(already_guessed, word, 20)
	
	count = 0
	attempts = 0
	left = 20
	while True:
		# player 2 gets 20 guesses
		if attempts >= 20:
			print "GAME OVER. Player two ran out of guesses. Player 1 wins!"
			print "The word / phrase was: '" + word + "'"
			break
		guess = sys.stdin.readline()[0] # only looks at first letter, even if there are multiple
		if not valid_guess(guess):
			print "Invalid guess, your guess must be an acscii_letter i.e. no spaces or special characters"
		else:
			attempts += 1
			left -= 1
			if guess in word:
				if guess in already_guessed:
					print "You already guessed that letter. Guess Again:"
				else:
					os.system('cls' if os.name == 'nt' else 'clear') #clean up screen
					print "Guess: " + guess + "\nResult: " + Color.UNDERLINE + Color.GREEN + "MATCH!\n"
					print Color.STOP
					already_guessed.append(guess)
					count = reveal_progress(already_guessed, word, left)
			else:				
				os.system('cls' if os.name == 'nt' else 'clear') #clean up screen
				print  "Guess: " + guess + "\nResult: " + Color.UNDERLINE + Color.RED + "No Match\n"
				print Color.STOP
				count = reveal_progress(already_guessed, word, left)
				
		if count == len(word)-1:
			print "GAME OVER. Player 2 won in {0} guesses!".format(attempts)
			break



