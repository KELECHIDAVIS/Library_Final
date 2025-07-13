package book;

import javax.persistence.*;

import bookcopy.BookCopy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="book")
public class Book {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Column(name="title")
    private String title;
    @Column(name="isbn")
    private String ISBN;
    @Column(name="description")
    private String description;
    @Column(name="num_pages")
    private int numPages;
    @Column(name="publisher")
    private String publisher;
    @Column(name="publication_date")
    private LocalDate publicationDate;

    // if a book is deleted from the library's system the copies are as well 
    @OneToMany(mappedBy="book", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<BookCopy> copies;

    public Book(String title, String ISBN, String description, int numPages, String publisher, LocalDate publicationDate) {
        this.title = title;
        this.ISBN = ISBN;
        this.description = description;
        this.numPages = numPages;
        this.publisher = publisher;
        this.publicationDate = publicationDate;
    }
    public Book() {}

    public void createCopy(BookCopy copy) {
        if (copies == null) {
            copies = new ArrayList<>();
        }
        copies.add(copy);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumPages() {
        return numPages;
    }

    public void setNumPages(int numPages) {
        this.numPages = numPages;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public List<BookCopy> getCopies() {
        return copies;
    }

    public void setCopies(List<BookCopy> copies) {
        this.copies = copies;
    }
}
