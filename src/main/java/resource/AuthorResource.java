package resource;

import java.net.URI;

import entity.Author;
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
import service.AuthorService;

@Path("/v1/authors")
public class AuthorResource {

	@Inject
	private AuthorService authorService;
	
    @GET
    @Path("{id}")
    public Uni<Response> getAuthor(@PathParam("id") Long id) throws Exception {
        return authorService.findAuthorById(id)
                .onItem().ifNotNull().transform(author -> Response.ok(author).build())
                .onItem().ifNull().continueWith(Response.ok().status(Status.NOT_FOUND)::build);
    }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> getAuthors() throws Exception {
		return authorService.getAllAuthors()
				.onItem().transform(author -> Response.ok(author))
                .onItem().transform(Response.ResponseBuilder::build);
	}
	
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> addAuthor(Author author) throws Exception {
        return authorService.createAuthor(author)
                .onItem().transform(authorTemp -> URI.create("/v1/authors/" + authorTemp.getId()))
                .onItem().transform(uri -> Response.created(uri))
                .onItem().transform(Response.ResponseBuilder::build);
    }
    
    @PUT
    @Path("{id}")
    public Uni<Response> updateAuthor(@PathParam("id") Long id, Author author) {
        return authorService.updateAuthor(id, author)
                .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
                .onItem().ifNull().continueWith(Response.ok().status(Status.NOT_FOUND)::build);
    }
    
    @DELETE
    @Path("{id}")
    public Uni<Response> delete(@PathParam("id") Long id) {
        return authorService.deleteAuthor(id)
                .onItem().transform(entity -> !entity ? Response.serverError().status(Status.NOT_FOUND).build()
                        : Response.ok().status(Status.OK).build());
    }
    
    @GET
    @Path("{id}/books")
    public Uni<Response> getBooks(@PathParam("id") Long id) {
    	return authorService.getAllBooks(id)
				.onItem().transform(books -> Response.ok(books))
                .onItem().transform(Response.ResponseBuilder::build);
    }
    
}
