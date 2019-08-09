import java.util.ArrayList;
import java.util.List;

public class BorrowBookControl {

	private BorrowBookUI ui;

	private library LIBRARY;
	private member M;
	private enum CONTROL_STATE { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };
	private CONTROL_STATE State;

	private List<book> PENDING;
	private List<loan> COMPLETED;
	private book BOOK;


	public BorrowBookControl() {
		this.LIBRARY = LIBRARY.INSTANCE();
		State = CONTROL_STATE.INITIALISED;
	}


	public void setUI(BorrowBookUI ui) {
		if (!State.equals(CONTROL_STATE.INITIALISED))
			throw new RuntimeException("BorrowBookControl: cannot call setUI except in INITIALISED state");

		this.ui = ui;
		ui.Set_State(BorrowBookUI.UI_STATE.READY);
		State = CONTROL_STATE.READY;
	}


	public void Swiped(int MEMMER_ID) {
		if (!State.equals(CONTROL_STATE.READY))
			throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");

		M = LIBRARY.MEMBER(MEMMER_ID);
		if (M == null) {
			ui.Display("Invalid memberId");
			return;
		}
		if (LIBRARY.MEMBER_CAN_BORROW(M)) {
			PENDING = new ArrayList<>();
			ui.Set_State(BorrowBookUI.UI_STATE.SCANNING);
			State = CONTROL_STATE.SCANNING; }
		else
		{
			ui.Display("Member cannot borrow at this time");
			ui.Set_State(BorrowBookUI.UI_STATE.RESTRICTED); }}


	public void Scanned(int bookId) {
		BOOK = null;
		if (!State.equals(CONTROL_STATE.SCANNING)) {
			throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING state");
		}
		BOOK = LIBRARY.Book(bookId);
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
		if (LIBRARY.Loans_Remaining_For_Member(M) - PENDING.size() == 0) {
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
			State = CONTROL_STATE.FINALISING;
		}
	}


	public void Commit_LOans() {
		if (!State.equals(CONTROL_STATE.FINALISING)) {
			throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING state");
		}
		for (book B : PENDING) {
			loan LOAN = LIBRARY.ISSUE_LAON(B, M);
			COMPLETED.add(LOAN);
		}
		ui.Display("Completed Loan Slip");
		for (loan LOAN : COMPLETED) {
			ui.Display(LOAN.toString());
		}
		ui.Set_State(BorrowBookUI.UI_STATE.COMPLETED);
		State = CONTROL_STATE.COMPLETED;
	}


	public void cancel() {
		ui.Set_State(BorrowBookUI.UI_STATE.CANCELLED);
		State = CONTROL_STATE.CANCELLED;
	}


}
