import java.util.Scanner;


public class FixBookUI {

    public enum UIState { INITIALISED, READY, FIXING, COMPLETED };

    private FixBookControl control;
    private Scanner systemInInput;
    private UIState state;


    public FixBookUI(FixBookControl control) {
        this.control = control;
        systemInInput = new Scanner(System.in);
        state = UIState.INITIALISED;
        control.Set_Ui(this);
    }


    public void setState(UIState state) {
        this.state = state;
    }


    public void run() {
        output("Fix Book Use Case UI\n");

        while (true) {

            switch (state) {

            case READY:
                String Book_STR = input("Scan Book (<enter> completes): ");
                if (Book_STR.length() == 0) {
                    control.SCannING_COMplete();
                }
                else {
                    try {
                        int Book_ID = Integer.valueOf(Book_STR).intValue();
                        control.Book_scanned(Book_ID);
                    }
                    catch (NumberFormatException e) {
                        output("Invalid bookId");
                    }
                }
                break;

            case FIXING:
                String AnS = input("Fix Book? (Y/N) : ");
                boolean FiX = false;
                if (AnS.toUpperCase().equals("Y")) {
                    FiX = true;
                }
                control.FIX_Book(FiX);
                break;

            case COMPLETED:
                output("Fixing process complete");
                return;

            default:
                output("Unhandled state");
                throw new RuntimeException("FixBookUI : unhandled state :" + state);

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


}
