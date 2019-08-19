import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class loan implements Serializable {
	
	public static enum LoanState { CURRENT, OVER_DUE, DISCHARGED };
	
	private int loanId;
	private Book book;
	private member member;
	private Date date;
	private LoanState loanState;

	
	public loan(int loanId, Book booksLoaned, member member, Date loanDueDate) {
		this.loanId = loanId;
		this.book = booksLoaned;
		this.member = member;
		this.date = loanDueDate;
		this.loanState = LoanState.CURRENT;
	}


	
	public void checkOverDue() {
		if (loanState == LoanState.CURRENT &&
			Calendar.getInstance().getDate().after(date)) {
			this.loanState = LoanState.OVER_DUE;
		}
	}

	
	public boolean isOverDue() {
		return loanState == LoanState.OVER_DUE;
	}

	
	public Integer getLoanId() {
		return loanId;
	}


	public Date getDueDate() {
		return date;
	}
	
	
	public String toString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		StringBuilder concatenate  = new StringBuilder();
		concatenate .append("Loan:  ").append(loanId).append("\n")
		  .append("  Borrower ").append(member.GeT_ID()).append(" : ")
		  .append(member.Get_LastName()).append(", ").append(member.Get_FirstName()).append("\n")
		  .append("  Book ").append(book.getBookId()).append(" : " )
		  .append(book.getTitle()).append("\n")
		  .append("  DueDate: ").append(dateFormat.format(date)).append("\n")
		  .append("  State: ").append(loanState);
		return concatenate .toString();
	}


	public member Member() {
		return member;
	}


	public Book Book() {
		return book;
	}


	public void DiScHaRgE() {
		loanState = LoanState.DISCHARGED;
	}

}
