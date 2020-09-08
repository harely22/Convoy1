package mypack;

import java.util.ArrayList;
import java.util.List;

import java.util.List;
//This project measures the percent of unsatisfied agents as a function of the number of convoys they participated in
// While running the repeated Sequential Online Chore Division (SOCD) game,
//Test is the main file that contains all of the definitions and runs the simulation
//It prints the statistics to the console, specifically it outputs data to plot a figure showing the percent of unsatisfied costumers as a function of the number of participation.
//For each number of participations it runs several trial and averages out the percent of unsatisfied costumers.
//An unsatisfied costumer is one who lead for more that 10% of it ex-post proportional share in practice.

/*
The experimental setup has 100 stations where vehicles can enter and exit. The stations are uniformly distributed on a ring road with a total length of 100km. %\comments{Shani: Maybe use miles instead?}. 
100 vehicles are randomly distributed over these 100 stations (using a uniform distribution).
A single convoy cycles through the stations, and when it reaches a station, the vehicles in that station have a 0.1 probability of entering the road and joining the convoy.
The distance they join for is also randomly generated from a uniform distribution $[0,100]$. Once a vehicle completes its distance, it leaves the convoy and parks at its destination station until the convoy passes it again, at which point it has a 0.1 probability of rejoining.
If multiple vehicles join at the same station, their order is randomized since the last one in becomes the leader.
Leaders are replaced when another vehicle joins or when they reach their destination. 
For every section between consecutive stations, every vehicle in the convoy accumulates $\frac{1}{n}$ to its proportional share, and the leader also accumulates 1 to its actual share.
Whenever a vehicle exits, the accumulated actual and ex-post proportional shares are recorded, and added to the list of convoys that the vehicle has participated in.
We measure how many participations on average it takes for the ratio of actual over ex-post proportional share to converge to 1. 
Specifically, we measure what is the percentage of vehicles that lead for $10\%$ or more than what they should have, which we refer to as \emph{unsatisfied agents}.
*/
public class Test1 {
	
	 public static int numStations=100; 
	 public static int numVehicles=100;  
	 public static int maxNumberOfCycles=8000; //Determines the maximal number of cycles that the convoy will make around the ring road, but the experiments will run for all the numbers of cycles from 0 to that number in increments of cycle step
	 public static int testsPerCycle=10; //Determines how many tests to run and average for every cycle step
	 public static int cycleStep=100; //Determines the increment of number of cycles in each test iteration up to maxNumberOfCycles
	 
//small setup for debugging	
//	public static int numStations=10;
//	public static int numVehicles=10;
//	public static int maxNumberOfCycles=100;
//	public static int testsPerCycle=10;
//	public static int cycleStep=20;

	public static double[] percentOfUnsatisfiedCostumersPerCycle=new double[maxNumberOfCycles/cycleStep];
	public static double[] percentOfUnsatisfiedCostumersPerTest=new double[testsPerCycle];//for 1 step size

	public static boolean debugMode=false;//set this to false to mute debug messages

//	public static double participationThreshold=0.9;
	public static double participationProbability=0.1;

	public static double TotalAccumulatedProportionalShare;
	public static double TotalAccumulatedActualShare;
	public static Station[] stations;
	public static Vehicle[] vehicles;
	public static Convoy convoy;
	Road r=new Road();

	public static void main(String[] args) {
		System.out.println("Starting Convoy");
		for (int i=0;i<maxNumberOfCycles/cycleStep;i++) {
			System.out.println();

			System.out.println("cycle step "+i);

			for (int j=0;j<testsPerCycle;j++) {
				System.out.println("    test number "+j);

		convoy=new Convoy();
		generateStations();
		generateVehicles();
		cycle((1+i)*cycleStep);	
		double testRatio=printStats();
		percentOfUnsatisfiedCostumersPerCycle[i]+=testRatio;
		percentOfUnsatisfiedCostumersPerTest[j]=testRatio;
			}
		}
		printFinalStats();
	}
	private static void generateStations() {
		System.out.println("Generating "+numStations+" Stations");
		Station.counter=0;
		stations=new Station[numStations];
		for (int i=0;i<numStations;i++) {
			stations[i]=new Station();
		}
	}
	private static void generateVehicles() {
		System.out.println("Generating "+numVehicles+" Vehicles and adding them to stations");
		vehicles=new Vehicle[numVehicles];
		Vehicle.counter=0;
		for (int i=0;i<numVehicles;i++) {
			vehicles[i]=new Vehicle();
			if(debugMode){System.out.println(vehicles[i]);}

			stations[vehicles[i].currentStation].vehicles.add(vehicles[i]);
		}
	}
	public static void cycle(int times) {
		System.out.println("Cycling "+times+" times");
		List enteringVehicles=new ArrayList<Vehicle>();
		for(int j=0;j<times;j++) {
			for(int i=0;i<numStations;i++) {
				Station s=stations[i];
				calculateShares();
				if(debugMode){System.out.println(s);}
				if(!s.isEmpty()) {
					enteringVehicles=s.chooseParticipatingVehicleOrder();
				}
				if(!enteringVehicles.isEmpty()) {
					addEnteringVehivclesToConvoy (enteringVehicles);
					switchLeadersDueToEntry();
				}
				boolean leaderLeft=addLeavingVehiclesToStation(s);
				if(leaderLeft) {
				if(debugMode){System.out.println("leaderLeft="+leaderLeft);}
				}
				if(leaderLeft) {
					switchLeadersDueToLeave();
				}

			}
		}
	}

	private static double printStats() {
		double averageNumberOfConvoys=0;
		double averageNumberOfVehicleWithMoreThanTenPercentRatio=0;
		System.out.println("==============================================");
		System.out.println("===============Printing Stats=================");
		System.out.println("==============================================");
		for(Vehicle v: vehicles) {
			if(debugMode){System.out.println(v);}
			TotalAccumulatedProportionalShare+=v.accumulatedProportionalShare;
			TotalAccumulatedActualShare+=v.accumulatedActualShare;
			
			averageNumberOfConvoys+=v.convoys.size();
			if(v.accumulatedActualShare/v.accumulatedProportionalShare>1.1) { //for unsatisfied costumers
//			if(v.accumulatedActualShare/v.accumulatedProportionalShare<0.9) { //for super satisfied costumenrs

			averageNumberOfVehicleWithMoreThanTenPercentRatio++;
			}
			
		}
		if(debugMode){System.out.println("TotalAccumulatedProportionalShare="+TotalAccumulatedProportionalShare+" TotalAccumulatedActualShare="+TotalAccumulatedActualShare+" Ratio="+ ((double)TotalAccumulatedActualShare/TotalAccumulatedProportionalShare));}
		System.out.println("Average Number Of Convoys="+averageNumberOfConvoys/numVehicles);
		double percent=100*averageNumberOfVehicleWithMoreThanTenPercentRatio/numVehicles;
		System.out.println("Percent Of Vehicles With More Than Ten Percent Ratio="+percent);
		return percent;

	}
	private static void printFinalStats() {

		System.out.println("==============================================");
		System.out.println("===============Printing Final Stats=================");
		System.out.println("==============================================");
		System.out.println("maxNumberOfCycles="+maxNumberOfCycles);
		System.out.println("cycleStep="+cycleStep);
		System.out.println("testsPerCycle="+testsPerCycle);

		System.out.println("averageNumberOfExpectedParticipations  percentOfUnsatisfiedCostumersPerCycle ");

		for (int i=0;i<maxNumberOfCycles/cycleStep;i++) {
			System.out.print((participationProbability)*(i+1)*(cycleStep));
			System.out.println("  "+percentOfUnsatisfiedCostumersPerCycle[i]/testsPerCycle);
			}
	

		
//		for (int j=0;j<testsPerCycle;j++) {	
//			System.out.println("  "+percentOfUnsatisfiedCostumersPerTest[j]);
//		}
		
	}
	private static void switchLeadersDueToEntry() {
		if(convoy.leader!=null) {
			convoy.leader.isLeader=false;
		}
		convoy.leader=(Vehicle) convoy.vehicles.get(convoy.vehicles.size()-1);
		convoy.leader.isLeader=true;
		if(debugMode){System.out.println("Switching Leaders. "+convoy.leader.getIndex()+" is now the leader- new entry");
		}
	}
	private static void switchLeadersDueToLeave() {
		if(!convoy.vehicles.isEmpty()) {

			convoy.leader=(Vehicle) convoy.vehicles.get(convoy.vehicles.size()-1);
			convoy.leader.isLeader=true;
			if(debugMode){System.out.println("Switchig Leaders. "+convoy.leader.getIndex()+" is now the leader - someone left");
			}
			}
	}

	private static void calculateShares() {
		if(convoy.vehicles.size()>1) {
			for(Vehicle v: (ArrayList<Vehicle>)convoy.vehicles) {
				v.calculateShares(convoy.vehicles.size());
			}
		}
	}

	private static boolean addLeavingVehiclesToStation(Station s) {
		boolean leaderLeft=false;
		List removeList=new ArrayList<Vehicle>();
		for(Vehicle v: (ArrayList<Vehicle>)convoy.vehicles) {
			if(v.destinationStation==s.getIndex()) {
				if(v.loopOnce==false) {
			 		if(debugMode){System.out.println("Vehicle "+v.getIndex() +" is leaving");
			 		}
					s.vehicles.add(v);
					removeList.add(v);
					//				convoy.vehicles.remove(v);// to avoid concurrent exception
					v.stopAtStation(s.getIndex());
					if(v.isLeader) {
						v.isLeader=false;
						leaderLeft=true;
					}
				}
				else {
					v.loopOnce=false;
				}
			}
		}
	//	if(!removeList.isEmpty()) {
		//	System.out.println("removeList.isEmpty()="+removeList.isEmpty());
		
//		}
		convoy.vehicles.removeAll(removeList);
		return leaderLeft;
	}

	private static void addEnteringVehivclesToConvoy(List enteringVehicles) {
		if(debugMode){System.out.print("Entering "+enteringVehicles.size()+" Vehicles into the convoy. ");}
		if(debugMode){System.out.println(convoy);}
		convoy.vehicles.addAll(enteringVehicles);
		for(Vehicle v: (ArrayList<Vehicle>)enteringVehicles) {
			v.addToConvoy();
			if(debugMode){System.out.println("Entering : "+v);}
		}
		enteringVehicles.clear();

	}
}