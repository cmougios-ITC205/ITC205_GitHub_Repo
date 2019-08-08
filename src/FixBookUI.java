import java.util.Scanner;


public class FixBookUI {

    public enum UIState { INITIALISED, READY, FIXING, COMPLETED };

    private FixBookControl control;
    private Scanner systemInInput;
    private UIState currentState;


    public FixBookUI(FixBookControl control) {
        this.control = control;
        this.systemInInput = new Scanner(System.in);
        this.currentState = UIState.INITIALISED;
        this.control.setUI(this);
    }


    public void setState(UIState state) {
        this.currentState = state;
    }


    public void run() {
        this.output("Fix Book Use Case UI\n");

        while (true) {

            switch (currentState) {

            case READY:
                String bookScan = askQuestion("Scan Book (<enter> completes): ");
                if (bookScan.length() == 0) {
                    this.control.SCannING_COMplete();
                }
                else {
                    try {
                        int Book_ID = Integer.valueOf(bookScan).intValue();
                        this.control.scanBook(Book_ID);
                    }
                    catch (NumberFormatException e) {
                        this.output("Invalid bookId");
                    }
                }
                break;

            case FIXING:
                String AnS = askQuestion("Fix Book? (Y/N) : ");
                boolean FiX = false;
                if (AnS.toUpperCase().equals("Y")) {
                    FiX = true;
                }
                this.control.fixBook(FiX);
                break;

            case COMPLETED:
                this.output("Fixing process complete");
                return;

            default:
                this.output("Unhandled state");
                throw new RuntimeException("FixBookUI : unhandled state :" + currentState);

            }
        }

    }


    private String askQuestion(String prompt) {
        System.out.print(prompt);
        return this.systemInInput.nextLine();
    }


    private void output(Object object) {
        System.out.println(object);
    }


    public void display(Object object) {
        this.output(object);
    }


}
