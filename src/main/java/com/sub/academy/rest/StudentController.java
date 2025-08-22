package com.sub.academy.rest;

import com.sub.academy.entity.Student;
import com.sub.academy.rest.dto.request.StudentRequestDto;
import com.sub.academy.rest.dto.response.StudentResponseDto;
import com.sub.academy.service.student.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService service;

    @PostMapping
    public ResponseEntity<StudentResponseDto> create(@Valid @RequestBody StudentRequestDto dto) {
        Student saved = service.create(dto);
        return ResponseEntity
                .created(ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(saved.getId())
                        .toUri())
                .body(ApiMapper.toDto(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDto> read(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiMapper.toDto(service.read(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDto> update(@PathVariable UUID id, @Valid @RequestBody StudentRequestDto student) {
        return ResponseEntity.ok(ApiMapper.toDto(service.update(id, student)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(service.count());
    }

    @GetMapping("/course-participation")
    public ResponseEntity<Page<StudentResponseDto>> getByCourse(@RequestParam UUID courseId, Pageable pageable) {
        Page<StudentResponseDto> page = service.findByCourseId(courseId, pageable).map(ApiMapper::toDto);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/group-participation")
    public ResponseEntity<Page<StudentResponseDto>> getByGroup(@RequestParam String name, Pageable pageable) {
        Page<StudentResponseDto> page = service.findByGroup(name, pageable).map(ApiMapper::toDto);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/age-course-participation")
    public ResponseEntity<Page<StudentResponseDto>> getByAgeAndCourse(@RequestParam Integer age,
                                                                      @RequestParam UUID courseId,
                                                                      Pageable pageable) {
        Page<StudentResponseDto> page = service.findByAgeGreaterThanAndCourseId(age, courseId, pageable).map(ApiMapper::toDto);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/group-course-participation")
    public ResponseEntity<Page<StudentResponseDto>> search(@RequestParam String group,
                                                           @RequestParam UUID courseId,
                                                           Pageable pageable) {
        Page<StudentResponseDto> page = service.findStudentsByCourseIdAndGroup(courseId, group, pageable).map(ApiMapper::toDto);
        return ResponseEntity.ok(page);
    }
}
