import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;


public class Main {
    
    private static Scanner scannerInInput;
    private static Library libraryService;
    private static String menuPrompt;
    private static Calendar calendarService;
    private static SimpleDateFormat displayDateFormat;

    private static String getMenu() {
        StringBuilder menuPrompt = new StringBuilder();
        
        menuPrompt.append("\nLibrary Main Menu\n\n")
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
          
        return menuPrompt.toString();
    }


    public static void main(String[] args) {
        try {
            scannerInInput = new Scanner(System.in);
            libraryService = Library.getInstance();
            calendarService = Calendar.getInstance();
            displayDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
            for (Member memberRecord : libraryService.getMemberList()) {
                output(memberRecord);
            }

            output(" ");

            for (Book bookRecord : libraryService.getBookList()) {
                output(bookRecord);
            }

            menuPrompt = getMenu();

            boolean finishedProcess = false;

            while (!finishedProcess) {

                output("\n" + displayDateFormat.format(calendarService.getDate()));
                String userInput = input(menuPrompt);

                switch (userInput.toUpperCase()) {

                    case "M":
                        addMember();
                        break;

                    case "LM":
                        outputMembers();
                        break;

                    case "B":
                        addBook();
                        break;

                    case "LB":
                        outputBooks();
                        break;

                    case "FB":
                        fixBooks();
                        break;

                    case "L":
                        borrowBook();
                        break;

                    case "R":
                        returnBook();
                        break;

                    case "LL":
                        outputCurrentLoans();
                        break;

                    case "P":
                        runFinesProcess();
                        break;

                    case "T":
                        incrementDate();
                        break;

                    case "Q":
                        finishedProcess = true;
                        break;

                    default:
                        output("\nInvalid option\n");
                        break;
                }
                
                Library.saveInstanceToFile();
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

        for (loan loanRecord : libraryService.getCurrentLoansList()) {
            output(loanRecord + "\n");
        }
    }

    private static void outputBooks() {
        output("");

        for (Book bookRecord : libraryService.getBookList()) {
            output(bookRecord + "\n");
        }
    }

    private static void outputMembers() {
        output("");

        for (Member memberRecord : libraryService.getMemberList()) {
            output(memberRecord + "\n");
        }
    }

    private static void borrowBook() {
        BorrowBookControl control = new BorrowBookControl();
        new BorrowBookUI(control).run();
    }

    private static void returnBook() {
        ReturnBookControl control = new ReturnBookControl();
        new ReturnBookUI(control).run();
    }

    private static void fixBooks() {
        FixBookControl control = new FixBookControl();
        new FixBookUI(control).run();
    }

    private static void incrementDate() {
        try {
            int days = Integer.valueOf(input("Enter number of days: ")).intValue();

            calendarService.incrementDate(days);
            libraryService.checkCurrentLoans();

            Date calendarDate = calendarService.getDate();
            String dateFormatted = displayDateFormat.format(calendarDate);
            output(dateFormatted);

        } catch (NumberFormatException e) {
             output("\nInvalid number of days\n");
        }
    }

    private static void addBook() {
        String author = input("Enter author: ");
        String title  = input("Enter title: ");
        String callNumber = input("Enter call number: ");
        Book bookAdded = libraryService.addBook(author, title, callNumber);

        output("\n" + bookAdded + "\n");
    }
    
    private static void addMember() {
        try {
            String lastName = input("Enter last name: ");
            String firstName  = input("Enter first name: ");
            String email = input("Enter email: ");
            String phoneNumberInput = input("Enter phone number: ");
            int pinNumber = Integer.valueOf(phoneNumberInput).intValue();
            Member memberAdded = libraryService.addMember(lastName, firstName, email, pinNumber);

            output("\n" + memberAdded + "\n");

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
