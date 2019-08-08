public class FixBookControl {

    private FixBookUI UI;
    private enum ControlState { INITIALISED, READY, FIXING };
    private ControlState state;

    private library library;
    private book currentBook;


    public FixBookControl() {
        this.library = library.INSTANCE();
        this.state = ControlState.INITIALISED;
    }


    public void setUI(FixBookUI fixBookUI) {
        if (!state.equals(ControlState.INITIALISED)) {
            throw new RuntimeException("FixBookControl: cannot call setUI except in INITIALISED state");
        }
        this.UI = fixBookUI;
        fixBookUI.setState(FixBookUI.UIState.READY);
        this.state = ControlState.READY;
    }


    public void scanBook(int bookId) {
        if (!state.equals(ControlState.READY)) {
            throw new RuntimeException("FixBookControl: cannot call scanBook except in READY state");
        }
        this.currentBook = library.Book(bookId);

        if (currentBook == null) {
            this.UI.display("Invalid bookId");
            return;
        }
        if (!currentBook.IS_Damaged()) {
            this.UI.display("Book has not been damaged");
            return;
        }
        this.UI.display(currentBook.toString());
        this.UI.setState(FixBookUI.UIState.FIXING);
        this.state = ControlState.FIXING;
    }


    public void fixBook(boolean mustFix) {
        if (!state.equals(ControlState.FIXING)) {
            throw new RuntimeException("FixBookControl: cannot call fixBook except in FIXING state");
        }
        if (mustFix) {
            this.library.Repair_BOOK(currentBook);
        }
        currentBook = null;
        this.UI.setState(FixBookUI.UIState.READY);
        state = ControlState.READY;
    }


    public void completeScanning() {
        if (!state.equals(ControlState.READY)) {
            throw new RuntimeException("FixBookControl: cannot call completeScanning except in READY state");
        }
        this.UI.setState(FixBookUI.UIState.COMPLETED);
    }






}
