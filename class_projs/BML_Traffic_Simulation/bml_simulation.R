#################################################################################
#### BML Simulation Study





#The function below uses bml.sim to perform <times> simulations for a grid of size r*c with density p.
#It outputs the average number of steps it took to reach gridlock. (bml.sim uses 1500 steps as a 
#cutoff to determine a free-flowing state)

find.average.steps<-function(r, c, p, times){
  j<-0
  results<-NULL
  
  for(i in 1:times){
    j <- j + 1
    print(j)
    results[j]<-bml.sim(r, c, p)
  }
  average.steps <- sum(results)/length(results)
  print('AVERAGE STEPS:')
  return(average.steps)
}


X4<- NULL
X10<-NULL
X25<-NULL
X50<-NULL
X100<-NULL

j <- 0

Densities <-c(.3,.4,.45, .5, .51, .52, .53, .54, .55, .56, .57, .58, .59, .6, .65,.7,.8)


#uncomment to run due to time elasped
for(i in Densities){
  j<- j+1
  #X4[j]<-(find.average.steps(4, 4, i, 100))
  #X10[j]<-(find.average.steps(10, 10, i, 100))
  #X25[j]<-(find.average.steps(25, 25, i, 100))
  #X50[j]<-(find.average.steps(50, 50, i, 100))
}


#the following were the results form running the above for-loop after uncommenting the assignment statements

# 4x4 grid
X4<-c(1500.00,1500.00,1500.00,1470.04,1128.12,1083.01,1172.10,1157.63,
      1082.17,1128.28,888.75,873.75,814.26,799.50,425.88,200.81,49.87)
# 10x10 grid
X10<-c(1500.00,1500.00,1409.80,1032.76,791.12,761.83,460.68,567.40,
       309.99,171.03,146.98,158.91,134.58,119.99,37.53,26.41,16.86)

# 25x25 grid
X25<-c(1500.00,1490.96,1093.87,308.87,240.17,204.00,178.75,144.70,
       132.58,110.52,109.51,104.56,100.13,89.51,65.36,58.05,35.14)
# 50x50 grid
X50<-c(1500.00,1383.91,702.93,328.45,315.81,265.76,239.69,202.00,
       215.40,184.01,167.34,172.27,157.98,153.96,123.09,91.65,50.03)


plot(Densities, X4,
     main = 'Steps Until Gridlock by Matrix Size/Density',
     sub = '(average of 100 simulations at each density)',
     xlab = 'Density',
     ylab = 'Steps',
     col='purple', type='l', lwd=5)
lines(Densities, X10, col='green3', type='l',lwd=5)
lines(Densities, X25, col='red', type='l', lwd=5)
lines(Densities, X50, col='blue2', type='l',lwd=5)
names<- c('4x4 grid', '10x10 grid','25x25 grid', '50x50 grid')
legend('topright', names, lty=1, col=c('purple', 'green3','red', 'blue2'), bty='o', cex=1, lwd=5)

abline(v=.4)
abline(v=.56)
axis(1, at=.56, col='grey')
