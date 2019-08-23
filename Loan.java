import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class Loan implements Serializable {

    public enum LoanState { CURRENT, OVER_DUE, DISCHARGED };

    private int bookLoan;
    private Book bookId;
    private Member loanMember;
    private Date dueDate;
   private LoanState loanState;


    public Loan(int loanId, Book booksLoaned, Member member, Date loanDueDate) {
        this.bookLoan = loanId;
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


    public Integer getBookLoan() {
        return this.bookLoan;
    }


    public Date getDueDate() {
        return this.dueDate;
    }

    public String getLoanMemberFirstName(){

        return this.loanMember.getFirstName();
    }
    public String getLoanMemberLastName(){

        return this.loanMember.getFirstName();
    }

    public int getLoanMembersBookId(){

        return this.bookId.getBookId();
    }
    public String getLoanMemberBookTitle(){

        return this.bookId.getTitle();
    }

    public int getLoanMemberId(){

        return this.loanMember.getId();
    }

    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        StringBuilder concatenate  = new StringBuilder();
          /*concatenate .append("Loan:  ").append(this.loanId).append("\n")*/
           concatenate .append("Loan:  ").append(this.getLoanMemberId()).append("\n")
          .append("  Borrower ").append(this.loanMember.getId()).append(" : ")
        /*.append(this.loanMember.getLastName()).append(", ").append(this.loanMember.getFirstName()).append("\n")*/
          .append(this.getLoanMemberFirstName()).append(", ").append(this.getLoanMemberLastName()).append("\n")
          /*.append("  Book ").append(this.bookId.getBookId()).append(" : " )
          .append(this.bookId.getTitle()).append("\n")*/
          .append("  Book ").append(this.getLoanMembersBookId()).append(" : " )
          .append(this.getLoanMemberBookTitle()).append("\n")
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
