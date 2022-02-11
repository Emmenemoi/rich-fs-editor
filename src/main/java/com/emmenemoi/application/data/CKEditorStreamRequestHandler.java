package com.emmenemoi.application.data;

import com.emmenemoi.application.components.CKEditor;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.communication.StreamReceiverHandler;
import com.vaadin.flow.server.communication.StreamRequestHandler;
import com.vaadin.flow.shared.ApplicationConstants;
import elemental.json.JsonObject;
import elemental.json.impl.JreJsonFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CKEditorStreamRequestHandler extends StreamRequestHandler {

    public CKEditorStreamRequestHandler(StorageProperties properties) throws URISyntaxException {
        super(new CKEditorStreamReceiverHandler(properties));

    }

    protected static class CKEditorStreamReceiverHandler extends StreamReceiverHandler {

        protected URI imagesBaseUrl;

        public CKEditorStreamReceiverHandler(StorageProperties properties) throws URISyntaxException {
            super();
            this.imagesBaseUrl = new URI(properties.getImagesUrl()+"/");
        }


        protected void sendUploadResponse(VaadinResponse response, boolean success)
                throws IOException {
            response.setContentType(
                    ApplicationConstants.CONTENT_TYPE_TEXT_JAVASCRIPT_UTF_8);
            if (success) {
                try (OutputStream out = response.getOutputStream()) {
                    final PrintWriter outWriter = new PrintWriter(
                            new BufferedWriter(new OutputStreamWriter(out, UTF_8)));
                    try {
                        VaadinSession.getCurrent().lock();
                        String filename = (String) VaadinSession.getCurrent().getAttribute(CKEditor.class.getName());
                        JsonObject url = new JreJsonFactory().createObject();
                        url.put("url", this.imagesBaseUrl.resolve(filename).toString());
                        outWriter.print(url.toJson());
                    } finally {
                        outWriter.flush();
                        VaadinSession.getCurrent().unlock();
                    }
                }
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }
}
