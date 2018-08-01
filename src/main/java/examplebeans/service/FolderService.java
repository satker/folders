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

  private int countIterators = 0;

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
    String basicFolder = "C:\\Users\\Artem_Kunats\\IdeaProjects\\folders\\src\\main\\resources";
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
    try {
      Files.move(Paths.get(directoryFolderFrom), Paths.get(directoryFolderTo));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public void editFolderName(String oldFolder, String newFolder) {
    String directoryOldFolder = getDirectoryFolder(oldFolder);
    String directoryNewFOlder = getDirectoryFolder(newFolder);
    File oldDirectory = new File(directoryOldFolder);
    File newDirectory = new File(directoryNewFOlder);
    oldDirectory.renameTo(newDirectory);
  }

  public void addNewNode(String newFolder) {
    String directoryFolderForAdd = getDirectoryFolder(newFolder);
    Path pathForAdd = Paths.get(directoryFolderForAdd);
    try {
      String parentDirectoryFromChild = getParentDirectoryFromChild(directoryFolderForAdd);
      folderRepository.addNewFolder(parentDirectoryFromChild, directoryFolderForAdd);
      Files.createDirectories(pathForAdd);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String getParentDirectoryFromChild (String newDirectory) {
    String[] splitStrings = newDirectory.split("\\\\");
    splitStrings[splitStrings.length - 1] = "";
    return String.join("\\\\", splitStrings);
  }

  private Set<Folder> getAllDirectoriesFromFolder(String directory) {
        if (countIterators == 0){
          folderRepository.isFolderPresentInDB();
        }
        countIterators++;
        Set<Folder> result = new HashSet<>(currentDirectories(directory));
        if (!result.isEmpty()) {
            folderRepository.writeAllFoundedDirectoriesIntoDB(result, directory);
        }
        return result;
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
    String path = String.format("%s\\%s", fileOrDirectoryPath, fileOrDirectoryName);
    return new File(path);
  }
}
