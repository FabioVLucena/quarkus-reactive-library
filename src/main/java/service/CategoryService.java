package service;

import java.util.List;
import java.util.stream.Collectors;

import entity.Book;
import entity.BookCategory;
import entity.Category;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CategoryService {

	public Uni<Category> findCategoryById(Long id) throws Exception {
		Uni<Category> categoryUni = Category.findCategoryById(id);
		return categoryUni;
	}
	
	public Uni<List<Category>> getAllCategories() throws Exception {
		Uni<List<Category>> categoryList = Category.getAllCategories();
		return categoryList;
	}
	
	public Uni<Category> createCategory(Category category) throws Exception {
		Uni<Category> categoryUni = Category.addCategory(category);
		
		return categoryUni;
	}
	
	public Uni<Category> updateCategory(Long id, Category category) {
		Uni<Category> categoryUni = Category.updateCategory(id, category);

		return categoryUni;
	}

	public Uni<Boolean> deleteCategory(Long id) {
		Uni<Boolean> deleted = Category.deleteCategoryById(id);
		
		return deleted;
	}
	
	public Uni<List<Book>> getAllBooks(Long id) {
		Uni<List<BookCategory>> uniBookCategoryList = BookCategory.getAllBookCategoryByCategoryId(id);
		
		Uni<List<Book>> uniBookList = uniBookCategoryList.onItem().transform(list -> {
			return list.stream()
					.map(BookCategory::getBook)
					.collect(Collectors.toList());
		});
		
		return uniBookList;
	}
	
}
