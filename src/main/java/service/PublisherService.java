package service;

import java.util.List;
import java.util.stream.Collectors;

import entity.Book;
import entity.BookPublisher;
import entity.Publisher;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PublisherService {

	public Uni<Publisher> findPublisherById(Long id) throws Exception {
		Uni<Publisher> publisherUni = Publisher.findPublisherById(id);
		return publisherUni;
	}
	
	public Uni<List<Publisher>> getAllPublishers() throws Exception {
		Uni<List<Publisher>> publisherList = Publisher.getAllPublishers();
		return publisherList;
	}
	
	public Uni<Publisher> createPublisher(Publisher author) throws Exception {
		Uni<Publisher> publisherUni = Publisher.addPublisher(author);
		
		return publisherUni;
	}
	
	public Uni<Publisher> updatePublisher(Long id, Publisher author) {
		Uni<Publisher> publisherUni = Publisher.updatePublisher(id, author);

		return publisherUni;
	}

	public Uni<Boolean> deletePublisher(Long id) {
		Uni<Boolean> deleted = Publisher.deletePublisherById(id);
		
		return deleted;
	}
	
	public Uni<List<Book>> getAllBooks(Long id) {
		Uni<List<BookPublisher>> uniBookPublisherList = BookPublisher.getAllBookPublisherByBookId(id);
		
		Uni<List<Book>> uniBookList = uniBookPublisherList.onItem().transform(list -> {
			return list.stream()
					.map(BookPublisher::getBook)
					.collect(Collectors.toList());
		});
		
		return uniBookList;
	}
	
}
