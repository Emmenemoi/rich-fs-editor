package com.emmenemoi.application;

import com.emmenemoi.application.data.CKEditorStreamRequestHandler;
import com.emmenemoi.application.data.MDImagesRequestHandler;
import com.emmenemoi.application.data.StorageProperties;
import com.emmenemoi.application.data.service.FileSystemStorageService;
import com.vaadin.flow.server.RequestHandler;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.communication.StreamRequestHandler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.Iterator;

@Component
public class ApplicationServiceInitListener implements VaadinServiceInitListener {

    @Autowired
    StorageProperties properties;

    @Autowired
    FileSystemStorageService fileSystemStorageService;

    @Override
    public void serviceInit(ServiceInitEvent event) {
        LoggerFactory.getLogger(ApplicationServiceInitListener.class).info("try to replace StreamRequestHandler");
        Iterator<RequestHandler> iterator = event.getAddedRequestHandlers().iterator();
        while (iterator.hasNext()) {
            RequestHandler rh = iterator.next();
            LoggerFactory.getLogger(ApplicationServiceInitListener.class).info("try {}",rh);
            if(rh instanceof StreamRequestHandler) {
                iterator.remove();
                LoggerFactory.getLogger(ApplicationServiceInitListener.class).info("replace StreamRequestHandler");
            }
        }
        try {
            event.addRequestHandler(new MDImagesRequestHandler(properties, fileSystemStorageService));
            event.addRequestHandler(new CKEditorStreamRequestHandler(properties));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Bad base images URL provided:", e);
        }

    }



}
