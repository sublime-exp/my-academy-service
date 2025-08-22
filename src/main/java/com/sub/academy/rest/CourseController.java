package com.sub.academy.rest;

import com.sub.academy.entity.Course;
import com.sub.academy.entity.CourseType;
import com.sub.academy.rest.dto.request.CoursePatchDto;
import com.sub.academy.rest.dto.request.CourseRequestDto;
import com.sub.academy.rest.dto.response.CourseResponseDto;
import com.sub.academy.service.course.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService service;

    @PostMapping
    public ResponseEntity<CourseResponseDto> create(@Valid @RequestBody CourseRequestDto dto) {
        Course saved = service.create(dto);
        return ResponseEntity
                .created(ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(saved.getId())
                        .toUri())
                .body(ApiMapper.toDto(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDto> read(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiMapper.toDto(service.read(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDto> update(@PathVariable UUID id,
                                                    @Valid @RequestBody CourseRequestDto course) {
        return ResponseEntity.ok(ApiMapper.toDto(service.update(id, course)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CourseResponseDto> patch(@PathVariable UUID id,
                                                   @Valid @RequestBody CoursePatchDto course) {
        return ResponseEntity.ok(ApiMapper.toDto(service.patch(id, course)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countByType(@RequestParam CourseType type) {
        return ResponseEntity.ok(service.countByType(type));
    }
}
