public class ReturnBookControl {

	private ReturnBookUI returnBookUI;
	private enum ControlState { INITIALISED, READY, INSPECTING };
	private ControlState state;
	
	private Library library;
	private loan currentLoan;
	

	public ReturnBookControl() {
		this.library = library.getInstance();
		this.state = ControlState.INITIALISED;
	}
	
	
	public void setUI(ReturnBookUI returnBookUI) {
		if (!state.equals(ControlState.INITIALISED)) {
			throw new RuntimeException("ReturnBookControl: cannot call setUI except in INITIALISED state");
		}

		this.returnBookUI = returnBookUI;

		returnBookUI.setState(ReturnBookUI.UIState.READY);

		this.state = ControlState.READY;
	}


	public void scanBook(int bookId) {
		if (!this.state.equals(ControlState.READY)) {
			throw new RuntimeException("ReturnBookControl: cannot call bookScanned except in READY state");
		}

		Book currentBook = library.Book(bookId);
		
		if (currentBook == null) {
			this.returnBookUI.display("Invalid Book Id");
			return;
		}

		if (!currentBook.isOnLoan()) {
			this.returnBookUI.display("Book has not been borrowed");
			return;
		}

		this.currentLoan = library.LOAN_BY_BOOK_ID(bookId);

		double overdueFine = 0.0;

		if (currentLoan.OVer_Due()) {
			overdueFine = library.CalculateOverDueFine(currentLoan);
		}

		String currentBookStr = currentBook.toString();
		String currentLoanStr = this.currentLoan.toString();

		this.returnBookUI.display("Inspecting");
		this.returnBookUI.display(currentBookStr);
		this.returnBookUI.display(currentLoanStr);
		
		if (this.currentLoan.OVer_Due()) {
		    String fineValue = String.format("\nOverdue fine : $%.2f", overdueFine);
			this.returnBookUI.display(fineValue);
		}

		this.returnBookUI.setState(ReturnBookUI.UIState.INSPECTING);
		this.state = ControlState.INSPECTING;
	}


	public void completeScanning() {
		if (!this.state.equals(ControlState.READY)) {
			throw new RuntimeException("ReturnBookControl: cannot call scanningComplete except in READY state");
		}

		this.returnBookUI.setState(ReturnBookUI.UIState.COMPLETED);
	}


	public void dischargeLoan(boolean isDamaged) {
		if (!this.state.equals(ControlState.INSPECTING)) {
			throw new RuntimeException("ReturnBookControl: cannot call dischargeLoan except in INSPECTING state");
		}

		this.library.Discharge_loan(currentLoan, isDamaged);
		this.currentLoan = null;
		this.returnBookUI.setState(ReturnBookUI.UIState.READY);
		this.state = ControlState.READY;
	}
}
