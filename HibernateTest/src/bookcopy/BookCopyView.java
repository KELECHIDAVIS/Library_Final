package bookcopy;

import java.time.LocalDate;

// to cleanly get book search results 
public class BookCopyView {
    private int barcode;
    private boolean available;
    private String location;
    private Integer loanNum;
    private Integer bookId;
    private String bookTitle; 
    private String studentName;
    private LocalDate borrowingDate;
    private LocalDate dueDate;

    public BookCopyView(int barcode, boolean available, String location,
                        Integer loanNum, Integer bookId,String bookTitle, String studentName,
                        LocalDate borrowingDate, LocalDate dueDate) {
        this.barcode = barcode;
        this.available = available;
        this.location = location;
        this.loanNum = loanNum;
        this.bookId = bookId;
        this.studentName = studentName;
        this.borrowingDate = borrowingDate;
        this.dueDate = dueDate;
        this.bookTitle= bookTitle; 
    }

    
    public String getBookTitle() {
		return bookTitle;
	}


	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}


	public int getBarcode() { return barcode; }
    public boolean isAvailable() { return available; }
    public String getLocation() { return location; }
    public Integer getLoanNum() { return loanNum; }
    public Integer getBookId() { return bookId; }
    public String getStudentName() { return studentName; }
    public LocalDate getBorrowingDate() { return borrowingDate; }
    public LocalDate getDueDate() { return dueDate; }
}
