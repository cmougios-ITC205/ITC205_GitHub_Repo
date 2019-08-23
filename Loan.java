import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class Loan implements Serializable {

    public enum LoanState { CURRENT, OVER_DUE, DISCHARGED };

    private int bookLoan;
    private Book bookLoanId;
    private Member loanMember;
    private Date dueDate;
    private LoanState loanState;


    public Loan(int loanId, Book booksLoaned, Member member, Date loanDueDate) {
        this.bookLoan = loanId;
        this.bookLoanId = booksLoaned;
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


    public Integer getBookLoan() {
        return this.bookLoan;
    }


    public Date getDueDate() {
        return this.dueDate;
    }



    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Integer loanMemberId = this.loanMember.getId();
        Integer loanId = this.bookLoan;
        Integer loanMemberBookId = this.bookLoanId.getBookId();
        String getLoanMemberLastName = this.loanMember.getLastName();
        String getLoanMemberFirstName = this.loanMember.getFirstName();
        String getLoanMemberBookTitle = this.bookLoanId.getTitle();
        String loanDueDate = dateFormat.format(this.dueDate);
        LoanState loanMemberState = this.loanState;


        StringBuilder concatenate  = new StringBuilder();
        concatenate .append("Loan:  ").append(loanId).append("\n")
          .append("  Borrower ").append(loanMemberId).append(" : ")
          .append(getLoanMemberLastName).append(", ").append(getLoanMemberFirstName).append("\n")
          .append("  Book ").append(loanMemberBookId).append(" : " )
          .append(getLoanMemberBookTitle).append("\n")
          .append("  DueDate: ").append(loanDueDate).append("\n")
          .append("  State: ").append(loanMemberState);
        return concatenate .toString();
    }


    public Member getLoanMember() {
        return this.loanMember;
    }


    public Book getBook() {
        return this.bookLoanId;
    }


    public void setLoanStateToDischarged() {
        this.loanState = LoanState.DISCHARGED;
    }

}
