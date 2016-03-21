import glob
import Sentiment
files = glob.glob('neg/*')

l = []
for f in files:
	S = Sentiment(f)
	l.append(S.average_sentiment())
