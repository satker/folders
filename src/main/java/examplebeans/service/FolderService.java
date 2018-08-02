package examplebeans.service;

import java.util.Set;

public interface FolderService {

    void removeNode(String folder);
    void moveNode(String from, String to);
    void editFolderName(String oldFolder, String newFolder);
    void addNewFolder(String newFolder);
    String getJsonOfChildsByParent(String parent);
    Set<String> getChildFoldersByParent(String parent);
}
