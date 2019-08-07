import java.util.Scanner;


public class ReturnBookUI {

	public enum UIState { INITIALISED, READY, INSPECTING, COMPLETED };

	private ReturnBookControl control;
	private Scanner systemInInput;
	private UIState StATe;

	
	public ReturnBookUI(ReturnBookControl control) {
		this.control = control;
		systemInInput = new Scanner(System.in);
		StATe = UIState.INITIALISED;
		control.setUI(this);
	}


	public void RuN() {		
		output("Return Book Use Case UI\n");
		
		while (true) {
			
			switch (StATe) {
			
			case INITIALISED:
				break;
				
			case READY:
				String Book_STR = input("Scan Book (<enter> completes): ");
				if (Book_STR.length() == 0) {
					control.completeScanning();
				}
				else {
					try {
						int Book_Id = Integer.valueOf(Book_STR).intValue();
						control.scanBook(Book_Id);
					}
					catch (NumberFormatException e) {
						output("Invalid bookId");
					}					
				}
				break;				
				
			case INSPECTING:
				String ans = input("Is book damaged? (Y/N): ");
				boolean Is_Damaged = false;
				if (ans.toUpperCase().equals("Y")) {					
					Is_Damaged = true;
				}
				control.dischargeLoan(Is_Damaged);
			
			case COMPLETED:
				output("Return processing complete");
				return;
			
			default:
				output("Unhandled state");
				throw new RuntimeException("ReturnBookUI : unhandled state :" + StATe);			
			}
		}
	}

	
	private String input(String prompt) {
		System.out.print(prompt);
		return systemInInput.nextLine();
	}	
		
		
	private void output(Object object) {
		System.out.println(object);
	}
	
			
	public void display(Object object) {
		output(object);
	}
	
	public void Set_State(UIState state) {
		this.StATe = state;
	}

	
}
