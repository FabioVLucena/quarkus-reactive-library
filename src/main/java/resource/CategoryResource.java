package resource;

import java.net.URI;

import entity.Category;
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
import service.CategoryService;

@Path("/v1/categories")
public class CategoryResource {

	@Inject
	private CategoryService categoryService;
	
    @GET
    @Path("{id}")
    public Uni<Response> getCategory(@PathParam("id") Long id) throws Exception {
        return categoryService.findCategoryById(id)
                .onItem().ifNotNull().transform(category -> Response.ok(category).build())
                .onItem().ifNull().continueWith(Response.ok().status(Status.NOT_FOUND)::build);
    }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> getCategories() throws Exception {
		return categoryService.getAllCategories()
				.onItem().transform(category -> Response.ok(category))
                .onItem().transform(Response.ResponseBuilder::build);
	}
	
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> addCategory(Category category) throws Exception {
        return categoryService.createCategory(category)
                .onItem().transform(categoryTemp -> URI.create("/v1/categories/" + categoryTemp.getId()))
                .onItem().transform(uri -> Response.created(uri))
                .onItem().transform(Response.ResponseBuilder::build);
    }
    
    @PUT
    @Path("{id}")
    public Uni<Response> updateCategory(@PathParam("id") Long id, Category category) {
        return categoryService.updateCategory(id, category)
                .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
                .onItem().ifNull().continueWith(Response.ok().status(Status.NOT_FOUND)::build);
    }
    
    @DELETE
    @Path("{id}")
    public Uni<Response> delete(@PathParam("id") Long id) {
        return categoryService.deleteCategory(id)
                .onItem().transform(entity -> !entity ? Response.serverError().status(Status.NOT_FOUND).build()
                        : Response.ok().status(Status.OK).build());
    }
    
    @GET
    @Path("{id}/books")
    public Uni<Response> getBooks(@PathParam("id") Long id) {
    	return categoryService.getAllBooks(id)
				.onItem().transform(books -> Response.ok(books))
                .onItem().transform(Response.ResponseBuilder::build);
    }
    
}
