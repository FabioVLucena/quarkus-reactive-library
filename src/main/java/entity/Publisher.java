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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "publisher")
public class Publisher extends PanacheEntityBase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name", nullable = false, unique = false)
	private String name;
	
	public static Uni<Publisher> findPublisherById(Long id) {
		return findById(id);
	}
	
	public static Uni<List<PanacheEntityBase>> getAllPublishers() {
		return Publisher
				.listAll(Sort.by("name"))
				.ifNoItem()
					.after(Duration.ofMillis(10000))
						.fail()
				.onFailure()
					.recoverWithUni(failure -> {
						List<PanacheEntityBase> list = new ArrayList<PanacheEntityBase>(); 
						return Uni.createFrom().item(list);
					});
	}
	
	public static Uni<Publisher> addPublisher(Publisher publisher) {
		return Panache
				.withTransaction(publisher::persist)
					.replaceWith(publisher)
				.ifNoItem()
					.after(Duration.ofMillis(10000))
						.fail()
				.onFailure()
					.transform(t -> new IllegalStateException(t));
	}

	public static Uni<Publisher> updatePublisher(Long id, Publisher publisher) {
		return Panache
				.withTransaction(() -> findPublisherById(id))
					.onItem()
						.ifNotNull()
							.transform(entity -> {
								entity.name = publisher.name;
								return entity;
							})
					.onFailure()
						.recoverWithNull();
	}
	
	public static Uni<Boolean> deletePublisherById(Long id) {
		return Panache.withTransaction(() -> deleteById(id));
	}
	
}
