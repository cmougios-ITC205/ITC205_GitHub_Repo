import java.util.ArrayList;
import java.util.List;

public class BorrowBookControl {

	private BorrowBookUI ui;

	private library library;
	private member member;
	private enum controlState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };
	private controlState State;

	private List<book> PENDING;
	private List<loan> COMPLETED;
	private book BOOK;


	public BorrowBookControl() {
		this.library = library.INSTANCE();
		State = controlState.INITIALISED;
	}


	public void setUI(BorrowBookUI ui) {
		if (!State.equals(controlState.INITIALISED))
			throw new RuntimeException("BorrowBookControl: cannot call setUI except in INITIALISED state");

		this.ui = ui;
		ui.Set_State(BorrowBookUI.UI_STATE.READY);
		State = controlState.READY;
	}


	public void Swiped(int MEMMER_ID) {
		if (!State.equals(controlState.READY))
			throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");

		member = library.MEMBER(MEMMER_ID);
		if (member == null) {
			ui.Display("Invalid memberId");
			return;
		}
		if (library.MEMBER_CAN_BORROW(member)) {
			PENDING = new ArrayList<>();
			ui.Set_State(BorrowBookUI.UI_STATE.SCANNING);
			State = controlState.SCANNING; }
		else
		{
			ui.Display("Member cannot borrow at this time");
			ui.Set_State(BorrowBookUI.UI_STATE.RESTRICTED); }}


	public void Scanned(int bookId) {
		BOOK = null;
		if (!State.equals(controlState.SCANNING)) {
			throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING state");
		}
		BOOK = library.Book(bookId);
		if (BOOK == null) {
			ui.Display("Invalid bookId");
			return;
		}
		if (!BOOK.AVAILABLE()) {
			ui.Display("Book cannot be borrowed");
			return;
		}
		PENDING.add(BOOK);
		for (book B : PENDING) {
			ui.Display(B.toString());
		}
		if (library.Loans_Remaining_For_Member(member) - PENDING.size() == 0) {
			ui.Display("Loan limit reached");
			Complete();
		}
	}


	public void Complete() {
		if (PENDING.size() == 0) {
			cancel();
		}
		else {
			ui.Display("\nFinal Borrowing List");
			for (book B : PENDING) {
				ui.Display(B.toString());
			}
			COMPLETED = new ArrayList<loan>();
			ui.Set_State(BorrowBookUI.UI_STATE.FINALISING);
			State = controlState.FINALISING;
		}
	}


	public void Commit_LOans() {
		if (!State.equals(controlState.FINALISING)) {
			throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING state");
		}
		for (book B : PENDING) {
			loan LOAN = library.ISSUE_LAON(B, member);
			COMPLETED.add(LOAN);
		}
		ui.Display("Completed Loan Slip");
		for (loan LOAN : COMPLETED) {
			ui.Display(LOAN.toString());
		}
		ui.Set_State(BorrowBookUI.UI_STATE.COMPLETED);
		State = controlState.COMPLETED;
	}


	public void cancel() {
		ui.Set_State(BorrowBookUI.UI_STATE.CANCELLED);
		State = controlState.CANCELLED;
	}


}
