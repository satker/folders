package examplebeans.dao;

import examplebeans.model.Folder;

import java.util.List;
import java.util.Set;

public interface FolderDao {
    void writeAllFoundedDirectoriesIntoDB(List<Folder> result, String directory);
    void addNewFolder(String parentFolderDirectory, String newFolderDirectory);
    void updateFoldersNameAfterEditFolderName(String directoryOldFolder, String directoryNewFolder);
    void moveFolderToAnotherRepository(String directoryFolderTo, String directoryFolderFrom, String to);
}
