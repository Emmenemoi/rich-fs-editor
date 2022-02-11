package com.emmenemoi.application.components;

import com.emmenemoi.application.data.service.FileSystemStorageService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DebounceSettings;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.dom.DebouncePhase;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import elemental.json.JsonObject;
import elemental.json.impl.JreJsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Random;

@Tag("x-ckeditor")
//@NpmPackage(value = "@ckeditor/ckeditor5-build-classic", version = "32.0.0")
//@NpmPackage(value = "@ckeditor/ckeditor5-markdown-gfm", version = "32.0.0")
//@JsModule("@ckeditor/ckeditor5-build-classic")
/*
@NpmPackage(value = "@ckeditor/ckeditor5-ui", version = "32.0.0")
@NpmPackage(value = "@ckeditor/ckeditor5-editor-classic", version = "32.0.0")
@NpmPackage(value = "@ckeditor/ckeditor5-alignment", version = "32.0.0")
@NpmPackage(value = "@ckeditor/ckeditor5-autoformat", version = "32.0.0")
@NpmPackage(value = "@ckeditor/ckeditor5-block-quote", version = "32.0.0")
@NpmPackage(value = "@ckeditor/ckeditor5-basic-styles", version = "32.0.0")
@NpmPackage(value = "@ckeditor/ckeditor5-adapter-ckfinder", version = "32.0.0")
@NpmPackage(value = "@ckeditor/ckeditor5-cloud-services", version = "32.0.0")
@NpmPackage(value = "@ckeditor/ckeditor5-essentials", version = "32.0.0")
@NpmPackage(value = "@ckeditor/ckeditor5-heading", version = "32.0.0")
@NpmPackage(value = "@ckeditor/ckeditor5-image", version = "32.0.0")
@NpmPackage(value = "@ckeditor/ckeditor5-indent", version = "32.0.0")
@NpmPackage(value = "@ckeditor/ckeditor5-link", version = "32.0.0")
@NpmPackage(value = "@ckeditor/ckeditor5-list", version = "32.0.0")
@NpmPackage(value = "@ckeditor/ckeditor5-markdown-gfm", version = "32.0.0")
@NpmPackage(value = "@ckeditor/ckeditor5-media-embed", version = "32.0.0")
@NpmPackage(value = "@ckeditor/ckeditor5-mention", version = "32.0.0")
@NpmPackage(value = "@ckeditor/ckeditor5-paragraph", version = "32.0.0")
@NpmPackage(value = "@ckeditor/ckeditor5-paste-from-office", version = "32.0.0")
@NpmPackage(value = "@ckeditor/ckeditor5-remove-format", version = "32.0.0")
@NpmPackage(value = "@ckeditor/ckeditor5-source-editing", version = "32.0.0")
@NpmPackage(value = "@ckeditor/ckeditor5-typing", version = "32.0.0")
@NpmPackage(value = "@ckeditor/ckeditor5-basic-styles", version = "32.0.0")
*/
@NpmPackage(value = "ckeditor5-webcomponent", version = "2.0.5")
//@JsModule("ckeditor5-webcomponent/dist/collection/editor-manager.js")
//@JsModule("ckeditor5-webcomponent/dist/esm/es2017/x-ckeditor.define.js")
@JsModule("./javascript/ckeditor-connect.js")
//@CssImport(value = "./ckeditor.css", themeFor = "x-ckeditor")
public class CKEditor extends CustomField<String>  {
    private static final Logger logger = LoggerFactory.getLogger(CKEditor.class);

    public CKEditor(FileSystemStorageService fileSystemStorageService) {
        super("");
        getElement().setProperty("editor", "custom");
        //setValue("");
        addChangeEventListener(e -> this.setModelValue(e.getContent(), true));

        MultiFileMemoryBuffer multiFileMemoryBuffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(multiFileMemoryBuffer);
        getElement().appendVirtualChild(upload.getElement());
        //add(upload);
        upload.addStartedListener(e-> VaadinSession.getCurrent().setAttribute(CKEditor.class.getName(), e.getFileName()));
        upload.addFailedListener(e-> VaadinSession.getCurrent().setAttribute(CKEditor.class.getName(), null));
        upload.addFinishedListener(e->{
            try (InputStream is = multiFileMemoryBuffer.getInputStream(e.getFileName())) {
                fileSystemStorageService.store(is, e.getFileName());
            } catch (IOException ex) {
                Notification.show(ex.getMessage());
            }
        });
        JsonObject configResult = new JreJsonFactory().createObject();
        JsonObject simpleUpload = new JreJsonFactory().createObject();
        simpleUpload.put("uploadUrl", upload.getElement().getAttribute("target"));
        simpleUpload.put("withCredentials", true);

        configResult.put("simpleUpload", simpleUpload);
        getElement().setProperty("config", configResult.toJson());
/*
        headers: {
            'X-CSRF-TOKEN': 'CSRF-Token'
            Authorization: 'Bearer <JSON Web Token>'
        }*/
    }

    protected String generateModelValue() {
        return this.getElement().getProperty("content");
        //return editorData;
    }

    protected void setPresentationValue(String newPresentationValue) {
        this.getElement().setProperty("content", newPresentationValue);
    }

    @Override
    public void setId(String id) {
        getElement().setProperty("target-id", id==null? "editor_"+Math.abs(new Random().nextInt()+1): id);
    }

    @Override
    public Optional<String> getId() {
        return Optional.of(getElement().getProperty("target-id"));
    }
/*
    public String getValue() {
        return this.editorData;
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        this.getElement().setProperty("content", value);
        this.updateValue();
    }

    @Override
    protected void setModelValue(String value, boolean fromClient) {
        String old = getValue();
        super.setModelValue(value, fromClient);;
        this.fireEvent(new ComponentValueChangeEvent(this, this, old, fromClient));
    }

/*
    private void updateValue(String content) {
        //if (this.getId().isPresent()) {
            //this.getElement().executeJs("this.updateData($0, $1)", new Serializable[]{(Serializable)this.getId().get(), content == null ? "" : content});
       // }
        //this.getElement().executeJs("this.setData($0)",content);
        this.getElement().setProperty("content", content);

    }
*/


    public Registration addChangeEventListener(ComponentEventListener<ChangeEvent> listener) {
        return addListener(ChangeEvent.class, listener);
    }

    @DomEvent(value = "ckeditorchange"
            , debounce = @DebounceSettings(
                    timeout = 250,
                    phases = DebouncePhase.TRAILING)
    )
    public static class ChangeEvent
            extends ComponentEvent<CKEditor> {
        protected String content;

        public ChangeEvent(CKEditor source,
                           boolean fromClient,
                           @EventData("event.detail") String content) {
            super(source, fromClient);
            logger.debug("ckeditorchange {}", content);
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }

}
