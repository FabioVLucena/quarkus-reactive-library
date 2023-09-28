package service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import entity.Author;
import entity.Book;
import entity.BookAuthor;
import entity.BookCategory;
import entity.BookPublisher;
import entity.Category;
import entity.Publisher;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

@ApplicationScoped
public class BookService {

	public Uni<Book> findBookById(Long id) throws Exception {
		Uni<Book> bookUni = Book.findBookById(id);
		return bookUni;
	}
	
	public Uni<List<Book>> getAllBooks() throws Exception {
		Uni<List<Book>> bookList = Book.getAllBooks();
		return bookList;
	}
	
	public Uni<Book> createBook(Book book) throws Exception {
		validateTitle(book);
		
		Uni<Book> bookUni = Book.addBook(book);
		
		return bookUni;
	}
	
	public Uni<Book> updateBook(Long id, Book book) {
		validateTitle(book);
		
		Uni<Book> bookUni = Book.updateBook(id, book);

		return bookUni;
	}

	public Uni<Boolean> deleteBook(Long id) {
		BookCategory.deleteAllBookCategoryByBookId(id);
		
		Uni<Boolean> deleted = Book.deleteBookById(id);
		
		return deleted;
	}
	
	private void validateTitle(Book book) {
	    if (book == null || book.getTitle() == null) {
            throw new WebApplicationException("Book title was not set on request.", Status.BAD_REQUEST);
        }		
	}

	public Uni<List<Category>> getAllCategoriesByBookId(Long id) {
		Uni<List<BookCategory>> bookCategoryUniList = BookCategory.getAllBookCategoryByBookId(id);
		
		Uni<List<Category>> categoryUniList = bookCategoryUniList.onItem().transform(list -> {
			return list.stream()
					.map(BookCategory::getCategory)
					.collect(Collectors.toList());
		});
		
		return categoryUniList;
	}
	
	public Uni<BookCategory> addCategory(Long id, Long categoryId) {
		BookCategory bookCategory = new BookCategory();
		bookCategory.setBook(new Book(id));
		bookCategory.setCategory(new Category(categoryId));
		bookCategory.setRegisterDate(new Date());
		
		Uni<BookCategory> bookCategoryUni = BookCategory.addBookCategory(bookCategory);
		return bookCategoryUni;
	}
	
	public Uni<Boolean> removeCategory(Long id, Long categoryId) {
		Uni<Boolean> deleted = BookCategory.deleteBookCategoryByBookIdAndCategoryId(id, categoryId);
		return deleted;
	}
	
	public Uni<List<Author>> getAllAuthorsByBookId(Long id) {
		Uni<List<BookAuthor>> uniBookAuthorList = BookAuthor.getAllBookAuthorByBookId(id);
		
		Uni<List<Author>> uniAuthorList = uniBookAuthorList.onItem().transform(list -> {
			return list.stream()
					.map(BookAuthor::getAuthor)
					.collect(Collectors.toList());
		});
		
		return uniAuthorList;
	}
	
	public Uni<BookAuthor> addAuthor(Long id, Long authorId) {
		BookAuthor bookAuthor = new BookAuthor();
		bookAuthor.setBook(new Book(id));
		bookAuthor.setAuthor(new Author(authorId));
		bookAuthor.setRegisterDate(new Date());
		
		Uni<BookAuthor> uniBookAuthor = BookAuthor.addBookAuthor(bookAuthor);
		return uniBookAuthor;
	}
	
	public Uni<Boolean> removeAuthor(Long id, Long authorId) {
		Uni<Boolean> deleted = BookAuthor.deleteBookAuthorByBookIdAndAuthorId(id, authorId);
		return deleted;
	}
	
	public Uni<List<Publisher>> getAllPublishersByBookId(Long id) {
		Uni<List<BookPublisher>> uniBookPublisherList = BookPublisher.getAllBookPublisherByBookId(id);
		
		Uni<List<Publisher>> uniPublisherList = uniBookPublisherList.onItem().transform(list -> {
			return list.stream()
					.map(BookPublisher::getPublisher)
					.collect(Collectors.toList());
		});
		
		return uniPublisherList;
	}
	
	public Uni<BookPublisher> addPublisher(Long id, Long publisherId) {
		BookPublisher bookAuthor = new BookPublisher();
		bookAuthor.setBook(new Book(id));
		bookAuthor.setPublisher(new Publisher(publisherId));
		bookAuthor.setRegisterDate(new Date());
		
		Uni<BookPublisher> uniBookPublisher = BookPublisher.addBookPublisher(bookAuthor);
		return uniBookPublisher;
	}
	
	public Uni<Boolean> removePublisher(Long id, Long publisherId) {
		Uni<Boolean> deleted = BookPublisher.deleteBookPublisherByBookIdAndPublisherId(id, publisherId);
		return deleted;
	}
	
}
