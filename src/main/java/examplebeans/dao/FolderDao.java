package examplebeans.dao;

import examplebeans.model.FolderManager;

import java.util.Set;

public interface FolderDao {
    void isFolderPresentInDB();
    void writeAllFoundedDirectoriesIntoDB(Set<FolderManager> result, String directory);
    void addNewFolder(String parentFolderDirectory, String newFolderDirectory);
    void editFolderName(String directoryOldFolder, String directoryNewFolder);
    void moveFolderToAnotherRepository(String directoryFolder, String directoryFolderTo, String directoryFolderFrom);
}
