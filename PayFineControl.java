public class PayFineControl {
	
	private PayFineUI controlInstance;
	private enum ControlState { INITIALISED, READY, PAYING, COMPLETED, CANCELLED };
	private ControlState currentState;
	
	private Library controlLibrary;
	private member controlMember;

	public PayFineControl() {
		this.controlLibrary = this.controlLibrary.getInstance();
		this.currentState = ControlState.INITIALISED;
	}
	
	public void setUI(PayFineUI payFineUI) {
		if (!this.currentState.equals(ControlState.INITIALISED)) {
			throw new RuntimeException("PayFineControl: cannot call setUI except in INITIALISED state");
		}

		this.controlInstance = payFineUI;
        payFineUI.setState(PayFineUI.UIState.READY);
		this.currentState = ControlState.READY;
	}

	public void cardSwiped(int memberId) {
		if (!this.currentState.equals(ControlState.READY)) {
			throw new RuntimeException("PayFineControl: cannot call cardSwiped except in READY state");
		}

		this.controlMember = this.controlLibrary.MEMBER(memberId);
		
		if (this.controlMember == null) {
			this.controlInstance.display("Invalid Member Id");
			return;
		}

		String controlMemberStr = controlMember.toString();

		this.controlInstance.display(controlMemberStr);
		this.controlInstance.setState(PayFineUI.UIState.PAYING);
		this.currentState = ControlState.PAYING;
	}
	
	public void cancel() {
		this.controlInstance.setState(PayFineUI.UIState.CANCELLED);
		this.currentState = ControlState.CANCELLED;
	}

	public double payFine(double amount) {
		if (!this.currentState.equals(ControlState.PAYING)) {
			throw new RuntimeException("PayFineControl: cannot call payFine except in PAYING state");
		}

		double change = this.controlMember.Pay_Fine(amount);

		if (change > 0) {
		    String changeFormatted = String.format("Change: $%.2f", change);
			this.controlInstance.display(changeFormatted);
		}

		String controlMemberStr = controlMember.toString();

		this.controlInstance.display(controlMemberStr);
		this.controlInstance.setState(PayFineUI.UIState.COMPLETED);
		this.currentState = ControlState.COMPLETED;

		return change;
	}

}
