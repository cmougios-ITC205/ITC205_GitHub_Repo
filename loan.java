import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class loan implements Serializable {

    public static enum LoanState { CURRENT, OVER_DUE, DISCHARGED };

    private int loanId;
    private Book bookId;
    private member memberId;
    private Date dueDate;
    private LoanState loanState;


    public loan(int loanId, Book booksLoaned, member member, Date loanDueDate) {
        this.loanId = loanId;
        this.bookId = booksLoaned;
        this.memberId = member;
        this.dueDate = loanDueDate;
        this.loanState = LoanState.CURRENT;
    }



    public void checkOverDueLoan() {
        if (this.loanState == LoanState.CURRENT &&
            Calendar.getInstance().getDate().after(this.dueDate)) {
            this.loanState = LoanState.OVER_DUE;
        }
    }


    public boolean isOverDue() {
        return this.loanState == LoanState.OVER_DUE;
    }


    public Integer getLoanId() {
        return this.loanId;
    }


    public Date getDueDate() {
        return this.dueDate;
    }


    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        StringBuilder concatenate  = new StringBuilder();
        concatenate .append("Loan:  ").append(this.loanId).append("\n")
          .append("  Borrower ").append(this.memberId.GeT_ID()).append(" : ")
          .append(this.memberId.Get_LastName()).append(", ").append(this.memberId.Get_FirstName()).append("\n")
          .append("  Book ").append(this.bookId.getBookId()).append(" : " )
          .append(this.bookId.getTitle()).append("\n")
          .append("  DueDate: ").append(dateFormat.format(this.dueDate)).append("\n")
          .append("  State: ").append(this.loanState);
        return concatenate .toString();
    }


    public member getMemberId() {
        return this.memberId;
    }


    public Book getBook() {
        return this.bookId;
    }


    public void setLoanStateToDischarged() {
        this.loanState = LoanState.DISCHARGED;
    }

}
