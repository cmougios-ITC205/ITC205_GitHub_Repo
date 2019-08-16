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
        bookStringBuilder.append("Book: ").append(this.bookId).append("\n")
          .append("  Title:  ").append(this.title).append("\n")
          .append("  Author: ").append(this.author).append("\n")
          .append("  CallNo: ").append(this.callNumber).append("\n")
          .append("  State:  ").append(this.State);

        return bookStringBuilder.toString();
    }

    public Integer getBookId() {
        return this.bookId;
    }

    public String getTitle() {
        return this.title;
    }



    public boolean isAvailable() {
        return this.State == StateOfBook.AVAILABLE;
    }


    public boolean isOnLoan() {
        return this.State == StateOfBook.ON_LOAN;
    }


    public boolean isDamaged() {
        return this.State == StateOfBook.DAMAGED;
    }


    public void borrowBook() {
        if (this.State.equals(StateOfBook.AVAILABLE)) {
            this.State = StateOfBook.ON_LOAN;
        }
        else {
            String errorMessage = String.format("Book: cannot borrow while Book is in state: %s", this.State);
            throw new RuntimeException(errorMessage);
        }

    }


    public void returnBook(boolean DAMAGED) {
        if (this.State.equals(StateOfBook.ON_LOAN)) {
            if (DAMAGED) {
                this.State = StateOfBook.DAMAGED;
            }
            else {
                this.State = StateOfBook.AVAILABLE;
            }
        }
        else {
            String errorMessage = String.format("Book: cannot Return while Book is in state: %s", this.State);
            throw new RuntimeException(errorMessage);
        }
    }


    public void repairBook() {
        if (this.State.equals(StateOfBook.DAMAGED)) {
            this.State = StateOfBook.AVAILABLE;
        }
        else {
            String errorMessage = String.format("Book: cannot repair while Book is in state: %s", this.State);
            throw new RuntimeException(errorMessage);
        }
    }


}
