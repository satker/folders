package examplebeans.controllers;

import examplebeans.service.FolderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@AllArgsConstructor
public class FolderController {
    private final FolderService folderService;

    @GetMapping
    public ModelAndView getFirstFolders() {
        List<String> collect = folderService.getChildFoldersByParent(null);
        ModelAndView model = new ModelAndView("WEB-INF/jsp/index.jsp");
        model.addObject("list", collect);
        return model;
    }

    @PostMapping(value = "/{folder}")
    public String getNextFolders(@PathVariable("folder") String folder) {
        String jsonOfChildsByParent = folderService.getJsonOfChildsByParent(folder);
        return jsonOfChildsByParent;
    }

    @DeleteMapping(value = "/{folder}")
    public void removeFolder(@PathVariable("folder") String folder) {
        folderService.removeNode(folder);
    }

    @PutMapping(value = "/{from}/{to}")
    public void moveFolder(@PathVariable("from") String from,
                           @PathVariable("to") String to) {
        folderService.moveNode(from, to);
    }

    @PostMapping(value = "/{oldFolder}/{newFolder}")
    public void editFolderName(@PathVariable("oldFolder") String oldFolder,
                               @PathVariable("newFolder") String newFolder) {
        folderService.editFolderName(oldFolder, newFolder);
    }

    @PutMapping(value = "/{folder}")
    public void addNewNode(@PathVariable("folder") String folder) {
        folderService.addNewFolder(folder);
    }
}
