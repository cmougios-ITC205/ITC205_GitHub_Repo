import java.util.ArrayList;
import java.util.List;

public class BorrowBookControl {

    private BorrowBookUI bookUI;
    private library library;
    private member member;
    private enum ControlState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };
    private ControlState state;
    private List<book> pending;
    private List<loan> completed;
    private book book;


    public BorrowBookControl() {

    this.library = library.INSTANCE();
    this.state = ControlState.INITIALISED;

    }


    public void setUI(BorrowBookUI bookUI) {

    if (!this.state.equals(ControlState.INITIALISED)) {

    throw new RuntimeException("BorrowBookControl: cannot call setUI except in INITIALISED state");

    }

    this.bookUI = bookUI;
    this.bookUI.setState(BorrowBookUI.UIState.READY);
    this.state = ControlState.READY;

    }


    public void swiped(int memberId) {

    if (!this.state.equals(ControlState.READY)) {

    throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");

    }

    this.member = library.MEMBER(memberId);

    if (this.member == null) {

    this.bookUI.display("Invalid memberId");
    return;

    }

    if (library.MEMBER_CAN_BORROW(this.member)) {

    pending = new ArrayList<>();
    this.bookUI.setState(BorrowBookUI.UIState.SCANNING);
    this.state = ControlState.SCANNING;

    }
    else {

    this.bookUI.display("Member cannot borrow at this time");
    this.bookUI.setState(BorrowBookUI.UIState.RESTRICTED);

    }
    }


    public void scanned(int bookId) {

    this.book = null;
    if (!state.equals(ControlState.SCANNING)) {

    throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING state");

    }

    this.book = library.Book(bookId);
    if (this.book == null) {

    this.bookUI.display("Invalid bookId");

    return;

    }
    if (!book.AVAILABLE()) {

    this.bookUI.display("Book cannot be borrowed");
    return;

    }
    pending.add(book);
    for (book addBook : pending) {

    this.bookUI.display(addBook.toString());

    }
    if (library.Loans_Remaining_For_Member(member) - pending.size() == 0) {

    this.bookUI.display("Loan limit reached");
    this.complete();

    }
    }


    public void complete() {

    if (pending.size() == 0) {

    this.cancel();

    }
    else {

    this.bookUI.display("\nFinal Borrowing List");

    for (book displayBook : pending) {

    this.bookUI.display(displayBook.toString());

    }
    this.completed = new ArrayList<loan>();
    this.bookUI.setState(BorrowBookUI.UIState.FINALISING);
    this.state = ControlState.FINALISING;
    }
    }


    public void commitLoans() {

    if (!this.state.equals(ControlState.FINALISING)) {

    throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING state");

    }

    for (book loanBook : pending) {

    loan LOAN = this.library.ISSUE_LAON(loanBook, member);
    this.completed.add(LOAN);

    }

    this.bookUI.display("Completed Loan Slip");
    for (loan LOAN : this.completed) {

    this.bookUI.display(LOAN.toString());

    }

    this.bookUI.setState(BorrowBookUI.UIState.COMPLETED);
    this.state = ControlState.COMPLETED;

    }


    public void cancel() {

    this.bookUI.setState(BorrowBookUI.UIState.CANCELLED);
    this.state = ControlState.CANCELLED;

    }


}
