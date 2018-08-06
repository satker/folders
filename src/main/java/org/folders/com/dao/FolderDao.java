package org.folders.com.dao;

import java.util.List;
import org.folders.com.model.Folder;

public interface FolderDao {
    void writeAllFoundedDirectoriesIntoDB(List<Folder> result, String directory);
    void addNewFolder(String parentFolderDirectory, String newFolderDirectory);
    void updateFoldersNameAfterEditFolderName(String directoryOldFolder, String directoryNewFolder);
    void moveFolderToAnotherRepository(String directoryFolderTo, String directoryFolderFrom, String to);
    void removeParentFolderAndChildFolders(String directory);
}
