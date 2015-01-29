######################################################
### Inaugural Speech Analysis


library("SnowballC")
setwd('~/src/stat133/hw7')
## We provide a function [ computeSJDistance() ] to calculate the 
## Shannon-Jensen divergence between two word vectors.
## The function is the file computeSJDistance.R, please *keep* the source
## statement in the file:
source("computeSJDistance.R")

######################################################



speeches <- readLines(file("stateoftheunion1790-2012.txt"), encoding = "UTF-8")


# The speeches are separated by a line with three stars (***).

#get number of speeches
breaks <- which(speeches == "***")
n.speeches <- length(breaks)

#get president's name 
presidents <- speeches[breaks + 3]

#get Dates, Year, Month
tempDates <- speeches[breaks+4]
speechYr <- gsub(".*, ", "", tempDates)
speechMo <- gsub(" .*$", "", tempDates)



#some quick cleanup 
speeches <- gsub("Mr.", "Mr", speeches)
speeches <- gsub("Mrs.", "Mrs", speeches)
speeches <- gsub("U.S.", "US", speeches)


#creates a list with each element being one speech (length =222)
speechesL <- list()
speechWords <-list()
#plaintxtspeeches <-list()
for(i in 1:n.speeches){
  start <-breaks[i]+6
  end <- breaks[i + 1] -1
  if(i == n.speeches){
    speech <- speeches[start:(length(speeches)-2)]
  }
  else{
    speech <- speeches[start:end]
  }  
  speech <- paste(speech, collapse = " ", sep = " ")
  speechWords[[i]] <- speech
  #plaintxtspeeches[[i]] <- speech
  gsub("  ", " ", speech)
  speech <-strsplit(speech[[1]], "[.!?]")
  speechesL[i] <- speech
  
}

#### Word Vectors 
# For each speech we are going to collect the following information:
# -- # of sentences
# -- # of words
# -- # of characters
# 
# We will also create a word vector for every speech.  The word vector 
# should have one entry for every word that appears in *any* speech
# and the value of the entry should be the number of times the word appears.
# The entries should be in alphabetical order.  
# Once all the word vectors are in place we will combine them into a matrix
# with one row for every word that appears and one column for every speech.
#
# Do this in a few steps:
# Write a function, [speechToWords], that takes a character vector and 
# creates a word vector of all the words in the input variable.  
# Input  : sentences, a character string
# Output : words, a character vector where each element is one word 

# In other words it should take a string of text and:
# -- cut it into words
# -- remove all punctuation marks (anything in :punct:)
# -- make all characters lower case
# -- Remove the phrase "Applause."
# -- use the function wordStem() from the package SnowballC to 
#    get the stem of each work
# -- finally, remove all empty words, i.e. strings that match "" 
#    both BEFORE running wordStem() *and* AFTER

#### The function wordStem() returns the "stem" of each word, e.g.:
#> wordStem(c("national", "nationalistic", "nation"))
#[1] "nation"      "nationalist" "nation"     

speechToWords = function(sentences) {
# Input  : sentences, a character string
# Output : words, a character vector where each element is one word 
	##lots of cleaning, Applause follows each speech so lets remove it
  words<- gsub("Applause.", "", sentences)
  words <- tolower(gsub("[[:punct:]]", " ", sentences))
  words<-gsub("  ", " ", words)
  words<-gsub("  ", " ", words)
  words <- strsplit(words," ")
  words<-wordStem(words[[1]])
  words <-gsub("[0-9]", "", words)
  words<-words[!words == ""]
  return(words) 
}



#speechWords created eariler when creating speechesL
#apply speechToWords to each speech
for(i in 1:length(speechWords)){
  speechWords[[i]] <- speechToWords(speechWords[[i]])
}
  
  

#creating a vector with every word that appears in the speeches in alphabetic order
unique.elems <- sort(unique(unlist(speechWords)))
uniqueWords <- unique(sort(unique.elems[!unique.elems == "" ]))[-1]

#creating a matrix with the same # of columns as unqiueWords, same # of rows as SpeechesL (222)
#column 1 represents element 1 in unqiueWords up to n
#row 1 represents speech 1 up to n
#each column contains the times that word appeared in each speech. 

# You may want to use an apply statment to first create a list of word vectors, one for each speech.
counts <- lapply(speechWords, table)
nms <- lapply(counts, names)
idcs <- lapply(nms, FUN = function(counts) match(uniqueWords, counts))

#extra function to create a vector to use for wordMat
replacer <- function(x, elem){
    
    for(i in 1:length(x)){
      if(is.na(x[i])){
        next
      }
      else{
        w <- speechWords[[elem]]
        x[i] <- as.numeric(table(w[w==nms[[elem]][x[i]]]))
      }
    }
    return(x)
  }

##########     THIS TAKES A MINTUTE OR TWO   ###########
j<-1
while(j <= 222){
  idcs[[j]] <- replacer(idcs[[j]], j)
  idcs[[j]][is.na(idcs[[j]])] <- 0
  j <- j +1

}

wordMat <- matrix(unlist(idcs), ncol = n.speeches, nrow = length(uniqueWords))
# Load the dataframe [speechesDF] which has two variables,
# president and party affiliation:

  load("speeches_dataframe_new.Rda")

#add to DF
speechesDF$yr <- speechYr
speechesDF$month <- speechMo

words <- lapply(speechWords, length)
chars <- lapply(speechWords, FUN = function(x) sum(nchar(x)))
sent <- lapply(speechesL, length)

# Update the data frame
speechesDF$words <- words
speechesDF$chars <- chars
speechesDF$sent <- sent

######################################################################

# The following matrix has one column for each president (instead of one for each speech)
# and that column is the sum of all the columns corresponding to speeches make by said president.

inits = speechesDF$initial
presidents <- as.vector(unique(speechesDF$initial))

presidentWords <- list()
for(i in 1:length(presidents)){
  if(length(which(inits == presidents[i])) == 1){
    presidentWords[[i]] <- wordMat[,which(inits == presidents[i])]
  }
  else{
    presidentWords[[i]]<- apply(wordMat[,which(inits == presidents[i])], 1,sum)
  }
}

presidentWordMat <- matrix(unlist(presidentWords), ncol = length(presidents))




#Final Plots
plot(speechesDF$yr, speechesDF$sent, xlab = 'Year', ylab = '# sentences')
plot(speechesDF$yr, speechesDF$words, xlab = 'Year', ylab = '# words')
plot(speechesDF$yr, speechesDF$chars, xlab = 'Year', ylab = '# characters')

plot(speechesDF$yr, (unlist(speechesDF$chars)
                     /unlist(speechesDF$words)), 
                    xlab = 'Year', 
                     ylab = 'avg word length')
plot(speechesDF$yr, (unlist(speechesDF$words)
                     /unlist(speechesDF$sent)), 
                     xlab = 'Year', 
                     ylab = 'avg sentence length')
# your plot statements below:

# One clear observation i made was that sentence length has been consistently decreaseing over time
# while sentence quantity understandably has increased consistently over time. Also there is a lot more
# variation in word length the past century whereas from 1800-1900 the avg word length is fairly consistent.
bogus <- c('the', 'of', 'and', 'this' , 'our', 'that', 'which', 'a', 
           'for', 'that', "to", "be", 'in', 'by', 'it', 'there', 'on', 'or', 'but', 
           'have', 'would', 'from', 'i', 'such', 'will', 'than', 'their', 'with', 'were', 
           'much', 'make', 'been', 'should', 'made', 'other', 'upon', 'all', 'can', 'an', 
           'ar', 'at', 'who', 'two', 'u', 'some', 'what', 'when', 'those', 'them', 'you')



#exploring words trends...
D <- counts[which(speechesDF$party == 'D')]
D <- unlist(D)
topD <- D[D > 30 & !names(D) %in% bogus]
TOPD <- rev(sort(topD[unique(names(topD))]))

R <- counts[which(speechesDF$party == 'R')]
R <- unlist(R)
topR <- R[R > 30 & !names(R) %in% bogus]
TOPR <- rev(sort(topR[unique(names(topR))]))



# get a list of vectors containing the top20 must used words by decade
century.trends <- list()
yrs <- c(1700, 1800, 1900, 2000)
j <- 1
while (j <= 4){
  trend <- unlist(counts[which(speechesDF$yr < (yrs[j] + 100) & speechesDF$yr >= yrs[j])])
  trend <- trend[trend > 10 & !names(trend) %in% bogus]
  century.trends[[j]] <- rev(sort(trend[unique(names(trend))]))[1:20]
  j <- j + 1
}
names(century.trends) <- yrs[1:4]





