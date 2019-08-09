import java.util.Scanner;


public class BorrowBookUI {
	
	public static enum UI_STATE { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };

	private BorrowBookControl CONTROL;
	private Scanner INPUT;
	private UI_STATE STATE;

	
	public BorrowBookUI(BorrowBookControl control) {
		this.CONTROL = control;
		INPUT = new Scanner(System.in);
		STATE = UI_STATE.INITIALISED;
		control.setUI(this);
	}

	
	private String input(String prompt) {
		System.out.print(prompt);
		return INPUT.nextLine();
	}	
		
		
	private void output(Object object) {
		System.out.println(object);
	}
	
			
	public void setState(UI_STATE STATE) {
		this.STATE = STATE;
	}

	
	public void run() {
		output("Borrow Book Use Case UI\n");
		
		while (true) {
			
			switch (STATE) {
			
			case CANCELLED:
				output("Borrowing Cancelled");
				return;

				
			case READY:
				String memStr = input("Swipe member card (press <enter> to cancel): ");
				if (memStr.length() == 0) {
					CONTROL.cancel();
					break;
				}
				try {
					int memberId = Integer.valueOf(memStr).intValue();
					CONTROL.swiped(memberId);
				}
				catch (NumberFormatException e) {
					output("Invalid Member Id");
				}
				break;

				
			case RESTRICTED:
				input("Press <any key> to cancel");
				CONTROL.cancel();
				break;
			
				
			case SCANNING:
				String bookStr = input("Scan Book (<enter> completes): ");
				if (bookStr.length() == 0) {
					CONTROL.isComplete();
					break;
				}
				try {
					int BiD = Integer.valueOf(bookStr).intValue();
					CONTROL.scanned(BiD);
					
				} catch (NumberFormatException e) {
					output("Invalid Book Id");
				} 
				break;
					
				
			case FINALISING:
				String Ans = input("Commit loans? (Y/N): ");
				if (Ans.toUpperCase().equals("N")) {
					CONTROL.cancel();
					
				} else {
					CONTROL.commitLoans();
					input("Press <any key> to complete ");
				}
				break;
				
				
			case COMPLETED:
				output("Borrowing Completed");
				return;
	
				
			default:
				output("Unhandled state");
				throw new RuntimeException("BorrowBookUI : unhandled state :" + STATE);
			}
		}		
	}


	public void display(Object object) {
		output(object);		
	}


}
