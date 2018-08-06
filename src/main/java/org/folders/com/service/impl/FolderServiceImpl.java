package org.folders.com.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.folders.com.dao.FolderDao;
import org.folders.com.dto.FolderDto;
import org.folders.com.mapper.FolderMapper;
import org.folders.com.model.Folder;
import org.folders.com.service.FolderService;
import org.folders.com.service.JSONFolderService;
import org.h2.store.fs.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FolderServiceImpl implements FolderService {
    private static final Logger log = Logger.getLogger(FolderServiceImpl.class);

    private final FolderDao folderDao;

    private final JSONFolderService jsonFolderService;

    private String directoryForSearch;

    @Autowired
    private FolderServiceImpl(FolderDao folderDao, JSONFolderService jsonFolderService) {
        this.folderDao = folderDao;
        this.jsonFolderService = jsonFolderService;
    }

    @PostConstruct
    private void initializeLogger() {
        BasicConfigurator.configure();
    }

    // TODO: убрать null
    private List<FolderDto> getAllForFolder(String folder) {
        String directory = getDirectoryFolder(folder);
        return getAllDirectoriesFromFolder(directory);
    }

    private String getDirectoryFolder(String folder) {
        return folder == null ? directoryForSearch : directoryForSearch + "\\" + getDirectoryOfFolder(folder);
    }

    private String getDirectoryOfFolder(String folder) {
        return folder.replaceAll("->", "\\\\")
                .replaceAll(" ", "");
    }

    private List<String> getStringCollectionFromFolder(List<FolderDto> allForFolderDto) {
        return allForFolderDto.stream()
                .map(FolderDto::getDirectory)
                .map(folder -> folder.split("\\\\"))
                .map(flatFolder -> flatFolder[flatFolder.length - 1])
                .collect(Collectors.toList());
    }


    private void waitTwoSeconds() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            log.error("Delay was failed by InterruptedException");
            // TODO: обработать exception
        }
    }

    public String getJsonOfChildsByParent(String parent) {
        List<String> stringCollectionFromFolder = getChildFoldersByParent(parent);
        if (stringCollectionFromFolder != null) {
            return jsonFolderService.getJSONChildesFromParentDirectory(
                    stringCollectionFromFolder);
        } else {
            return "";
        }
    }

    public List<String> getChildFoldersByParent(String parent) {
        waitTwoSeconds();
        List<FolderDto> allForFolderManager = getAllForFolder(parent);
        return getStringCollectionFromFolder(allForFolderManager);
    }

    @Override
    public boolean setDirectoryForSearch(String directoryForSearch) {
        File file = new File(directoryForSearch);
        if(file.exists() && file.isDirectory()){
            this.directoryForSearch = directoryForSearch;
            return true;
        } else {
            return false;
        }
    }

    public void removeNode(String folder) {
        String directoryRemovingNode = getDirectoryFolder(folder);
        FileUtils.deleteRecursive(directoryRemovingNode, true);
        folderDao.removeParentFolderAndChildFolders(directoryRemovingNode);
    }

    public void moveNode(String from, String to) {
        String directoryFolderFrom = getDirectoryFolder(from);
        String directoryFolderToWithoutResultFolder = getDirectoryFolder(to);
        String[] split = from.replaceAll(" ", "").split("->");
        String directoryFolderTo = getDirectoryFolder(to + "->" + split[split.length - 1]);
        try {
            Files.move(Paths.get(directoryFolderFrom), Paths.get(directoryFolderTo));
        } catch (IOException e) {
            log.error("Failed to move " + directoryFolderFrom + " to " + directoryFolderTo + " by one of this file don't present");
        }
        folderDao.moveFolderToAnotherRepository(directoryFolderTo, directoryFolderFrom, directoryFolderToWithoutResultFolder);
    }


    public void editFolderName(String oldFolder, String newFolder) {
        String directoryOldFolder = getDirectoryFolder(oldFolder);
        String directoryNewFolder = getDirectoryFolder(newFolder);
        File oldDirectory = new File(directoryOldFolder);
        File newDirectory = new File(directoryNewFolder);
        oldDirectory.renameTo(newDirectory);
        folderDao.updateFoldersNameAfterEditFolderName(directoryOldFolder, directoryNewFolder);
    }

    public void addNewFolder(String newFolder) {
        String directoryFolderForAdd = getDirectoryFolder(newFolder);
        Path pathForAdd = Paths.get(directoryFolderForAdd);
        try {
            String parentDirectoryFromChild = getParentDirectoryFromChild(directoryFolderForAdd);
            folderDao.addNewFolder(parentDirectoryFromChild, directoryFolderForAdd);
            Files.createDirectories(pathForAdd);
        } catch (IOException e) {
            log.error("Failed to add new folder " + pathForAdd + ", because this directory don't present");
        }
    }

    private String getParentDirectoryFromChild(String newDirectory) {
        String[] splitStrings = newDirectory.split("\\\\");
        splitStrings[splitStrings.length - 1] = null;
        return String.join("\\", splitStrings).replaceAll("\\\\null", "");
    }

    private List<FolderDto> getAllDirectoriesFromFolder(String directory) {
        List<Folder> result = currentDirectories(directory);
        if (result != null) {
            folderDao.writeAllFoundedDirectoriesIntoDB(result, directory);
            return FolderMapper.INSTANCE.foldersToFolderDTOs(result);
        } else {
            return new ArrayList<>();
        }
    }

    // Возвращает список директорий в папке
    private List<Folder> currentDirectories(String path) {
        List<Folder> result = null;
        // Список файлов текущей директории
        String[] currentFiles = new File(path).list();
        if (currentFiles != null) {
            result = Arrays.stream(currentFiles)
                    .map(fileOrDirectoryName ->
                            getDirectoryFromFullNameFileOrDirectory(path, fileOrDirectoryName))
                    .map(directory -> FolderDto.builder().directory(directory).build())
                    .map(FolderMapper.INSTANCE::folderDtoToFolder)
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
