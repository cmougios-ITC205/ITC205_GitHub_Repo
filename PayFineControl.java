public class PayFineControl {
	
	private PayFineUI controlInstance;
	private enum ControlState { INITIALISED, READY, PAYING, COMPLETED, CANCELLED };
	private ControlState currentState;
	
	private library controlLibrary;
	private member controlMember;

	public PayFineControl() {
		this.controlLibrary = this.controlLibrary.INSTANCE();
		this.currentState = ControlState.INITIALISED;
	}
	
	public void setUI(PayFineUI payFineUI) {
		if (!this.currentState.equals(ControlState.INITIALISED)) {
			throw new RuntimeException("PayFineControl: cannot call setUI except in INITIALISED state");
		}

		this.controlInstance = payFineUI;
        payFineUI.Set_State(PayFineUI.UI_STATE.READY);
		this.currentState = ControlState.READY;
	}

	public void Card_Swiped(int memberId) {
		if (!currentState.equals(ControlState.READY)) {
			throw new RuntimeException("PayFineControl: cannot call cardSwiped except in READY state");
		}	
		controlMember = controlLibrary.MEMBER(memberId);
		
		if (controlMember == null) {
			controlInstance.DiSplAY("Invalid Member Id");
			return;
		}
		controlInstance.DiSplAY(controlMember.toString());
		controlInstance.Set_State(PayFineUI.UI_STATE.PAYING);
		currentState = ControlState.PAYING;
	}
	
	
	public void CaNcEl() {
		controlInstance.Set_State(PayFineUI.UI_STATE.CANCELLED);
		currentState = ControlState.CANCELLED;
	}


	public double PaY_FiNe(double AmOuNt) {
		if (!currentState.equals(ControlState.PAYING)) {
			throw new RuntimeException("PayFineControl: cannot call payFine except in PAYING state");
		}	
		double ChAnGe = controlMember.Pay_Fine(AmOuNt);
		if (ChAnGe > 0) {
			controlInstance.DiSplAY(String.format("Change: $%.2f", ChAnGe));
		}
		controlInstance.DiSplAY(controlMember.toString());
		controlInstance.Set_State(PayFineUI.UI_STATE.COMPLETED);
		currentState = ControlState.COMPLETED;
		return ChAnGe;
	}
	


}
