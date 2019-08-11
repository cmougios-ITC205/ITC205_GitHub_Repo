import java.util.Scanner;


public class BorrowBookUI {
	
	public static enum UIState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };

	private BorrowBookControl control;
	private Scanner input;
	private UIState state;

	
	public BorrowBookUI(BorrowBookControl control) {
		this.control = control;
		input = new Scanner(System.in);
		this.state = UIState.INITIALISED;
		this.control.setUI(this);
		/*control.setUI(this);*/
	}

	
	private String input(String prompt) {
		/*System.out.print(prompt);*/
        System.out.print(this);
		return input.nextLine();
	}	
		
		
	private void output(Object object) {

    	System.out.println(object);
	}
	
			
	public void setState(UIState STATE) {

		this.state = STATE;

	}

	
	public void run() {

		output("Borrow Book Use Case UI\n");
		
		while (true) {
			
			switch (state) {
			
			case CANCELLED:
				output("Borrowing Cancelled");
				return;

				
			case READY:
				String memberCard = input("Swipe member card (press <enter> to cancel): ");
				if (memberCard.length() == 0) {

					this.control.cancel();
					break;
				}
				try {

					int memberId = Integer.valueOf(memberCard).intValue();
					this.control.swiped(memberId);
				}
				catch (NumberFormatException e) {

					output("Invalid Member Id");
				}
				break;

				
			case RESTRICTED:
				input("Press <any key> to cancel");
				this.control.cancel();
				break;
			
				
			case SCANNING:
				String scanBook = input("Scan Book (<enter> completes): ");
				if (scanBook.length() == 0) {

					this.control.complete();
					break;
				}
				try {
					int bookId = Integer.valueOf(scanBook).intValue();
					this.control.scanned(bookId);
					
				} catch (NumberFormatException e) {
					output("Invalid Book Id");
				} 
				break;
					
				
			case FINALISING:
				String askQuestion = input("Commit loans? (Y/N): ");
				if (askQuestion.toUpperCase().equals("N")) {
					this.control.cancel();
					
				} else {
					this.control.commitLoans();
					input("Press <any key> to complete ");
				}
				break;
				
				
			case COMPLETED:
				output("Borrowing Completed");
				return;
	
				
			default:
				output("Unhandled state");
				throw new RuntimeException("BorrowBookUI : unhandled state :" + state);
			}
		}		
	}


	public void display(Object object) {

    	output(object);
	}


}
