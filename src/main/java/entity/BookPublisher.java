package entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;

@Builder
@Entity(name = "book_publisher")
public class BookPublisher {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne
	private Book book;

	@ManyToOne
	private Publisher publisher;
	
	@Column(name = "register_date", nullable = false, unique = false)
	private Date registerDate;

	@Column(name = "delete_date", nullable = true, unique = false)
	private Date deleteDate;
	
}
