package service;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import entity.Book;
import entity.BookCategory;
import entity.Category;
import io.smallrye.mutiny.Multi;
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
	
	public Uni<BookCategory> addCategory(Long bookId, Long categoryId) {
		BookCategory bookCategory = new BookCategory();
		bookCategory.setBook(new Book(bookId));
		bookCategory.setCategory(new Category(categoryId));
		bookCategory.setRegisterDate(new Date());
		
		Uni<BookCategory> bookCategoryUni = BookCategory.addBookCategory(bookCategory);
		return bookCategoryUni;
	}
	
	public Uni<Boolean> deleteCategory(Long bookId, Long categoryId) {
		Uni<Boolean> deleted = BookCategory.deleteBookCategoryByBookIdAndCategoryId(bookId, categoryId);
		return deleted;
	}
}
