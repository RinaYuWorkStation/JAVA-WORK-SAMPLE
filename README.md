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



