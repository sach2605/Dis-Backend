package sgsits.cse.dis.academics.serviceImpl;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sgsits.cse.dis.academics.exception.ConflictException;
import sgsits.cse.dis.academics.model.FacultyTimetable;
import sgsits.cse.dis.academics.model.SemTimeTable;
import sgsits.cse.dis.academics.repo.FacultyTimetableRepository;
import sgsits.cse.dis.academics.repo.SemTimeTableRepository;
import sgsits.cse.dis.academics.request.FacultyTimeTableForm;
import sgsits.cse.dis.academics.service.CoursesService;
import sgsits.cse.dis.academics.service.SemesterTimeTableServices;

/**
 * <h1><b>SemesterTimeTableServicesImpl</b> class.</h1>
 * <p>This class contains implementation of all the time table settings
 *  services which are defined in the <b>TimeTableSettingServices</b> interface.
 * 
 * @author Arjit Mishra.
 * @version 1.0.
 * @since 3-MAR-2020
 * @throws ConflictException.
 * @inheritDoc
 */

@Service
public class SemesterTimeTableServicesImpl implements SemesterTimeTableServices {

	@Autowired
	private SemTimeTableRepository semTimeTableRepository;
	
	@Autowired
	private FacultyTimetableRepository facultyTimeTableRepository;
	
	@Autowired
	private CoursesService coursesService;
	
	@Override
	@Transactional
	public String addTimeTable(FacultyTimeTableForm facultyTimeTableForm,String userId) throws ConflictException {
		String message = "not updated" ;
		
		//if no entry in SemTimeTable model then will create new entry else ignore
		Optional<SemTimeTable> semTimeTable = semTimeTableRepository.findBySubjectCodeAndLectureTypeAndSession(facultyTimeTableForm.getSubjectCode(), 
				facultyTimeTableForm.getLectureType(), facultyTimeTableForm.getSession());
		
		if(!semTimeTable.isPresent()) {
			semTimeTable = Optional.of(semTimeTableRepository.save(new SemTimeTable(userId, java.time.Clock.systemUTC().instant(), 
					facultyTimeTableForm.getSubjectCode(), facultyTimeTableForm.getLectureType(), facultyTimeTableForm.getSession(),
					facultyTimeTableForm.getYear(), facultyTimeTableForm.getSemester(),coursesService.getCourseIdByName(facultyTimeTableForm.getCourse()))));
		}
		
		final String id = semTimeTable.get().getId();
			
		//add metadata to all the objects in list facultyTineTableForm
		facultyTimeTableForm.getFacultyTimeTableEntries().stream()
			.forEach(facultyTimeTable -> {
				facultyTimeTable.setCreatedBy(userId);
				facultyTimeTable.setCreatedDate(java.time.Clock.systemUTC().instant());
				facultyTimeTable.setModifiedBy(userId);
				facultyTimeTable.setModifiedDate(java.time.Clock.systemUTC().instant());
				facultyTimeTable.setSemTimeTableId(id);
			});
		
		List<FacultyTimetable> test = facultyTimeTableRepository.saveAll(facultyTimeTableForm.getFacultyTimeTableEntries());
		
		if (test.size() == facultyTimeTableForm.getFacultyTimeTableEntries().size())
				message = "all values updated sucessfully";
		
		if (test.size() < facultyTimeTableForm.getFacultyTimeTableEntries().size())
			message = "all data were not sucessfully update. Event rolled back.";
		
		if (test.size() == 0)
			throw new ConflictException("unable to update the values");
		
		return message;
			
		
	}

}
