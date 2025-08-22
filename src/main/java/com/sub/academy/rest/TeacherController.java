package com.sub.academy.rest;

import com.sub.academy.entity.Teacher;
import com.sub.academy.rest.dto.request.TeacherRequestDto;
import com.sub.academy.rest.dto.response.TeacherResponseDto;
import com.sub.academy.service.teacher.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService service;

    @PostMapping
    public ResponseEntity<TeacherResponseDto> create(@Valid @RequestBody TeacherRequestDto dto) {
        Teacher saved = service.create(ApiMapper.toDomain(dto));
        return ResponseEntity
                .created(ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(saved.getId())
                        .toUri())
                .body(ApiMapper.toDto(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponseDto> read(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiMapper.toDto(service.read(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherResponseDto> update(@PathVariable UUID id, @Valid @RequestBody TeacherRequestDto teacher) {
        return ResponseEntity.ok(ApiMapper.toDto(service.update(id, teacher)));
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

    @GetMapping("/group-course-participation")
    public ResponseEntity<Page<TeacherResponseDto>> search(@RequestParam String group,
                                                           @RequestParam UUID courseId,
                                                           Pageable pageable) {
        Page<TeacherResponseDto> page = service.findTeachersByCourseIdAndGroup(courseId, group, pageable)
                .map(ApiMapper::toDto);
        return ResponseEntity.ok(page);
    }
}
