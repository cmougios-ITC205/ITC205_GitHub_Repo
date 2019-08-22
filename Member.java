import java.io.Serializable;
import java.util.*;

public class Member implements Serializable {

    private String lastName;
    private String firstName;
    private String email;
    private int phoneNumber;
    private int id;
    private double fines;

    private Map<Integer, Loan> loanCollection;


    public Member(String lastName, String firstName, String email, int phoneNo, int id) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.phoneNumber = phoneNo;
        this.id = id;

        this.loanCollection = new HashMap<>();
    }

    public String toString() {
        StringBuilder memberDetailPrompt = new StringBuilder();

        String finesOwed = String.format("  Fines Owed :  $%.2f", this.fines);

        memberDetailPrompt.append("Member:  ").append(this.id).append("\n")
          .append("  Name:  ").append(this.lastName).append(", ").append(this.firstName).append("\n")
          .append("  Email: ").append(this.email).append("\n")
          .append("  Phone: ").append(this.phoneNumber)
          .append("\n")
          .append(finesOwed)
          .append("\n");
        
        for (Loan loanRecord : loanCollection.values()) {
            memberDetailPrompt.append(loanRecord).append("\n");
        }

        return memberDetailPrompt.toString();
    }

    
    public int getId() {
        return this.id;
    }

    public List<Loan> getLoans() {
        Collection<Loan> loanCollectionValues = this.loanCollection.values();
        return new ArrayList<Loan>(loanCollectionValues);
    }
    
    public int getNumberOfCurrentLoans() {
        return this.loanCollection.size();
    }

    public double getFinesOwed() {
        return this.fines;
    }
    
    public void takeOutLoan(Loan loanRecord) {
        Integer loanId = loanRecord.getLoanId();

        if (!this.loanCollection.containsKey(loanId)) {
            this.loanCollection.put(loanId, loanRecord);
        }
        else {
            throw new RuntimeException("Duplicate loan added to member");
        }
    }

    public String getLastName() {
        return this.lastName;
    }
    
    public String getFirstName() {
        return this.firstName;
    }

    public void addFine(double fine) {
        this.fines += fine;
    }
    
    public double payFine(double amount) {
        if (amount < 0) {
            throw new RuntimeException("Member.payFine: amount must be positive");
        }

        double change = 0;

        if (amount > this.fines) {
            change = amount - this.fines;
            this.fines = 0;
        }
        else {
            this.fines -= amount;
        }

        return change;
    }

    public void dischargeLoan(Loan loanToDischarge) {
        Integer loanId = loanToDischarge.getLoanId();

        if (this.loanCollection.containsKey(loanId)) {
            this.loanCollection.remove(loanId);
        }
        else {
            throw new RuntimeException("No such loan held by member");
        }
    }

}
