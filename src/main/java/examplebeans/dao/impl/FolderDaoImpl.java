package examplebeans.dao.impl;

import examplebeans.dao.FolderDao;
import examplebeans.model.Folder;
import examplebeans.service.impl.FolderServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class FolderDaoImpl implements FolderDao {
    private JdbcTemplate jdbcTemplate;
    private boolean isFirstStep = true;
    private static final Logger log = Logger.getLogger(FolderServiceImpl.class);

    @Autowired
    private void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void writeAllFoundedDirectoriesIntoDB(Set<Folder> result, String directory) {
        Integer idParent = getIdParent(directory);
        for (Folder folder : result) {
            String absolutePath = folder.getDirectory()
                    .getAbsolutePath();
            try {
                jdbcTemplate.update("INSERT INTO folder (name_folder, id_parent) VALUES (?, ?)",
                        absolutePath, idParent);
            } catch (DuplicateKeyException e) {
                log.warn("Try to add existing folder.");
            }
        }
    }

    public void addNewFolder(String parentFolderDirectory, String newFolderDirectory) {
        Integer idParent = getIdParent(parentFolderDirectory);
        jdbcTemplate.update("INSERT INTO folder (name_folder, id_parent) VALUES (?, ?)", newFolderDirectory, idParent);
    }

    public void editFolderName(String directoryOldFolder, String directoryNewFolder) {
        Integer idFolder = getIdByDirectoryName(directoryOldFolder);
        updateChildsWithNewNameParent(directoryOldFolder, directoryNewFolder, idFolder);
        jdbcTemplate.update("UPDATE folder SET name_folder = ? WHERE id = ?;", directoryNewFolder, idFolder);
    }

    private void updateChildsWithNewNameParent(String directoryOldFolder, String directoryNewFolder,
                                               Integer idFolder) {
        objectsForBatchUpdate.clear();
        getSqlForBatchUpdate(directoryOldFolder, directoryNewFolder, idFolder);
        jdbcTemplate.batchUpdate("UPDATE folder SET name_folder = ? WHERE id = ?;", objectsForBatchUpdate);
    }

    private List<Object[]> objectsForBatchUpdate = new ArrayList<>();

    private void getSqlForBatchUpdate(String directoryOldFolder, String directoryNewFolder, Integer idFolder) {
        List<Folder> childFoldersFromIdParent = getChildFoldersFromIdParent(idFolder);
        childFoldersFromIdParent.forEach(folder ->
                objectsForBatchUpdate.add(updateChildFolderAndDBAfterMoving(directoryOldFolder, directoryNewFolder, folder)));
    }

    private Object[] updateChildFolderAndDBAfterMoving(String directoryOldFolder, String directoryNewFolder, Folder folder) {
        String newDirectory = folder.getDirectory()
                .getAbsolutePath()
                .replace(directoryOldFolder,
                        directoryNewFolder);
        folder.setDirectory(new File(newDirectory));
        getSqlForBatchUpdate(directoryOldFolder, directoryNewFolder, folder.getId());
        return new Object[]{newDirectory, folder.getId()};
    }

    private List<Folder> getChildFoldersFromIdParent(Integer idParent) {
        return jdbcTemplate.query("SELECT id, name_folder FROM folder where id_parent = ?",
                (var1, var2) -> Folder.builder()
                        .id(var1.getInt("id"))
                        .directory(new File(var1.getString("name_folder")))
                        .build(), idParent);
    }

    public void moveFolderToAnotherRepository(String directoryFolderTo,
                                              String directoryFolderFrom) {
        editFolderName(directoryFolderFrom, directoryFolderTo);
    }

    private Integer getIdParent(String directory) {
        try {
            return getIdByDirectoryName(directory);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Folder don't present in DB, try to create it.");
            if (isFirstStep) {
                jdbcTemplate.update("INSERT INTO folder (name_folder, id_parent) VALUES (?, ?)", directory, 0);
                isFirstStep = false;
            }
            return getIdByDirectoryName(directory);
        }
    }

    private Integer getIdByDirectoryName(String directory) {
        return jdbcTemplate.queryForObject(
            "SELECT id FROM folder WHERE name_folder = ?",
                Integer.class, directory);
    }
}
