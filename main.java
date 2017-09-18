import java.io.File;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;


public class main {
	
	public static void main(String[] args) throws IOException {
		//first chunk of code for FIFO, second chunk for banker's
		
		//setting up variables and inputs 
		int blockedCount=0;
		int numberOfTask;
		int numberOfResourceType;
		int [] resourceLeft;
		int abortedTotal=0;
		int terminatedTotal=0;
		task [] taskList;
		Queue [] queueList;
		boolean allfinished=false;
		boolean allblocked=false;
		
		// process first line and scanner
		
        File newfile = new File("Input.txt");
        Scanner scn= new Scanner(newfile);
		String [] start=scn.nextLine().split("\\s+");
		numberOfTask=Integer.parseInt(start[0]);
		numberOfResourceType=Integer.parseInt(start[1]);
		resourceLeft=new int[numberOfResourceType];
		for(int i=0;i<numberOfResourceType;i++){
			resourceLeft[i]=Integer.parseInt(start[i+2]);
		}

		//task list 
		taskList= new task[numberOfTask];
		for(int i=0;i<numberOfTask;i++){
			task newTask= new task();
			newTask.numberOfResourceType=numberOfResourceType;
			taskList[i]=newTask;
			
		}
		
	// records of current loans of each task 
		int [][] allLoans = new int [numberOfTask][];
		for(int i=0;i<numberOfTask;i++){
		  allLoans[i]=new int[numberOfResourceType];
		   for(int j=0;j<numberOfResourceType;j++){
		    allLoans[i][j]=0;
		   }	   
	
		}
		
		
		//setting up queues of task
		queueList= new LinkedList[numberOfTask];
		Boolean TaskInputFinished=false;
		for(int i=0;i<numberOfTask;i++){
			LinkedList <instruction>  newQueue = new LinkedList <instruction>();
			queueList[i]= newQueue;
		}
		
		int allterminated=0;
		
		//putting all input instructions into the queues 
		while(allterminated<numberOfTask){
			String next=scn.nextLine();
			if(!next.isEmpty()){
			String [] nextLine=next.split("\\s+");
			instruction nextInstruction= new instruction(nextLine[0], Integer.parseInt(nextLine[2]), Integer.parseInt(nextLine[3]), Integer.parseInt(nextLine[4]), Integer.parseInt(nextLine[1]));
			if(nextInstruction.instructionType.equals("terminate")){allterminated+=1;}
			queueList[Integer.parseInt(nextLine[1]) -1].add(nextInstruction);
	    	}
		}
		
		int [] nextCycleAvailable= new int [numberOfResourceType];
		for(int i=0;i<numberOfResourceType;i++){
			nextCycleAvailable[i]=0;
		}
		int cycleCount=0;
			LinkedList <instruction> blockedList = new LinkedList<instruction>();
			LinkedList <instruction> computingList = new LinkedList<instruction>();
			int computingCount=0;
			
		// main loop for fifo	
		while(allfinished==false){
			for(int y=0;y<taskList.length;y++){taskList[y].isProcessed=false;}
			for(int i=0;i<numberOfResourceType;i++){
				resourceLeft[i]+=nextCycleAvailable[i];
			}
			for(int i=0;i<numberOfResourceType;i++){
				nextCycleAvailable[i]=0;
			}
		    int blockedReduce=0;
		    int abortedCount=0;
			
		    //make a copy of the current blocked list
			LinkedList <instruction> lastBlockedList = new LinkedList<instruction>();
			lastBlockedList=blockedList;
		
			// for those instructions that are computing
			    int loopnum=computingCount;
			for(int x=0;x<loopnum;x++){
				if(!computingList.isEmpty()){
				instruction nextComputing= computingList.poll();
				if(nextComputing.delay>0){
					nextComputing.delay-=1;
					taskList[nextComputing.taskNumber-1].isProcessed=true;
					computingList.add(nextComputing);
				}
				else{
					computingCount-=1;
				}
				}
			}
			
			// for those instructions that are blocked
			for(int x=0;x<blockedCount;x++){
				if(!blockedList.isEmpty()){
				instruction nextBlocked= blockedList.poll();
				if(taskList[nextBlocked.taskNumber-1]!=null && taskList[nextBlocked.taskNumber-1].isProcessed==false){
				if(nextBlocked.instructionType.equals("request")&& taskList[nextBlocked.taskNumber-1].isAborted==false){
					//so that the processed block won't be processed again in this cycle
					taskList[nextBlocked.taskNumber-1].isProcessed=true;
					if(nextBlocked.amount<=resourceLeft[nextBlocked.resourceType-1]){
					   allLoans[nextBlocked.taskNumber-1][nextBlocked.resourceType-1]+=nextBlocked.amount;
						taskList[nextBlocked.taskNumber-1].isBlocked=false;
						blockedReduce+=1;
						resourceLeft[nextBlocked.resourceType-1]-=nextBlocked.amount;
						System.out.println("cycle"+cycleCount+" : task"+ (nextBlocked.taskNumber)+ "unblocked");}
					else{
						taskList[nextBlocked.taskNumber-1].waitTime+=1;
						blockedList.add(nextBlocked);
						System.out.println("cycle"+cycleCount+" : task"+ (nextBlocked.taskNumber)+ "reblocked");
					}
				}
				}
			} 
			}blockedCount-=blockedReduce;
		  
			
			// usual instruction processing loop
			for(int i=0;i<queueList.length;i++){
				
				if(!queueList[i].isEmpty() && taskList[i].isTerminated==false){
				instruction nextIns= (instruction) queueList[i].peek();
				if(taskList[nextIns.taskNumber-1].isProcessed){	continue;}
				
				if(nextIns.instructionType.equals("initiate")){
					if(nextIns.delay!=0 && !computingList.contains(nextIns)){computingList.add(nextIns);computingCount+=1;nextIns.delay-=1;continue;}
					queueList[i].poll();System.out.println("cycle"+cycleCount+" : task"+ (i+1)+ "initiate ins");}
				else if(nextIns.instructionType.equals("terminate")){
					if(nextIns.delay!=0 && !computingList.contains(nextIns)){computingList.add(nextIns);computingCount+=1;nextIns.delay-=1;continue;}
					taskList[i].isTerminated=true; 
					terminatedTotal+=1;System.out.println("cycle "+cycleCount+" : task"+ (i+1)+ "terminate ins");
					queueList[i].poll();
					taskList[nextIns.taskNumber-1].totalTime=cycleCount;
				}
			
				else if(nextIns.instructionType.equals("request")){
						if(nextIns.delay!=0 && !computingList.contains(nextIns)){computingList.add(nextIns);computingCount+=1;nextIns.delay-=1;continue;}
					if(nextIns.amount<=resourceLeft[nextIns.resourceType-1]){
						 allLoans[nextIns.taskNumber-1][nextIns.resourceType-1]+=nextIns.amount;
						queueList[i].poll(); 
						resourceLeft[nextIns.resourceType-1]-=nextIns.amount;
						System.out.println("cycle"+cycleCount+" : task"+ (i+1)+ "request successful"+resourceLeft[nextIns.resourceType-1]);}
					else if(nextIns.amount>resourceLeft[nextIns.resourceType-1]){
						blockedList.add(nextIns);
						taskList[i].isBlocked=true;
						blockedCount+=1;
						queueList[i].poll();
						taskList[nextIns.taskNumber-1].waitTime+=1;
						
						System.out.println("cycle"+cycleCount+" : task"+ (i+1)+ "blocked");
				
					}
				
				}
				else if(nextIns.instructionType.equals("release")){
					if(nextIns.delay!=0 && !computingList.contains(nextIns)){computingList.add(nextIns);computingCount+=1;nextIns.delay-=1;continue;}
					 nextCycleAvailable[nextIns.resourceType-1]+=nextIns.amount;
					 allLoans[nextIns.taskNumber-1][nextIns.resourceType-1]-=nextIns.amount;
					 queueList[i].poll();
					 System.out.println("cycle"+cycleCount+" : task"+ (i+1)+ "release");
				}
			}
		}
			
			//test for if all is blocked - deadlocked
			allblocked=true;
			int iteration=numberOfTask;
			for(int j=0;j<iteration;j++){
				if(taskList[j].isAborted==false && taskList[j].isTerminated==false){if(taskList[j].isBlocked==false){allblocked=false;} }
			}
			
			int ite=blockedCount;
			while(allblocked){
			if((abortedTotal+terminatedTotal+1)<numberOfTask){
				for(int z=0;z<numberOfTask;z++){
					if(taskList[z].isAborted==false && taskList[z].isTerminated==false){
						taskList[z].isAborted=true;
						abortedTotal+=1;
						System.out.println("task"+(z+1) + "is aborted:"+taskList[z].isAborted);
						taskList[z].isTerminated=true;
						 queueList[z].clear();
						 for(int i=0;i<numberOfResourceType;i++){
							 nextCycleAvailable[i]+= allLoans[z][i];
							}
						 for(int i=0;i<numberOfResourceType;i++){
							 allLoans[z][i]=0;
						 }
						 blockedCount-=1;
						 ite-=1;
						break;
						}
				} 
				
				for(int x=0;x<blockedCount;x++){
					if(!lastBlockedList.isEmpty()){
					instruction nextBlocked= blockedList.poll();
					if(taskList[nextBlocked.taskNumber-1]!=null){
						
					if(nextBlocked.instructionType.equals("request")&& taskList[nextBlocked.taskNumber-1].isAborted==false &&taskList[nextBlocked.taskNumber-1].isTerminated==false){
				
						if(nextBlocked.amount<=resourceLeft[nextBlocked.resourceType-1]+nextCycleAvailable[nextBlocked.resourceType-1]){
							allblocked=false;
							System.out.println("deadlock ended");
							blockedList.add(nextBlocked);
						  break;
							
					}		
			blockedList.add(nextBlocked);	}
				} 
					}
			}
		}	
			else{break;}
			}

			
			//test for all finished 
			allfinished=true;
			for(int i=0;i<taskList.length;i++){if(taskList[i].isTerminated==false ){allfinished=false;} }
			cycleCount+=1;

	   }
		
		System.out.println("         FIFO");
		int entireTotalTime=0;
		int entireTotalWaitTime=0;
		for(int d=0;d<numberOfTask;d++){
			if(!taskList[d].isAborted){
			System.out.println("Task "+(d+1)+"      "+taskList[d].totalTime+"    "+taskList[d].waitTime+"  "+(int)(taskList[d].waitTime*100/taskList[d].totalTime)+"%");
			entireTotalTime+=taskList[d].totalTime;
			entireTotalWaitTime+=taskList[d].waitTime;}
			else{System.out.println("Task "+(d+1)+"      aborted   ");}
		}
		System.out.println("total       "+entireTotalTime+"    "+entireTotalWaitTime+"  "+(int)(entireTotalWaitTime*100/entireTotalTime)+"%");
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//setting up variables and inputs 
		int blockedCount2=0;
		int numOfTask;
		int numOfResourceType;
		int [] resourceLeft2;
		//int abortedTotal=0;
		int terminatedTotal2=0;
		task [] taskList2;
		Queue [] queueList2;
		Boolean allfinished2=false;
		//boolean allblocked=false;
		boolean requestFlag=false;
		
		// process first line and scanner
		File inputFile2= new File("Input.txt");
		Scanner scn2= new Scanner(inputFile2);
		String [] start2=scn2.nextLine().split("\\s+");
		numOfTask=Integer.parseInt(start2[0]);
		numOfResourceType=Integer.parseInt(start2[1]);
		resourceLeft2=new int[numOfResourceType];
		for(int i=0;i<numOfResourceType;i++){
			resourceLeft2[i]=Integer.parseInt(start2[i+2]);
		}

		//task list 
		taskList2= new task[numOfTask];
		for(int i=0;i<numOfTask;i++){
			task newTask= new task();
			newTask.numberOfResourceType=numOfResourceType;
			taskList2[i]=newTask;
			
		}
		
	// records of current loans of each task 
		int [][] allLoans2 = new int [numOfTask][];
		for(int i=0;i<numOfTask;i++){
		  allLoans2[i]=new int[numOfResourceType];
		   for(int j=0;j<numOfResourceType;j++){
		    allLoans2[i][j]=0;
		   }	   
	
		}
		
		//maxClaim array
		int [][] maxClaim = new int [numOfTask][];
		for(int i=0;i<numOfTask;i++){
		  maxClaim[i]=new int[numOfResourceType];
		   for(int j=0;j<numOfResourceType;j++){
		    maxClaim[i][j]=0;
		   }	   
	
		}
		
		//setting up queues of task
		queueList2= new LinkedList[numOfTask];
		 TaskInputFinished=false;
		for(int i=0;i<numOfTask;i++){
			LinkedList <instruction>  newQueue = new LinkedList <instruction>();
			queueList2[i]= newQueue;
		}
		
		int allterminated2=0;
		
		//putting all input instructions into the queues 
		while(allterminated2<numOfTask){
			String next=scn2.nextLine();
			if(!next.isEmpty()){
			String [] nextLine=next.split("\\s+");
			instruction nextInstruction= new instruction(nextLine[0], Integer.parseInt(nextLine[2]), Integer.parseInt(nextLine[3]), Integer.parseInt(nextLine[4]), Integer.parseInt(nextLine[1]));
			if(nextInstruction.instructionType.equals("terminate")){allterminated2+=1;}
			queueList2[Integer.parseInt(nextLine[1]) -1].add(nextInstruction);
	    	}
		}
		
		int [] nextCycleAvailable2= new int [numOfResourceType];
		for(int i=0;i<numOfResourceType;i++){
			nextCycleAvailable2[i]=0;
		}
		int cycleCount2=0;
			LinkedList <instruction> blockedList2 = new LinkedList<instruction>();
			LinkedList <instruction> computingList2 = new LinkedList<instruction>();
			int computingCount2=0;
			
		// main loop for banker	
		while(allfinished2==false){
			for(int y=0;y<taskList2.length;y++){taskList2[y].isProcessed=false;}
			for(int i=0;i<numOfResourceType;i++){
				resourceLeft2[i]+=nextCycleAvailable2[i];
			}
			
			
			//test for resource left
	/*		for(int j=0;j<numOfResourceType;j++){
				System.out.print(" "+resourceLeft2[j]);
			
			}	System.out.println();
	*/		
			
			
			
			
			for(int i=0;i<numOfResourceType;i++){
				nextCycleAvailable2[i]=0;
			}
		    int blockedReduce2=0;
		   // int abortedCount2=0;
			
		    //make a copy of the current blocked list
			LinkedList <instruction> lastBlockedList2 = new LinkedList<instruction>();
			lastBlockedList2=blockedList2;
		//System.out.println("computingCount:"+computingCount2);
			// for those instructions that are computing
		   int loop=computingCount2;
			if(!computingList2.isEmpty()){
				for(int x=0;x<loop;x++){
				//System.out.println("test");
				
				instruction nextComputing= computingList2.poll();
				
				if(nextComputing.delay>0){System.out.println("Cycle "+cycleCount2+"Task "+nextComputing.taskNumber+"   delay is"+nextComputing.delay);
					nextComputing.delay-=1;
					taskList2[nextComputing.taskNumber-1].isProcessed=true;
					computingList2.add(nextComputing);
				}
				else{
			//		taskList2[nextComputing.taskNumber-1].isProcessed=true;
					computingCount2-=1;
				}
				}
			}
			
			// for those instructions that are blocked
			for(int x=0;x<blockedCount2;x++){
				if(!blockedList2.isEmpty()){
				instruction nextBlocked= blockedList2.poll();
				if(taskList2[nextBlocked.taskNumber-1]!=null && taskList2[nextBlocked.taskNumber-1].isProcessed==false){
				if(nextBlocked.instructionType.equals("request")&& taskList2[nextBlocked.taskNumber-1].isAborted==false){
					if(nextBlocked.amount+allLoans2[nextBlocked.taskNumber-1][nextBlocked.resourceType-1]>maxClaim[nextBlocked.taskNumber-1][nextBlocked.resourceType-1]){
						taskList2[nextBlocked.taskNumber-1].isAborted=true;
						allLoans2[nextBlocked.taskNumber-1][nextBlocked.resourceType-1]=0;
						nextCycleAvailable2[nextBlocked.resourceType-1]+=nextBlocked.amount;
						taskList2[nextBlocked.taskNumber-1].isProcessed=true;
						taskList2[nextBlocked.taskNumber-1].isTerminated=true;
						terminatedTotal2+=1;
						continue;
						//queueList2[nextBlocked.taskNumber-1].clear();
					}
			
					//so that the processed block won't be processed again in this cycle
					taskList2[nextBlocked.taskNumber-1].isProcessed=true;
					
					for(int i=0;i<numOfResourceType;i++){
						if(resourceLeft2[i]<maxClaim[nextBlocked.taskNumber-1][i]-allLoans2[nextBlocked.taskNumber-1][i]){
							requestFlag=false;break;
						}
						requestFlag=true;
					}
					
					if(requestFlag){
					   allLoans2[nextBlocked.taskNumber-1][nextBlocked.resourceType-1]+=nextBlocked.amount;
						taskList2[nextBlocked.taskNumber-1].isBlocked=false;
						blockedReduce2+=1;
						resourceLeft2[nextBlocked.resourceType-1]-=nextBlocked.amount;
						System.out.println("cycle"+cycleCount2+" : task"+ (nextBlocked.taskNumber)+ "unblocked");}
					else{
						taskList2[nextBlocked.taskNumber-1].waitTime+=1;
						blockedList2.add(nextBlocked);
						System.out.println("cycle"+cycleCount2+" : task"+ (nextBlocked.taskNumber)+ "reblocked");
					}
				}
				}
			} 
			}blockedCount2-=blockedReduce2;
		  
			
			// usual instruction processing loop
			for(int i=0;i<queueList2.length;i++){
				instruction nextIns= (instruction) queueList2[i].peek();
				if(!queueList2[i].isEmpty() && taskList2[i].isTerminated==false &&taskList2[i].isAborted==false){
				
				if(taskList2[nextIns.taskNumber-1].isProcessed){	continue;}
				
				if(nextIns.instructionType.equals("initiate")){
					if(nextIns.amount>resourceLeft2[nextIns.resourceType-1]){
						
						queueList2[i].poll(); 
						taskList2[nextIns.taskNumber-1].isAborted=true;
						allLoans2[nextIns.taskNumber-1][nextIns.resourceType-1]=0;
				//		nextCycleAvailable2[nextIns.resourceType-1]+=nextIns.amount;
						taskList2[nextIns.taskNumber-1].isTerminated=true;
						terminatedTotal2+=1;
						continue;
						
					}
					if(nextIns.delay!=0 && !computingList2.contains(nextIns)){nextIns.delay-=1;computingList2.add(nextIns);computingCount2+=1;
					System.out.println("cycle "+cycleCount2+" : task"+ (i+1)+ "computing"+"delay is "+nextIns.delay);continue;}
					// setting up maxClaim
					maxClaim[nextIns.taskNumber-1][nextIns.resourceType -1]=nextIns.amount;
					queueList2[i].poll();System.out.println("cycle"+cycleCount2+" : task"+ (i+1)+ "initiate ins");}
				else if(nextIns.instructionType.equals("terminate")){
					if(nextIns.delay!=0 && !computingList2.contains(nextIns)){nextIns.delay-=1;computingList2.add(nextIns);computingCount2+=1;System.out.println("cycle "+cycleCount2+" : task"+ (i+1)+ "computing"+"delay is "+nextIns.delay);continue;}
					taskList2[i].isTerminated=true; 
					terminatedTotal2+=1;System.out.println("cycle "+cycleCount2+" : task"+ (i+1)+ "terminate ins");
					queueList2[i].poll();
					taskList2[nextIns.taskNumber-1].totalTime=cycleCount2;
				}
			
				else if(nextIns.instructionType.equals("request")){
					if(nextIns.amount+allLoans2[nextIns.taskNumber-1][nextIns.resourceType-1]>maxClaim[nextIns.taskNumber-1][nextIns.resourceType-1]){
						queueList2[i].poll(); 
						taskList2[nextIns.taskNumber-1].isAborted=true;
						allLoans2[nextIns.taskNumber-1][nextIns.resourceType-1]=0;
						nextCycleAvailable2[nextIns.resourceType-1]+=nextIns.amount;
						taskList2[nextIns.taskNumber-1].isTerminated=true;
						terminatedTotal2+=1;
						continue;
					//	queueList2[nextIns.taskNumber-1].clear();
					}
			
						if(nextIns.delay!=0 && !computingList2.contains(nextIns)){nextIns.delay-=1;computingList2.add(nextIns);computingCount2+=1;System.out.println("cycle "+cycleCount2+" : task"+ (i+1)+ "computing"+"delay is "+nextIns.delay);continue;}
				
						boolean requestFlag2=false;
						for(int j=0;j<numOfResourceType;j++){
							if(resourceLeft2[j]<maxClaim[nextIns.taskNumber-1][j]-allLoans2[nextIns.taskNumber-1][j]){
								requestFlag2=false;break;
							}
							requestFlag2=true;
						}
						
						
						if(requestFlag2){
						 allLoans2[nextIns.taskNumber-1][nextIns.resourceType-1]+=nextIns.amount;
						queueList2[i].poll(); 
						resourceLeft2[nextIns.resourceType-1]-=nextIns.amount;
						System.out.println("cycle"+cycleCount2+" : task"+ (i+1)+ "request successful"+resourceLeft2[nextIns.resourceType-1]);}
					else{
						blockedList2.add(nextIns);
						taskList2[i].isBlocked=true;
						blockedCount2+=1;
						queueList2[i].poll();
						taskList2[nextIns.taskNumber-1].waitTime+=1;
						
						System.out.println("cycle"+cycleCount2+" : task"+ (i+1)+ "blocked");
				
					}
				
				}
				else if(nextIns.instructionType.equals("release")){
					if(nextIns.delay!=0 && !computingList2.contains(nextIns)){nextIns.delay-=1;computingList2.add(nextIns);computingCount2+=1;System.out.println("cycle "+cycleCount2+" : task"+ (i+1)+ "computing"+"delay is "+nextIns.delay);continue;}
					 nextCycleAvailable2[nextIns.resourceType-1]+=nextIns.amount;
					 allLoans2[nextIns.taskNumber-1][nextIns.resourceType-1]-=nextIns.amount;
					 queueList2[i].poll();
					 System.out.println("cycle"+cycleCount2+" : task"+ (i+1)+ "release");
				}
			}
		}
			

			//test for all finished 
			allfinished2=true;
			for(int i=0;i<taskList2.length;i++){if(taskList2[i].isTerminated==false ){allfinished2=false;} }
			cycleCount2+=1;
			
		

	   }
		
		System.out.println("         Banker's");
		entireTotalTime=0;
		 entireTotalWaitTime=0;
		for(int d=0;d<numOfTask;d++){
			if(taskList2[d].isAborted){System.out.println("Task "+(d+1)+"      aborted");continue;}
			if(!taskList2[d].isAborted){
			System.out.println("Task "+(d+1)+"      "+taskList2[d].totalTime+"    "+taskList2[d].waitTime+"  "+(int)(taskList2[d].waitTime*100/taskList2[d].totalTime)+"%");
			entireTotalTime+=taskList2[d].totalTime;
			entireTotalWaitTime+=taskList2[d].waitTime;}
			else{System.out.println("Task "+(d+1)+"      aborted   ");}
		}
		System.out.println("total       "+entireTotalTime+"    "+entireTotalWaitTime+"  "+(int)(entireTotalWaitTime*100/entireTotalTime)+"%");
		
		
		
		
		
		
		
		
	}

	
}
