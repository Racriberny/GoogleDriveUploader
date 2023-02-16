package com.joanjorgedanimichaeltobal.GoogleDriveUploader;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.JsonToken;
import com.google.api.client.util.Preconditions;

import java.io.IOException;
import java.util.Collections;


public class GoogleDriveUploader {

    private static final String APPLICATION_NAME = "Nombre de tu aplicación";
    private static final String FOLDER_NAME = "https://drive.google.com/drive/u/0/folders/1oEvg47Uxo-97yt1NyLtefvGLZdENvA8x";

    public static void main(String[] args) throws IOException, GeneralSecurityException {

        // Autenticación
        Credential credential = getCredentials();
        Drive driveService = new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Obtener el ID de la carpeta
        String folderId = getFolderId(driveService);

        // Subir los archivos a la carpeta
        uploadFiles(driveService, folderId);







    }
    private static Credential getCredentials() throws IOException {
        // Carga las credenciales del archivo JSON generado en la consola de Google Cloud Platform
        InputStream inputStream = GoogleDriveUploader.class.getResourceAsStream("/credentials.json");
        GoogleCredential credential = GoogleCredential.fromStream(inputStream)
                .createScoped(Collections.singleton(DriveScopes.DRIVE));
        return credential;
    }

    private static String getFolderId(Drive driveService) throws IOException {
        // Busca la carpeta con el nombre especificado
        FileList result = driveService.files().list()
                .setQ("mimeType='application/vnd.google-apps.folder' and trashed = false and name='" + FOLDER_NAME + "'")
                .setFields("nextPageToken, files(id, name)")
                .execute();

        // Si no se encuentra la carpeta, se crea una nueva
        if (result.getFiles().isEmpty()) {
            File folder = new File();
            folder.setName(FOLDER_NAME);
            folder.setMimeType("application/vnd.google-apps.folder");
            File newFolder = driveService.files().create(folder)
                    .setFields("id")
                    .execute();
            return newFolder.getId();
        } else {
            return result.getFiles().get(0).getId();
        }
    }

    private static void uploadFiles(Drive driveService, String folderId) throws IOException {
        // Archivo a subir
        java.io.File file = new java.io.File("ruta/al/archivo.pdf");
        FileContent mediaContent = new FileContent("application/pdf", file);

        // Crea un objeto de tipo File para especificar la carpeta de destino
        File fileMetadata = new File();
        fileMetadata.setName(file.getName());
        fileMetadata.setParents(Collections.singletonList(folderId));

        // Sube el archivo a la carpeta
        File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id, parents")
        .execute();
    }

}
