package examplebeans.service.impl;

import examplebeans.dao.FolderDao;
import examplebeans.dto.FolderManagerDto;
import examplebeans.mapper.FolderManagerMapper;
import examplebeans.model.FolderManager;
import examplebeans.service.FolderService;
import examplebeans.service.JSONFolderService;
import lombok.AllArgsConstructor;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.h2.store.fs.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FolderServiceImpl implements FolderService {
    private static final Logger log = Logger.getLogger(FolderServiceImpl.class);

    private final FolderDao folderDao;

    private final JSONFolderService jsonFolderService;

    private Set<FolderManagerDto> getAllForFolder(String folder) {
        String directory = getDirectoryFolder(folder);
        return getAllDirectoriesFromFolder(directory, folder);
    }

    private String getDirectoryFolder(String folder) {
        String basicFolder = "D:\\java_projects\\folders\\src\\main\\resources\\examplefolders";
        return folder == null ? basicFolder : basicFolder + "\\" + getDirectoryOfFolder(folder);
    }

    private String getDirectoryOfFolder(String folder) {
        return folder.replaceAll("->", "\\\\")
                .replaceAll(" ", "");
    }

    private Set<String> getStringCollectionFromFolder(Set<FolderManagerDto> allForFolderManagerDto) {
        return allForFolderManagerDto.stream()
                .map(FolderManagerDto::getDirectory)
                .map(folder -> folder.split("\\\\"))
                .map(flatFolder -> flatFolder[flatFolder.length - 1])
                .collect(Collectors.toSet());
    }


    private void waitTwoSeconds() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            log.error("Delay was failed by InterruptedException");
        }
    }

    public String getJsonOfChildsByParent(String parent) {
        Set<String> stringCollectionFromFolder = getChildFoldersByParent(parent);
        if (stringCollectionFromFolder != null) {
            return jsonFolderService.getJSONChildesFromParentDirectory(
                    stringCollectionFromFolder);
        } else {
            return "";
        }
    }

    public Set<String> getChildFoldersByParent(String parent) {
        BasicConfigurator.configure();
        waitTwoSeconds();
        Set<FolderManagerDto> allForFolderManager = getAllForFolder(parent);
        return getStringCollectionFromFolder(allForFolderManager);
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
            log.error("Failed to move " + directoryFolderFrom + " to " + directoryFolderTo + " by one of this file don't present");
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

    public void addNewFolder(String newFolder) {
        String directoryFolderForAdd = getDirectoryFolder(newFolder);
        Path pathForAdd = Paths.get(directoryFolderForAdd);
        try {
            String parentDirectoryFromChild = getParentDirectoryFromChild(directoryFolderForAdd);
            folderDao.addNewFolder(parentDirectoryFromChild, directoryFolderForAdd);
            Files.createDirectories(pathForAdd);
        } catch (IOException e) {
            log.error("Failed to add new folder "+ pathForAdd + ", because this directory don't present");
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
