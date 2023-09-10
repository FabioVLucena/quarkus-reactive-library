package entity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Entity(name = "book_author")
public class BookAuthor extends PanacheEntityBase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false)
	private Book book;

	@ManyToOne
	@JoinColumn(name = "author_id", referencedColumnName = "id", nullable = false)
	private Author author;
	
	@Column(name = "register_date", nullable = false, unique = false)
	private Date registerDate;

	@Column(name = "delete_date", nullable = true, unique = false)
	private Date deleteDate;
	
	public static Uni<BookAuthor> addBookAuthor(BookAuthor bookAuthor) {
		return Panache
				.withTransaction(bookAuthor::persist)
					.replaceWith(bookAuthor)
				.ifNoItem()
					.after(Duration.ofMillis(10000))
						.fail()
				.onFailure()
					.transform(t -> new IllegalStateException(t));
	}
	
	public static Uni<List<BookAuthor>> getAllBookAuthorByBookId(Long bookId) {
		return BookAuthor
				.list("book_id", bookId)
					.onItem().transform(entities -> entities.stream()
                        .map(entity -> (BookAuthor) entity)
                        	.collect(Collectors.toList()))
				.ifNoItem()
					.after(Duration.ofMillis(10000))
						.fail()
				.onFailure()
					.recoverWithUni(failure -> {
						List<BookAuthor> list = new ArrayList<BookAuthor>(); 
						return Uni.createFrom().item(list);
					});
	}

	public static Uni<List<BookAuthor>> getAllBookAuthorByAuthorId(Long authorId) {
		return BookAuthor
				.list("author_id", authorId)
				.onItem().transform(entities -> entities.stream()
                        .map(entity -> (BookAuthor) entity)
                        	.collect(Collectors.toList()))
				.ifNoItem()
					.after(Duration.ofMillis(10000))
						.fail()
				.onFailure()
					.recoverWithUni(failure -> {
						List<BookAuthor> list = new ArrayList<BookAuthor>(); 
						return Uni.createFrom().item(list);
					});
	}
	
	public static Uni<Boolean> deleteBookAuthorById(Long id) {
		return Panache.withTransaction(() -> deleteById(id));
	}
}
