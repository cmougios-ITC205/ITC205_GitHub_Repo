import java.io.Serializable;


@SuppressWarnings("serial")
public class Book implements Serializable {

    private String title;
    private String author;
    private String callNumber;
    private int bookId;

    private enum StateOfBook { AVAILABLE, ON_LOAN, DAMAGED, RESERVED };
    private StateOfBook State;


    public Book(String author, String title, String callNumber, int bookId) {
        this.author = author;
        this.title = title;
        this.callNumber = callNumber;
        this.bookId = bookId;
        this.State = StateOfBook.AVAILABLE;
    }

    public String toString() {
        StringBuilder bookStringBuilder = new StringBuilder();
        bookStringBuilder.append("Book: ").append(bookId).append("\n")
          .append("  Title:  ").append(title).append("\n")
          .append("  Author: ").append(author).append("\n")
          .append("  CallNo: ").append(callNumber).append("\n")
          .append("  State:  ").append(State);

        return bookStringBuilder.toString();
    }

    public Integer getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }



    public boolean isAvailable() {
        return State == StateOfBook.AVAILABLE;
    }


    public boolean isOnLoan() {
        return State == StateOfBook.ON_LOAN;
    }


    public boolean isDamaged() {
        return State == StateOfBook.DAMAGED;
    }


    public void borrowBook() {
        if (State.equals(StateOfBook.AVAILABLE)) {
            State = StateOfBook.ON_LOAN;
        }
        else {
            throw new RuntimeException(String.format("Book: cannot borrow while Book is in state: %s", State));
        }

    }


    public void returnBook(boolean DAMAGED) {
        if (State.equals(StateOfBook.ON_LOAN)) {
            if (DAMAGED) {
                State = StateOfBook.DAMAGED;
            }
            else {
                State = StateOfBook.AVAILABLE;
            }
        }
        else {
            throw new RuntimeException(String.format("Book: cannot Return while Book is in state: %s", State));
        }
    }


    public void repairBook() {
        if (State.equals(StateOfBook.DAMAGED)) {
            State = StateOfBook.AVAILABLE;
        }
        else {
            throw new RuntimeException(String.format("Book: cannot repair while Book is in state: %s", State));
        }
    }


}
