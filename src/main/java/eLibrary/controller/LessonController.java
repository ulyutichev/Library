package eLibrary.controller;

import eLibrary.domain.Lesson;
import eLibrary.domain.SubGroup;
import eLibrary.domain.User;
import eLibrary.repos.LessonRepo;
import eLibrary.repos.SubGroupRepo;
import eLibrary.service.LessonService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/lesson")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    @GetMapping("/new")
    public String newLesson(
            Model model,
            @RequestParam(name = "teacherId") User teacher
    ){
        model.addAttribute("teacher", teacher);
        return "lessonCreate";
    }

    @PostMapping("/new")
    public String saveLesson(
            @Valid Lesson lesson,
            BindingResult bindingResult,
            Model model,
            @RequestParam(name = "teacherId") User teacher
    ){
        if(bindingResult.hasErrors()){
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            model.addAttribute("teacher", teacher);
            return "lessonCreate";
        }
        lessonService.saveLesson(lesson, teacher);

        model.addAttribute("teacher", teacher);
        return "lessonCreate";
    }



    @GetMapping("/list/{teacher}")
    public String teacherLesson(
            Model model,
            @PathVariable User teacher
    ){
        model.addAttribute("lessons", lessonService.getByTeacher(teacher));
        return "lessonList";
    }



    @GetMapping("/list")
    public String allLesson(Model model){
        model.addAttribute("lessons", lessonService.getAll());
        return "lessonList";
    }



    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @GetMapping("/myLessons")
    public String userLesson(
            Model model,
            @AuthenticationPrincipal User user
    ){
        model.addAttribute("lessons", lessonService.getByTeacher(user));
        return "lessonList";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    @PostMapping("/{lesson}")
    public String newSubGroup(
            @Valid SubGroup subGroup,
            BindingResult bindingResult,
            Model model,
            @PathVariable Lesson lesson
    ){
        if(bindingResult.hasErrors()){
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            model.addAttribute("lesson", lesson);
            model.addAttribute("subGroups", lesson.getSubGroups());
            return "lessonEdit";
        }
        lessonService.addSubGroup(subGroup, lesson);
        return "redirect:/lesson/"+lesson.getId();
    }



    @GetMapping("{lesson}")
    public String lessonEdit(
            Model model,
            @PathVariable Lesson lesson,
            @AuthenticationPrincipal User user
    ){
        model.addAttribute("lesson", lesson);
        model.addAttribute("subGroups", lesson.getSubGroups());
        model.addAttribute("myLesson", user.getId().equals(lesson.getTeacher().getId()));

        return "lessonEdit";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    @GetMapping("/{lesson}/delete/{subGroup}")
    public String deleteSubGroup(
            Model model,
            @PathVariable Lesson lesson,
            @PathVariable SubGroup subGroup
    ){
        lessonService.removeSubGroup(lesson, subGroup);

        return "redirect:/lesson/"+lesson.getId();
    }


}
