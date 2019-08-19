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
	private LoanState state;

	
	public loan(int loanId, Book booksLoaned, member member, Date loanDueDate) {
		this.loanId = loanId;
		this.book = booksLoaned;
		this.member = member;
		this.date = loanDueDate;
		this.state = LoanState.CURRENT;
	}


	
	public void checkOverDueLoan() {
		if (this.state == LoanState.CURRENT &&
			Calendar.getInstance().getDate().after(this.date)) {
			this.state = LoanState.OVER_DUE;
		}
	}

	
	public boolean isOverDue() {
		return this.state == LoanState.OVER_DUE;
	}

	
	public Integer getLoanId() {
		return this.loanId;
	}


	public Date getDueDate() {
		return this.date;
	}
	
	
	public String toString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		StringBuilder concatenate  = new StringBuilder();
		concatenate .append("Loan:  ").append(this.loanId).append("\n")
		  .append("  Borrower ").append(this.member.GeT_ID()).append(" : ")
		  .append(this.member.Get_LastName()).append(", ").append(this.member.Get_FirstName()).append("\n")
		  .append("  Book ").append(this.book.getBookId()).append(" : " )
		  .append(this.book.getTitle()).append("\n")
		  .append("  DueDate: ").append(dateFormat.format(this.date)).append("\n")
		  .append("  State: ").append(this.state);
		return concatenate .toString();
	}


	public member getMember() {
		return this.member;
	}


	public Book getBook() {
		return this.book;
	}


	public void setLoanStateToDischarged() {
		this.state = LoanState.DISCHARGED;
	}

}
