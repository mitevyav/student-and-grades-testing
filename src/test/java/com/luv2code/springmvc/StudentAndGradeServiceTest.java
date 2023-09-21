package com.luv2code.springmvc;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.HistoryGradesDao;
import com.luv2code.springmvc.repository.MathGradesDao;
import com.luv2code.springmvc.repository.ScienceGradesDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@TestPropertySource("/application.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MathGradesDao mathGradesDao;

    @Autowired
    private ScienceGradesDao scienceGradesDao;

    @Autowired
    private HistoryGradesDao historyGradesDao;

    @Value("${sql.script.create.student}")
    private String sqlAddStudent;

    @Value("${sql.script.create.math.grade}")
    private String sqlAddMathGrade;

    @Value("${sql.script.create.science.grade}")
    private String sqlAddScienceGrade;

    @Value("${sql.script.create.history.grade}")
    private String sqlAddHistoryGrade;

    @Value("${sql.script.delete.student}")
    private String sqlDeleteStudent;

    @Value("${sql.script.delete.math.grade}")
    private String sqlDeleteMathGrade;

    @Value("${sql.script.delete.science.grade}")
    private String sqlDeleteScienceGrade;

    @Value("${sql.script.delete.history.grade}")
    private String sqlDeleteHistoryGrade;


    @BeforeEach
    public void setupDatabase() {
        jdbcTemplate.execute(sqlAddStudent);
        jdbcTemplate.execute(sqlAddMathGrade);
        jdbcTemplate.execute(sqlAddScienceGrade);
        jdbcTemplate.execute(sqlAddHistoryGrade);

    }

    @Test
    public void createStudentService() {
        studentService.createStudent("Chad", "Darby", "chad.darby@luv2code_school.com");

        CollegeStudent student = studentDao.findByEmailAddress("chad.darby@luv2code_school.com");

        assertEquals("chad.darby@luv2code_school.com", student.getEmailAddress(), "find by emaill");
    }

    @Test
    public void isStudentNullCheck() {

        assertTrue(studentService.checkIfStudentIsNull(1));

        assertFalse(studentService.checkIfStudentIsNull(0));
    }

    @Test
    public void deleteStudentService() {
        Optional<CollegeStudent> deleteCollegeStudent = studentDao.findById(1);
        Optional<MathGrade> deleteMathGrade = mathGradesDao.findById(1);
        Optional<ScienceGrade> deleteScienceGrade = scienceGradesDao.findById(1);
        Optional<HistoryGrade> deleteHistoryGrade = historyGradesDao.findById(1);

        assertTrue(deleteCollegeStudent.isPresent(), "Return true");
        assertTrue(deleteMathGrade.isPresent());
        assertTrue(deleteScienceGrade.isPresent());
        assertTrue(deleteHistoryGrade.isPresent());

        studentService.deleteStudent(1);

        deleteCollegeStudent = studentDao.findById(1);
        deleteMathGrade = mathGradesDao.findById(1);
        deleteScienceGrade = scienceGradesDao.findById(1);
        deleteHistoryGrade = historyGradesDao.findById(1);

        assertFalse(deleteCollegeStudent.isPresent(), "Return false");
        assertFalse(deleteMathGrade.isPresent());
        assertFalse(deleteScienceGrade.isPresent());
        assertFalse(deleteHistoryGrade.isPresent());
    }

    @Test
    public void getGradebookService() {
        Iterable<CollegeStudent> iterableCollegeStudents = studentService.getGradebook();

        List<CollegeStudent> collegeStudents = new ArrayList<>();

        for (CollegeStudent collegeStudent : iterableCollegeStudents) {
            collegeStudents.add(collegeStudent);
        }

        assertEquals(1, collegeStudents.size());
    }

    @Test
    public void createGradeService() {
        assertTrue(studentService.createGrade(85.0, 1, "math"));
        assertTrue(studentService.createGrade(85.0, 1, "science"));
        assertTrue(studentService.createGrade(85.0, 1, "history"));

        Iterable<MathGrade> mathGrades = mathGradesDao.findGradeByStudentId(1);
        Iterable<ScienceGrade> scienceGrades = scienceGradesDao.findGradeByStudentId(1);
        Iterable<HistoryGrade> historyGrades = historyGradesDao.findGradeByStudentId(1);

        assertTrue(((Collection<MathGrade>) mathGrades).size() == 2, "Student has two math grades");
        assertTrue(((Collection<ScienceGrade>) scienceGrades).size() == 2, "Student has two science grades");
        assertTrue(((Collection<HistoryGrade>) historyGrades).size() == 2, "Student has two history grades");
    }

    @Test
    public void createGradeServiceReturnFalse() {
        assertFalse(studentService.createGrade(105, 1, "math"));
        assertFalse(studentService.createGrade(-5, 1, "science"));
        assertFalse(studentService.createGrade(85.0, 2, "history"));
        assertFalse(studentService.createGrade(85.0, 1, "literature"));
    }

    @Test
    public void deleteGradeService() {
        assertEquals(1, studentService.deleteGrade(1, "math"), "Return student id after the delete");
        assertEquals(1, studentService.deleteGrade(1, "science"), "Return student id after the delete");
        assertEquals(1, studentService.deleteGrade(1, "history"), "Return student id after the delete");
    }

    @Test
    public void deleteGradeServiceReturnStudentIdOfZero() {
        assertEquals(0, studentService.deleteGrade(0, "science"), "No student should have id of 0");
        assertEquals(0, studentService.deleteGrade(1, "literature"), "No student should have grade type of literature");
    }

    @Test
    public void studentInformation() {
        GradebookCollegeStudent gradebookCollegeStudent = studentService.studentInformation(1);

        assertNotNull(gradebookCollegeStudent);
        assertEquals(1, gradebookCollegeStudent.getId());
        assertEquals("Eric", gradebookCollegeStudent.getFirstname());
        assertEquals("Roby", gradebookCollegeStudent.getLastname());
        assertEquals("eric.roby@luv2code_school.com", gradebookCollegeStudent.getEmailAddress());
        assertTrue(gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size() == 1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size() == 1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size() == 1);
    }

    @Test
    public void studentInformationReturnNull() {
        GradebookCollegeStudent gradebookCollegeStudent = studentService.studentInformation(0);

        assertNull(gradebookCollegeStudent);
    }

    @AfterEach
    public void setupAfterTransaction() {
        jdbcTemplate.execute(sqlDeleteStudent);
        jdbcTemplate.execute(sqlDeleteMathGrade);
        jdbcTemplate.execute(sqlDeleteScienceGrade);
        jdbcTemplate.execute(sqlDeleteHistoryGrade);
    }
}
