package resource;

import java.net.URI;

import entity.Book;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import service.BookService;

@Path("/v1/books")
public class BookResource {

	@Inject
	private BookService bookService;
	
    @GET
    @Path("{id}")
    public Uni<Response> getBook(@PathParam("id") Long id) throws Exception {
        return bookService.findBookById(id)
                .onItem().ifNotNull().transform(book -> Response.ok(book).build())
                .onItem().ifNull().continueWith(Response.ok().status(Status.NOT_FOUND)::build);
    }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> getBooks() throws Exception {
		return bookService.getAllBooks()
				.onItem().transform(books -> Response.ok(books))
                .onItem().transform(Response.ResponseBuilder::build);
	}
	
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> addBook(Book book) throws Exception {
        return bookService.createBook(book)
                .onItem().transform(bookTemp -> URI.create("/v1/books/" + bookTemp.getId()))
                .onItem().transform(uri -> Response.created(uri))
                .onItem().transform(Response.ResponseBuilder::build);
    }
    
    @PUT
    @Path("{id}")
    public Uni<Response> updateBook(@PathParam("id") Long id, Book book) {
        return bookService.updateBook(id, book)
                .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
                .onItem().ifNull().continueWith(Response.ok().status(Status.NOT_FOUND)::build);
    }
    
    @DELETE
    @Path("{id}")
    public Uni<Response> delete(@PathParam("id") Long id) {
        return bookService.deleteBook(id)
                .onItem().transform(entity -> !entity ? Response.serverError().status(Status.NOT_FOUND).build()
                        : Response.ok().status(Status.OK).build());
    }
    
    @GET
    @Path("{id}/categories")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getCategoriesByBookId(@PathParam("id") Long id) {
    	return bookService.getAllCategoriesByBookId(id)
				.onItem().transform(books -> Response.ok(books))
                .onItem().transform(Response.ResponseBuilder::build);
    }

    @POST
    @Path("{id}/categories/{detailid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> addCategory(@PathParam("id") Long id, @PathParam("detailid") Long detailid) {
    	return bookService.addCategory(id, detailid)
    			.onItem().transform(bookCategory -> Response.ok(bookCategory).status(Status.CREATED))
    			.onItem().transform(Response.ResponseBuilder::build);
    }

    @DELETE
    @Path("{id}/categories/{detailid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> deleteCategory(@PathParam("id") Long id, @PathParam("detailid") Long detailid) {
    	return bookService.deleteCategory(id, detailid)
    			.onItem().transform(entity -> !entity ? Response.serverError().status(Status.NOT_FOUND).build()
                        : Response.ok().status(Status.OK).build());
    }
}
