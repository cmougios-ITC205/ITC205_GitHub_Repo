import java.io.Serializable;


@SuppressWarnings("serial")
public class Book implements Serializable {

    private String title;
    private String author;
    private String callNumber;
    private int bookId;

    private enum StateOfBook { AVAILABLE, ON_LOAN, DAMAGED, RESERVED };
    private StateOfBook state;


    public Book(String author, String title, String callNumber, int bookId) {
        this.author = author;
        this.title = title;
        this.callNumber = callNumber;
        this.bookId = bookId;
        this.state = StateOfBook.AVAILABLE;
    }

    public String toString() {
        StringBuilder bookStringBuilder = new StringBuilder();
        bookStringBuilder.append("Book: ").append(bookId).append("\n")
          .append("  Title:  ").append(title).append("\n")
          .append("  Author: ").append(author).append("\n")
          .append("  CallNo: ").append(callNumber).append("\n")
          .append("  State:  ").append(state);

        return bookStringBuilder.toString();
    }

    public Integer getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }



    public boolean isAvailable() {
        return state == StateOfBook.AVAILABLE;
    }


    public boolean isOnLoan() {
        return state == StateOfBook.ON_LOAN;
    }


    public boolean isDamaged() {
        return state == StateOfBook.DAMAGED;
    }


    public void borrowBook() {
        if (state.equals(StateOfBook.AVAILABLE)) {
            state = StateOfBook.ON_LOAN;
        }
        else {
            throw new RuntimeException(String.format("Book: cannot borrow while Book is in state: %s", state));
        }

    }


    public void returnBook(boolean DAMAGED) {
        if (state.equals(StateOfBook.ON_LOAN)) {
            if (DAMAGED) {
                state = StateOfBook.DAMAGED;
            }
            else {
                state = StateOfBook.AVAILABLE;
            }
        }
        else {
            throw new RuntimeException(String.format("Book: cannot Return while Book is in state: %s", state));
        }
    }


    public void repairBook() {
        if (state.equals(StateOfBook.DAMAGED)) {
            state = StateOfBook.AVAILABLE;
        }
        else {
            throw new RuntimeException(String.format("Book: cannot repair while Book is in state: %s", state));
        }
    }


}
