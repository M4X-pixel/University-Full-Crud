package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    SubjectRepository subjectRepository;

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    @GetMapping("/byFaculty/{facultyId}")
    public Page<Student> getStudentListByFaculty(@PathVariable Integer facultyId, @RequestParam int page){
        Pageable pageable = PageRequest.of(page,10);
        Page<Student> allByFacultyId = studentRepository.findAllByGroup_FacultyId(facultyId, pageable);
        return allByFacultyId;
    }


    //4. GROUP OWNER
    @GetMapping("/allByGroupId/{groupId}")
    public Page<Student> getStudentsInGroup(@PathVariable Integer groupId, @RequestParam int page){
        Pageable pageable = PageRequest.of(page,10);
        Page<Student> allByGroupId = studentRepository.findAllByGroupId(groupId, pageable);
return allByGroupId;
    }

    @PostMapping
    public String addStudent(@RequestBody StudentDto studentDto){
        Student student = new Student();
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        Address address = new Address();
        address.setStreet(studentDto.getStreet());
        address.setDistrict(studentDto.getDistrict());
        address.setCity(studentDto.getCity());
        addressRepository.save(address);
        student.setAddress(address);
        Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
        if (!optionalGroup.isPresent()) return "Invalid Group ID";
        student.setGroup(optionalGroup.get());
        List<Subject> subjects = new ArrayList<>();
        for (Integer subjectId : studentDto.getSubjectIds()) {
            Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);
            if (!optionalSubject.isPresent()){
                subjects.add(optionalSubject.get());
            }
        }
        student.setSubjects(subjects);
        studentRepository.save(student);
        return "Student saved Successfully";
    }

    @PutMapping("/{id}")
    public String editStudent(@RequestBody StudentDto studentDto, @PathVariable Integer id){
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (!optionalStudent.isPresent()) return "Invalid Student ID";
        Student student = optionalStudent.get();
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        Address address = new Address();
        address.setStreet(studentDto.getStreet());
        address.setDistrict(studentDto.getDistrict());
        address.setCity(studentDto.getCity());
        addressRepository.save(address);
        student.setAddress(address);
        Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
        if (!optionalGroup.isPresent()) return "Invalid Group ID";
        student.setGroup(optionalGroup.get());
        List<Subject> subjects = new ArrayList<>();
        for (Integer subjectId : studentDto.getSubjectIds()) {
            Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);
            if (!optionalSubject.isPresent()){
                subjects.add(optionalSubject.get());
            }
        }
        student.setSubjects(subjects);
        studentRepository.save(student);
        return "Student edited Successfully";
    }

    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Integer id){
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (!optionalStudent.isPresent()) return "Invalid Student ID";
        studentRepository.delete(optionalStudent.get());
        return "Student Deleted";
    }


}
