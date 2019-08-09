import java.util.ArrayList;
import java.util.List;

public class BorrowBookControl {

	private BorrowBookUI ui;
	private library library;
	private member member;
	private enum controlState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };
	private controlState state;
	private List<book> pending;
	private List<loan> completed;
	private book book;


	public BorrowBookControl() {
		this.library = library.INSTANCE();
		state = controlState.INITIALISED;
	}


	public void setUI(BorrowBookUI ui) {
		if (!state.equals(controlState.INITIALISED))
			throw new RuntimeException("BorrowBookControl: cannot call setUI except in INITIALISED state");

		this.ui = ui;
		ui.setState(BorrowBookUI.UI_STATE.READY);
		state = controlState.READY;
	}


	public void swiped(int memberId) {
		if (!state.equals(controlState.READY))
			throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");

		member = library.MEMBER(memberId);
		if (member == null) {
			ui.display("Invalid memberId");
			return;
		}
		if (library.MEMBER_CAN_BORROW(member)) {
			pending = new ArrayList<>();
			ui.setState(BorrowBookUI.UI_STATE.SCANNING);
			state = controlState.SCANNING; }
		else
		{
			ui.display("Member cannot borrow at this time");
			ui.setState(BorrowBookUI.UI_STATE.RESTRICTED); }}


	public void scanned(int bookId) {
		book = null;
		if (!state.equals(controlState.SCANNING)) {
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
			for (book B : pending) {
				ui.display(B.toString());
			}
			completed = new ArrayList<loan>();
			ui.setState(BorrowBookUI.UI_STATE.FINALISING);
			state = controlState.FINALISING;
		}
	}


	public void Commit_LOans() {
		if (!state.equals(controlState.FINALISING)) {
			throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING state");
		}
		for (book B : pending) {
			loan LOAN = library.ISSUE_LAON(B, member);
			completed.add(LOAN);
		}
		ui.display("Completed Loan Slip");
		for (loan LOAN : completed) {
			ui.display(LOAN.toString());
		}
		ui.setState(BorrowBookUI.UI_STATE.COMPLETED);
		state = controlState.COMPLETED;
	}


	public void cancel() {
		ui.setState(BorrowBookUI.UI_STATE.CANCELLED);
		state = controlState.CANCELLED;
	}


}
