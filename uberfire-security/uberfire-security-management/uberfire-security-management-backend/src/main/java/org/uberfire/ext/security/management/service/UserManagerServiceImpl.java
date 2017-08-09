/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.service;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.security.management.BackendUserSystemManager;
import org.uberfire.ext.security.management.api.UserManager;
import org.uberfire.ext.security.management.api.UserManagerSettings;
import org.uberfire.ext.security.management.api.exception.NoImplementationAvailableException;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;
import org.uberfire.ext.security.management.api.service.UserManagerService;
import org.uberfire.ext.security.management.util.SecurityManagementUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

/**
 * <p>The UberFire service implementation for UsersManager API.</p>
 */
@Service
@ApplicationScoped
@Path("user")
public class UserManagerServiceImpl implements UserManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(UserManagerServiceImpl.class);

    @Inject
    private BackendUserSystemManager userSystemManager;
    
    private UserManager service;
    
    @PostConstruct
    public void init() {
        service = userSystemManager.users();
    }
    
    private UserManager getService() throws SecurityManagementException {
        if (!userSystemManager.isActive() || service == null) throw new NoImplementationAvailableException();
        return service;
    }

    @Override
    public void assignGroups(String username, Collection<String> groups)  {
        final UserManager serviceImpl = getService();
        serviceImpl.assignGroups(username, groups);
    }

    @Override
    public void assignRoles(String username, Collection<String> roles)  {
        final UserManager serviceImpl = getService();
        serviceImpl.assignRoles(username, roles);
    }

    @Override
    public void changePassword(String username, String newPassword)  {
        final UserManager serviceImpl = getService();
        serviceImpl.changePassword(username, newPassword);
    }

    @Override
    public SearchResponse<User> search(SearchRequest request)  {
        final UserManager serviceImpl = getService();

        // Delegate to the current service provider implementation.
        SearchResponse<User> response = null;
        try {
            if (request.getPage() == 0) throw new IllegalArgumentException("First page must be 1.");
            response = serviceImpl.search(request);
        } catch (RuntimeException e) {
            throw new SecurityManagementException(e);
        } 
        return response;        
    }

    @Override
    public User get(String identifier)  {
        final UserManager serviceImpl = getService();
        return serviceImpl.get(identifier);
    }

    @Override
    public User create(User entity)  {
        final UserManager serviceImpl = getService();
        return serviceImpl.create(entity);

    }

    @Override
    public User update(User entity)  {
        final UserManager serviceImpl = getService();
        return serviceImpl.update(entity);

    }

    @Override
    public void delete(String... identifiers)  {
        final UserManager serviceImpl = getService();
        serviceImpl.delete(identifiers);
    }

    @Override
    public UserManagerSettings getSettings() {
        final UserManager serviceImpl = getService();
        return serviceImpl.getSettings();
    }

    @Path("password/{username}")
    @PUT
    public void changePasswordByRest(
            @PathParam("username") String username,
            @QueryParam("password") String password)
    {
        changePassword(username, password);
    }

    @Path("roles/{username}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void assignRolesByRest(
            @PathParam("username") String username,
            Collection<String> roles)
    {
        assignRoles(username, roles);
    }

    @Path("groups/{username}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void assignGroupsByRest(
            @PathParam("username") String username,
            Collection<String> groups)
    {
        assignGroups(username, groups);
    }

    @Path("{username}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public User getByRest(@PathParam("username") String username)  {
        return get(username);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createByRest(String username) {
        create(SecurityManagementUtils.createUser(username));
    }

    @Path("{username}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateByRest(User user) {
        update(user);
    }

    @Path("{username}")
    @DELETE
    public void deleteByRest(@PathParam("username") String username) {
        delete(username);
    }

}
