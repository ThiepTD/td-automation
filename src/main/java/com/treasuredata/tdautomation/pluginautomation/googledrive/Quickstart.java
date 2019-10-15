package com.treasuredata.tdautomation.pluginautomation.googledrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.Permission;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class Quickstart {
    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private  String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/client_secret.json";
    private static  Drive service;
     /** Creates an authorized Credential object.
     // @param http-transport The network HTTP Transport.
     /* @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     **/
     public Quickstart(String token)  {
         this.TOKENS_DIRECTORY_PATH = token;
         // Build a new authorized API client service.
         NetHttpTransport HTTP_TRANSPORT;
         try {
             HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
             service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                     .setApplicationName(APPLICATION_NAME)
                     .build();
         } catch (GeneralSecurityException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }


     }
    public  Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = Quickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**public static void main(String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        
        getFiles(service);
        //uploadFile(service,"target.csv","text/csv","target.csv");
    	updateFile("1e9C077eNyRSMYOMFT6VnNyFxvcWPOJ4D","screen.png","image/png","screen.png");
    }*/

    /** get all the files from drive account **/
    public  void getFiles(Drive service)throws IOException{
    	
    	// Print the names and IDs for up to 10 files.
        FileList result = service.files().list()
                .setPageSize(10)
                .setFields("nextPageToken, files(id, name)")
                .execute();
        List<File> files = result.getFiles();
        
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }
    }

    /** upload a file to drive account **/
    public  String uploadFile(String newTitle, String newMimeType, String newFilename)throws IOException{
    	//upload a file to google drive
    	File fileMetadata = new File();
    	
    	//// File's new metadata.
    	fileMetadata.setName(newTitle);
    	
    	 // File's new content.
    	java.io.File filePath = new java.io.File(newFilename);
    	FileContent mediaContent = new FileContent(newMimeType, filePath);
    	
    	// Send the request to the API.
    	File file = service.files().create(fileMetadata, mediaContent)
    	    .setFields("id")
    	    .execute();
    	return file.getId();
    	
    }

    /** update a file to drive account **/
    public  String updateFile(String fileId, String newTitle, String newMimeType, String newFilename) {
    	    try {
    	      
    	      File file = new File();
    	      // File's new metadata.
    	      file.setName(newTitle); 
    	      
    	      // File's new content.
    	      java.io.File fileContent = new java.io.File(newFilename);
    	      FileContent mediaContent = new FileContent(newMimeType, fileContent);

    	      // Send the request to the API.
    	      File updatedFile = service.files().update(fileId, file, mediaContent).execute();
    	      System.out.println("File ID: " + fileId + " updated!");
    	      return updatedFile.getId();
    	    } catch (IOException e) {
    	      System.out.println("An error occurred: " + e);
    	      return null;
    	    }
    }

    /** upload a file to google drive account folder **/
    public String uploadFileInFolder(String folderId,String newTitle, String fileMimeType, String newFilename) throws IOException {

        //String folderId = createFolder(folderName);

        File fileMetadata = new File();
        fileMetadata.setName(newTitle);
        fileMetadata.setParents(Collections.singletonList(folderId));
        java.io.File filePath = new java.io.File(newFilename);
        FileContent mediaContent = new FileContent(fileMimeType, filePath);
        File file = service.files().create(fileMetadata, mediaContent)
                .setFields("id, parents")
                .execute();
        return file.getId();
    }

    public String createFolder(String folderName) throws IOException {
        File folderMetadata = new File();
        folderMetadata.setName(folderName);
        folderMetadata.setMimeType("application/vnd.google-apps.folder");

        File folder = service.files().create(folderMetadata)
                .setFields("id")
                .execute();
        String folderId = folder.getId();
        return folderId;
    }

    /** delete a file or folder in drive account **/
    public void deleteFile(String fileId){
        try {
            service.files().delete(fileId).execute();
        }catch(IOException ex){
            System.out.println(ex);
        }

    }

    /**  set permission for a file or folder in drive account **/
    public  Permission createPublicPermission(String fileId) throws IOException{
        String permissionType = "anyone";
        String permissionRole = "reader";

        Permission newPermission = new Permission();
        newPermission.setType(permissionType);
        newPermission.setRole(permissionRole);

        return service.permissions().create(fileId,newPermission).execute();

    }

    /**  set permission for a file or folder to a particular email in drive account **/
    public Permission createPermissionForEmail(String fileId, String googleEmail) throws IOException {
        String permissionType = "user";
        String permissionRole = "reader";

        Permission newPermission = new Permission();
        newPermission.setType(permissionType);
        newPermission.setRole(permissionRole);

        newPermission.setEmailAddress(googleEmail);
        return service.permissions().create(fileId,newPermission).execute();
    }

    /** This method is to move a file to a specified folder **/
    public String moveFileBetweenFolders(String fileId,String newFolderId) throws IOException{

        File file = service.files().get(fileId)
                .setFields("parents")
                .execute();

        StringBuilder previousParents = new StringBuilder();
        for (String parent : file.getParents()) {
            previousParents.append(parent);
            previousParents.append(',');
        }

        file = service.files().update(fileId, null)
                .setAddParents(newFolderId)
                .setRemoveParents(previousParents.toString())
                .setFields("id, parents")
                .execute();

        return file.getId();

    }

    /** This method is to clone or copy a file **/
    public String cloneFile(String originFileId,String copyTitle) throws IOException{
        File copiedFile = new File();
        copiedFile.setName(copyTitle);
        try {
            return service.files().copy(originFileId, copiedFile).execute().getId();
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
        return null;
    }

}


