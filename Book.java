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

	public Integer getBookID() {
		return bookId;
	}

	public String TITLE() {
		return title;
	}


	
	public boolean AVAILABLE() {
		return State == StateOfBook.AVAILABLE;
	}

	
	public boolean On_loan() {
		return State == StateOfBook.ON_LOAN;
	}

	
	public boolean IS_Damaged() {
		return State == StateOfBook.DAMAGED;
	}

	
	public void Borrow() {
		if (State.equals(StateOfBook.AVAILABLE)) {
			State = StateOfBook.ON_LOAN;
		}
		else {
			throw new RuntimeException(String.format("Book: cannot borrow while Book is in state: %s", State));
		}
		
	}


	public void Return(boolean DAMAGED) {
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

	
	public void Repair() {
		if (State.equals(StateOfBook.DAMAGED)) {
			State = StateOfBook.AVAILABLE;
		}
		else {
			throw new RuntimeException(String.format("Book: cannot repair while Book is in state: %s", State));
		}
	}


}
