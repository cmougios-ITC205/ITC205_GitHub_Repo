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
        bookStringBuilder.append("Book: ").append(this.bookId).append("\n")
          .append("  Title:  ").append(this.title).append("\n")
          .append("  Author: ").append(this.author).append("\n")
          .append("  CallNo: ").append(this.callNumber).append("\n")
          .append("  State:  ").append(this.state);

        return bookStringBuilder.toString();
    }

    public Integer getBookId() {
        return this.bookId;
    }

    public String getTitle() {
        return this.title;
    }



    public boolean isAvailable() {
        return this.state == StateOfBook.AVAILABLE;
    }


    public boolean isOnLoan() {
        return this.state == StateOfBook.ON_LOAN;
    }


    public boolean isDamaged() {
        return this.state == StateOfBook.DAMAGED;
    }


    public void borrowBook() {
        if (this.state.equals(StateOfBook.AVAILABLE)) {
            this.state = StateOfBook.ON_LOAN;
        }
        else {
            String errorMessage = String.format("Book: cannot borrow while Book is in state: %s", this.state);
            throw new RuntimeException(errorMessage);
        }

    }


    public void returnBook(boolean isDamaged) {
        if (this.state.equals(StateOfBook.ON_LOAN)) {
            if (isDamaged) {
                this.state = StateOfBook.DAMAGED;
            }
            else {
                this.state = StateOfBook.AVAILABLE;
            }
        }
        else {
            String errorMessage = String.format("Book: cannot Return while Book is in state: %s", this.state);
            throw new RuntimeException(errorMessage);
        }
    }


    public void repairBook() {
        if (this.state.equals(StateOfBook.DAMAGED)) {
            this.state = StateOfBook.AVAILABLE;
        }
        else {
            String errorMessage = String.format("Book: cannot repair while Book is in state: %s", this.state);
            throw new RuntimeException(errorMessage);
        }
    }


}
