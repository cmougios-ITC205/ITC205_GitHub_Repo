import java.util.ArrayList;
import java.util.List;

public class BorrowBookControl {
	
	private BorrowBookUI UI;
	private library LIBRARY;
	private member MEMBERS;
	private enum ControlState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };
	private ControlState STATE;
	private List<book> PENDING;
	private List<loan> COMPLETED;
	private book BOOK;
	
	
	public BorrowBookControl()
	{
		this.LIBRARY = LIBRARY.INSTANCE();
		STATE = ControlState.INITIALISED;
	}
	

	public void setUI(BorrowBookUI ui)
	{
		if (!STATE.equals(ControlState.INITIALISED))

		    throw new RuntimeException("BorrowBookControl: cannot call setUI except in INITIALISED state");
			this.UI = ui;
	    	ui.setState(BorrowBookUI.UI_STATE.READY);
		    STATE = ControlState.READY;
	}

		
	public void swiped(int memberId)
	{
		if (!STATE.equals(ControlState.READY))

		    throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");
			MEMBERS = LIBRARY.member(memberId);
		if (MEMBERS == null)
		{
			UI.Display("Invalid memberId");
			return;
		}
		if (LIBRARY.memberCanBorrow(MEMBERS))
			{
			PENDING = new ArrayList<>();
			UI.setState(BorrowBookUI.UI_STATE.SCANNING);
			STATE = ControlState.SCANNING;
			}
		else 
		{
			UI.Display("Member cannot borrow at this time");
			UI.setState(BorrowBookUI.UI_STATE.RESTRICTED);
		}
	}
	
	
	public void scanned(int bookId)
	{
		BOOK = null;
		if (!STATE.equals(ControlState.SCANNING))
		{
			throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING state");
		}	
		BOOK = LIBRARY.Book(bookId);
		if (BOOK == null)
		{
			UI.Display("Invalid bookId");
			return;
		}
		if (!BOOK.AVAILABLE())
		{
			UI.Display("Book cannot be borrowed");
			return;
		}
		PENDING.add(BOOK);
		for (book B : PENDING)
		{
			UI.Display(B.toString());
		}
		if (LIBRARY.loansRemainingForMember(MEMBERS) - PENDING.size() == 0)
		{
			UI.Display("Loan limit reached");
			isComplete();
		}
	}
	
	
	public void isComplete()
	{
		if (PENDING.size() == 0)
		{
			cancel();
		}
		else {
			UI.Display("\nFinal Borrowing List");
			for (book B : PENDING)
			{
				UI.Display(B.toString());
			}
			COMPLETED = new ArrayList<loan>();
			UI.setState(BorrowBookUI.UI_STATE.FINALISING);
			STATE = ControlState.FINALISING;
			}
	}


	public void commitLoans()
	{
		if (!STATE.equals(ControlState.FINALISING))
		{
			throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING state");
		}	
		for (book B : PENDING)
		{
			loan LOAN = LIBRARY.ISSUE_LAON(B, MEMBERS);
			COMPLETED.add(LOAN);			
		}
		UI.Display("Completed Loan Slip");
		for (loan LOAN : COMPLETED)
		{
			UI.Display(LOAN.toString());
		}
		UI.setState(BorrowBookUI.UI_STATE.COMPLETED);
		STATE = ControlState.COMPLETED;
	}

	
	public void cancel()
	{
        UI.setState(BorrowBookUI.UI_STATE.CANCELLED);
		STATE = ControlState.CANCELLED;
	}
	
	
}
