package entity;

import java.time.Duration;
import java.util.Date;

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
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
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
	
	public static Uni<Boolean> deleteBookAuthorById(Long id) {
		return Panache.withTransaction(() -> deleteById(id));
	}
}
