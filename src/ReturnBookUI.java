import java.util.Scanner;


public class ReturnBookUI {

	public enum UIState { INITIALISED, READY, INSPECTING, COMPLETED };

	private ReturnBookControl control;
	private Scanner systemInInput;
	private UIState currentState;

	public ReturnBookUI(ReturnBookControl control) {
		this.control = control;
		this.systemInInput = new Scanner(System.in);
		this.currentState = UIState.INITIALISED;
		this.control.setUI(this);
	}

	public void run() {
		this.output("Return Book Use Case UI\n");
		
		while (true) {
			
			switch (this.currentState) {
			
			case INITIALISED:
				break;
				
			case READY:
				String bookScan = input("Scan Book (<enter> completes): ");
				if (bookScan.length() == 0) {
					this.control.completeScanning();
				}
				else {
					try {
						int bookId = Integer.valueOf(bookScan).intValue();
						this.control.scanBook(bookId);
					}
					catch (NumberFormatException e) {
						this.output("Invalid bookId");
					}					
				}
				break;
				
			case INSPECTING:
				String answer = this.input("Is book damaged? (Y/N): ");

				boolean isDamaged = false;
				if (answer.toUpperCase().equals("Y")) {
					isDamaged = true;
				}

				this.control.dischargeLoan(isDamaged);
			
			case COMPLETED:
				this.output("Return processing complete");
				return;
			
			default:
				this.output("Unhandled state");
				throw new RuntimeException("ReturnBookUI : unhandled state :" + this.currentState);
			}
		}
	}
	
	private String input(String prompt) {
		System.out.print(prompt);
		return this.systemInInput.nextLine();
	}
		
	private void output(Object object) {
		System.out.println(object);
	}
			
	public void display(Object object) {
		this.output(object);
	}
	
	public void Set_State(UIState state) {
		this.currentState = state;
	}
}
