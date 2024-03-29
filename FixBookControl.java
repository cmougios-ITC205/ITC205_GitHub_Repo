public class FixBookControl {

    private FixBookUI UI;
    private enum ControlState { INITIALISED, READY, FIXING };
    private ControlState state;

    private Library library;
    private Book currentBook;


    public FixBookControl() {
        this.library = library.getInstance();
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
        this.currentBook = library.getBookId(bookId);

        if (currentBook == null) {
            this.UI.display("Invalid bookId");
            return;
        }
        if (!currentBook.isDamaged()) {
            this.UI.display("Book has not been damaged");
            return;
        }

        String currentBookString = currentBook.toString();
        this.UI.display(currentBookString);
        this.UI.setState(FixBookUI.UIState.FIXING);
        this.state = ControlState.FIXING;
    }


    public void fixBook(boolean mustFix) {
        if (!state.equals(ControlState.FIXING)) {
            throw new RuntimeException("FixBookControl: cannot call fixBook except in FIXING state");
        }
        if (mustFix) {
            this.library.repairBook(currentBook);
        }
        this.currentBook = null;
        this.UI.setState(FixBookUI.UIState.READY);
        this.state = ControlState.READY;
    }


    public void completeScanning() {
        if (!state.equals(ControlState.READY)) {
            throw new RuntimeException("FixBookControl: cannot call completeScanning except in READY state");
        }
        this.UI.setState(FixBookUI.UIState.COMPLETED);
    }
}
