package entity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@Entity(name = "book_publisher")
public class BookPublisher extends PanacheEntityBase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false)
	private Book book;

	@ManyToOne
	@JoinColumn(name = "publisher_id", referencedColumnName = "id", nullable = false)
	private Publisher publisher;
	
	@Column(name = "register_date", nullable = false, unique = false)
	private Date registerDate;
	
	public static Uni<BookPublisher> addBookPublisher(BookPublisher bookPublisher) {
		return Panache
				.withTransaction(bookPublisher::persist)
					.replaceWith(bookPublisher)
				.ifNoItem()
					.after(Duration.ofMillis(10000))
						.fail()
				.onFailure()
					.transform(t -> new IllegalStateException(t));
	}
	
	public static Uni<List<BookPublisher>> getAllBookPublisherByBookId(Long bookId) {
		return BookPublisher
				.list("book.id", bookId)
					.onItem().transform(entities -> entities.stream()
                        .map(entity -> (BookPublisher) entity)
                        	.collect(Collectors.toList()))
				.ifNoItem()
					.after(Duration.ofMillis(10000))
						.fail()
				.onFailure()
					.recoverWithUni(failure -> {
						List<BookPublisher> list = new ArrayList<BookPublisher>(); 
						return Uni.createFrom().item(list);
					});
	}

	public static Uni<List<BookPublisher>> getAllBookPublisherByAuthorId(Long publisherId) {
		return BookPublisher
				.list("publisher.id", publisherId)
				.onItem().transform(entities -> entities.stream()
                        .map(entity -> (BookPublisher) entity)
                        	.collect(Collectors.toList()))
				.ifNoItem()
					.after(Duration.ofMillis(10000))
						.fail()
				.onFailure()
					.recoverWithUni(failure -> {
						List<BookPublisher> list = new ArrayList<BookPublisher>(); 
						return Uni.createFrom().item(list);
					});
	}
	
	public static Uni<Boolean> deleteBookPublisherById(Long id) {
		return Panache.withTransaction(() -> deleteById(id));
	}

	public static Uni<BookPublisher> getBookAuthorByBookIdAndPublisherId(Long bookId, Long publisherId) {
		Map<String, Object> params = new HashMap<>();
		params.put("bookId", bookId);
		params.put("publisherId", publisherId);
		
		return BookPublisher.find("book.id = :bookId and publisher.id = :publisherId", params)
				.firstResult()
					.onItem()
						.transform(entitie -> (BookPublisher) entitie)
					.ifNoItem()
						.after(Duration.ofMillis(10000))
							.fail()
					.onFailure()
						.recoverWithUni(failure -> {
							return Uni.createFrom().item(new BookPublisher());
						});
	}

	public static Uni<Boolean> deleteBookPublisherByBookIdAndPublisherId(Long bookId, Long publisherId) {
		Uni<Boolean> deleted = BookPublisher.getBookAuthorByBookIdAndPublisherId(bookId, publisherId)
				.onItem()
					.ifNotNull()
						.transformToUni(entity -> deleteBookPublisherById(entity.getId()))
				.onItem()
					.ifNull()
						.continueWith(true);
		
		return deleted;
	}
}
