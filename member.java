import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class member implements Serializable {

	private String LN;
	private String FN;
	private String EM;
	private int PN;
	private int ID;
	private double FINES;
	
	private Map<Integer, loan> LNS;

	
	public member(String lastName, String firstName, String email, int phoneNo, int id) {
		this.LN = lastName;
		this.FN = firstName;
		this.EM = email;
		this.PN = phoneNo;
		this.ID = id;
		
		this.LNS = new HashMap<>();
	}

	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Member:  ").append(ID).append("\n")
		  .append("  Name:  ").append(LN).append(", ").append(FN).append("\n")
		  .append("  Email: ").append(EM).append("\n")
		  .append("  Phone: ").append(PN)
		  .append("\n")
		  .append(String.format("  Fines Owed :  $%.2f", FINES))
		  .append("\n");
		
		for (loan LoAn : LNS.values()) {
			sb.append(LoAn).append("\n");
		}		  
		return sb.toString();
	}

	
	public int GeT_ID() {
		return ID;
	}

	
	public List<loan> GeT_LoAnS() {
		return new ArrayList<loan>(LNS.values());
	}

	
	public int Number_Of_Current_Loans() {
		return LNS.size();
	}

	
	public double Fines_OwEd() {
		return FINES;
	}

	
	public void Take_Out_Loan(loan loan) {
		if (!LNS.containsKey(loan.ID())) {
			LNS.put(loan.ID(), loan);
		}
		else {
			throw new RuntimeException("Duplicate loan added to member");
		}		
	}

	
	public String Get_LastName() {
		return LN;
	}

	
	public String Get_FirstName() {
		return FN;
	}


	public void Add_Fine(double fine) {
		FINES += fine;
	}
	
	public double Pay_Fine(double AmOuNt) {
		if (AmOuNt < 0) {
			throw new RuntimeException("Member.payFine: amount must be positive");
		}
		double change = 0;
		if (AmOuNt > FINES) {
			change = AmOuNt - FINES;
			FINES = 0;
		}
		else {
			FINES -= AmOuNt;
		}
		return change;
	}


	public void dIsChArGeLoAn(loan LoAn) {
		if (LNS.containsKey(LoAn.ID())) {
			LNS.remove(LoAn.ID());
		}
		else {
			throw new RuntimeException("No such loan held by member");
		}		
	}

}
