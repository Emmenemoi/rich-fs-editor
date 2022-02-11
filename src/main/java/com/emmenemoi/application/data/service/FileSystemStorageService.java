package com.emmenemoi.application.data.service;

import com.emmenemoi.application.data.StorageException;
import com.emmenemoi.application.data.StorageFileNotFoundException;
import com.emmenemoi.application.data.StorageProperties;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {
    private static Logger logger = LoggerFactory.getLogger(FileSystemStorageService.class);
    private final Path uploadRootFs;
    protected final Path rootFs;
    protected final Path rootGitFs;
    protected final Path baseImageUrl;
    protected Git git;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) throws IOException {
        this.uploadRootFs = Paths.get(properties.getUploadDir()).normalize();
        this.rootFs = Path.of(properties.getRoot()).normalize();
        this.baseImageUrl = Path.of(properties.getImagesUrl());
        this.rootGitFs = Path.of(properties.getGitRoot());

        if (properties.isCreateDirectories()) {
            Files.createDirectories(this.uploadRootFs);
        }
        logger.info("Loading filesystem with root at {} / uploads in {}", rootFs.toAbsolutePath(), uploadRootFs.toAbsolutePath());
        if (!uploadRootFs.toFile().exists()) {
            logger.info("Create directories for {}", uploadRootFs);
        }

        if (!properties.getGitRepo().isEmpty()){
            logger.info("Loading git repo at {} in {}", properties.getGitRepo(), getRootFs().toAbsolutePath());
            try {
                if(!rootFs.toFile().exists() || !new File(rootGitFs.toFile(), ".git").exists()){
                    Git.cloneRepository()
                        .setURI(properties.getGitRepo())
                        .setDirectory(rootGitFs.toFile())
                        .call();
                }
                FileRepository localRepo = new FileRepository(new File(rootGitFs.toFile(), ".git").toString());
                git = new Git(localRepo);
                git.pull().call();
            } catch (Exception e) {
                logger.warn("Git pb",e);
            }

        }
    }

    public Path getRootFs() {
        return rootFs;
    }

    public Path getUploadRootFs() {
        return uploadRootFs;
    }

    public boolean hasGit() {
        return git != null;
    }

    public void publish() throws GitAPIException {
        if(git != null) {
            git.add().addFilepattern(".").call();
            git.commit().setMessage("Publish content").call();
        }
    }

    public String getSrc(File image){
        Path rel = uploadRootFs.relativize(image.toPath());
        return baseImageUrl.resolve(rel).toString();
    }

    @Override
    public void store(InputStream inputStream, String filename) {
        try {
            if (inputStream.available() <= 0) {
                throw new StorageException("Failed to store empty file.");
            }
            Path destinationFile = this.uploadRootFs.resolve(
                            Paths.get(filename))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.uploadRootFs.toAbsolutePath())) {
                logger.error("Cannot store file outside current directory: {} within root {}",destinationFile.getParent(), this.uploadRootFs.toAbsolutePath());
                // This is a security check
                throw new StorageException(
                        "Cannot store file outside current directory.");
            }
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Files.copy to {}", destinationFile);
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.uploadRootFs, 1)
                    .filter(path -> !path.equals(this.uploadRootFs))
                    .map(this.uploadRootFs::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return uploadRootFs.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        //FileSystemUtils.deleteRecursively(uploadRootFs.toFile());
        logger.info("deleteAll {}", uploadRootFs);
    }

    @Override
    public void init() {
        try {
            //Files.createDirectories(uploadRootFs);
            logger.info("Files.createDirectories {}", uploadRootFs);
        }
        catch (Exception e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
