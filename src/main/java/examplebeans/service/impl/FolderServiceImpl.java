package examplebeans.service.impl;

import examplebeans.dao.FolderDao;
import examplebeans.dto.FolderManagerDto;
import examplebeans.dto.JSONFolderDto;
import examplebeans.mapper.FolderManagerMapper;
import examplebeans.mapper.JSONFolderMapper;
import examplebeans.model.FolderManager;
import examplebeans.model.JSONFolder;
import examplebeans.service.FolderService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.h2.store.fs.FileUtils;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FolderServiceImpl implements FolderService {
    private FolderDao folderDao;
    public Set<FolderManagerDto> getAllForFolder(String folder) {
        String directory = getDirectoryFolder(folder);
        return getAllDirectoriesFromFolder(directory, folder);
    }

    private String getDirectoryFolder(String folder) {
        String basicFolder = "C:\\Users\\Artem_Kunats\\IdeaProjects\\folders\\src\\main\\resources";
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
        return allForFolder.stream()
                .map(folder -> JSONFolder.builder().text(folder).build())
                .collect(Collectors.toList());
    }

    public String getJSONChildesFromParentDirectory(Set<FolderManagerDto> allForFolderManager) {
        if (allForFolderManager != null) {
            Set<String> stringCollectionFromFolder = getStringCollectionFromFolder(allForFolderManager);
            List<JSONFolder> jsonFromStringFolders = getJSONFromStringFolders(
                stringCollectionFromFolder);
            List<JSONFolderDto> jsonFolderDtos = JSONFolderMapper.INSTANCE.jsonFoldersToJsonFolderDtos(
                jsonFromStringFolders);
            return getFormedStringFromListJSON(jsonFolderDtos);
        } else {
            return "";
        }
    }

    private String getFormedStringFromListJSON(List<JSONFolderDto> jsonFolderDtos) {
        return jsonFolderDtos.stream().map(JSONFolderDto::getJson).collect(Collectors.joining(",","[","]"));
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

    private Set<FolderManagerDto> getAllDirectoriesFromFolder(String directory, String folder) {
        if (folder == null) {
            folderDao.isFolderPresentInDB();
        }
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
