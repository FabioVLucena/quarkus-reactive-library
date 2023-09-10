package service;

import java.util.List;

import entity.Author;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthorService {

	public Uni<Author> findAuthorById(Long id) throws Exception {
		Uni<Author> authorUni = Author.findAuthorById(id);
		return authorUni;
	}
	
	public Uni<List<Author>> getAllAuthors() throws Exception {
		Uni<List<Author>> authorList = Author.getAllAuthors();
		return authorList;
	}
	
	public Uni<Author> createAuthor(Author author) throws Exception {
		Uni<Author> authorUni = Author.addAuthor(author);
		
		return authorUni;
	}
	
	public Uni<Author> updateAuthor(Long id, Author author) {
		Uni<Author> authorUni = Author.updateAuthor(id, author);

		return authorUni;
	}

	public Uni<Boolean> deleteAuthor(Long id) {
		Uni<Boolean> deleted = Author.deleteAuthorById(id);
		
		return deleted;
	}
	
}
