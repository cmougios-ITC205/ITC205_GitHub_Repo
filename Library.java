
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
	private static final int LOAN_PERIOD = 2;
	private static final double FINE_PER_DAY = 1.0;
	private static final double MAX_FINES_OWED = 1.0;
	private static final double DAMAGE_FEE = 2.0;
	
	private static Library self;
	private int bookId;
	private int memberId;
	private int loanId;
	private Date loanDate;
	
	private Map<Integer, Book> catalog;
	private Map<Integer, member> members;
	private Map<Integer, loan> loans;
	private Map<Integer, loan> currentLoans;
	private Map<Integer, Book> damagedBooks;
	

	private Library() {
		catalog = new HashMap<>();
		members = new HashMap<>();
		loans = new HashMap<>();
		currentLoans = new HashMap<>();
		damagedBooks = new HashMap<>();
		bookId = 1;
		memberId = 1;
		loanId = 1;
	}

	
	public static synchronized Library getInstance() {
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
		return new ArrayList<member>(members.values());
	}


	public List<Book> BOOKS() {
		return new ArrayList<Book>(catalog.values());
	}


	public List<loan> CurrentLoans() {
		return new ArrayList<loan>(currentLoans.values());
	}


	public member Add_mem(String lastName, String firstName, String email, int phoneNo) {		
		member member = new member(lastName, firstName, email, phoneNo, NextMID());
		members.put(member.GeT_ID(), member);
		return member;
	}

	
	public Book Add_book(String a, String t, String c) {
		Book b = new Book(a, t, c, NextBID());
		catalog.put(b.getBookId(), b);
		return b;
	}

	
	public member MEMBER(int memberId) {
		if (members.containsKey(memberId))
			return members.get(memberId);
		return null;
	}

	
	public Book Book(int bookId) {
		if (catalog.containsKey(bookId))
			return catalog.get(bookId);
		return null;
	}

	
	public int LOAN_LIMIT() {
		return LOAN_LIMIT;
	}

	
	public boolean MEMBER_CAN_BORROW(member member) {		
		if (member.Number_Of_Current_Loans() == LOAN_LIMIT)
			return false;
				
		if (member.Fines_OwEd() >= MAX_FINES_OWED)
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
		Date dueDate = Calendar.getInstance().getDueDate(LOAN_PERIOD);
		loan loan = new loan(NextLID(), book, member, dueDate);
		member.Take_Out_Loan(loan);
		book.borrowBook();
		loans.put(loan.ID(), loan);
		currentLoans.put(book.getBookId(), loan);
		return loan;
	}
	
	
	public loan LOAN_BY_BOOK_ID(int bookId) {
		if (currentLoans.containsKey(bookId)) {
			return currentLoans.get(bookId);
		}
		return null;
	}

	
	public double CalculateOverDueFine(loan loan) {
		if (loan.OVer_Due()) {
			long daysOverDue = Calendar.getInstance().getDaysDifference(loan.Get_Due_Date());
			double fine = daysOverDue * FINE_PER_DAY;
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
			member.Add_Fine(DAMAGE_FEE);
			damagedBooks.put(book.getBookId(), book);
		}
		currentLoan.DiScHaRgE();
		currentLoans.remove(book.getBookId());
	}


	public void checkCurrentLoans() {
		for (loan loan : currentLoans.values()) {
			loan.checkOverDue();
		}		
	}


	public void Repair_BOOK(Book currentBook) {
		if (damagedBooks.containsKey(currentBook.getBookId())) {
			currentBook.repairBook();
			damagedBooks.remove(currentBook.getBookId());
		}
		else {
			throw new RuntimeException("Library: repairBook: Book is not damaged");
		}
		
	}
	
	
}
