package examplebeans.controllers;

import examplebeans.dao.Folder;
import examplebeans.service.FolderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class FolderController {
    private FolderService folderService;

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public ModelAndView getHello(){
        List<Folder> allForFolder = folderService.getAllForFolder(null);
        List<String> collect = allForFolder.stream().
                map(Folder::getDirectory).
                map(File::getName).
                map(folder -> folder.split("\\\\")).
                map(flatFolder -> flatFolder[flatFolder.length - 1]).
                collect(Collectors.toList());
        ModelAndView model = new ModelAndView("index");
        model.addObject("list", collect);
        return model;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/")
    public ModelAndView getNextFolder(@RequestParam(value = "text", required = false) String text){
        System.out.println(text);
        List<Folder> allForFolder = folderService.getAllForFolder(null);
        List<String> collect = allForFolder.stream().
                map(Folder::getDirectory).
                map(File::getName).
                map(folder -> folder.split("\\\\")).
                map(flatFolder -> flatFolder[flatFolder.length - 1]).
                collect(Collectors.toList());
        ModelAndView model = new ModelAndView("index");
        model.addObject("list", collect);
        return model;
    }
}
