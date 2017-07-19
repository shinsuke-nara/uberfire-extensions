package org.uberfire.ext.security.management.service;

import org.jboss.errai.marshalling.client.Marshalling;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class UserMessageBodyReader implements MessageBodyReader<User> {

    @Override
    public boolean isReadable(
            Class<?> aClass,
            Type type,
            Annotation[] annotations,
            MediaType mediaType)
    {
        return User.class.isAssignableFrom(aClass);
    }

    @Override
    public User readFrom(
            Class<User> aClass,
            Type type,
            Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String,
            String> multivaluedMap,
            InputStream inputStream)
        throws IOException, WebApplicationException
    {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream));
        StringBuffer buffer = new StringBuffer();
        for (String line = reader.readLine();
             line != null;
             line = reader.readLine())
        {
            buffer.append(line);
        }
        return (User)Marshalling.fromJSON(buffer.toString(), UserImpl.class);
    }
}
