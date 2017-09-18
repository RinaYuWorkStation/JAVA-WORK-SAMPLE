
public class instruction {
String instructionType;
int delay;
int resourceType;
int amount;
int taskNumber;

	
public instruction(){
	
	
}

public instruction(String instructionType, int delay, int resourceType, int amount,int taskNumber){
	this.instructionType=instructionType;
	this.delay=delay;
	this.resourceType=resourceType;
	this.amount=amount;
	this.taskNumber=taskNumber;
	
	
}
	
}
