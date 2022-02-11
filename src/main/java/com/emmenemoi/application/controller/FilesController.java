package com.emmenemoi.application.controller;

import com.emmenemoi.application.data.StorageFileNotFoundException;
import com.emmenemoi.application.data.StorageProperties;
import com.emmenemoi.application.data.service.StorageService;
import elemental.json.JsonObject;
import elemental.json.impl.JreJsonFactory;
import elemental.json.impl.JreJsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.stream.Collectors;
/*
@Controller
@RequestMapping( "/files")
public class FilesController {

    Logger logger = LoggerFactory.getLogger(FilesController.class);

    private URI imageUrl;

    private final StorageService storageService;

    @Autowired
    public FilesController(StorageService storageService, StorageProperties properties) throws URISyntaxException {
        this.storageService = storageService;
        this.imageUrl = new URI(properties.getImagesUrl());
    }

    @RequestMapping(method = RequestMethod.GET)
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(FilesController.class,
                                "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @RequestMapping(value = "/{filename}" ,method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces="application/json")
    @ResponseBody
    public JsonObject handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        JsonObject url = new JreJsonFactory().createObject();
        url.put("url", imageUrl.resolve(file.getOriginalFilename()).toString());
        return url;
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
*/