package com.emmenemoi.application.data;

import com.emmenemoi.application.data.service.FileSystemStorageService;
import com.vaadin.flow.server.RequestHandler;
import com.vaadin.flow.server.StreamResourceWriter;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class MDImagesRequestHandler implements RequestHandler {
    private static Logger logger = LoggerFactory.getLogger(MDImagesRequestHandler.class);
    protected final String baseImagesUrl;
    protected final FileSystemStorageService fileSystemStorageService;

    public MDImagesRequestHandler(StorageProperties properties, FileSystemStorageService fileSystemStorageService) {
        super();
        this.baseImagesUrl = properties.getImagesUrl();
        this.fileSystemStorageService = fileSystemStorageService;
    }

    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request,
                                 VaadinResponse response) throws IOException {
        VaadinServletRequest httpRequest = (VaadinServletRequest) request;
        boolean isImage = httpRequest.getContextPath().isEmpty()
                && httpRequest.getServletPath().isEmpty()
                && httpRequest.getPathInfo() != null
                && httpRequest.getPathInfo().startsWith(this.baseImagesUrl);

        if (!isImage) {
            return false;
        }

        // don't use here "try resource" syntax sugar because in case there is
        // an exception the {@code outputStream} will be closed before "catch"
        // block which sets the status code and this code will not have any
        // effect being called after closing the stream (see #8740).

        OutputStream outputStream = response.getOutputStream();
        try {
            Path image = fileSystemStorageService.load(httpRequest.getPathInfo().replace(this.baseImagesUrl, ""));
            logger.debug("serve {} from URL {}", image, httpRequest.getPathInfo());
            response.setContentType(Files.probeContentType(image));
            //response.setCacheTime(streamResource.getCacheTime());
            Files.copy(image, outputStream);
        } catch (Exception exception) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw exception;
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }

        return true;
    }
}
