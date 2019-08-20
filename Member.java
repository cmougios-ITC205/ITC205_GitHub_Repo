import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Member implements Serializable {

	private String lastName;
	private String firstName;
	private String email;
	private int phoneNumber;
	private int id;
	private double fines;
	
	private Map<Integer, loan> loanCollection;

	
	public Member(String lastName, String firstName, String email, int phoneNo, int id) {
		this.lastName = lastName;
		this.firstName = firstName;
		this.email = email;
		this.phoneNumber = phoneNo;
		this.id = id;
		
		this.loanCollection = new HashMap<>();
	}

	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Member:  ").append(id).append("\n")
		  .append("  Name:  ").append(lastName).append(", ").append(firstName).append("\n")
		  .append("  Email: ").append(email).append("\n")
		  .append("  Phone: ").append(phoneNumber)
		  .append("\n")
		  .append(String.format("  Fines Owed :  $%.2f", fines))
		  .append("\n");
		
		for (loan LoAn : loanCollection.values()) {
			sb.append(LoAn).append("\n");
		}		  
		return sb.toString();
	}

	
	public int GeT_ID() {
		return id;
	}

	
	public List<loan> GeT_LoAnS() {
		return new ArrayList<loan>(loanCollection.values());
	}

	
	public int Number_Of_Current_Loans() {
		return loanCollection.size();
	}

	
	public double Fines_OwEd() {
		return fines;
	}

	
	public void Take_Out_Loan(loan loan) {
		if (!loanCollection.containsKey(loan.ID())) {
			loanCollection.put(loan.ID(), loan);
		}
		else {
			throw new RuntimeException("Duplicate loan added to member");
		}		
	}

	
	public String Get_LastName() {
		return lastName;
	}

	
	public String Get_FirstName() {
		return firstName;
	}


	public void Add_Fine(double fine) {
		fines += fine;
	}
	
	public double Pay_Fine(double AmOuNt) {
		if (AmOuNt < 0) {
			throw new RuntimeException("Member.payFine: amount must be positive");
		}
		double change = 0;
		if (AmOuNt > fines) {
			change = AmOuNt - fines;
			fines = 0;
		}
		else {
			fines -= AmOuNt;
		}
		return change;
	}


	public void dIsChArGeLoAn(loan LoAn) {
		if (loanCollection.containsKey(LoAn.ID())) {
			loanCollection.remove(LoAn.ID());
		}
		else {
			throw new RuntimeException("No such loan held by member");
		}		
	}

}
