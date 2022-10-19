package edu.java.spring.controller;

import edu.java.spring.dao.StudentDAO;
import edu.java.spring.dao.impl.StudentDAOImpl;
import edu.java.spring.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Controller
public class StudentController {
    private StudentDAOImpl studentDAO;

    @Autowired
    public void setStudentDAO(StudentDAOImpl studentDAO) {
        this.studentDAO = studentDAO;
    }

    @RequestMapping(value = "student/add", method = RequestMethod.GET)
    public ModelAndView add() {
        ModelAndView mv = new ModelAndView("student.form", "command", new Student());
        return mv;
    }

    @RequestMapping(value = "student/save", method = RequestMethod.POST)
    public ModelAndView save(@Valid @ModelAttribute("command") Student student, BindingResult result) {
        if (result.hasErrors()) {
            ModelAndView model = new ModelAndView("student.form", "command", student);
            model.addObject("errors", result);
            return model;
        }
        if (student.getId() > 0) studentDAO.update(student);
        else studentDAO.insert(student);

        return new ModelAndView("redirect:/student/list");
    }

    @RequestMapping(value = "/student/list")
    public ModelAndView listStudent(@RequestParam(value = "q", required = false) String query) {
        ModelAndView model = new ModelAndView("student.list");
        model.addObject("students", studentDAO.list(query));
        return model;
    }

    @RequestMapping(value = "/student/delete/{id}")
    public String delete(@PathVariable String id) {
        studentDAO.delete(id);
        return "redirect:/student/list";
    }

    @RequestMapping(value = "/student/edit/{id}")
    public ModelAndView edit(@PathVariable String id) {
        Student student = studentDAO.get(id);
        return new ModelAndView("../student.form", "command", student);
    }

    @RequestMapping(value = "/student/edit/save", method = RequestMethod.POST)
    public ModelAndView saveEdit() {
        return new ModelAndView("forward:/student/save");
    }

    @RequestMapping(value = "/student/json/{id}", method = RequestMethod.GET)
    public @ResponseBody
    Student viewJson(@PathVariable String id) {
        return studentDAO.get(id);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(){
        return "redirect:/student/list";
    }

    @RequestMapping(value = "/student/avatar/save", method = RequestMethod.POST)
    public String handleFormUpload(@RequestParam("file") MultipartFile file, int id, HttpServletRequest request) {
        if (file.isEmpty()) return "../student.error";
        try {
            byte[] bytes = file.getBytes();
            Path avatarFile = getImageFile(request, id);
            Files.write(avatarFile, file.getBytes(), StandardOpenOption.CREATE);
            System.out.println(avatarFile);
            System.out.println("found--------> " + bytes.length);
            return "redirect:/student/list";
        } catch (IOException e) {
            return "../student.error";
        }

    }

    private Path getImageFile(HttpServletRequest request, int id){
        ServletContext servletContext = request.getSession().getServletContext();
        String diskPath = servletContext.getRealPath("/");
        File folder = new File(diskPath + File.separator + "avatar" + File.separator);
        folder.mkdirs();
        return new File(folder, String.valueOf(id) + ".jpg").toPath();
    }

    @RequestMapping(value = "/student/avatar/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Integer id, HttpServletRequest request) throws IOException {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        if(id != null){
            Path avatarPath = getImageFile(request, id);
            if(Files.exists(avatarPath)) byteOutput.write(Files.readAllBytes(avatarPath));

        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<byte[]>(byteOutput.toByteArray(), headers, HttpStatus.CREATED);
    }
}
