package org.uberfire.ext.security.management.service;

import org.jboss.errai.marshalling.client.Marshalling;
import org.jboss.errai.security.shared.api.identity.UserImpl;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class UserMessageBodyWriter implements MessageBodyWriter<UserImpl> {
    @Override
    public boolean isWriteable(
            Class<?> aClass,
            Type type,
            Annotation[] annotations,
            MediaType mediaType)
    {
        return aClass == UserImpl.class;
    }

    @Override
    public long getSize(
            UserImpl user,
            Class<?> aClass,
            Type type,
            Annotation[] annotations,
            MediaType mediaType)
    {
        return -1;
    }

    @Override
    public void writeTo(
            UserImpl user,
            Class<?> aClass,
            Type type,
            Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String,
            Object> multivaluedMap,
            OutputStream outputStream)
        throws IOException, WebApplicationException
    {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        writer.write(Marshalling.toJSON(user));
        writer.flush();
    }
}
