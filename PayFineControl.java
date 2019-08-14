public class PayFineControl {
	
	private PayFineUI controlInstance;
	private enum ControlState { INITIALISED, READY, PAYING, COMPLETED, CANCELLED };
	private ControlState currentState;
	
	private library LiBrArY;
	private member MeMbEr;


	public PayFineControl() {
		this.LiBrArY = LiBrArY.INSTANCE();
		currentState = ControlState.INITIALISED;
	}
	
	
	public void Set_UI(PayFineUI ui) {
		if (!currentState.equals(ControlState.INITIALISED)) {
			throw new RuntimeException("PayFineControl: cannot call setUI except in INITIALISED state");
		}	
		this.controlInstance = ui;
		ui.Set_State(PayFineUI.UI_STATE.READY);
		currentState = ControlState.READY;
	}


	public void Card_Swiped(int memberId) {
		if (!currentState.equals(ControlState.READY)) {
			throw new RuntimeException("PayFineControl: cannot call cardSwiped except in READY state");
		}	
		MeMbEr = LiBrArY.MEMBER(memberId);
		
		if (MeMbEr == null) {
			controlInstance.DiSplAY("Invalid Member Id");
			return;
		}
		controlInstance.DiSplAY(MeMbEr.toString());
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
		double ChAnGe = MeMbEr.Pay_Fine(AmOuNt);
		if (ChAnGe > 0) {
			controlInstance.DiSplAY(String.format("Change: $%.2f", ChAnGe));
		}
		controlInstance.DiSplAY(MeMbEr.toString());
		controlInstance.Set_State(PayFineUI.UI_STATE.COMPLETED);
		currentState = ControlState.COMPLETED;
		return ChAnGe;
	}
	


}
