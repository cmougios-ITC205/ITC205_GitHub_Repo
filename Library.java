
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
import java.util.Collection;

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
    private Map<Integer, Member> members;
    private Map<Integer, Loan> loans;
    private Map<Integer, Loan> currentLoans;
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
                try (FileInputStream libraryFileInput = new FileInputStream(LIBRARY_FILE);
                        ObjectInputStream libraryObjectInput = new ObjectInputStream(libraryFileInput);) {

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
            try (FileOutputStream libraryFileOutput = new FileOutputStream(LIBRARY_FILE);
                    ObjectOutputStream libraryObjectOutput = new ObjectOutputStream(libraryFileOutput);) {
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


    public List<Member> getMemberList() {
        Collection<Member> membersInMap = members.values();
        return new ArrayList<Member>(membersInMap);
    }


    public List<Book> getBookList() {
        Collection<Book> booksInMap = catalog.values();
        return new ArrayList<Book>(booksInMap);
    }


    public List<Loan> getCurrentLoansList() {
        Collection<Loan> loansInMap = currentLoans.values();
        return new ArrayList<Loan>(loansInMap);
    }


    public Member addMember(String lastName, String firstName, String email, int phoneNo) {
        int nextMemberId = this.getNextMemberId();
        Member newMember = new Member(lastName, firstName, email, phoneNo, nextMemberId);
        members.put(newMember.getId(), newMember);
        return newMember;
    }


    public Book addBook(String author, String title, String callNumber) {
        int nextBookId = this.getNextBookId();
        Book newBook = new Book(author, title, callNumber, nextBookId);
        catalog.put(newBook.getBookId(), newBook);
        return newBook;
    }


    public Member getMemberId(int memberId) {
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


    public boolean canMemberBorrow(Member currentMember) {
        if (currentMember.getNumberOfCurrentLoans() == LOAN_LIMIT)
            return false;

        if (currentMember.getFinesOwed() >= MAX_FINES_OWED)
            return false;

        for (Loan loanBeingAssessed : currentMember.getLoans())
            if (loanBeingAssessed.isOverDue())
                return false;

        return true;
    }


    public int getNumberOfLoansAvailable(Member currentMember) {
        return LOAN_LIMIT - currentMember.getNumberOfCurrentLoans();
    }


    public Loan issueLoan(Book currentBook, Member currentMember) {
        Date dueDate = Calendar.getInstance().getDueDate(LOAN_PERIOD);
        int nextLoanId = this.getNextLoanId();
        Loan newLoan = new Loan(nextLoanId, currentBook, currentMember, dueDate);
        currentMember.takeOutLoan(newLoan);
        currentBook.borrowBook();
        int newLoanId = newLoan.getBookLoan();
        loans.put(newLoanId, newLoan);
        int currentBookId = currentBook.getBookId();
        currentLoans.put(currentBookId, newLoan);
        return newLoan;
    }


    public Loan getLoanByBookId(int bookId) {
        if (currentLoans.containsKey(bookId)) {
            return currentLoans.get(bookId);
        }
        return null;
    }


    public double calculateOverDueFine(Loan currentLoan) {
        if (currentLoan.isOverDue()) {
            Date loanDueDate = currentLoan.getDueDate();
            long daysOverDue = Calendar.getInstance().getDaysDifference(loanDueDate);
            double fine = daysOverDue * FINE_PER_DAY;
            return fine;
        }
        return 0.0;
    }


    public void dischargeLoan(Loan currentLoan, boolean isDamaged) {
        Member loanee = currentLoan.getLoanMember();
        Book loanedBook  = currentLoan.getBook();

        double overDueFine = calculateOverDueFine(currentLoan);
        loanee.addFine(overDueFine);

        loanee.dischargeLoan(currentLoan);
        loanedBook.returnBook(isDamaged);

        int loanedBookId = loanedBook.getBookId();

        if (isDamaged) {
            loanee.addFine(DAMAGE_FEE);
            damagedBooks.put(loanedBookId, loanedBook);
        }
        currentLoan.setLoanStateToDischarged();
        currentLoans.remove(loanedBookId);
    }


    public void checkCurrentLoans() {
        for (Loan loanBeingChecked : currentLoans.values()) {
            loanBeingChecked.isOverDue();
        }
    }


    public void repairBook(Book currentBook) {
        int currentBookId = currentBook.getBookId();
        if (damagedBooks.containsKey(currentBookId)) {
            currentBook.repairBook();
            damagedBooks.remove(currentBookId);
        }
        else {
            throw new RuntimeException("Library: repairBook: Book is not damaged");
        }

    }

}
