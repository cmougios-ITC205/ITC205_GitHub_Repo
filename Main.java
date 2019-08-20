import java.text.SimpleDateFormat;
import java.util.Scanner;


public class Main {
	
	private static Scanner scannerInInput;
	private static library libraryService;
	private static String menuPrompt;
	private static Calendar calendarService;
	private static SimpleDateFormat displayDateFormat;

	private static String getMenu() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("\nLibrary Main Menu\n\n")
		  .append("  M  : add member\n")
		  .append("  LM : list members\n")
		  .append("\n")
		  .append("  B  : add Book\n")
		  .append("  LB : list books\n")
		  .append("  FB : fix books\n")
		  .append("\n")
		  .append("  L  : take out a loan\n")
		  .append("  R  : return a loan\n")
		  .append("  LL : list loans\n")
		  .append("\n")
		  .append("  P  : pay fine\n")
		  .append("\n")
		  .append("  T  : increment date\n")
		  .append("  Q  : quit\n")
		  .append("\n")
		  .append("Choice : ");
		  
		return sb.toString();
	}


	public static void main(String[] args) {		
		try {			
			scannerInInput = new Scanner(System.in);
			libraryService = library.INSTANCE();
			calendarService = Calendar.getInstance();
			displayDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
			for (member memberRecord : libraryService.MEMBERS()) {
				output(memberRecord);
			}

			output(" ");

			for (Book bookRecord : libraryService.BOOKS()) {
				output(bookRecord);
			}
						
			menuPrompt = getMenu();
			
			boolean finishedProcess = false;
			
			while (!finishedProcess) {
				
				output("\n" + displayDateFormat.format(calendarService.getDate()));
				String userInput = input(menuPrompt);
				
				switch (userInput.toUpperCase()) {

                    case "M":
                        ADD_MEMBER();
                        break;

                    case "LM":
                        MEMBERS();
                        break;

                    case "B":
                        ADD_BOOK();
                        break;

                    case "LB":
                        outputBooks();
                        break;

                    case "FB":
                        FIX_BOOKS();
                        break;

                    case "L":
                        BORROW_BOOK();
                        break;

                    case "R":
                        RETURN_BOOK();
                        break;

                    case "LL":
                        outputCurrentLoans();
                        break;

                    case "P":
                        runFinesProcess();
                        break;

                    case "T":
                        INCREMENT_DATE();
                        break;

                    case "Q":
                        finishedProcess = true;
                        break;

                    default:
                        output("\nInvalid option\n");
                        break;
				}
				
				library.SAVE();
			}			
		} catch (RuntimeException e) {
			output(e);
		}

		output("\nEnded\n");
	}	

	private static void runFinesProcess() {
	    PayFineControl payFineControl = new PayFineControl();
		new PayFineUI(payFineControl).run();
	}

	private static void outputCurrentLoans() {
		output("");

		for (loan loanRecord : libraryService.CurrentLoans()) {
			output(loanRecord + "\n");
		}		
	}

	private static void outputBooks() {
		output("");
		for (Book book : libraryService.BOOKS()) {
			output(book + "\n");
		}		
	}



	private static void MEMBERS() {
		output("");
		for (member member : libraryService.MEMBERS()) {
			output(member + "\n");
		}		
	}



	private static void BORROW_BOOK() {
		new BorrowBookUI(new BorrowBookControl()).run();		
	}


	private static void RETURN_BOOK() {
		new ReturnBookUI(new ReturnBookControl()).run();
	}


	private static void FIX_BOOKS() {
		new FixBookUI(new FixBookControl()).run();		
	}


	private static void INCREMENT_DATE() {
		try {
			int days = Integer.valueOf(input("Enter number of days: ")).intValue();
			calendarService.incrementDate(days);
			libraryService.checkCurrentLoans();
			output(displayDateFormat.format(calendarService.getDate()));
			
		} catch (NumberFormatException e) {
			 output("\nInvalid number of days\n");
		}
	}


	private static void ADD_BOOK() {
		
		String A = input("Enter author: ");
		String T  = input("Enter title: ");
		String C = input("Enter call number: ");
		Book B = libraryService.Add_book(A, T, C);
		output("\n" + B + "\n");
		
	}

	
	private static void ADD_MEMBER() {
		try {
			String LN = input("Enter last name: ");
			String FN  = input("Enter first name: ");
			String EM = input("Enter email: ");
			int PN = Integer.valueOf(input("Enter phone number: ")).intValue();
			member M = libraryService.Add_mem(LN, FN, EM, PN);
			output("\n" + M + "\n");
			
		} catch (NumberFormatException e) {
			 output("\nInvalid phone number\n");
		}
		
	}


	private static String input(String prompt) {
		System.out.print(prompt);
		return scannerInInput.nextLine();
	}
	
	
	
	private static void output(Object object) {
		System.out.println(object);
	}

	
}
