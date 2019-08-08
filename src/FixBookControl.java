public class FixBookControl {

    private FixBookUI UI;
    private enum ControlState { INITIALISED, READY, FIXING };
    private ControlState state;

    private library library;
    private book currentBook;


    public FixBookControl() {
        this.library = library.INSTANCE();
        state = ControlState.INITIALISED;
    }


    public void setUI(FixBookUI fixBookUI) {
        if (!state.equals(ControlState.INITIALISED)) {
            throw new RuntimeException("FixBookControl: cannot call setUI except in INITIALISED state");
        }
        this.UI = fixBookUI;
        fixBookUI.setState(FixBookUI.UIState.READY);
        state = ControlState.READY;
    }


    public void scanBook(int bookId) {
        if (!state.equals(ControlState.READY)) {
            throw new RuntimeException("FixBookControl: cannot call scanBook except in READY state");
        }
        currentBook = library.Book(bookId);

        if (currentBook == null) {
            UI.display("Invalid bookId");
            return;
        }
        if (!currentBook.IS_Damaged()) {
            UI.display("Book has not been damaged");
            return;
        }
        UI.display(currentBook.toString());
        UI.setState(FixBookUI.UIState.FIXING);
        state = ControlState.FIXING;
    }


    public void fixBook(boolean mustFix) {
        if (!state.equals(ControlState.FIXING)) {
            throw new RuntimeException("FixBookControl: cannot call fixBook except in FIXING state");
        }
        if (mustFix) {
            library.Repair_BOOK(currentBook);
        }
        currentBook = null;
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
