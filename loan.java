import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class loan implements Serializable {
	
	public static enum LOAN_STATE { CURRENT, OVER_DUE, DISCHARGED };
	
	private int ID;
	private Book B;
	private Member M;
	private Date D;
	private LOAN_STATE state;

	
	public loan(int loanId, Book book, Member member, Date dueDate) {
		this.ID = loanId;
		this.B = book;
		this.M = member;
		this.D = dueDate;
		this.state = LOAN_STATE.CURRENT;
	}

	
	public void checkOverDue() {
		if (state == LOAN_STATE.CURRENT &&
			Calendar.getInstance().getDate().after(D)) {
			this.state = LOAN_STATE.OVER_DUE;			
		}
	}

	
	public boolean OVer_Due() {
		return state == LOAN_STATE.OVER_DUE;
	}

	
	public Integer ID() {
		return ID;
	}


	public Date Get_Due_Date() {
		return D;
	}
	
	
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		StringBuilder sb = new StringBuilder();
		sb.append("Loan:  ").append(ID).append("\n")
		  .append("  Borrower ").append(M.getId()).append(" : ")
		  .append(M.Get_LastName()).append(", ").append(M.Get_FirstName()).append("\n")
		  .append("  Book ").append(B.getBookId()).append(" : " )
		  .append(B.getTitle()).append("\n")
		  .append("  DueDate: ").append(sdf.format(D)).append("\n")
		  .append("  State: ").append(state);		
		return sb.toString();
	}


	public Member Member() {
		return M;
	}


	public Book Book() {
		return B;
	}


	public void DiScHaRgE() {
		state = LOAN_STATE.DISCHARGED;		
	}

}
