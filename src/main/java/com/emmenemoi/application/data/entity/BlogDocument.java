package com.emmenemoi.application.data.entity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

public class BlogDocument {

    private Path filepath;
    private String filename;
    private String frontmatterDelimiter = "";
    private String frontmatter = "";
    private String content = "";

    public Path getFilepath() {
        return filepath;
    }

    public void setFilepath(Path filepath) {
        this.filepath = filepath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFrontmatter() {return frontmatter;}

    public void setFrontmatter(String frontmatter) { this.frontmatter = frontmatter.trim();}

    public String getRawContent() {
        if( getFrontmatter().isEmpty()){
            return getContent();
        } else {
            return frontmatterDelimiter+"\n"+getFrontmatter()+"\n"+frontmatterDelimiter+"\n"+getContent();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlogDocument that = (BlogDocument) o;

        if (!filepath.equals(that.filepath)) return false;
        return content.equals(that.content);
    }

    @Override
    public int hashCode() {
        int result = filepath.hashCode();
        result = 31 * result + content.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "BlogDocument{" +
                "filepath=" + filepath +
                ", filename='" + filename + '\'' +
                '}';
    }

    public static BlogDocument from(Path file) throws IOException {
        BlogDocument blogDocument = new BlogDocument();
        blogDocument.setFilename(file.getFileName().toString());
        blogDocument.setFilepath(file);
        setContent(blogDocument);
        return blogDocument;
    }

    public static void setContent(BlogDocument blogDocument) throws IOException {
        List<String> lines = Files.readAllLines(blogDocument.getFilepath());
        if (lines.isEmpty()){
            return;
        }
        // detect YAML front matter
        Iterator<String> lineItr = lines.iterator();
        String line = lineItr.next();
        while (line.isEmpty()) line = lineItr.next();
        if (!line.matches("[-]{3,}")) { // use at least three dashes
            // No frontmatter
            blogDocument.setContent(Files.readString(blogDocument.getFilepath()));
            return ;
        }
        blogDocument.frontmatterDelimiter = line;

        // scan YAML front matter
        StringBuilder frontmatter = new StringBuilder();
        line = lineItr.next();
        while (!line.equals(blogDocument.frontmatterDelimiter)) {
            frontmatter.append(line);
            frontmatter.append("\n");
            line = lineItr.next();
        }
        // Yaml done, now content
        StringBuilder content = new StringBuilder();
        while(lineItr.hasNext()) {
            content.append(lineItr.next());
        }
        blogDocument.setContent(content.toString());
        blogDocument.setFrontmatter(frontmatter.toString());
    }
}
