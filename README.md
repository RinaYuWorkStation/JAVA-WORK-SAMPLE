# Multi-process Resource Allocation Simulation
This is a multi-process resource allocation simulation of low level operating system using FIFO and Banker's Algorithm


To run, download files 
put input into the input file (already in, for more input refer to moreInput.txt)
compile by javac main.java
run by java main

the output format will be :
1. FIFO run details
2. FIFO simple output (finish time, waiting time, percentage of time spent waiting)
3. BANKER’S run details
4. BANKER’S simple output(finish time, waiting time, percentage of time spent waiting)

# Detailed Description:
# Banker’s algorithm and multi-process resource allocation

The program uses an optimistic resource manager and the Banker’s algorithm of Dijkstra to complete all requests and avoid deadlocks. The optimistic resource manager satisfies a request if possible, otherwise make it wait. When a release occurs, try to satisfy the pending requests in FIFO order. The Banker's algorithm is better at preventing deadlocks compared to the FIFO. 

Input contains: T, # tasks, and R, # resource types and # units of each resource types. Then comes multiple inputs, each representing the next activity of a task. Possible activities: initiate, request, release and terminate. The manager can process one activity (initiate, request or release) for each task in one cycle, but terminate does not require a cycle. 

## **Initiate** activity: string, 4 integers
-	initiate task-number delay resource-type initial-claim

## The **request** and **release** activities are written
-	request task-number delay resource-type number-requested*
-	release task-number delay resource-type number-released

the delay represents the number of cycles between the completion of the previous activity for this process and the the beginning of current activity. 

## **terminate** activity:
-	terminate task-number delay unused unused

## sample input:
  2 1 4
  initiate  1 0 1 4
  request   1 5 1 1
  release   1 5 1 1
  terminate 1 5 0 0
  initiate  2 0 1 4
  request   2 5 1 1
  release   2 5 1 1
  terminate 2 5 0 0


## sample output: (detailed)
  cycle0 : task1initiate ins
  cycle0 : task2initiate ins
  cycle6 : task1request successful3
  cycle6 : task2request successful2
  cycle12 : task1release
  cycle12 : task2release
  cycle 18 : task1terminate ins
  cycle 18 : task2terminate ins
           FIFO
  Task 1      18    0  0%
  Task 2      18    0  0%
  total       36    0  0%
  cycle0 : task1initiate ins
  cycle0 : task2initiate ins
  cycle 1 : task1computingdelay is 4
  cycle 1 : task2computingdelay is 4
  Cycle 2Task 1   delay is4
  Cycle 2Task 2   delay is4
  Cycle 3Task 1   delay is3
  Cycle 3Task 2   delay is3
  Cycle 4Task 1   delay is2
  Cycle 4Task 2   delay is2
  Cycle 5Task 1   delay is1
  Cycle 5Task 2   delay is1
  cycle6 : task1request successful3
  cycle6 : task2blocked
  cycle7 : task2reblocked
  cycle 7 : task1computingdelay is 4
  Cycle 8Task 1   delay is4
  cycle8 : task2reblocked
  Cycle 9Task 1   delay is3
  cycle9 : task2reblocked
  Cycle 10Task 1   delay is2
  cycle10 : task2reblocked
  Cycle 11Task 1   delay is1
  cycle11 : task2reblocked
  cycle12 : task2reblocked
  cycle12 : task1release
  cycle13 : task2unblocked
  cycle 13 : task1computingdelay is 4
  Cycle 14Task 1   delay is4
  cycle 14 : task2computingdelay is 4
  Cycle 15Task 1   delay is3
  Cycle 15Task 2   delay is4
  Cycle 16Task 1   delay is2
  Cycle 16Task 2   delay is3
  Cycle 17Task 1   delay is1
  Cycle 17Task 2   delay is2
  Cycle 18Task 2   delay is1
  cycle 18 : task1terminate ins
  cycle19 : task2release
  cycle 20 : task2computingdelay is 4
  Cycle 21Task 2   delay is4
  Cycle 22Task 2   delay is3
  Cycle 23Task 2   delay is2
  Cycle 24Task 2   delay is1
  cycle 25 : task2terminate ins
           Banker's
  Task 1      18    0  0%
  Task 2      25    7  28%
  total       43    7  16%



