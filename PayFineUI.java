import java.util.Scanner;


public class PayFineUI {

	public enum UIState { INITIALISED, READY, PAYING, COMPLETED, CANCELLED };

	private PayFineControl payFineControl;
	private Scanner input;
	private UIState currentState;
	
	public PayFineUI(PayFineControl control) {
		this.payFineControl = control;
		this.input = new Scanner(System.in);
		this.currentState = UIState.INITIALISED;

		control.setUI(this);
	}

	public void setState(UIState state) {
	    this.currentState = state;
	}

	public void run() {
		this.output("Pay Fine Use Case UI\n");
		
		while (true) {
			
			switch (this.currentState) {

                case READY:
                    String memberCardValue = input("Swipe member card (press <enter> to cancel): ");

                    if (memberCardValue.length() == 0) {
                        this.payFineControl.cancel();
                        break;
                    }

                    try {
                        int memberId = Integer.valueOf(memberCardValue);
                        this.payFineControl.cardSwiped(memberId);
                    } catch (NumberFormatException e) {
                        this.output("Invalid memberId");
                    }

                    break;
				
                case PAYING:
                    double amount = 0;
                    String amountStr = this.input("Enter amount (<Enter> cancels) : ");

                    if (amountStr.length() == 0) {
                        this.payFineControl.cancel();
                        break;
                    }

                    try {
                        amount = Double.valueOf(amountStr);
                    } catch (NumberFormatException e) {

                    }

                    if (amount <= 0) {
                        this.output("Amount must be positive");
                        break;
                    }

                    this.payFineControl.payFine(amount);

                    break;
                                    
                case CANCELLED:
                    this.output("Pay Fine process cancelled");
                    return;
                
                case COMPLETED:
                    this.output("Pay Fine process complete");
                    return;
                
                default:
                    this.output("Unhandled state");
                    throw new RuntimeException("FixBookUI : unhandled state :" + this.currentState);
			}		
		}
	}
	
	private String input(String prompt) {
		System.out.print(prompt);
		return input.nextLine();
	}	
		
		
	private void output(Object object) {
		System.out.println(object);
	}	
			

	public void DiSplAY(Object object) {
		output(object);
	}


}
