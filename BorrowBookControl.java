import java.util.ArrayList;
import java.util.List;

public class BorrowBookControl {

    private BorrowBookUI UI;

    private library library;
    private member member;
    private enum ControlState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };
    private ControlState state;

    private List<book> booksPendingBorrow;
    private List<loan> booksLoaned;
    private book book;


    public BorrowBookControl() {
        this.library = library.INSTANCE();
        this.state = ControlState.INITIALISED;
    }


    public void setUI(BorrowBookUI UI) {
        if (!this.state.equals(ControlState.INITIALISED))
            throw new RuntimeException("BorrowBookControl: cannot call setUI except in INITIALISED state");

        this.UI = UI;
        UI.setState(BorrowBookUI.UIState.READY);
        this.state = ControlState.READY;
    }


    public void swipeMemberCard(int memberId) {
        if (!this.state.equals(ControlState.READY))
            throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");

        this.member = library.MEMBER(memberId);
        if (this.member == null) {
            this.UI.display("Invalid memberId");
            return;
        }
        if (this.library.MEMBER_CAN_BORROW(this.member)) {
            this.booksPendingBorrow = new ArrayList<>();
            this.UI.setState(BorrowBookUI.UIState.SCANNING);
            this.state = ControlState.SCANNING;
        }
        else {
            this.UI.display("Member cannot borrow at this time");
            this.UI.setState(BorrowBookUI.UIState.RESTRICTED);
        }
    }


    public void scanBook(int bookId) {
        this.book = null;
        if (!this.state.equals(ControlState.SCANNING)) {
            throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING state");
        }
        this.book = this.library.Book(bookId);
        if (this.book == null) {
            this.UI.display("Invalid bookId");
            return;
        }
        if (!this.book.AVAILABLE()) {
            this.UI.display("Book cannot be borrowed");
            return;
        }
        this.booksPendingBorrow.add(this.book);
        for (book borrowBookList : this.booksPendingBorrow) {
            this.UI.display(borrowBookList.toString());
        }
        if (this.library.Loans_Remaining_For_Member(this.member) - this.booksPendingBorrow.size() == 0) {
            this.UI.display("Loan limit reached");
            this.completeBorrow();
        }
    }


    public void completeBorrow() {
        if (this.booksPendingBorrow.size() == 0) {
            this.cancel();
        }
        else {
            this.UI.display("\nFinal Borrowing List");
            for (book borrowBookList : this.booksPendingBorrow) {
                this.UI.display(borrowBookList.toString());
            }
            this.booksLoaned = new ArrayList<loan>();
            this.UI.setState(BorrowBookUI.UIState.FINALISING);
            this.state = ControlState.FINALISING;
        }
    }


    public void commitLoans() {
        if (!this.state.equals(ControlState.FINALISING)) {
            throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING state");
        }
        for (book borrowBookList : this.booksPendingBorrow) {
            loan LOAN = this.library.ISSUE_LAON(borrowBookList, this.member);
            this.booksLoaned.add(LOAN);
        }
        this.UI.display("Completed Loan Slip");
        for (loan loan : this.booksLoaned) {
            this.UI.display(loan.toString());
        }
        this.UI.setState(BorrowBookUI.UIState.COMPLETED);
        this.state = ControlState.COMPLETED;
    }


    public void cancel() {
        this.UI.setState(BorrowBookUI.UIState.CANCELLED);
        this.state = ControlState.CANCELLED;
    }


}
