package mypack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Convoy {
public List vehicles=new ArrayList<Vehicle>();
public int numberOfVehicles;
public Vehicle leader;
public int length;

public double actual;
public double proportional;

public String toString() {
	String s="Convoy has "+vehicles.size()+" participants";
	return s;
}
}
