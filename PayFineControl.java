public class PayFineControl {
	
	private PayFineUI controlInstance;
	private enum ControlState { INITIALISED, READY, PAYING, COMPLETED, CANCELLED };
	private ControlState StAtE;
	
	private library LiBrArY;
	private member MeMbEr;


	public PayFineControl() {
		this.LiBrArY = LiBrArY.INSTANCE();
		StAtE = ControlState.INITIALISED;
	}
	
	
	public void Set_UI(PayFineUI ui) {
		if (!StAtE.equals(ControlState.INITIALISED)) {
			throw new RuntimeException("PayFineControl: cannot call setUI except in INITIALISED state");
		}	
		this.controlInstance = ui;
		ui.Set_State(PayFineUI.UI_STATE.READY);
		StAtE = ControlState.READY;
	}


	public void Card_Swiped(int memberId) {
		if (!StAtE.equals(ControlState.READY)) {
			throw new RuntimeException("PayFineControl: cannot call cardSwiped except in READY state");
		}	
		MeMbEr = LiBrArY.MEMBER(memberId);
		
		if (MeMbEr == null) {
			controlInstance.DiSplAY("Invalid Member Id");
			return;
		}
		controlInstance.DiSplAY(MeMbEr.toString());
		controlInstance.Set_State(PayFineUI.UI_STATE.PAYING);
		StAtE = ControlState.PAYING;
	}
	
	
	public void CaNcEl() {
		controlInstance.Set_State(PayFineUI.UI_STATE.CANCELLED);
		StAtE = ControlState.CANCELLED;
	}


	public double PaY_FiNe(double AmOuNt) {
		if (!StAtE.equals(ControlState.PAYING)) {
			throw new RuntimeException("PayFineControl: cannot call payFine except in PAYING state");
		}	
		double ChAnGe = MeMbEr.Pay_Fine(AmOuNt);
		if (ChAnGe > 0) {
			controlInstance.DiSplAY(String.format("Change: $%.2f", ChAnGe));
		}
		controlInstance.DiSplAY(MeMbEr.toString());
		controlInstance.Set_State(PayFineUI.UI_STATE.COMPLETED);
		StAtE = ControlState.COMPLETED;
		return ChAnGe;
	}
	


}
