package org.folders.com.service;

import java.util.List;

public interface FolderService {

    void removeNode(String folder);
    void moveNode(String from, String to);
    void editFolderName(String oldFolder, String newFolder);
    void addNewFolder(String newFolder);
    String getJsonOfChildsByParent(String parent);
    List<String> getChildFoldersByParent(String parent);
    boolean setDirectoryForSearch(String directoryForSearch);
}
