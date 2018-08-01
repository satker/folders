package examplebeans.service;

import examplebeans.dao.Folder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import examplebeans.repository.FolderRepository;
import org.h2.store.fs.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FolderService {

  @Autowired
  public void setFolderRepository(FolderRepository folderRepository) {
    this.folderRepository = folderRepository;
  }

  private FolderRepository folderRepository;

  private boolean isASearchedFromFileSystem;

  private String jsonPart1 = "{\"isActive\":false," +
      "\"enableDnd\": true," +
      "\"isFolder\":true," +
      "\"isExpanded\":false," +
      "\"isLazy\":true," +
      "\"iconUrl\":null," +
      "\"id\":null," +
      "\"href\":null," +
      "\"hrefTarget\":null," +
      "\"lazyUrl\":null," +
      "\"lazyUrlJson\":null," +
      "\"liClass\":null," +
      "\"text\":\"";

  private String jsonPart2 = " \"," +
      "\"textCss\":null," +
      "\"tooltip\":null," +
      "\"uiIcon\":null," +
      "\"children\":null}";

  public Set<Folder> getAllForFolder(String folder) {
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

  public Set<String> getStringCollectionFromFolder(Set<Folder> allForFolder) {
    return allForFolder.stream()
                       .map(Folder::getDirectory)
                       .map(File::getName)
                       .map(folder -> folder.split("\\\\"))
                       .map(flatFolder -> flatFolder[flatFolder.length - 1])
                       .collect(Collectors.toSet());
  }

  private String getJSONFromStringFolders(Set<String> allForFolder) {
    return "[" + allForFolder.stream()
                             .map(folder -> jsonPart1.concat(folder)
                                                     .concat(jsonPart2))
                             .collect(Collectors.joining(", ")) + "]";
  }

  public String getJSONChildesFromParentDirectory(Set<Folder> allForFolder) {
    if (allForFolder != null) {
      Set<String> stringCollectionFromFolder = getStringCollectionFromFolder(allForFolder);
      return getJSONFromStringFolders(stringCollectionFromFolder);
    } else {
      return "";
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
    //FileUtils.move(directoryFolderFrom, directoryFolderTo);
    try {
      Files.move(Paths.get(directoryFolderFrom), Paths.get(directoryFolderTo));
    } catch (IOException e) {
      e.printStackTrace();
    }
    //System.out.println(directoryFolderFrom + " moved to " + directoryFolderTo);
  }


  public void editFolderName(String oldFolder, String newFolder) {
    String directoryOldFolder = getDirectoryFolder(oldFolder);
    String directoryNewFOlder = getDirectoryFolder(newFolder);
    File oldDirectory = new File(directoryOldFolder);
    File newDirectory = new File(directoryNewFOlder);

    //System.out.println(oldDirectory+ " rename to "+newDirectory + " result: " + oldDirectory.renameTo(newDirectory));

  }

  public void addNewNode(String newFolder) {
    String directoryFolderForAdd = getDirectoryFolder(newFolder);
    Path pathForAdd = Paths.get(directoryFolderForAdd);
    try {
      Files.createDirectories(pathForAdd);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Set<Folder> getAllDirectoriesFromFolder(String directory) {
        Set<Folder> result = new HashSet<>();
        extractDataFromDBorFileSystem(directory, result);
        if (result.size() != 0 && isASearchedFromFileSystem) {
            folderRepository.writeAllFoundedDirectoriesIntoDB(result);
        }
        isASearchedFromFileSystem = false;
        return result;
    //return currentDirectories(directory);
  }

  private void extractDataFromDBorFileSystem(String directory, Set<Folder> result) {
//    if (folderRepository.isFolderPresentInDB(directory)) {
//      String sqlForChilds = folderRepository.extractSqlForChildFolders(directory);
//      folderRepository.getChildFoldersFromDatabase(result, sqlForChilds);
//    } else {
      isASearchedFromFileSystem = true;
      result.addAll(currentDirectories(directory));
    //}
  }

  // Возвращает список директорий в папке
  private List<Folder> currentDirectories(String path) {
    List<Folder> result = null;

    // Список файлов текущей директории
    String[] currentFiles = new File(path).list();
    if (currentFiles != null) {
      result = Arrays.stream(currentFiles)
              .map(fileOrDirectoryName ->
                      getFileFromFullNameFileOrDirectory(path, fileOrDirectoryName))
              .filter(File::isDirectory)
              .map(directory -> Folder.builder().directory(new File(directory.getAbsolutePath())).build())
              .collect(Collectors.toList());
    }
    return result;
  }

  private File getFileFromFullNameFileOrDirectory(String fileOrDirectoryPath,
                                                  String fileOrDirectoryName) {
    String path = fileOrDirectoryPath + "\\" + fileOrDirectoryName;
    return new File(path);
  }
}
