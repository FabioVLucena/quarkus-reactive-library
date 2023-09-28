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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Entity(name = "author")
public class Author extends PanacheEntityBase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name", nullable = false, unique = false)
	private String name;
	
	public Author(Long id) {
		this.id = id;
	}
	
	public static Uni<Author> findAuthorById(Long id) {
		return findById(id);
	}

	public static Uni<List<Author>> getAllAuthors() {
		return Author
				.listAll(Sort.by("name"))
				.onItem().transform(entities -> entities.stream()
                        .map(entity -> (Author) entity)
                        	.collect(Collectors.toList()))
				.ifNoItem()
					.after(Duration.ofMillis(10000))
						.fail()
				.onFailure()
					.recoverWithUni(failure -> {
						List<Author> list = new ArrayList<Author>(); 
						return Uni.createFrom().item(list);
					});
	}
	
	public static Uni<Author> addAuthor(Author author) {
		return Panache
				.withTransaction(author::persist)
					.replaceWith(author)
				.ifNoItem()
					.after(Duration.ofMillis(10000))
						.fail()
				.onFailure()
					.transform(t -> new IllegalStateException(t));
	}
	
	public static Uni<Author> updateAuthor(Long id, Author author) {
		return Panache
				.withTransaction(() -> findAuthorById(id)
						.onItem()
							.ifNotNull()
								.transform(entity -> {
									entity.name = author.name;
									return entity;
								}))
						.onFailure()
							.recoverWithNull();
	}
	
	public static Uni<Boolean> deleteAuthorById(Long id) {
		return Panache.withTransaction(() -> deleteById(id));
	}

}
