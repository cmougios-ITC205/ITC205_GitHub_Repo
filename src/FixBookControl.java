public class FixBookControl {

    private FixBookUI UI;
    private enum ControlState { INITIALISED, READY, FIXING };
    private ControlState state;

    private library LIB;
    private book Cur_Book;


    public FixBookControl() {
        this.LIB = LIB.INSTANCE();
        state = ControlState.INITIALISED;
    }


    public void Set_Ui(FixBookUI ui) {
        if (!state.equals(ControlState.INITIALISED)) {
            throw new RuntimeException("FixBookControl: cannot call setUI except in INITIALISED state");
        }
        this.UI = ui;
        ui.setState(FixBookUI.UIState.READY);
        state = ControlState.READY;
    }


    public void Book_scanned(int bookId) {
        if (!state.equals(ControlState.READY)) {
            throw new RuntimeException("FixBookControl: cannot call bookScanned except in READY state");
        }
        Cur_Book = LIB.Book(bookId);

        if (Cur_Book == null) {
            UI.display("Invalid bookId");
            return;
        }
        if (!Cur_Book.IS_Damaged()) {
            UI.display("Book has not been damaged");
            return;
        }
        UI.display(Cur_Book.toString());
        UI.setState(FixBookUI.UIState.FIXING);
        state = ControlState.FIXING;
    }


    public void FIX_Book(boolean MUST_fix) {
        if (!state.equals(ControlState.FIXING)) {
            throw new RuntimeException("FixBookControl: cannot call fixBook except in FIXING state");
        }
        if (MUST_fix) {
            LIB.Repair_BOOK(Cur_Book);
        }
        Cur_Book = null;
        UI.setState(FixBookUI.UIState.READY);
        state = ControlState.READY;
    }


    public void SCannING_COMplete() {
        if (!state.equals(ControlState.READY)) {
            throw new RuntimeException("FixBookControl: cannot call scanningComplete except in READY state");
        }
        UI.setState(FixBookUI.UIState.COMPLETED);
    }






}
