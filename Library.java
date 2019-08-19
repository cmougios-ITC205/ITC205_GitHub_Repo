
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
public class Library implements Serializable {
	
	private static final String LIBRARY_FILE = "Library.obj";
	private static final int LOAN_LIMIT = 2;
	private static final int loanPeriod = 2;
	private static final double finePerDay = 1.0;
	private static final double maxFinesOwed = 1.0;
	private static final double damageFee = 2.0;
	
	private static Library self;
	private int bookId;
	private int memberId;
	private int loanId;
	private Date loanDate;
	
	private Map<Integer, Book> CATALOG;
	private Map<Integer, member> MEMBERS;
	private Map<Integer, loan> LOANS;
	private Map<Integer, loan> CURRENT_LOANS;
	private Map<Integer, Book> DAMAGED_BOOKS;
	

	private Library() {
		CATALOG = new HashMap<>();
		MEMBERS = new HashMap<>();
		LOANS = new HashMap<>();
		CURRENT_LOANS = new HashMap<>();
		DAMAGED_BOOKS = new HashMap<>();
		bookId = 1;
		memberId = 1;
		loanId = 1;
	}

	
	public static synchronized Library INSTANCE() {
		if (self == null) {
			Path PATH = Paths.get(LIBRARY_FILE);
			if (Files.exists(PATH)) {	
				try (ObjectInputStream LiF = new ObjectInputStream(new FileInputStream(LIBRARY_FILE));) {
			    
					self = (Library) LiF.readObject();
					Calendar.getInstance().setDate(self.loanDate);
					LiF.close();
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			else self = new Library();
		}
		return self;
	}

	
	public static synchronized void SAVE() {
		if (self != null) {
			self.loanDate = Calendar.getInstance().getDate();
			try (ObjectOutputStream LoF = new ObjectOutputStream(new FileOutputStream(LIBRARY_FILE));) {
				LoF.writeObject(self);
				LoF.flush();
				LoF.close();	
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	
	public int BookID() {
		return bookId;
	}
	
	
	public int MemberID() {
		return memberId;
	}
	
	
	private int NextBID() {
		return bookId++;
	}

	
	private int NextMID() {
		return memberId++;
	}

	
	private int NextLID() {
		return loanId++;
	}

	
	public List<member> MEMBERS() {		
		return new ArrayList<member>(MEMBERS.values()); 
	}


	public List<Book> BOOKS() {
		return new ArrayList<Book>(CATALOG.values());
	}


	public List<loan> CurrentLoans() {
		return new ArrayList<loan>(CURRENT_LOANS.values());
	}


	public member Add_mem(String lastName, String firstName, String email, int phoneNo) {		
		member member = new member(lastName, firstName, email, phoneNo, NextMID());
		MEMBERS.put(member.GeT_ID(), member);		
		return member;
	}

	
	public Book Add_book(String a, String t, String c) {
		Book b = new Book(a, t, c, NextBID());
		CATALOG.put(b.getBookId(), b);
		return b;
	}

	
	public member MEMBER(int memberId) {
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
		return LOAN_LIMIT;
	}

	
	public boolean MEMBER_CAN_BORROW(member member) {		
		if (member.Number_Of_Current_Loans() == LOAN_LIMIT)
			return false;
				
		if (member.Fines_OwEd() >= maxFinesOwed) 
			return false;
				
		for (loan loan : member.GeT_LoAnS()) 
			if (loan.OVer_Due()) 
				return false;
			
		return true;
	}

	
	public int Loans_Remaining_For_Member(member member) {		
		return LOAN_LIMIT - member.Number_Of_Current_Loans();
	}

	
	public loan ISSUE_LAON(Book book, member member) {
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
		member member = currentLoan.Member();
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
