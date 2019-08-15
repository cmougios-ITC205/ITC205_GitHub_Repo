import java.util.ArrayList;
import java.util.List;

public class BorrowBookControl {
	
	private BorrowBookUI UI;
	
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
	

	public void setUI(BorrowBookUI UI) {
		if (!this.state.equals(ControlState.INITIALISED))
			throw new RuntimeException("BorrowBookControl: cannot call setUI except in INITIALISED state");
			
		this.UI = UI;
		UI.setState(BorrowBookUI.UI_STATE.READY);
		this.state = ControlState.READY;
	}

		
	public void swiped(int memberId) {
		if (!this.state.equals(ControlState.READY))
			throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");
			
		this.member = library.MEMBER(memberId);
		if (this.member == null) {
			this.UI.display("Invalid memberId");
			return;
		}
		if (this.library.MEMBER_CAN_BORROW(this.member)) {
			this.pending = new ArrayList<>();
			this.UI.setState(BorrowBookUI.UI_STATE.SCANNING);
			this.state = ControlState.SCANNING; }
		else 
		{
			this.UI.display("Member cannot borrow at this time");
			this.UI.setState(BorrowBookUI.UI_STATE.RESTRICTED); }}
	
	
	public void scanned(int bookId) {
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
		this.pending.add(this.book);
		for (book B : this.pending) {
			this.UI.display(B.toString());
		}
		if (this.library.Loans_Remaining_For_Member(this.member) - this.pending.size() == 0) {
			this.UI.display("Loan limit reached");
			complete();
		}
	}
	
	
	public void complete() {
		if (this.pending.size() == 0) {
			this.cancel();
		}
		else {
			this.UI.display("\nFinal Borrowing List");
			for (book B : this.pending) {
				this.UI.display(B.toString());
			}
			this.completed = new ArrayList<loan>();
			this.UI.setState(BorrowBookUI.UI_STATE.FINALISING);
			this.state = ControlState.FINALISING;
		}
	}


	public void commitLoans() {
		if (!this.state.equals(ControlState.FINALISING)) {
			throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING state");
		}	
		for (book B : this.pending) {
			loan LOAN = this.library.ISSUE_LAON(B, this.member);
			completed.add(LOAN);
		}
		this.UI.display("Completed Loan Slip");
		for (loan LOAN : this.completed) {
			this.UI.display(LOAN.toString());
		}
		this.UI.setState(BorrowBookUI.UI_STATE.COMPLETED);
		this.state = ControlState.COMPLETED;
	}

	
	public void cancel() {
		this.UI.setState(BorrowBookUI.UI_STATE.CANCELLED);
		this.state = ControlState.CANCELLED;
	}
	
	
}
