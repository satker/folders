package examplebeans.service.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import examplebeans.dto.FolderManagerDto;
import examplebeans.mapper.FolderManagerMapper;
import examplebeans.model.FolderManager;
import examplebeans.dao.FolderDao;
import examplebeans.model.JSONFolder;
import examplebeans.service.FolderService;
import org.h2.store.fs.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FolderServiceImpl implements FolderService {

    @Autowired
    private void setFolderDao(FolderDao folderDao) {
        this.folderDao = folderDao;
    }

    private FolderDao folderDao;

    private int countIterators = 0;
    public Set<FolderManagerDto> getAllForFolder(String folder) {
        String directory = getDirectoryFolder(folder);
        return getAllDirectoriesFromFolder(directory);
    }

    private String getDirectoryFolder(String folder) {
        String basicFolder = "D:\\java_projects\\folders\\src\\main\\resources\\examplefolders";
        return folder == null ? basicFolder : basicFolder + "\\" + getDirectoryOfFolder(folder);
    }

    private String getDirectoryOfFolder(String folder) {
        return folder.replaceAll("->", "\\\\")
                .replaceAll(" ", "");
    }

    public Set<String> getStringCollectionFromFolder(Set<FolderManagerDto> allForFolderManagerDto) {
        return allForFolderManagerDto.stream()
                .map(FolderManagerDto::getDirectory)
                .map(folder -> folder.split("\\\\"))
                .map(flatFolder -> flatFolder[flatFolder.length - 1])
                .collect(Collectors.toSet());
    }

    private List<JSONFolder> getJSONFromStringFolders(Set<String> allForFolder) {
        ObjectMapper mapper = new ObjectMapper();
        allForFolder.stream()
                .map(folder -> JSONFolder.builder().text(folder).build())
                .map(fol -> {
                    try {
                        return mapper.writeValueAsString(fol);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        return "";
                    }
                })
                .forEach(System.out::println);
        return allForFolder.stream()
                .map(folder -> JSONFolder.builder().text(folder).build())
                .collect(Collectors.toList());
    }

    public List<JSONFolder> getJSONChildesFromParentDirectory(Set<FolderManagerDto> allForFolderManager) {
        if (allForFolderManager != null) {
            Set<String> stringCollectionFromFolder = getStringCollectionFromFolder(allForFolderManager);
            return getJSONFromStringFolders(stringCollectionFromFolder);
        } else {
            return Collections.singletonList(null);
        }
    }

    public void removeNode(String folder) {
        String directoryRemovingNode = getDirectoryFolder(folder);
        FileUtils.deleteRecursive(directoryRemovingNode, true);
    }

    public void moveNode(String from, String to) {
        String directoryFolderFrom = getDirectoryFolder(from);
        String[] split = from.replaceAll(" ", "").split("->");
        String directoryFolderTo = getDirectoryFolder(to + "->" + split[split.length - 1]);
        try {
            Files.move(Paths.get(directoryFolderFrom), Paths.get(directoryFolderTo));
        } catch (IOException e) {
            e.printStackTrace();
        }
        folderDao.moveFolderToAnotherRepository(getDirectoryFolder(to), directoryFolderTo, directoryFolderFrom);
    }


    public void editFolderName(String oldFolder, String newFolder) {
        String directoryOldFolder = getDirectoryFolder(oldFolder);
        String directoryNewFolder = getDirectoryFolder(newFolder);
        File oldDirectory = new File(directoryOldFolder);
        File newDirectory = new File(directoryNewFolder);
        oldDirectory.renameTo(newDirectory);
        folderDao.editFolderName(directoryOldFolder, directoryNewFolder);
    }

    public void addNewNode(String newFolder) {
        String directoryFolderForAdd = getDirectoryFolder(newFolder);
        Path pathForAdd = Paths.get(directoryFolderForAdd);
        try {
            String parentDirectoryFromChild = getParentDirectoryFromChild(directoryFolderForAdd);
            folderDao.addNewFolder(parentDirectoryFromChild, directoryFolderForAdd);
            Files.createDirectories(pathForAdd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getParentDirectoryFromChild(String newDirectory) {
        String[] splitStrings = newDirectory.split("\\\\");
        splitStrings[splitStrings.length - 1] = null;
        return String.join("\\", splitStrings).replaceAll("\\\\null", "");
    }

    private Set<FolderManagerDto> getAllDirectoriesFromFolder(String directory) {
        if (countIterators == 0) {
            folderDao.isFolderPresentInDB();
        }
        countIterators++;
        Set<FolderManager> result = new HashSet<>(currentDirectories(directory));
        if (!result.isEmpty()) {
            folderDao.writeAllFoundedDirectoriesIntoDB(result, directory);
        }
        return FolderManagerMapper.INSTANCE.foldersToFolderDTOs(result);
    }

    // Возвращает список директорий в папке
    private List<FolderManager> currentDirectories(String path) {
        List<FolderManager> result = null;
        // Список файлов текущей директории
        String[] currentFiles = new File(path).list();
        if (currentFiles != null) {
            result = Arrays.stream(currentFiles)
                    .map(fileOrDirectoryName ->
                            getDirectoryFromFullNameFileOrDirectory(path, fileOrDirectoryName))
                    .map(directory -> FolderManagerDto.builder().directory(directory).build())
                    .map(FolderManagerMapper.INSTANCE::folderDtoToFolder)
                    .filter(file -> file.getDirectory().isDirectory())
                    .collect(Collectors.toList());
        }
        return result;
    }

    private String getDirectoryFromFullNameFileOrDirectory(String fileOrDirectoryPath,
                                                    String fileOrDirectoryName) {
        return String.format("%s\\%s", fileOrDirectoryPath, fileOrDirectoryName);
    }
}
