package resource;

import java.net.URI;

import entity.Publisher;
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
import service.PublisherService;

@Path("/v1/publishers")
public class PublisherResource {

	@Inject
	private PublisherService publisherService;
	
    @GET
    @Path("{id}")
    public Uni<Response> getPublisher(@PathParam("id") Long id) throws Exception {
        return publisherService.findPublisherById(id)
                .onItem().ifNotNull().transform(publisher -> Response.ok(publisher).build())
                .onItem().ifNull().continueWith(Response.ok().status(Status.NOT_FOUND)::build);
    }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> getPublishers() throws Exception {
		return publisherService.getAllPublishers()
				.onItem().transform(publisher -> Response.ok(publisher))
                .onItem().transform(Response.ResponseBuilder::build);
	}
	
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> addPublisher(Publisher publisher) throws Exception {
        return publisherService.createPublisher(publisher)
                .onItem().transform(publisherTemp -> URI.create("/v1/publishers/" + publisherTemp.getId()))
                .onItem().transform(uri -> Response.created(uri))
                .onItem().transform(Response.ResponseBuilder::build);
    }
    
    @PUT
    @Path("{id}")
    public Uni<Response> updatePublisher(@PathParam("id") Long id, Publisher publisher) {
        return publisherService.updatePublisher(id, publisher)
                .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
                .onItem().ifNull().continueWith(Response.ok().status(Status.NOT_FOUND)::build);
    }
    
    @DELETE	
    @Path("{id}")
    public Uni<Response> delete(@PathParam("id") Long id) {
        return publisherService.deletePublisher(id)
                .onItem().transform(entity -> !entity ? Response.serverError().status(Status.NOT_FOUND).build()
                        : Response.ok().status(Status.OK).build());
    }
}
