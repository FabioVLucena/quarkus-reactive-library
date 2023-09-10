package entity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
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

	public static Uni<Book> findBookById(Long id) {
		return findById(id);
	}
	
	public static Uni<List<Book>> getAllBooks() {
		return Book
				.listAll(Sort.by("title"))
				.onItem().transform(entities -> entities.stream()
                        .map(entity -> (Book) entity)
                        	.collect(Collectors.toList()))
				.ifNoItem()
					.after(Duration.ofMillis(10000))
						.fail()
				.onFailure()
					.recoverWithUni(failure -> {
						List<Book> list = new ArrayList<Book>(); 
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
									return entity;
								}))
						.onFailure()
							.recoverWithNull();
	}

	public static Uni<Boolean> deleteBookById(Long id) {
		return Panache.withTransaction(() -> deleteById(id));
	}
	
}
