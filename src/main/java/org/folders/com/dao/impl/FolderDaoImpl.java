package org.folders.com.dao.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.folders.com.dao.FolderDao;
import org.folders.com.model.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FolderDaoImpl implements FolderDao {

    private static final Logger log = Logger.getLogger(FolderDaoImpl.class);

    @Autowired
    public FolderDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final JdbcTemplate jdbcTemplate;

    public void writeAllFoundedDirectoriesIntoDB(List<Folder> result, String directory) {
        Integer idParent = getIdParent(directory);
        String sqlForInsertValues = "INSERT INTO folder (name_folder, id_parent) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE id_parent = id_parent;";
        jdbcTemplate.batchUpdate(sqlForInsertValues,
                result, result.size(), (ps, folder) -> {
                    ps.setString(1, folder.getDirectory().getAbsolutePath());
                    ps.setInt(2, idParent);
                });
    }

    public void removeParentFolderAndChildFolders(String directory) {
        String sqlForGetFoldersWithoutParent = "select id from folder "
            + "where id_parent not IN (SELECT id FROM FOLDER) "
            + "and id_parent <> 0;";
        jdbcTemplate.update("delete from folder where id = ?", getIdByDirectoryName(directory));
        List<Integer> idsFodlersWithoutParent = jdbcTemplate.query(sqlForGetFoldersWithoutParent,
            (var1, var2) -> var1.getInt("id"));
        jdbcTemplate.batchUpdate("delete from folder where id = ?", idsFodlersWithoutParent,
            idsFodlersWithoutParent.size(), (ps, var) -> ps.setInt(1, var));

    }

    public void moveFolderToAnotherRepository(String directoryFolderTo,
                                              String directoryFolderFrom, String to) {
        Integer idParent = getIdParent(to);
        updateChildsAndParentRecursively(directoryFolderFrom, directoryFolderTo, idParent);
    }

    public void addNewFolder(String parentFolderDirectory, String newFolderDirectory) {
        Integer idParent = getIdParent(parentFolderDirectory);
        jdbcTemplate.update("INSERT INTO folder (name_folder, id_parent) VALUES (?, ?)", newFolderDirectory, idParent);
    }

    public void updateFoldersNameAfterEditFolderName(String directoryOldFolder, String directoryNewFolder) {
        Integer idParent = jdbcTemplate.queryForObject(
                "SELECT id_parent FROM folder WHERE name_folder = ?",
                Integer.class, directoryOldFolder);
        updateChildsAndParentRecursively(directoryOldFolder, directoryNewFolder, idParent);
    }

    private void updateChildsAndParentRecursively (String directoryOldFolder, String directoryNewFolder, Integer idParent) {
        Integer idFolder = getIdByDirectoryName(directoryOldFolder);
        updateParent(directoryOldFolder, directoryNewFolder, idParent);
        updateChildes(directoryOldFolder, directoryNewFolder,
                idFolder);
    }

    private void updateParent(String directoryOldFolder, String directoryNewFolder, Integer idParent) {
        String sqlForInsertNewValues = "INSERT INTO folder (name_folder, id_parent) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE id_parent = id_parent;";
        String sqlForDelete = "DELETE FROM folder WHERE name_folder = ?;";
        jdbcTemplate.update(sqlForInsertNewValues, directoryNewFolder, idParent);
        jdbcTemplate.update(sqlForDelete, directoryOldFolder);
    }

    private void updateChildes(String directoryOldFolder, String directoryNewFolder,
                               Integer idFolder) {
        objectsForBatchUpdate.clear();
        Integer idByDirectoryName = getIdByDirectoryName(directoryNewFolder);
        getListOfObjectsForUpdate(directoryOldFolder, directoryNewFolder, idFolder, idByDirectoryName);
        updateRecursivelyByListOfObjects();
    }

    private void updateRecursivelyByListOfObjects() {
        String sqlForInsertNewValues = "INSERT INTO folder (name_folder, id_parent) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE id_parent = id_parent;";
        String sqlForDelete = "DELETE FROM folder WHERE id = ?;";
        jdbcTemplate.batchUpdate(sqlForInsertNewValues,
                objectsForBatchUpdate,
                objectsForBatchUpdate.size(),
                ((ps, argument) -> {
                    ps.setString(1, (String) argument[0]);
                    ps.setInt(2, (Integer) argument[2]);
                }));
        jdbcTemplate.batchUpdate(sqlForDelete,
                objectsForBatchUpdate,
                objectsForBatchUpdate.size(),
                (ps, objectWithId) -> ps.setInt(1, (Integer) objectWithId[1]));
    }


    private List<Object[]> objectsForBatchUpdate = new ArrayList<>();

    private List<Object[]> getListOfObjectsForUpdate(String directoryOldFolder, String directoryNewFolder, Integer idOldFolder, Integer idNewFolder) {
        List<Folder> childFoldersFromIdParent = getChildFoldersFromIdParent(idOldFolder);
        childFoldersFromIdParent.forEach(folder -> {
            String newDirectory = folder.getDirectory()
                    .getAbsolutePath()
                    .replace(directoryOldFolder,
                            directoryNewFolder);
            folder.setDirectory(new File(newDirectory));
            objectsForBatchUpdate.add(new Object[]{newDirectory, folder.getId(), idNewFolder});
            getListOfObjectsForUpdate(directoryOldFolder, directoryNewFolder, folder.getId(), folder.getId());
        });
        return objectsForBatchUpdate;
    }

    private List<Folder> getChildFoldersFromIdParent(Integer idParent) {
        return jdbcTemplate.query("SELECT id, name_folder FROM folder where id_parent = ?",
                (var1, var2) -> Folder.builder()
                        .id(var1.getInt("id"))
                        .directory(new File(var1.getString("name_folder")))
                        .build(), idParent);
    }

    private Integer getIdParent(String directory) {
        try {
            return getIdByDirectoryName(directory);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Folder don't present in DB, try to create it.");
            jdbcTemplate.update("INSERT INTO folder (name_folder, id_parent) VALUES (?, ?)", directory, 0);
            return getIdByDirectoryName(directory);
        }
    }

    private Integer getIdByDirectoryName(String directory) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM folder WHERE name_folder = ?",
                Integer.class, directory);
    }
}
