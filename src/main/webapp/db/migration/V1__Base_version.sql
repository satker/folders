CREATE TABLE IF NOT EXISTS folder (id INTEGER PRIMARY KEY AUTO_INCREMENT,
 name_folder VARCHAR(255) NOT NULL unique,
  id_parent INTEGER);