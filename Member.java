import java.io.Serializable;
import java.util.*;

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

		String finesOwed = String.format("  Fines Owed :  $%.2f", this.fines);

		sb.append("Member:  ").append(this.id).append("\n")
		  .append("  Name:  ").append(this.lastName).append(", ").append(this.firstName).append("\n")
		  .append("  Email: ").append(this.email).append("\n")
		  .append("  Phone: ").append(this.phoneNumber)
		  .append("\n")
		  .append(finesOwed)
		  .append("\n");
		
		for (loan loanRecord : loanCollection.values()) {
			sb.append(loanRecord).append("\n");
		}

		return sb.toString();
	}

	
	public int getId() {
		return this.id;
	}

	public List<loan> getLoans() {
        Collection<loan> loanCollectionValues = this.loanCollection.values();
        return new ArrayList<loan>(loanCollectionValues);
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
