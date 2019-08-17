import java.util.Scanner;


public class BorrowBookUI {

    public static enum UIState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };

    private BorrowBookControl control;
    private Scanner systemInInput;
    private UIState state;


    public BorrowBookUI(BorrowBookControl control) {
        this.control = control;
        this.systemInInput = new Scanner(System.in);
        this.state = UIState.INITIALISED;
        this.control.setUI(this);
    }


    private String askQuestion(String prompt) {
        System.out.print(prompt);
        return this.systemInInput.nextLine();
    }


    private void output(Object object) {
        System.out.println(object);
    }


    public void setState(UIState state) {
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
                String memberCard = askQuestion("Swipe member card (press <enter> to cancel): ");
                if (memberCard.length() == 0) {
                    this.control.cancel();
                    break;
                }
                try {
                    int memberId = Integer.valueOf(memberCard).intValue();
                    this.control.swipeMemberCard(memberId);
                }
                catch (NumberFormatException e) {
                    this.output("Invalid Member Id");
                }
                break;


            case RESTRICTED:
                this.askQuestion("Press <any key> to cancel");
                this.control.cancel();
                break;


            case SCANNING:
                String scannedBook = askQuestion("Scan Book (<enter> completes): ");
                if (scannedBook.length() == 0) {
                    this.control.completeBorrow();
                    break;
                }
                try {
                    int bookId = Integer.valueOf(scannedBook).intValue();
                    this.control.scanBook(bookId);

                } catch (NumberFormatException e) {
                    this.output("Invalid Book Id");
                }
                break;


            case FINALISING:
                String answerQuestion = askQuestion("Commit loans? (Y/N): ");
                if (answerQuestion.toUpperCase().equals("N")) {
                    this.control.cancel();

                } else {
                    this.control.commitLoans();
                    this.askQuestion("Press <any key> to complete ");
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
