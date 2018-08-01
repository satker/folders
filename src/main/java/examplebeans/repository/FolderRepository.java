package examplebeans.repository;

import examplebeans.dao.Folder;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Set;

@Repository
public class FolderRepository {

    public static final String INSERT_INTO_FOLDER_WITH_VALUES = "INSERT INTO folder (name_folder, id_parent) VALUES (?, ?)";

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private JdbcTemplate jdbcTemplate;

    public void isFolderPresentInDB() {
        try {
            jdbcTemplate.execute("TRUNCATE folder");
        } catch (BadSqlGrammarException | IncorrectResultSizeDataAccessException e) {
                createTableIfNotExists();
        }
    }

    public void writeAllFoundedDirectoriesIntoDB(Set<Folder> result, String directory) {
        for (Folder folder : result) {
            Integer idParent = getIdParent(directory);
            String absolutePath = folder.getDirectory()
                                        .getAbsolutePath();
            jdbcTemplate.update(INSERT_INTO_FOLDER_WITH_VALUES,
                absolutePath, idParent);
        }
    }

//    public void getChildFoldersFromDatabase(Set<Folder> result, String sqlForChilds) {
//        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlForChilds);
//        for (Map row : rows) {
//            Folder folder = new Folder();
//            String name_folder = (String) (row.get("name_folder"));
//            folder.setDirectory(new File(name_folder));
//            result.add(folder);
//        }
//    }

//    public String extractSqlForChildFolders(String directory) {
//        try {
//            idParent = getIdParent(directory);
//            return "SELECT * FROM folder WHERE id_parent = " + idParent;
//        } catch (EmptyResultDataAccessException | NullPointerException e) {
//            String sql = "INSERT INTO folder (name_folder) VALUES (?)";
//            jdbcTemplate.update(sql, directory);
//            idParent = getIdParent(directory);
//            return null;
//        }
//    }

    public void addNewFolder(String parentFolderDirectory, String newFolderDirectory) {
        Integer idParent = getIdParent(parentFolderDirectory);
        jdbcTemplate.update(INSERT_INTO_FOLDER_WITH_VALUES, newFolderDirectory, idParent);
    }

    private Integer getIdParent(String directory) {
        try {
            String s = "SELECT id FROM folder WHERE name_folder = '" + directory + "';";
            System.out.println(s);
            List<String> query = jdbcTemplate.query(s, new RowMapper() {
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString(1);
                }
            });
            query.forEach(System.out::println);
            return Integer.t;
        } catch (EmptyResultDataAccessException e){
            jdbcTemplate.update(INSERT_INTO_FOLDER_WITH_VALUES, directory, 0);
            return jdbcTemplate.queryForObject(
                "SELECT id FROM folder WHERE name_folder = ?",
                new Object[]{directory}, Integer.class);
        }
    }

    private void createTableIfNotExists() {
            String sql = "CREATE TABLE IF NOT EXISTS folder ( " +
                    " id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                    " name_folder VARCHAR(255) NOT NULL, " +
                    " id_parent INTEGER);";
            jdbcTemplate.execute(sql);
    }
}
