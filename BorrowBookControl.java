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
		if (!state.equals(ControlState.INITIALISED))
			throw new RuntimeException("BorrowBookControl: cannot call setUI except in INITIALISED state");

		this.bookUI = bookUI;
		bookUI.setState(BorrowBookUI.UIState.READY);
		state = ControlState.READY;
	}


	public void swiped(int memberId) {
		if (!state.equals(ControlState.READY))
			throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");

		member = library.MEMBER(memberId);
		if (member == null) {
			bookUI.display("Invalid memberId");
			return;
		}
		if (library.MEMBER_CAN_BORROW(member)) {
			pending = new ArrayList<>();
			bookUI.setState(BorrowBookUI.UIState.SCANNING);
			state = ControlState.SCANNING; }
		else {
			bookUI.display("Member cannot borrow at this time");
			bookUI.setState(BorrowBookUI.UIState.RESTRICTED);
		}
	}


	public void scanned(int bookId) {
		book = null;
		if (!state.equals(ControlState.SCANNING)) {
			throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING state");
		}
		book = library.Book(bookId);
		if (book == null) {
			bookUI.display("Invalid bookId");
			return;
		}
		if (!book.AVAILABLE()) {
			bookUI.display("Book cannot be borrowed");
			return;
		}
		pending.add(book);
		for (book addBook : pending) {
			bookUI.display(addBook.toString());
		}
		if (library.Loans_Remaining_For_Member(member) - pending.size() == 0) {
			bookUI.display("Loan limit reached");
			complete();
		}
	}


	public void complete() {
		if (pending.size() == 0) {
			cancel();
		}
		else {
			bookUI.display("\nFinal Borrowing List");
			for (book displayBook : pending) {
				bookUI.display(displayBook.toString());
			}
			completed = new ArrayList<loan>();
			bookUI.setState(BorrowBookUI.UIState.FINALISING);
			state = ControlState.FINALISING;
			 }
	}


	public void commitLoans() {
		if (!state.equals(ControlState.FINALISING)) {
			throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING state");
		}
		for (book loanBook : pending) {
			loan LOAN = library.ISSUE_LAON(loanBook, member);
			completed.add(LOAN);
		}
		bookUI.display("Completed Loan Slip");
		for (loan LOAN : completed) {
			bookUI.display(LOAN.toString());
		}
		bookUI.setState(BorrowBookUI.UIState.COMPLETED);
		state = ControlState.COMPLETED;
	}


	public void cancel() {
		bookUI.setState(BorrowBookUI.UIState.CANCELLED);
		state = ControlState.CANCELLED;
	}


}
