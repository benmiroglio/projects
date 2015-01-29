import csv
from string import ascii_letters


# still need to parse words so it is only lower-case ascii letters

class Sentiment:
    """Inputs:
       1. .txt file
       2. two column csv: word | sentiment """

    def __init__(self, text_file, sentiment_values='sentiments_dictionary.csv'):
        self.sentiment_values = sentiment_values
        self.title = text_file
        self.text = open('{0}'.format(text_file), 'r').read().lower()
        self.sentiment_dic = {}
        self.word_counts = {}
        self.word_lst = []
        self.value_lst = []
        self.read_sentiments()
        self.get_sentiments()
        self.count_words()
        print("Average sentiment for {0}: {1}".format(self.title,
                                                      self.average_sentiment()))


    ## read in sentiment dictionary and form dictionary attribute in the form word: value
    ## after standardizing text to include only ascii characters
    def read_sentiments(self):
        words = csv.reader(open(self.sentiment_values))
        for i in words:
            self.sentiment_dic[i[0]] = i[1]
        self.text = self.text.replace("\n", "")
        for i in self.text:
            if i not in ascii_letters:
                self.text = self.text.replace(i, ' ')
        self.word_lst = self.text.split()
     

    ## go through each word in the text and store its sentiment value if it exists    
    def get_sentiments(self):
        words = self.word_lst[:]
        keys = self.sentiment_dic
        for i in range(0, len(words)):
            if words[i] in keys:
                words[i] = float(keys[words[i]])
            else:
                words[i] = None
        self.value_lst = [i for i in words if i]

    def average_sentiment(self):
        return sum(self.value_lst)/ len(self.value_lst)

    def count_words(self):
        for word in self.word_lst:
            if word not in self.word_counts:
                self.word_counts[word] = 1
            else:
                self.word_counts[word] += 1


