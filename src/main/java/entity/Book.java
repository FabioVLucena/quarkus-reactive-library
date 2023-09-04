package entity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Builder;

@Builder
@Entity(name = "book")
public class Book extends PanacheEntityBase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "title", nullable = false, unique = true)
	private String title;

	@Column(name = "subtitle", nullable = true, unique = false)
	private String subtitle;
	
	@Column(name = "sinopse", nullable = true, unique = false)
	private String sinopse;

	@Column(name = "quantity", nullable = false, unique = false)
	private Integer quantity;

	@Column(name = "available_quantity", nullable = true, unique = false)
	private Integer availableQuantity;

	@Column(name = "publisher_id", nullable = true, unique = false)
	private Publisher publisher; 

	@OneToMany(mappedBy = "book_category")
	private List<Category> categoryList;
	
	@OneToMany(mappedBy = "book_author")
	private List<Author> authorList;

	@OneToMany(mappedBy = "book_publisher")
	private List<Publisher> publisherList;
	
	public static Uni<Book> findBookById(Long id) {
		return findById(id);
	}
	
	public static Uni<Book> findAllBooksByAuthorId(Long authorId) {
		return null;
	}
	
	public static Uni<List<PanacheEntityBase>> getAllBooks() {
		return Book
				.listAll(Sort.by("title"))
				.ifNoItem()
					.after(Duration.ofMillis(10000))
						.fail()
				.onFailure()
					.recoverWithUni(failure -> {
						List<PanacheEntityBase> list = new ArrayList<PanacheEntityBase>(); 
						return Uni.createFrom().item(list);
					});
	}

	public static Uni<Book> addBook(Book book) {
		return Panache
				.withTransaction(book::persist)
					.replaceWith(book)
				.ifNoItem()
					.after(Duration.ofMillis(10000))
						.fail()
				.onFailure()
					.transform(t -> new IllegalStateException(t));
	}

	public static Uni<Book> updateBook(Long id, Book book) {
		return Panache
				.withTransaction(() -> findBookById(id)
						.onItem()
							.ifNotNull()
								.transform(entity -> {
									entity.title = book.title;
									entity.subtitle = book.subtitle;
									entity.sinopse = book.sinopse;
									entity.quantity = book.quantity;
									entity.availableQuantity = book.availableQuantity;
									entity.publisher = book.publisher;
									return entity;
								}))
						.onFailure()
							.recoverWithNull();
	}

	public static Uni<Boolean> deleteBookById(Long id) {
		return Panache.withTransaction(() -> deleteById(id));
	}
	
}
