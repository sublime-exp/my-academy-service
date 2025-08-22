package com.sub.academy;

import com.sub.academy.entity.CourseType;
import com.sub.academy.rest.dto.request.CoursePatchDto;
import com.sub.academy.rest.dto.request.CourseRequestDto;
import com.sub.academy.rest.dto.request.StudentRequestDto;
import com.sub.academy.rest.dto.request.TeacherRequestDto;
import com.sub.academy.rest.dto.response.CourseResponseDto;
import com.sub.academy.rest.dto.response.StudentResponseDto;
import com.sub.academy.rest.dto.response.TeacherResponseDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AcademyApplicationTests {

    @LocalServerPort
    private int port;

    private final String COURSE_NAME = "aws_certified_developer_associate";
    private final String TEST_GROUP = "A";
    private final CourseType COURSE_TYPE = CourseType.MAIN;

    private UUID courseId;
    private UUID teacherId;
    private UUID assistantId;
    private UUID studentAId;
    private UUID studentBId;
    private UUID studentCId;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        loadData();
    }

    @Test
    void assert100CoursesWith300StudentsAnd3TeachersEachCreated() {

        UUID firstCourseId = null;

        for (int i = 0; i < 100; i++) {
            //first 50 courses are MAIN, last 50 are SECONDARY
            CourseType courseType = i < 50 ? CourseType.MAIN : CourseType.SECONDARY;
            String courseName = courseType.name() + "_course_" + i + 1;

            TeacherRequestDto teacher1 = new TeacherRequestDto("teacher_1_" + courseName, "A", Set.of());
            TeacherRequestDto teacher2 = new TeacherRequestDto("teacher_2_" + courseName, "B", Set.of());
            TeacherRequestDto teacher3 = new TeacherRequestDto("teacher_3_" + courseName, "C", Set.of());

            Set<StudentRequestDto> students = generateStudents(courseName);

            CourseRequestDto course = new CourseRequestDto(courseName, courseType,
                    Set.of(), students,
                    Set.of(), Set.of(teacher1, teacher2, teacher3));

            UUID id = assertCourseCreated(course).id();

            if (i == 0) {
                firstCourseId = id;
            }
        }

        RestAssured
                .given()
                .when()
                .queryParam("page", 0)
                .queryParam("size", 101)
                .queryParam("group", "A")
                .queryParam("courseId", firstCourseId)
                .get("/students/group-course-participation")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content.size()", equalTo(100));

        RestAssured
                .given()
                .when()
                .queryParam("page", 0)
                .queryParam("size", 101)
                .queryParam("group", "B")
                .queryParam("courseId", firstCourseId)
                .get("/students/group-course-participation")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content.size()", equalTo(100));

        RestAssured
                .given()
                .when()
                .queryParam("page", 0)
                .queryParam("size", 101)
                .queryParam("group", "C")
                .queryParam("courseId", firstCourseId)
                .get("/students/group-course-participation")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content.size()", equalTo(100));
    }

    @Test
    void assertAssistantRemovedFromCourse() {
        CourseResponseDto course = RestAssured
                .given()
                .when()
                .get("/courses/" + courseId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CourseResponseDto.class);

        CourseRequestDto updatedCourse = new CourseRequestDto(course.name(), course.type(),
                course.students(), null, Set.of(teacherId), null);

        RestAssured
                .given()
                .when()
                .body(updatedCourse)
                .contentType(ContentType.JSON)
                .put("/courses/" + courseId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(updatedCourse.name()))
                .body("type", equalTo(updatedCourse.type().name()))
                .body("teachers", notNullValue())
                .body("teachers", hasSize(updatedCourse.teachers().size()));
    }

    @Test
    void assertStudentAddedToCourse() {
        CourseResponseDto course = RestAssured
                .given()
                .when()
                .get("/courses/" + courseId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CourseResponseDto.class);

        StudentRequestDto newStudentRequest = new StudentRequestDto("Dani", 25, TEST_GROUP, Set.of());
        StudentResponseDto newStudentResponse = assertStudentCreated(newStudentRequest);
        course.students().add(newStudentResponse.id());

        CoursePatchDto updatedCourse = new CoursePatchDto(null, null, course.students(),
                null, null, null);

        RestAssured
                .given()
                .when()
                .body(updatedCourse)
                .contentType(ContentType.JSON)
                .patch("/courses/" + course.id())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("students", notNullValue())
                .body("students", hasSize(updatedCourse.students().size()))
                .body("teachers", notNullValue())
                .body("id", equalTo(course.id().toString()));
    }

    @Test
    void assertStudentRemovedFromCourse() {
        CourseResponseDto course = RestAssured
                .given()
                .when()
                .get("/courses/" + courseId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CourseResponseDto.class);

        course.students().remove(studentBId);
        CoursePatchDto updatedCourse = new CoursePatchDto(null, null, course.students(),
                null, null, null);

        RestAssured
                .given()
                .when()
                .body(updatedCourse)
                .contentType(ContentType.JSON)
                .patch("/courses/" + course.id())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("students", notNullValue())
                .body("students", hasSize(updatedCourse.students().size()))
                .body("teachers", notNullValue())
                .body("id", equalTo(course.id().toString()));
    }

    @Test
    void assertTeacherAddedToCourse() {
        CourseResponseDto course = RestAssured
                .given()
                .when()
                .get("/courses/" + courseId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CourseResponseDto.class);

        TeacherRequestDto newTeacherRequest = new TeacherRequestDto("Ivaylo", TEST_GROUP, Set.of());
        TeacherResponseDto newTeacherResponse = assertTeacherCreated(newTeacherRequest);
        course.teachers().add(newTeacherResponse.id());

        CoursePatchDto updatedCourse = new CoursePatchDto(null, null, null,
                null, course.teachers(), null);

        RestAssured
                .given()
                .when()
                .body(updatedCourse)
                .contentType(ContentType.JSON)
                .patch("/courses/" + course.id())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("teachers", notNullValue())
                .body("teachers", hasSize(updatedCourse.teachers().size()))
                .body("students", notNullValue())
                .body("id", equalTo(course.id().toString()));
    }

    @Test
    void assertTeacherRemovedFromCourse() {
        CourseResponseDto course = RestAssured
                .given()
                .when()
                .get("/courses/" + courseId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CourseResponseDto.class);

        course.teachers().remove(teacherId);
        CoursePatchDto updatedCourse = new CoursePatchDto(null, null, null, null,
                course.teachers(), null);

        RestAssured
                .given()
                .when()
                .body(updatedCourse)
                .contentType(ContentType.JSON)
                .patch("/courses/" + course.id())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("teachers", notNullValue())
                .body("teachers", hasSize(updatedCourse.teachers().size()))
                .body("students", notNullValue())
                .body("id", equalTo(course.id().toString()));
    }

    @Test
    void assertAssistantDeleted() {
        RestAssured
                .given()
                .when()
                .delete("/teachers/" + assistantId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        RestAssured
                .given()
                .when()
                .get("/courses/" + courseId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("teachers", notNullValue())
                .body("teachers", hasSize(1))
                .body("teachers", hasItem(teacherId.toString()))
                .body("id", equalTo(courseId.toString()));
    }

    @Test
    void assertAssistantNameUpdated() {
        TeacherResponseDto assistant = RestAssured
                .given()
                .when()
                .get("/teachers/" + assistantId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(TeacherResponseDto.class);

        TeacherRequestDto updatedAssistant = new TeacherRequestDto("Dony", assistant.group(), assistant.courses());

        RestAssured
                .given()
                .when()
                .contentType(ContentType.JSON)
                .body(updatedAssistant)
                .put("/teachers/" + assistantId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(updatedAssistant.name()))
                .body("group", equalTo(updatedAssistant.group()))
                .body("courses", notNullValue())
                .body("courses", hasSize(updatedAssistant.courses().size()))
                .body("id", equalTo(assistantId.toString()));
    }

    @Test
    void assertCourseDeleted() {
        RestAssured
                .given()
                .when()
                .delete("/courses/" + courseId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        RestAssured
                .given()
                .when()
                .get("/teachers/" + teacherId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("courses", notNullValue())
                .body("courses", hasSize(0))
                .body("id", equalTo(teacherId.toString()));
    }

    @Test
    void assertStudentNameUpdated() {

        StudentResponseDto student = RestAssured
                .given()
                .when()
                .get("/students/" + studentAId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(StudentResponseDto.class);

        StudentRequestDto updatedStudent = new StudentRequestDto("Vanko", 22,
                student.group(), student.courses());

        RestAssured
                .given()
                .when()
                .contentType(ContentType.JSON)
                .body(updatedStudent)
                .put("/students/" + student.id())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(updatedStudent.name()))
                .body("age", equalTo(updatedStudent.age()))
                .body("group", equalTo(updatedStudent.group()))
                .body("courses", notNullValue())
                .body("courses", hasSize(updatedStudent.courses().size()))
                .body("id", equalTo(student.id().toString()));
    }

    @Test
    void assertStudentDeleted() {

        RestAssured
                .given()
                .when()
                .delete("/students/" + studentCId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        RestAssured
                .given()
                .when()
                .get("/courses/" + courseId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("students", notNullValue())
                .body("students", hasSize(2))
                .body("id", equalTo(courseId.toString()));

        RestAssured
                .given()
                .when()
                .get("/students/count")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("2"));
    }

    @Test
    void assertGetStudentsByCourseParticipation() {
        RestAssured
                .given()
                .when()
                .param("courseId", courseId)
                .get("/students/course-participation")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(3));
    }

    @Test
    void assertGetStudentsByGroupParticipation() {
        RestAssured
                .given()
                .when()
                .param("name", TEST_GROUP)
                .get("/students/group-participation")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(2));
    }

    @Test
    void assertGetStudentsByAgeOlderThanAndCourse() {
        RestAssured
                .given()
                .when()
                .param("age", 27)
                .param("courseId", courseId)
                .get("/students/age-course-participation")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(2));
    }

    @Test
    void assertGetStudentsByGroupAndCourse() {
        RestAssured
                .given()
                .when()
                .param("group", "B")
                .param("courseId", courseId)
                .get("/students/group-course-participation")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(1));
    }

    @Test
    void assertGetTeacherCount() {
        RestAssured
                .given()
                .when()
                .get("/teachers/count")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("2"));
    }

    @Test
    void assertGetTeacherNotFoundError() {
        RestAssured
                .given()
                .when()
                .get("/teachers/" + UUID.randomUUID())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void assertGetStudentNotFoundError() {
        RestAssured
                .given()
                .when()
                .get("/students/" + UUID.randomUUID())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void assertGetCourseNotFoundError() {
        RestAssured
                .given()
                .when()
                .get("/courses/" + UUID.randomUUID())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void assertGetTeachersByGroupAndCourse() {
        RestAssured
                .given()
                .when()
                .param("group", TEST_GROUP)
                .param("courseId", courseId)
                .get("/teachers/group-course-participation")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(1));
    }

    @Test
    void assertGetCoursesCountByType() {
        RestAssured
                .given()
                .when()
                .param("type", COURSE_TYPE)
                .get("/courses/count")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("1"));
    }

    @Test
    void assertFailCreateCourseWithoutTeacher() {
        CourseRequestDto course = new CourseRequestDto(COURSE_NAME, COURSE_TYPE, Set.of(), null, Set.of(), null);

        RestAssured
                .given()
                .when()
                .contentType(ContentType.JSON)
                .body(course)
                .post("/courses")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo("Validation failed"))
                .body("errors.teachers", equalTo("At least one teacher (existing or new) must be provided"))
                .body("type", equalTo("https://example.com/problems/validation-error"));
    }

    @Test
    void assertFailCreateStudentWithoutNameAgeAndGroup() {
        StudentRequestDto student = new StudentRequestDto(null, null, null, Set.of());

        RestAssured
                .given()
                .when()
                .contentType(ContentType.JSON)
                .body(student)
                .post("/students")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo("Validation failed"))
                .body("errors.name", equalTo("Student name must not be blank"))
                .body("errors.age", equalTo("Student age is required"))
                .body("errors.group", equalTo("Student group must not be blank"))
                .body("type", equalTo("https://example.com/problems/validation-error"));
    }

    @Test
    void assertFailUpdateStudentWithoutNameAgeAndGroup() {
        StudentRequestDto student = new StudentRequestDto(null, null, null, Set.of());

        RestAssured
                .given()
                .when()
                .contentType(ContentType.JSON)
                .body(student)
                .put("/students/" + studentAId)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo("Validation failed"))
                .body("errors.name", equalTo("Student name must not be blank"))
                .body("errors.age", equalTo("Student age is required"))
                .body("errors.group", equalTo("Student group must not be blank"))
                .body("type", equalTo("https://example.com/problems/validation-error"));
    }

    @Test
    void assertFailCreateTeacherWithoutNameAndGroup() {
        TeacherRequestDto teacher = new TeacherRequestDto(null, null, Set.of());

        RestAssured
                .given()
                .when()
                .contentType(ContentType.JSON)
                .body(teacher)
                .post("/teachers")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo("Validation failed"))
                .body("errors.name", equalTo("Teacher name must not be blank"))
                .body("errors.group", equalTo("Teacher group must not be blank"))
                .body("type", equalTo("https://example.com/problems/validation-error"));
    }

    @Test
    void assertFailUpdateTeacherWithoutNameAndGroup() {
        TeacherRequestDto teacher = new TeacherRequestDto(null, null, Set.of());

        RestAssured
                .given()
                .when()
                .contentType(ContentType.JSON)
                .body(teacher)
                .put("/teachers/" + teacherId)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo("Validation failed"))
                .body("errors.name", equalTo("Teacher name must not be blank"))
                .body("errors.group", equalTo("Teacher group must not be blank"))
                .body("type", equalTo("https://example.com/problems/validation-error"));
    }

    @Test
    void assertFailFullUpdateCourseWithoutTeacher() {
        CourseResponseDto course = RestAssured
                .given()
                .when()
                .get("/courses/" + courseId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(CourseResponseDto.class);

        CourseRequestDto courseUpdate = new CourseRequestDto(course.name(), course.type(),
                course.students(), null, Set.of(), null);

        RestAssured
                .given()
                .when()
                .contentType(ContentType.JSON)
                .body(courseUpdate)
                .put("/courses/" + course.id())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo("Validation failed"))
                .body("errors.teachers", equalTo("At least one teacher (existing or new) must be provided"))
                .body("type", equalTo("https://example.com/problems/validation-error"));
    }

    @Test
    void assertPartialUpdateCourseWithoutTeacher() {
        CoursePatchDto courseUpdate = new CoursePatchDto(null, null, null,
                null, Set.of(), null);

        RestAssured
                .given()
                .when()
                .contentType(ContentType.JSON)
                .body(courseUpdate)
                .patch("/courses/" + courseId)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo("Validation failed"))
                .body("errors.teachers", equalTo("At least one teacher (existing or new) must be provided"))
                .body("type", equalTo("https://example.com/problems/validation-error"));
    }

    private CourseResponseDto assertCourseWithTwoTeachersCreated() {
        //Creates the teacher
        TeacherRequestDto teacher = new TeacherRequestDto("Stephan", TEST_GROUP, Set.of());
        teacherId = assertTeacherCreated(teacher).id();

        //Creates the assistant
        TeacherRequestDto assistant = new TeacherRequestDto("Mary", "B", Set.of());
        assistantId = assertTeacherCreated(assistant).id();

        //Creates the course with the teachers
        CourseRequestDto course = new CourseRequestDto(COURSE_NAME, COURSE_TYPE, Set.of(),
                null, Set.of(teacherId, assistantId), null);
        return assertCourseCreated(course);
    }

    private TeacherResponseDto assertTeacherCreated(TeacherRequestDto dto) {
        return RestAssured
                .given()
                .when()
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/teachers")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", equalTo(dto.name()))
                .body("group", equalTo(dto.group()))
                .body("id", notNullValue())
                .extract()
                .as(TeacherResponseDto.class);
    }

    private StudentResponseDto assertStudentCreated(StudentRequestDto dto) {
        return RestAssured
                .given()
                .when()
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/students")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", equalTo(dto.name()))
                .body("age", equalTo(dto.age()))
                .body("group", equalTo(dto.group()))
                .body("courses", notNullValue())
                .body("courses", hasSize(dto.courses().size()))
                .body("id", notNullValue())
                .extract()
                .response()
                .as(StudentResponseDto.class);
    }

    private CourseResponseDto assertCourseCreated(CourseRequestDto dto) {
        return RestAssured
                .given()
                .when()
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/courses")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", equalTo(dto.name()))
                .body("type", equalTo(dto.type().name()))
                .body("teachers", notNullValue())
                .body("teachers", hasSize(dto.newTeachers() != null
                        ? dto.newTeachers().size()
                        : dto.teachers().size()))

                .body("students", notNullValue())
                .body("students", hasSize(dto.newStudents() != null
                        ? dto.newStudents().size()
                        : dto.students().size()))

                .body("id", notNullValue())
                .extract()
                .as(CourseResponseDto.class);
    }

    private Set<StudentRequestDto> generateStudents(String courseName) {
        Set<StudentRequestDto> students = new HashSet<>();

        for (int j = 0; j < 300; j++) {
            String number = String.valueOf(j + 1);
            String name = "student_" + number + "_" + courseName;
            //first 100 are in group A, next 100 in group B and last 100 in group C
            String group = j < 100 ? "A" : j < 200 ? "B" : "C";
            //first 100 are 20, next 100 are 30 and last 100 are 40 years old
            Integer age = j < 100 ? 20 : j < 200 ? 30 : 40;
            StudentRequestDto student = new StudentRequestDto(name, age, group, Set.of());
            students.add(student);
        }
        return students;
    }

    private void loadData() {
        courseId = assertCourseWithTwoTeachersCreated().id();

        StudentRequestDto studentA = new StudentRequestDto("Ivo", 28, TEST_GROUP, Set.of(courseId));
        studentAId = assertStudentCreated(studentA).id();

        StudentRequestDto studentB = new StudentRequestDto("Adi", 33, TEST_GROUP, Set.of(courseId));
        studentBId = assertStudentCreated(studentB).id();

        StudentRequestDto studentC = new StudentRequestDto("Radi", 20, "B", Set.of(courseId));
        studentCId = assertStudentCreated(studentC).id();
    }
}
