
library('foreign')

#Visit Days------------------------------------

day3date<- '2013-12-01 '
day4date<- '2013-12-07 '
day5date<- '2013-12-15 '
day6date<- '2013-12-21 '
day7date<- '2014-01-04 '
day8date<- '2014-01-11 '
day9date<- '2014-01-19 ' #matched time frame: 14.02 - 15.25
day10date<-'2014-01-26 '
day11date<-'2014-02-01 '
day12date<-'2014-02-16 '
day13date<-'2014-03-02 '
day14date<-'2014-03-15 '
day15date<-'2014-04-06 '
day16date<-'2014-04-19 '


berk <- 'Berkeley'
el <- 'El Cerrito'


#helper function for items--------------

std.date <- function(day){
  if(nchar(day) == 1){
    day <- paste('0', day, sep='')
  }
  return(day)
}


  

# inputs -------------------------
items <- F
date <- day6date
day <- 6
city <- el
regnum<- 2
start <- 14.46
finish <- 15.09

# subsetting--------------------- --------------------------------------------------------------

  #item data slows process, set <item> to true to include it otherwise  subsetting is much quicker
  if(items){
    #Read item-level data in STATA format-
        items<-read.dta('itemDATA.dta')
    
    # Format IDs, Date and time to consistent, numeric form
        items$txn_id <- format(items$txn_id, scientific = F)
        items$txn_time <-  as.numeric(gsub(':', '.',
                                          gsub(':00$', '',
                                          gsub('.* ', "", items$txn_time))))
        items$txn_date <- as.character(items$txn_date)
        months <- unlist(lapply(gsub('\\/.*$', '', items$txn_date), std.date))
        days <- unlist(lapply(gsub('\\/.*$', '', 
                              gsub('^[0-9]{1,2}\\/', '', items$txn_date)),
                                    std.date))
        years <- gsub('.*\\/', '', days.years.temp)
        items$txn_date <- item_date <- gsub('$', ' ', paste(years, months, days, sep = '-'))


        item.day.temp <- items[items$txn_date == date,]
        all.regs.temp <- item.day.temp[(item.day.temp$txn_time > start) &
                                       (item.day.temp$txn_time < finish),]
        ss.Items<-all.regs.temp[all.regs.temp$reg_nbr == regnum,]
        ss.Items <- ss.Items[order(ss.Items$txn_time),]
        #remvove temporary variables
        rm(item.day.temp, all.regs.temp)
}



# instore and transaction data
  in.store<-read.csv('instore_data.csv', header = T)
  transactions<-read.dta('ScannerData_Transaction_NEW.dta')

  transactions$txn_ID <- sprintf("%..41g", transactions$txn_id)#format txn_id to include all digits
  in.store.temp <-in.store[(in.store$day == day) & (in.store$stnmb == city), ]
  tran.temp <- transactions[(transactions$day == day) & (transactions$stnmb == city), ]

  numeric.transaction.time <- gsub(date, '', tran.temp$txn_time) # get rid of date
  numeric.transaction.time <- gsub(':00', '', numeric.transaction.time) # covert to decimal for subsetting
  numeric.transaction.time <- as.numeric(gsub(':', '.', numeric.transaction.time))

  tran.temp$txn_float_time <- numeric.transaction.time


  aIn.store <- in.store.temp[in.store.temp$reg_nbr ==regnum,]
  reg.temp <- tran.temp[tran.temp$reg_nbr == regnum,]

  float <- reg.temp$txn_float_time
  aTransactions <- reg.temp[(float > start) & (float < finish),]
  aTransactions$txn_id <- format(aTransactions$txn_id, scientific = F)

#remove temporary variables that help create final DFs
rm(in.store.temp, tran.temp, reg.temp)











