#################################################################################
#### Functions for BML Simulation Study


#### Initialization function.
## Input : size of grid [r and c] and density [p]
## Output : A matrix [m] with entries 0 (no cars) 1 (red cars) or 2 (blue cars)
## that stores the state of the system (i.e. location of red and blue cars)
#library(animation)



bml.init <- function(r, c, p){
  
  
  ncars <- p*r*c
  total <- r*c
  #ceiling divison ensures that the grid always has at least one car with p > 0
  colors = c(rep(0, total-ncars), rep(1, ceiling(ncars/2)), rep(2, ceiling(ncars/2)))
  
  m <- matrix(sample(colors, total), nrow = r)
  if(nrow(m) > 1 & ncol(m) > 1){
    image(t(apply(m,2,rev)), col=c('white','red','blue'), ask =T, axes=F)
  }

  return(m)
}


#move red cars east
red.step <- function(row){
  ones<-rev(which(row==1))
  zeros<-which(row==0)
  for(i in ones){
    if(length(row) %in% ones & row[1] == 0){
      row[1]<-1
      row[length(row)]<-0
    }
    if(!(i+1) %in% zeros){
      next
    }
    if(i+1 %in% zeros){
      row[i+1]<-row[i]
      row[i]<-0
    }
  }
  return(row)
}

#move blue cars north
blue.step <- function(row){
  twos<-which(row==2)
  zeros<-which(row==0)
  for(i in twos){
    if(1 %in% twos & row[length(row)] == 0){
      row[length(row)] <- 2
      row[1] <- 0
    }
    if(!(i-1) %in% zeros){
      next 
    }
    if(i-1 %in% zeros){
      row[i-1] <- row[i]
      row[i] <- 0
    }
  }
  return(row)
}


#### Function to move the system one step (east and north)
## Input : a matrix [m] of the same type as the output from bml.init()
## Output : TWO variables, the updated [m] and a logical variable
## [grid.new] which should be TRUE if the system changed, FALSE otherwise.

bml.step <- function(m){
  grid.new<-T
  if(nrow(m) == 1){
    new.m<-red.step(m)
  }
  else{
    move.reds<-matrix(apply(m, 1, red.step), ncol=ncol(m), byrow=T)
    new.m<-apply(t(move.reds), 1, blue.step)
    image(t(apply(new.m,2,rev)), col=c('white','red','blue'), ask =T, axes=F)
  }
  
  for(i in (new.m==m)){
    if(i==F){
      grid.new<-F
    }
  }
  return(list(new.m, grid.new))
}




#### Function to do a simulation for a given set of input parameters


par(ask=T)
bml.sim <- function(r, c, p){
  s<-1
  iters<-seq(100, 1400, by=100)
  m<-bml.step(bml.init(r, c, p))
  
  while(m[[2]]==F){
    for(i in iters){
      if(s==i){
      cat('steps:') #keeps track every 100 iterations for peace of mind
      cat(s, '\n')  #during long/large simulations
      }
    }
    s<- s+1
    m<-bml.step(m[[1]])
    
    if(s==1500){ #max iterations allowed = 1500
      break
    }
  }
  if(s<1500){
    cat('!!     GRIDLOCK     !!','\n')
    cat(' DENSITY:',p,'\n','SIZE:',r*c, '\n', 'STEPS:', '\n')
  }
  else{
    cat('!!     MAX ITERATIONS REACHED     !!', '\n')
    cat('1500 steps taken at DENSITY:',p,'SIZE:',r*c, '\n' )
  }
  return(s)
}

















