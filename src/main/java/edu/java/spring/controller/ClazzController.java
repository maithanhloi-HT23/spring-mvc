package edu.java.spring.controller;

import edu.java.spring.dao.impl.StudentDAOImpl;
import edu.java.spring.model.JavaClazz;
import edu.java.spring.utils.XSLTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Controller
public class ClazzController {
    private StudentDAOImpl studentDAO;

    @Autowired
    public void setStudentDAO(StudentDAOImpl studentDAO) {
        this.studentDAO = studentDAO;
    }


    @RequestMapping(value = "/clazz/xml", produces = {MediaType.APPLICATION_ATOM_XML_VALUE})
    public @ResponseBody
    JavaClazz viewInXML() {
        return new JavaClazz(studentDAO.list(""));
    }

    @RequestMapping(value = "/clazz/json", produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    JavaClazz viewInJSON() {
        return new JavaClazz(studentDAO.list(""));
    }

    @RequestMapping(value = "/clazz/xslt")
    public ModelAndView viewXSLT() throws JAXBException, ParserConfigurationException, IOException, SAXException {
        JavaClazz clazz = new JavaClazz(studentDAO.list(""));
        ModelAndView model = new ModelAndView("ClazzView");
        model.getModelMap().put("data", XSLTUtils.clazzToDomSource(clazz));
        return model;
    }
    @RequestMapping(value = "/clazz/excel")
    public ModelAndView viewExcel(){
        JavaClazz clazz = new JavaClazz(studentDAO.list(""));
        ModelAndView model = new ModelAndView("excelView");
        model.getModelMap().put("clazzObj", clazz);
        return model;
    }
}
