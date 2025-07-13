package bookcopy;

import javax.persistence.*;

import book.Book;
import loan.Loan;

@Entity
@Table(name="book_copy")
public class BookCopy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int barcode;
    @Column(name="available")
    private boolean available;
    @Column(name="location")
    private String location;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name="loan_num")
    private Loan loan;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="book_id")
    private Book book;

    public BookCopy(String location , boolean isAvailable, Book book, Loan loan) {
        this.location = location;
        this.available = isAvailable;
        this.book = book;
        this.loan = loan; 
    }
    public BookCopy() {}
    public int getBarcode() {
        return barcode;
    }

    public void setBarcode(int barcode) {
        this.barcode = barcode;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
