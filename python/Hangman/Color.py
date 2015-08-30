#enables color printing in terminal
class Color:
	#print Color.BLUE before termial output to make text Blue
	#print Color.STOP after termial output to make the following output normal

    BLUE = '\033[94m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    RED = '\033[91m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'
    STOP = '\033[0m'