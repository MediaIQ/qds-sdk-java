package com.qubole.qds.sdk.java.client;

import org.glassfish.jersey.message.internal.ReaderWriter;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.logging.Logger;

public class ErrorResponseFilter implements ClientResponseFilter {

    private static final Logger LOG = Logger.getLogger(ErrorResponseFilter.class.getName());

    @Override
    public void filter(final ClientRequestContext requestContext,
                       final ClientResponseContext responseContext) {
        try {
            // For non-200 response, log the custom error message.
            if (responseContext.getStatus() != Response.Status.OK.getStatusCode()) {
                if (responseContext.hasEntity()) {
                    InputStream in = responseContext.getEntityStream();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    if (in.available() > 0) {
                        ReaderWriter.writeTo(in, out);
                        byte[] responseEntity = out.toByteArray();
                        printEntity(responseEntity);
                        responseContext.setEntityStream(new ByteArrayInputStream(responseEntity));
                    }
                }
            }
        } catch (Exception e) {
            // Silently pass. We don't want anything to fail because of this filter.
            LOG.warning("Error while checking response code: " + e.getMessage());
        }
    }

    private void printEntity(byte[] entity) throws IOException {
        if (entity.length == 0)
            return;
        String error = new String(entity);
        LOG.severe(error);
        System.err.println(error);
    }

}
