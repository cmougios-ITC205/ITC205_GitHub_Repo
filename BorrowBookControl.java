import java.util.ArrayList;
import java.util.List;

public class BorrowBookControl {

	private BorrowBookUI ui;
	private library library;
	private member member;
	private enum ControlState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };
	private ControlState state;
	private List<book> pending;
	private List<loan> completed;
	private book book;


	public BorrowBookControl() {
		this.library = library.INSTANCE();
		state = ControlState.INITIALISED;
	}


	public void setUI(BorrowBookUI ui) {
		if (!state.equals(ControlState.INITIALISED))
			throw new RuntimeException("BorrowBookControl: cannot call setUI except in INITIALISED state");

		this.ui = ui;
		ui.setState(BorrowBookUI.UIState.READY);
		state = ControlState.READY;
	}


	public void swiped(int memberId) {
		if (!state.equals(ControlState.READY))
			throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");

		member = library.MEMBER(memberId);
		if (member == null) {
			ui.display("Invalid memberId");
			return;
		}
		if (library.MEMBER_CAN_BORROW(member)) {
			pending = new ArrayList<>();
			ui.setState(BorrowBookUI.UIState.SCANNING);
			state = ControlState.SCANNING; }
		else {
			ui.display("Member cannot borrow at this time");
			ui.setState(BorrowBookUI.UIState.RESTRICTED);
		}
	}


	public void scanned(int bookId) {
		book = null;
		if (!state.equals(ControlState.SCANNING)) {
			throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING state");
		}
		book = library.Book(bookId);
		if (book == null) {
			ui.display("Invalid bookId");
			return;
		}
		if (!book.AVAILABLE()) {
			ui.display("Book cannot be borrowed");
			return;
		}
		pending.add(book);
		for (book addBook : pending) {
			ui.display(addBook.toString());
		}
		if (library.Loans_Remaining_For_Member(member) - pending.size() == 0) {
			ui.display("Loan limit reached");
			complete();
		}
	}


	public void complete() {
		if (pending.size() == 0) {
			cancel();
		}
		else {
			ui.display("\nFinal Borrowing List");
			for (book displayBook : pending) {
				ui.display(displayBook.toString());
			}
			completed = new ArrayList<loan>();
			ui.setState(BorrowBookUI.UIState.FINALISING);
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
		ui.display("Completed Loan Slip");
		for (loan LOAN : completed) {
			ui.display(LOAN.toString());
		}
		ui.setState(BorrowBookUI.UIState.COMPLETED);
		state = ControlState.COMPLETED;
	}


	public void cancel() {
		ui.setState(BorrowBookUI.UIState.CANCELLED);
		state = ControlState.CANCELLED;
	}


}
