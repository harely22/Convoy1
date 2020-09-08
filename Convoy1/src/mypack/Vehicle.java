package mypack;
import java.util.ArrayList;
import java.util.List;

public class Vehicle implements Comparable<Vehicle> {
	int currentStation;
	int destinationStation;
	String status;
	double accumulatedProportionalShare;
	double accumulatedActualShare;
	double accumulatedRatio;

	public List convoys=new ArrayList<Convoy>();
	public Convoy currentConvoy;
	public List ratios=new ArrayList<Double>();
	boolean isLeader;
	boolean loopOnce;//for cases where destination == current to prevent immediate leave

	public double participationScore;
	public static int counter;
	private int index;
	public Vehicle() {
		index=counter++;
		currentStation=generateNewStation();
		status="Parked";
	}
	public int getIndex() {
		return index;
	}

	public int generateNewStation() {
		loopOnce=false;
		destinationStation=((int)(Math.random()*Test1.numStations));
		if(destinationStation==currentStation) {
			loopOnce=true;

		}
		return	destinationStation;
	}
	public void updateParticipationScore() {
		participationScore=Math.random();
		if(participationScore<Test1.participationProbability) {
			currentConvoy=new Convoy();
		}
	}
	public void stopAtStation(int destinationStationIndex) {
		//	System.out.println(" Vehicle "+index+" stopped at station "+destinationStationIndex);
		currentStation=destinationStationIndex;
		convoys.add(currentConvoy);
		//	System.out.println(" Vehicle "+index+" now participated in "+convoys.size()+" convoys");

		double addToRatios= currentConvoy.actual/currentConvoy.proportional;
		ratios.add (addToRatios);
		status="Parked";
	}

	public int compareTo(Vehicle other) {
		if (participationScore>=other.participationScore)
			return 1;
		else
			return -1;
	}
	public void calculateShares(int convoySize) {

		accumulatedProportionalShare+=((double)1.0/convoySize);
		currentConvoy.proportional+=((double)1.0/convoySize);
		if(isLeader) {
			accumulatedActualShare+=1.0;
			currentConvoy.actual+=1.0;

		}
		if(Test1.debugMode){System.out.println("Vehicle "+ getIndex()+" leader=" + isLeader+" is calculating shares for convoy size "+ convoySize+" adding to prop"+ ((double)1.0/convoySize)+" ratio="+((double)accumulatedActualShare/accumulatedProportionalShare));
		}

	}

	public String toString() {
		String s="Vehicle "+index;
		s+=", Status= "+status;
		s+=", Current station="+currentStation;
		s+=", Destination station="+destinationStation;
		s+=", Ratio="+this.accumulatedActualShare/this.accumulatedProportionalShare;
		s+=" Participated in "+convoys.size()+" convoys";
		s+=" accumulatedActualShare="+accumulatedActualShare;
		s+=" accumulatedProportionalShare="+accumulatedProportionalShare;
		//s+="\n";
		//for(Convoy c: (ArrayList<Convoy>)convoys) {
		//this.accumulatedActualShare+=c.actual;
		//this.accumulatedProportionalShare+=c.proportional;
		//this.accumulatedRatio=accumulatedActualShare/accumulatedProportionalShare;
		//		s+=",for loop accumulatedRatio= "+accumulatedRatio;
		//	}
		return s;
	}
	public void addToConvoy() {
		status="In Convoy";
	}
}
