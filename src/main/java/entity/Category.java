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
import lombok.Builder;

@Builder
@Entity(name = "category")
public class Category extends PanacheEntityBase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "description", nullable = false, unique = false)
	private String description;
	
	public static Uni<Category> findCategoryById(Long id) {
		return findById(id);
	}
	
	public static Uni<List<PanacheEntityBase>> getAllCategorys() {
		return Category
				.listAll(Sort.by("description"))
				.ifNoItem()
					.after(Duration.ofMillis(10000))
						.fail()
				.onFailure()
					.recoverWithUni(failure -> {
						List<PanacheEntityBase> list = new ArrayList<PanacheEntityBase>(); 
						return Uni.createFrom().item(list);
					});
	}
	
	public static Uni<Category> addCategory(Category category) {
		return Panache
				.withTransaction(category::persist)
					.replaceWith(category)
				.ifNoItem()
					.after(Duration.ofMillis(10000))
						.fail()
				.onFailure()
					.transform(t -> new IllegalStateException(t));
	}

	public static Uni<Category> updateCategory(Long id, Category category) {
		return Panache
				.withTransaction(() -> findCategoryById(id))
					.onItem()
						.ifNotNull()
							.transform(entity -> {
								entity.description = category.description;
								return entity;
							})
					.onFailure()
						.recoverWithNull();
	}

	public static Uni<Boolean> deleteCategoryById(Long id) {
		return Panache.withTransaction(() -> deleteById(id));
	}
	
}
