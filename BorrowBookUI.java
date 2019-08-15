import java.util.Scanner;


public class BorrowBookUI {
	
	public static enum UiState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };

	private BorrowBookControl control;
	private Scanner input;
	private UiState state;

	
	public BorrowBookUI(BorrowBookControl control) {
		this.control = control;
		this.input = new Scanner(System.in);
		this.state = UiState.INITIALISED;
		this.control.setUI(this);
	}

	
	private String input(String prompt) {
		System.out.print(prompt);
		return this.input.nextLine();
	}	
		
		
	private void output(Object object) {
		System.out.println(object);
	}
	
			
	public void setState(UiState state) {
		this.state = state;
	}

	
	public void run() {
		this.output("Borrow Book Use Case UI\n");
		
		while (true) {
			
			switch (this.state) {
			
			case CANCELLED:
				this.output("Borrowing Cancelled");
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
					this.output("Invalid Member Id");
				}
				break;

				
			case RESTRICTED:
				this.input("Press <any key> to cancel");
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
					this.output("Invalid Book Id");
				} 
				break;
					
				
			case FINALISING:
				String answerQuestion = input("Commit loans? (Y/N): ");
				if (answerQuestion.toUpperCase().equals("N")) {
					this.control.cancel();
					
				} else {
					this.control.commitLoans();
					this.input("Press <any key> to complete ");
				}
				break;
				
				
			case COMPLETED:
				this.output("Borrowing Completed");
				return;
	
				
			default:
				this.output("Unhandled state");
				throw new RuntimeException("BorrowBookUI : unhandled state :" + this.state);
			}
		}		
	}


	public void display(Object object) {

		this.output(object);
	}


}
