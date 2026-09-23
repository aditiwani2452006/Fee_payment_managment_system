package com.mmcoe.feepay.student;

import com.mmcoe.feepay.common.ResourceNotFoundException;
import com.mmcoe.feepay.feestructure.FeeStructure;
import com.mmcoe.feepay.feestructure.FeeStructureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Team 1: FR4 Manage Student Profile & FR6 View Fee Structure & Dues
 * Concepts:
 *  - DBMS: Relational query navigation across Student and FeeAssignment
 *  - OOP: Profile encapsulation & state maintenance
 */
@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final FeeAssignmentRepository feeAssignmentRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final FeeAssignmentService feeAssignmentService;

    public StudentService(
            StudentRepository studentRepository,
            FeeAssignmentRepository feeAssignmentRepository,
            FeeStructureRepository feeStructureRepository,
            FeeAssignmentService feeAssignmentService
    ) {
        this.studentRepository = studentRepository;
        this.feeAssignmentRepository = feeAssignmentRepository;
        this.feeStructureRepository = feeStructureRepository;
        this.feeAssignmentService = feeAssignmentService;
    }

    @Transactional
    public StudentProfileDto getProfile(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        FeeAssignment assignment = feeAssignmentService.getOrCreateStudentAssignment(student);
        return new StudentProfileDto(student, assignment);
    }

    @Transactional
    public StudentProfileDto updateProfile(Long studentId, UpdateStudentRequest req) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        if (req.getName() != null && !req.getName().isBlank()) {
            student.setName(req.getName().trim());
        }
        student.setContactNumber(req.getContactNumber().trim());
        student.setEmail(req.getEmail().trim());
        Student updated = studentRepository.save(student);

        FeeAssignment assignment = feeAssignmentService.getOrCreateStudentAssignment(updated);
        return new StudentProfileDto(updated, assignment);
    }

    @Transactional
    public List<FeeAssignmentDto> getStudentFeeAssignments(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        feeAssignmentService.getOrCreateStudentAssignment(student);

        return feeAssignmentRepository.findByStudent_StudentId(studentId).stream()
                .map(FeeAssignmentDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StudentProfileDto> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(student -> {
                    List<FeeAssignment> assignments = feeAssignmentRepository.findByStudent_StudentId(student.getStudentId());
                    FeeAssignment fa = assignments.isEmpty() ? null : assignments.get(0);
                    return new StudentProfileDto(student, fa);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public FeeAssignmentDto assignFee(Long studentId, Long feeStructureId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
        FeeStructure feeStructure = feeStructureRepository.findById(feeStructureId)
                .orElseThrow(() -> new ResourceNotFoundException("FeeStructure not found with ID: " + feeStructureId));

        FeeAssignment assignment = new FeeAssignment(student, feeStructure, feeStructure.getAmount());
        assignment.setStatus("PENDING");
        FeeAssignment saved = feeAssignmentRepository.save(assignment);
        return new FeeAssignmentDto(saved);
    }
}
