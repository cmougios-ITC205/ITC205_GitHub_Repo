
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
            Path filePath = Paths.get(LIBRARY_FILE);
            if (Files.exists(filePath)) {
                try (ObjectInputStream libraryObjectInput = new ObjectInputStream(new FileInputStream(LIBRARY_FILE));) {

                    self = (Library) libraryObjectInput.readObject();
                    Calendar.getInstance().setDate(self.loanDate);
                    libraryObjectInput.close();
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else self = new Library();
        }
        return self;
    }


    public static synchronized void saveInstanceToFile() {
        if (self != null) {
            self.loanDate = Calendar.getInstance().getDate();
            try (ObjectOutputStream libraryObjectOutput = new ObjectOutputStream(new FileOutputStream(LIBRARY_FILE));) {
                libraryObjectOutput.writeObject(self);
                libraryObjectOutput.flush();
                libraryObjectOutput.close();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    public int getBookId() {
        return bookId;
    }


    public int getMemberId() {
        return memberId;
    }


    private int getNextBookId() {
        return bookId++;
    }


    private int getNextMemberId() {
        return memberId++;
    }


    private int getNextLoanId() {
        return loanId++;
    }


    public List<member> getMemberList() {
        return new ArrayList<member>(members.values());
    }


    public List<Book> getBookList() {
        return new ArrayList<Book>(catalog.values());
    }


    public List<loan> getCurrentLoansList() {
        return new ArrayList<loan>(currentLoans.values());
    }


    public member addMember(String lastName, String firstName, String email, int phoneNo) {
        member newMember = new member(lastName, firstName, email, phoneNo, getNextMemberId());
        members.put(newMember.GeT_ID(), newMember);
        return newMember;
    }


    public Book addBook(String a, String t, String c) {
        Book newBook = new Book(a, t, c, getNextBookId());
        catalog.put(newBook.getBookId(), newBook);
        return newBook;
    }


    public member getMemberId(int memberId) {
        if (members.containsKey(memberId))
            return members.get(memberId);
        return null;
    }


    public Book getBookId(int bookId) {
        if (catalog.containsKey(bookId))
            return catalog.get(bookId);
        return null;
    }


    public int getLoanLimit() {
        return LOAN_LIMIT;
    }


    public boolean canMemberBorrow(member currentMember) {
        if (currentMember.Number_Of_Current_Loans() == LOAN_LIMIT)
            return false;

        if (currentMember.Fines_OwEd() >= MAX_FINES_OWED)
            return false;

        for (loan loanBeingAssessed : currentMember.GeT_LoAnS())
            if (loanBeingAssessed.OVer_Due())
                return false;

        return true;
    }


    public int getNumberOfLoansAvailable(member currentMember) {
        return LOAN_LIMIT - currentMember.Number_Of_Current_Loans();
    }


    public loan issueLoan(Book currentBook, member currentMember) {
        Date dueDate = Calendar.getInstance().getDueDate(LOAN_PERIOD);
        loan newLoan = new loan(getNextLoanId(), currentBook, currentMember, dueDate);
        currentMember.Take_Out_Loan(newLoan);
        currentBook.borrowBook();
        loans.put(newLoan.ID(), newLoan);
        currentLoans.put(currentBook.getBookId(), newLoan);
        return newLoan;
    }


    public loan getLoanByBookId(int bookId) {
        if (currentLoans.containsKey(bookId)) {
            return currentLoans.get(bookId);
        }
        return null;
    }


    public double calculateOverDueFine(loan currentLoan) {
        if (currentLoan.OVer_Due()) {
            long daysOverDue = Calendar.getInstance().getDaysDifference(currentLoan.Get_Due_Date());
            double fine = daysOverDue * FINE_PER_DAY;
            return fine;
        }
        return 0.0;
    }


    public void dischargeLoan(loan currentLoan, boolean isDamaged) {
        member loanee = currentLoan.Member();
        Book loanedBook  = currentLoan.Book();

        double overDueFine = calculateOverDueFine(currentLoan);
        loanee.Add_Fine(overDueFine);

        loanee.dIsChArGeLoAn(currentLoan);
        loanedBook.returnBook(isDamaged);
        if (isDamaged) {
            loanee.Add_Fine(DAMAGE_FEE);
            damagedBooks.put(loanedBook.getBookId(), loanedBook);
        }
        currentLoan.DiScHaRgE();
        currentLoans.remove(loanedBook.getBookId());
    }


    public void checkCurrentLoans() {
        for (loan loanBeingChecked : currentLoans.values()) {
            loanBeingChecked.checkOverDue();
        }
    }


    public void repairBook(Book currentBook) {
        if (damagedBooks.containsKey(currentBook.getBookId())) {
            currentBook.repairBook();
            damagedBooks.remove(currentBook.getBookId());
        }
        else {
            throw new RuntimeException("Library: repairBook: Book is not damaged");
        }

    }


}
