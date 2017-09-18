
public class task {
int taskNumber;
int initialClaim;
//int [] currentLoan;
Boolean isTerminated;
Boolean isAborted;
Boolean isBlocked;
int totalTime;
int waitTime;
int percentage;
Boolean isProcessed;
int numberOfResourceType;


	public task(){
		this.isTerminated=false;
		this.isAborted=false;
		this.isBlocked= false;
		this.totalTime=0;
		this.waitTime=0;
		this.percentage=0;
		//this.currentLoan=0;
		this.isProcessed=false;
		this.numberOfResourceType=0;
	//	this.currentLoan=new int[this.numberOfResourceType];

		
	}

	public task(int taskNumber, int delay, int resourceType, int initialClaim){
		this.initialClaim=initialClaim;
		this.taskNumber=taskNumber;
		this.isTerminated=false;
		this.isAborted=false;
		this.isBlocked= false;
		this.totalTime=0;
		this.waitTime=0;
		this.percentage=0;
	//	this.currentLoan=0;
		this.isProcessed=false;
		this.numberOfResourceType=0;
	//	this.currentLoan=new int[this.numberOfResourceType];

	}
	public void displayOutput(){
		System.out.println("Task" +taskNumber+"        "+totalTime+"   "+waitTime+"  "+percentage+"%");
	}
	
}
