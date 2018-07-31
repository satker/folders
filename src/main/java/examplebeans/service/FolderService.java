package examplebeans.service;

import examplebeans.dao.Folder;
import examplebeans.repository.FolderRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import org.h2.store.fs.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FolderService {

  private FolderRepository folderRepository;

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

  @Autowired
  public void setFolderRepository(FolderRepository folderRepository) {
    this.folderRepository = folderRepository;
  }

  public List<Folder> getAllForFolder(String folder) {
    String directory = getDirectoryFolder(folder);
    return folderRepository.getAllDirectoriesFromFolder(directory);
  }

  private String getDirectoryFolder(String folder) {
    String basicFolder = "D:\\java_projects\\folders\\src\\main\\resources\\examplefolders";
    return folder == null ? basicFolder : basicFolder + "\\" + getDirectoryOfFolder(folder);
  }

  private String getDirectoryOfFolder(String folder) {
    return folder.replaceAll("->", "\\\\")
                 .replaceAll(" ", "");
  }

  public List<String> getStringCollectionFromFolder(List<Folder> allForFolder) {
    return allForFolder.stream()
                       .
                           map(Folder::getDirectory)
                       .
                           map(File::getName)
                       .
                           map(folder -> folder.split("\\\\"))
                       .
                           map(flatFolder -> flatFolder[flatFolder.length - 1])
                       .
                           collect(Collectors.toList());
  }

  private String getJSONFromStringFolders(List<String> allForFolder) {
    return "[" + allForFolder.stream()
                             .map(folder -> jsonPart1.concat(folder)
                                                     .concat(jsonPart2))
                             .
                                 collect(Collectors.joining(", ")) + "]";
  }

  public String getJSONChildesFromParentDirectory(List<Folder> allForFolder) {
    if (allForFolder != null) {
      List<String> stringCollectionFromFolder = getStringCollectionFromFolder(allForFolder);
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
    String directoryFolderTo = getDirectoryFolder(to);
    FileUtils.move(directoryFolderFrom, directoryFolderTo);
    System.out.println(directoryFolderFrom + " moved to " + directoryFolderTo);
  }


  public void editFolderName(String oldFolder, String newFolder) {
    String directoryOldFolder = getDirectoryFolder(oldFolder);
    String directoryNewFOlder = getDirectoryFolder(newFolder);
    File oldDirectory = new File(directoryOldFolder);
    File newDirectory = new File(directoryNewFOlder);

    System.out.println(oldDirectory+ " rename to "+newDirectory + " result: " + oldDirectory.renameTo(newDirectory));

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
}
