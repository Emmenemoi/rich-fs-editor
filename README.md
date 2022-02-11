# FileEditor

This project provides a filesystem navigator to edit files using CKEditor 5 and its Markdown plugin.

The puprose of this editor is to ease the edition and publication of pages on static web site built with Hugo, Eleventy, Gatsby or others.\
It manages frontmatter headers.\
A media navigator is included for linking images within Markdown.

## Configuration

### Environment vars
SECURITY_USERS: yaml file location defining users' authorizations, bcrypted (yaml array):
```
- username: admin
  hashedPassword: "$2a$12$TfvLzlC6ETVuHmBCx7sAVeHj9uuZUG3wq99QYg25MKU3PkezC0wx2"
  roles: [ADMIN]
```
STORAGE_ROOT: (default: /git) base location for filesystem navigator and target directory of git clone (if any)
STORAGE_UPLOAD_DIR: (default: /uploads) location of images uploads
STORAGE_IMAGES_URL: (default: /img/) Base url used for uploaded images in MD: <STORAGE_IMAGES_URL><filename>
STORAGE_NEWFILETEMPLATELOCATION: template used when creating new files
STORAGE_GITREPO: If specified, the git repo will automatically be cloned at /git. A publish action will then be allowed to ADMIN role users, to commit and push modifications.
STORAGE_FILTERFILES: regex filter to restrict filename visibility in contents navigator
STORAGE_FILTERIMAGESS: regex filter to restrict filename visibility in media navigator


### properties file swap

You can also configure the app by providing your own application.properties file within /app directory. Check original in src/main/resources as template.

## Running the application

docker run -p 8080:8080  asaoweb/richtext-fs-editor
-v <upload dir>:/uploads

# Build the project

- build artifact: make local
- build image: make dist