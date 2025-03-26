package com.epita.controller;

import com.epita.controller.contracts.UserRequest;
import com.epita.controller.contracts.UserResponse;
import com.epita.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
@Path("/api/users")
public class UserController {

    @Inject
    UserService userService;

    @Inject
    Logger logger;

    /**
     * Retrieves a user by their tag.
     *
     * @param userTag the tag of the user
     * @return a Response containing the UserResponse if found, or an error status
     */
    @GET
    @Path("/getUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@HeaderParam("userTag") String userTag) {
        if (userTag == null || userTag.isEmpty()) {
            logger.warn("getUser - Invalid userTag");
            return Response.status(Response.Status.BAD_REQUEST).build(); // 400
        }

        logger.infof("Fetching user with tag: %s", userTag);
        UserResponse userResponse = userService.getUser(userTag);

        if (userResponse == null) {
            logger.warnf("User not found for tag: %s", userTag);
            return Response.status(Response.Status.NOT_FOUND).build(); // 404
        }

        return Response.ok(userResponse).build();
    }

    /**
     * Authenticates a user based on the provided request.
     *
     * @param userRequest the user request containing authentication details
     * @return a Response indicating the result of the authentication
     */
    @POST
    @Path("/auth")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authUser(UserRequest userRequest) {
        if (!isRequestValid(userRequest, true)) {
            logger.warn("authUser - Invalid request");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        logger.info("Authenticating user");
        Integer authResult = userService.authUser(userRequest);

        return switch (authResult) {
            case 404 -> {
                logger.warn("User not found during authentication");
                yield Response.status(Response.Status.NOT_FOUND).build();
            }
            case 401 -> {
                logger.warn("Unauthorized access attempt");
                yield Response.status(Response.Status.UNAUTHORIZED).build();
            }
            case 200 -> {
                logger.info("User authenticated successfully");
                yield Response.ok().build();
            }
            default -> {
                logger.warn("Internal server error during authentication");
                yield Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        };
    }

    /**
     * Creates a new user based on the provided request.
     *
     * @param userRequest the user request containing user details
     * @return a Response containing the created UserResponse or an error status
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(UserRequest userRequest) {
        if (!isRequestValid(userRequest, false)) {
            logger.warn("createUser - Invalid request");
            return Response.status(Response.Status.BAD_REQUEST).build(); // 400
        }

        logger.info("Creating user");
        UserResponse userCreated = userService.createUser(userRequest);

        if (userCreated != null) {
            logger.info("User created successfully");
            return Response.status(Response.Status.CREATED).entity(userCreated).build();
        }

        logger.warn("Conflict occurred during user creation");
        return Response.status(Response.Status.CONFLICT).build(); // 409 caused by tag not unique
    }

    /**
     * Updates an existing user based on the provided request.
     *
     * @param userRequest the user request containing updated user details
     * @return a Response indicating the result of the update operation
     */
    @PATCH
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(UserRequest userRequest) {
        if (!isRequestValid(userRequest, false)) {
            logger.warn("updateUser - Invalid request");
            return Response.status(Response.Status.BAD_REQUEST).build(); // 400
        }

        logger.info("Updating user");
        Boolean updateDone = userService.updateUser(userRequest);

        if (updateDone) {
            logger.info("User updated successfully");
            return Response.status(Response.Status.OK).build(); // 200
        }

        logger.warn("User not found during update");
        return Response.status(Response.Status.NOT_FOUND).build(); // 404 caused by user not found
    }

    /**
     * Deletes a user by their tag.
     *
     * @param userTag the tag of the user to delete
     * @return a Response containing the deleted UserResponse or an error status
     */
    @DELETE
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUser(@HeaderParam("userTag") String userTag) {
        if (userTag == null || userTag.isEmpty()) {
            logger.warn("deleteUser - Invalid userTag");
            return Response.status(Response.Status.BAD_REQUEST).build(); // 400
        }

        logger.infof("Deleting user with tag: %s", userTag);
        UserResponse userDeleted = userService.deleteUser(userTag);

        if (userDeleted == null) {
            logger.warnf("User not found for deletion with tag: %s", userTag);
            return Response.status(Response.Status.NOT_FOUND).build(); // 404 if user not found
        }

        logger.info("User deleted successfully");
        return Response.ok(userDeleted).build(); // 200
    }

    /**
     * Validates a user request.
     *
     * @param userRequest the user request to validate
     * @param isPasswordNeeded whether the password is required for validation
     * @return true if the request is valid, false otherwise
     */
    private Boolean isRequestValid(final UserRequest userRequest, final Boolean isPasswordNeeded) {
        if (userRequest == null) {
            return Boolean.FALSE;
        }

        String tag = userRequest.getTag();
        String pseudo = userRequest.getPseudo();
        String password = userRequest.getPassword();
        List<ObjectId> blockedList = userRequest.getBlockedUsers();

        if (tag == null || tag.isEmpty() || pseudo == null || pseudo.isEmpty()) {
            return Boolean.FALSE;
        }

        if (isPasswordNeeded && (password == null || password.isEmpty())) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }
}
