package com.emmenemoi.application.data.service;

import com.emmenemoi.application.data.StorageProperties;
import com.emmenemoi.application.data.entity.BlogDocument;
import com.emmenemoi.application.security.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class BlogDocumentService {
    static Logger logger = LoggerFactory.getLogger(BlogDocumentService.class);

    final String templateContent;

    @Autowired
    public BlogDocumentService(StorageProperties properties) throws IOException {
        String templateSrc = properties.getNewFileTemplateLocation();
        if (templateSrc.isEmpty()) {
            templateContent = "";
        } else {
            File source = StringUtils.startsWithIgnoreCase(templateSrc,"classpath:") ?
                    ResourceUtils.getFile(templateSrc)
                    : new File(templateSrc);
            templateContent = Files.readString(source.toPath());
        }
    }

    public Optional<BlogDocument> get(Path id) {
        try {
            return Optional.of(BlogDocument.from(id));
        } catch (IOException e) {
            logger.warn("Problem loading {}", id);
        }
        return Optional.empty();
    }

    public BlogDocument update(BlogDocument entity) throws IOException {
        logger.info("update {}", entity);
        Files.writeString(entity.getFilepath(), entity.getRawContent());// default options CREATE, TRUNCATE_EXISTING, and WRITE
        return entity;
    }

    public void delete(Path id) throws IOException {
        Files.delete(id);
        logger.info("Delete {}", id);
    }

    public BlogDocument newDocument(Path destination) throws IOException {
        logger.info("new {}", destination);
        Files.writeString(destination, templateContent, StandardOpenOption.CREATE);
        return BlogDocument.from(destination);
    }

}
