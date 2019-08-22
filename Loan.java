import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class Loan implements Serializable {

    public enum LoanState { CURRENT, OVER_DUE, DISCHARGED };

    private int loanId;
    private Book bookId;
    private Member loanMember;
    private Date dueDate;
   private LoanState loanState;


    public Loan(int loanId, Book booksLoaned, Member member, Date loanDueDate) {
        this.loanId = loanId;
        this.bookId = booksLoaned;
        this.loanMember = member;
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
          .append("  Borrower ").append(this.loanMember.getId()).append(" : ")
          .append(this.loanMember.getLastName()).append(", ").append(this.loanMember.getFirstName()).append("\n")
          .append("  Book ").append(this.bookId.getBookId()).append(" : " )
          .append(this.bookId.getTitle()).append("\n")
          .append("  DueDate: ").append(dateFormat.format(this.dueDate)).append("\n")
          .append("  State: ").append(this.loanState);
        return concatenate .toString();
    }


    public Member getLoanMember() {
        return this.loanMember;
    }


    public Book getBook() {
        return this.bookId;
    }


    public void setLoanStateToDischarged() {
        this.loanState = LoanState.DISCHARGED;
    }

}
