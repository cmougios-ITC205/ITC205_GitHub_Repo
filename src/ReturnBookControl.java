public class ReturnBookControl {

	private ReturnBookUI returnBookUI;
	private enum ControlState { INITIALISED, READY, INSPECTING };
	private ControlState state;
	
	private library library;
	private loan currentLoan;
	

	public ReturnBookControl() {
		this.library = library.INSTANCE();
		state = ControlState.INITIALISED;
	}
	
	
	public void setUI(ReturnBookUI returnBookUI) {
		if (!state.equals(ControlState.INITIALISED)) {
			throw new RuntimeException("ReturnBookControl: cannot call setUI except in INITIALISED state");
		}

		this.returnBookUI = returnBookUI;
        returnBookUI.Set_State(ReturnBookUI.UI_STATE.READY);
		state = ControlState.READY;
	}


	public void scanBook(int bookId) {
		if (!state.equals(ControlState.READY)) {
			throw new RuntimeException("ReturnBookControl: cannot call bookScanned except in READY state");
		}

		book currentBook = library.Book(bookId);
		
		if (currentBook == null) {
			returnBookUI.display("Invalid Book Id");
			return;
		}

		if (!currentBook.On_loan()) {
			returnBookUI.display("Book has not been borrowed");
			return;
		}

		currentLoan = library.LOAN_BY_BOOK_ID(bookId);

		double overdueFine = 0.0;

		if (currentLoan.OVer_Due()) {
			overdueFine = library.CalculateOverDueFine(currentLoan);
		}

		returnBookUI.display("Inspecting");
		returnBookUI.display(currentBook.toString());
		returnBookUI.display(currentLoan.toString());
		
		if (currentLoan.OVer_Due()) {
			returnBookUI.display(String.format("\nOverdue fine : $%.2f", overdueFine));
		}

		returnBookUI.Set_State(ReturnBookUI.UI_STATE.INSPECTING);
		this.state = ControlState.INSPECTING;
	}


	public void completeScanning() {
		if (!this.state.equals(ControlState.READY)) {
			throw new RuntimeException("ReturnBookControl: cannot call scanningComplete except in READY state");
		}

		returnBookUI.Set_State(ReturnBookUI.UI_STATE.COMPLETED);
	}


	public void dischargeLoan(boolean isDamaged) {
		if (!state.equals(ControlState.INSPECTING)) {
			throw new RuntimeException("ReturnBookControl: cannot call dischargeLoan except in INSPECTING state");
		}

		this.library.Discharge_loan(currentLoan, isDamaged);
		this.currentLoan = null;
		this.returnBookUI.Set_State(ReturnBookUI.UI_STATE.READY);
		this.state = ControlState.READY;
	}
}
