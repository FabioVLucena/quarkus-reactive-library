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
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "book_category")
public class BookCategory extends PanacheEntityBase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false)
	private Book book;

	@ManyToOne
	@JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
	private Category category;
	
	@Column(name = "register_date", nullable = false, unique = false)
	private Date registerDate;

	@Column(name = "delete_date", nullable = true, unique = false)
	private Date deleteDate;
	
	public static Uni<BookCategory> addBookCategory(BookCategory bookCategory) {
		return Panache
				.withTransaction(bookCategory::persist)
					.replaceWith(bookCategory)
				.ifNoItem()
					.after(Duration.ofMillis(10000))
						.fail()
				.onFailure()
					.transform(t -> new IllegalStateException(t));
	}
	
	public static Uni<List<BookCategory>> getAllBookCategoryByBookId(Long bookId) {
		return BookCategory
				.list("book_id", bookId)
					.onItem().transform(entities -> entities.stream()
                        .map(entity -> (BookCategory) entity)
                        	.collect(Collectors.toList()))
				.ifNoItem()
					.after(Duration.ofMillis(10000))
						.fail()
				.onFailure()
					.recoverWithUni(failure -> {
						List<BookCategory> list = new ArrayList<BookCategory>(); 
						return Uni.createFrom().item(list);
					});
	}

	public static Uni<List<BookCategory>> getAllBookCategoryByAuthorId(Long categoryId) {
		return BookCategory
				.list("category_id", categoryId)
				.onItem().transform(entities -> entities.stream()
                        .map(entity -> (BookCategory) entity)
                        	.collect(Collectors.toList()))
				.ifNoItem()
					.after(Duration.ofMillis(10000))
						.fail()
				.onFailure()
					.recoverWithUni(failure -> {
						List<BookCategory> list = new ArrayList<BookCategory>(); 
						return Uni.createFrom().item(list);
					});
	}
	
	public static Uni<Boolean> deleteBookCategoryById(Long id) {
		return Panache.withTransaction(() -> deleteById(id));
	}
}
