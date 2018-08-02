package examplebeans.service;

import examplebeans.dto.FolderManagerDto;
import examplebeans.dto.JSONFolderDto;
import examplebeans.model.FolderManager;
import examplebeans.model.JSONFolder;

import java.util.List;
import java.util.Set;

public interface FolderService {
    Set<FolderManagerDto> getAllForFolder(String folder);
    Set<String> getStringCollectionFromFolder(Set<FolderManagerDto> allForFolderManager);
    String getJSONChildesFromParentDirectory(Set<FolderManagerDto> allForFolderManager);
    void removeNode(String folder);
    void moveNode(String from, String to);
    void editFolderName(String oldFolder, String newFolder);
    void addNewNode(String newFolder);

}
