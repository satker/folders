package examplebeans.dao.impl;

import examplebeans.dao.FolderDao;
import examplebeans.model.FolderManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FolderDaoImpl implements FolderDao {

  private static final String SELECT_NAME_FOLDER_FROM_FOLDER_WHERE_ID_PARENT = "SELECT id, name_folder FROM folder where id_parent = ?";
  private static final String INSERT_INTO_FOLDER_WITH_VALUES = "INSERT INTO folder (name_folder, id_parent) VALUES (?, ?)";
  private static final String SELECT_ID_FROM_FOLDER_WHERE_NAME_FOLDER = "SELECT id FROM folder WHERE name_folder = ?";
  private static final String UPDATE_FOLDER_SET_NAME_FOLDER_WHERE_ID = "UPDATE folder SET name_folder = ? WHERE id = ?;";
  private static final String DELETE_FROM_FOLDER_WHERE_NAME_FOLDER = "DELETE FROM folder where name_folder = ?";
  private JdbcTemplate jdbcTemplate;
  private boolean isFirstStep = true;

  @Autowired
  private void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }


  public void isFolderPresentInDB() {
    //jdbcTemplate.execute("DROP TABLE folder");
    createTableIfNotExists();
    //jdbcTemplate.execute("TRUNCATE folder");
  }

  public void writeAllFoundedDirectoriesIntoDB(Set<FolderManager> result, String directory) {
    for (FolderManager folderManager : result) {
      Integer idParent = getIdParent(directory);
      String absolutePath = folderManager.getDirectory()
                                         .getAbsolutePath();
      try {
        jdbcTemplate.update(INSERT_INTO_FOLDER_WITH_VALUES,
            absolutePath, idParent);
      } catch (DuplicateKeyException e) {

      }
    }
  }

  public void addNewFolder(String parentFolderDirectory, String newFolderDirectory) {
    Integer idParent = getIdParent(parentFolderDirectory);
    jdbcTemplate.update(INSERT_INTO_FOLDER_WITH_VALUES, newFolderDirectory, idParent);
  }

  public void editFolderName(String directoryOldFolder, String directoryNewFolder) {
    Integer idFolder = getIdByDirectoryName(directoryOldFolder);
    updateChildsWithNewNameParent(directoryOldFolder, directoryNewFolder, idFolder);
    jdbcTemplate.update(UPDATE_FOLDER_SET_NAME_FOLDER_WHERE_ID, directoryNewFolder, idFolder);
  }

  private void updateChildsWithNewNameParent(String directoryOldFolder, String directoryNewFolder,
                                             Integer idFolder) {
    List<FolderManager> childFoldersFromIdParent = getChildFoldersFromIdParent(idFolder);
    if (childFoldersFromIdParent != null) {
      childFoldersFromIdParent.forEach(folder -> {
        String newDirectory = folder.getDirectory()
                                    .getAbsolutePath()
                                    .replace(
                                        directoryOldFolder,
                                        directoryNewFolder);
        folder.setDirectory(new File(newDirectory));
        updateChildsWithNewNameParent(directoryOldFolder, directoryNewFolder, folder.getId());
      });
      childFoldersFromIdParent.forEach(
          newFolder -> jdbcTemplate.update(UPDATE_FOLDER_SET_NAME_FOLDER_WHERE_ID,
              newFolder.getDirectory()
                       .getAbsolutePath(),
              newFolder.getId()));
    }
  }

  private List<FolderManager> getChildFoldersFromIdParent(Integer idParent) {
    List<FolderManager> result = null;
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(
        SELECT_NAME_FOLDER_FROM_FOLDER_WHERE_ID_PARENT, idParent);
    if (!rows.isEmpty()) {
      result = new ArrayList<>();
      for (Map row : rows) {
        FolderManager folderManager = new FolderManager();
        String nameFolder = (String) (row.get("name_folder"));
        Integer id = (Integer) (row.get("id"));
        folderManager.setId(id);
        folderManager.setDirectory(new File(nameFolder));
        result.add(folderManager);
      }
    }
    return result;
  }

  public void moveFolderToAnotherRepository(String directoryFolder, String directoryFolderTo,
                                            String directoryFolderFrom) {
    Integer idFolder = getIdByDirectoryName(directoryFolder);
    jdbcTemplate.update(INSERT_INTO_FOLDER_WITH_VALUES, directoryFolderTo, idFolder);
    jdbcTemplate.update(DELETE_FROM_FOLDER_WHERE_NAME_FOLDER, directoryFolderFrom);
  }

  private Integer getIdParent(String directory) {
    try {
      return getIdByDirectoryName(directory);
    } catch (EmptyResultDataAccessException e) {
      if (isFirstStep) {
        jdbcTemplate.update(INSERT_INTO_FOLDER_WITH_VALUES, directory, 0);
        isFirstStep = false;
      }
      return getIdByDirectoryName(directory);
    }
  }

  private Integer getIdByDirectoryName(String directory) {
    return jdbcTemplate.queryForObject(
        SELECT_ID_FROM_FOLDER_WHERE_NAME_FOLDER,
        new Object[]{directory}, Integer.class);
  }

  private void createTableIfNotExists() {
    String sql = "CREATE TABLE IF NOT EXISTS folder ( " +
        " id INTEGER PRIMARY KEY AUTO_INCREMENT," +
        " name_folder VARCHAR(255) NOT NULL unique, " +
        " id_parent INTEGER);";
    jdbcTemplate.execute(sql);
  }
}
