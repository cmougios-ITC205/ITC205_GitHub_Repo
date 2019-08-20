
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class library implements Serializable {
	
	private static final String libraryFile = "library.obj";
	private static final int loanLimit = 2;
	private static final int loanPeriod = 2;
	private static final double finePerDay = 1.0;
	private static final double maxFinesOwed = 1.0;
	private static final double damageFee = 2.0;
	
	private static library SeLf;
	private int BOOK_ID;
	private int MEMBER_ID;
	private int LOAN_ID;
	private Date LOAN_DATE;
	
	private Map<Integer, Book> CATALOG;
	private Map<Integer, Member> MEMBERS;
	private Map<Integer, loan> LOANS;
	private Map<Integer, loan> CURRENT_LOANS;
	private Map<Integer, Book> DAMAGED_BOOKS;
	

	private library() {
		CATALOG = new HashMap<>();
		MEMBERS = new HashMap<>();
		LOANS = new HashMap<>();
		CURRENT_LOANS = new HashMap<>();
		DAMAGED_BOOKS = new HashMap<>();
		BOOK_ID = 1;
		MEMBER_ID = 1;		
		LOAN_ID = 1;		
	}

	
	public static synchronized library INSTANCE() {		
		if (SeLf == null) {
			Path PATH = Paths.get(libraryFile);			
			if (Files.exists(PATH)) {	
				try (ObjectInputStream LiF = new ObjectInputStream(new FileInputStream(libraryFile));) {
			    
					SeLf = (library) LiF.readObject();
					Calendar.getInstance().setDate(SeLf.LOAN_DATE);
					LiF.close();
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			else SeLf = new library();
		}
		return SeLf;
	}

	
	public static synchronized void SAVE() {
		if (SeLf != null) {
			SeLf.LOAN_DATE = Calendar.getInstance().getDate();
			try (ObjectOutputStream LoF = new ObjectOutputStream(new FileOutputStream(libraryFile));) {
				LoF.writeObject(SeLf);
				LoF.flush();
				LoF.close();	
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	
	public int BookID() {
		return BOOK_ID;
	}
	
	
	public int MemberID() {
		return MEMBER_ID;
	}
	
	
	private int NextBID() {
		return BOOK_ID++;
	}

	
	private int NextMID() {
		return MEMBER_ID++;
	}

	
	private int NextLID() {
		return LOAN_ID++;
	}

	
	public List<Member> MEMBERS() {
		return new ArrayList<Member>(MEMBERS.values());
	}


	public List<Book> BOOKS() {
		return new ArrayList<Book>(CATALOG.values());
	}


	public List<loan> CurrentLoans() {
		return new ArrayList<loan>(CURRENT_LOANS.values());
	}


	public Member Add_mem(String lastName, String firstName, String email, int phoneNo) {
		Member member = new Member(lastName, firstName, email, phoneNo, NextMID());
		MEMBERS.put(member.getId(), member);
		return member;
	}

	
	public Book Add_book(String a, String t, String c) {
		Book b = new Book(a, t, c, NextBID());
		CATALOG.put(b.getBookId(), b);
		return b;
	}

	
	public Member MEMBER(int memberId) {
		if (MEMBERS.containsKey(memberId)) 
			return MEMBERS.get(memberId);
		return null;
	}

	
	public Book Book(int bookId) {
		if (CATALOG.containsKey(bookId)) 
			return CATALOG.get(bookId);		
		return null;
	}

	
	public int LOAN_LIMIT() {
		return loanLimit;
	}

	
	public boolean MEMBER_CAN_BORROW(Member member) {
		if (member.getNumberOfCurrentLoans() == loanLimit )
			return false;
				
		if (member.getFinesOwed() >= maxFinesOwed)
			return false;
				
		for (loan loan : member.getLoans())
			if (loan.OVer_Due()) 
				return false;
			
		return true;
	}

	
	public int Loans_Remaining_For_Member(Member member) {
		return loanLimit - member.getNumberOfCurrentLoans();
	}

	
	public loan ISSUE_LAON(Book book, Member member) {
		Date dueDate = Calendar.getInstance().getDueDate(loanPeriod);
		loan loan = new loan(NextLID(), book, member, dueDate);
		member.Take_Out_Loan(loan);
		book.borrowBook();
		LOANS.put(loan.ID(), loan);
		CURRENT_LOANS.put(book.getBookId(), loan);
		return loan;
	}
	
	
	public loan LOAN_BY_BOOK_ID(int bookId) {
		if (CURRENT_LOANS.containsKey(bookId)) {
			return CURRENT_LOANS.get(bookId);
		}
		return null;
	}

	
	public double CalculateOverDueFine(loan loan) {
		if (loan.OVer_Due()) {
			long daysOverDue = Calendar.getInstance().getDaysDifference(loan.Get_Due_Date());
			double fine = daysOverDue * finePerDay;
			return fine;
		}
		return 0.0;		
	}


	public void Discharge_loan(loan currentLoan, boolean isDamaged) {
		Member member = currentLoan.Member();
		Book book  = currentLoan.Book();
		
		double overDueFine = CalculateOverDueFine(currentLoan);
		member.Add_Fine(overDueFine);	
		
		member.dIsChArGeLoAn(currentLoan);
		book.returnBook(isDamaged);
		if (isDamaged) {
			member.Add_Fine(damageFee);
			DAMAGED_BOOKS.put(book.getBookId(), book);
		}
		currentLoan.DiScHaRgE();
		CURRENT_LOANS.remove(book.getBookId());
	}


	public void checkCurrentLoans() {
		for (loan loan : CURRENT_LOANS.values()) {
			loan.checkOverDue();
		}		
	}


	public void Repair_BOOK(Book currentBook) {
		if (DAMAGED_BOOKS.containsKey(currentBook.getBookId())) {
			currentBook.repairBook();
			DAMAGED_BOOKS.remove(currentBook.getBookId());
		}
		else {
			throw new RuntimeException("Library: repairBook: Book is not damaged");
		}
		
	}
	
	
}
