package com.emmenemoi.application.data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    private String root = "";

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    private String uploadDir = "";

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    private String imagesUrl = "";

    public String getImagesUrl() {
        return imagesUrl;
    }

    public void setImagesUrl(String imagesUrl) {
        this.imagesUrl = imagesUrl;
    }

    private String newFileTemplateLocation = "";

    public String getNewFileTemplateLocation() {
        return newFileTemplateLocation;
    }

    public void setNewFileTemplateLocation(String newFileTemplateLocation) {
        this.newFileTemplateLocation = newFileTemplateLocation;
    }

    private String gitRepo = "";
    public String getGitRepo() {
        return gitRepo;
    }
    public void setGitRepo(String gitRepo) {
        this.gitRepo = gitRepo;
    }

    private String gitRoot = "";
    public String getGitRoot() {return gitRoot;}
    public void setGitRoot(String gitRoot) {this.gitRoot = gitRoot;}

    private String filterFiles = "";
    public String getFilterFiles() {return filterFiles;}
    public void setFilterFiles(String filterFiles) {this.filterFiles = filterFiles;}

    private String filterImages = "";
    public String getFilterImages() {return filterImages;}
    public void setFilterImages(String filterImages) {this.filterImages = filterImages;}

    private boolean createDirectories = true;
    public boolean isCreateDirectories() {return createDirectories;}
    public void setCreateDirectories(boolean createDirectories) {this.createDirectories = createDirectories;}
}