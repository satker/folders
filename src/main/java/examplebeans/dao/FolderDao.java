package examplebeans.dao;

import examplebeans.model.Folder;

import java.util.Set;

public interface FolderDao {
    void writeAllFoundedDirectoriesIntoDB(Set<Folder> result, String directory);
    void addNewFolder(String parentFolderDirectory, String newFolderDirectory);
    void editFolderName(String directoryOldFolder, String directoryNewFolder);
    void moveFolderToAnotherRepository(String directoryFolderTo, String directoryFolderFrom);
}
