package service;

import java.util.List;

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
	
}
