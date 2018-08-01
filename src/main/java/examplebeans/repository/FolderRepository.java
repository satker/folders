package examplebeans.repository;

import examplebeans.dao.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Repository
public class FolderRepository {
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private JdbcTemplate jdbcTemplate;
    private Integer idParent = 0;

    @PostConstruct
    public void isFolderPresentInDB() {
        try {
                jdbcTemplate.execute("TRUNCATE folder");

        } catch (BadSqlGrammarException | IncorrectResultSizeDataAccessException e) {
                createTableIfNotExists();
        }
    }

    public void writeAllFoundedDirectoriesIntoDB(Set<Folder> result) {
        String sql = "INSERT INTO folder (name_folder, id_parent) VALUES (?, ?)";
        result.stream().filter(Objects::nonNull).forEach(folder ->
                jdbcTemplate.update(sql, new Object[]{folder.getDirectory().getAbsolutePath(),
                        idParent
                }));
    }

    public void getChildFoldersFromDatabase(Set<Folder> result, String sqlForChilds) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlForChilds);
        for (Map row : rows) {
            Folder folder = new Folder();
            String name_folder = (String) (row.get("name_folder"));
            folder.setDirectory(new File(name_folder));
            result.add(folder);
        }
    }

    public String extractSqlForChildFolders(String directory) {
        try {
            idParent = getIdParent(directory);
            return "SELECT * FROM folder WHERE id_parent = " + idParent;
        } catch (EmptyResultDataAccessException | NullPointerException e) {
            String sql = "INSERT INTO folder (name_folder) VALUES (?)";
            jdbcTemplate.update(sql, directory);
            idParent = getIdParent(directory);
            return null;
        }
    }

    private Integer getIdParent(String directory) {
        return jdbcTemplate.queryForObject(
                "(SELECT id FROM folder WHERE name_folder = ?)",
                new Object[]{directory}, Integer.class);
    }

    public void createTableIfNotExists() {
            String sql = "CREATE TABLE IF NOT EXISTS folder ( " +
                    " id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                    " name_folder VARCHAR(255) NOT NULL, " +
                    " id_parent INTEGER);";
            jdbcTemplate.execute(sql);
    }
}
