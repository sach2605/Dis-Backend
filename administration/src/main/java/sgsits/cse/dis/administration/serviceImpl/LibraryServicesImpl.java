package sgsits.cse.dis.administration.serviceImpl;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sgsits.cse.dis.administration.exception.ConflictException;
import sgsits.cse.dis.administration.exception.EventDoesNotExistException;
import sgsits.cse.dis.administration.feignClient.AcademicsClient;
import sgsits.cse.dis.administration.model.LibraryBookCount;
import sgsits.cse.dis.administration.model.LibraryBookRecords;
import sgsits.cse.dis.administration.model.LibraryThesisRecords;
import sgsits.cse.dis.administration.repo.LibraryBookCountRepository;
import sgsits.cse.dis.administration.repo.LibraryBookRecordsRepository;
import sgsits.cse.dis.administration.repo.LibraryThesisRecordsRepository;
import sgsits.cse.dis.administration.request.AddBookForm;
import sgsits.cse.dis.administration.request.AddThesisForm;
import sgsits.cse.dis.administration.response.LibraryBookRecordsResponse;
import sgsits.cse.dis.administration.service.LibraryServices;

@Component
public class LibraryServicesImpl implements LibraryServices,Serializable {
	

	private static final long serialVersionUID = 1L;
	
	@Autowired
	private LibraryBookRecordsRepository libraryBookRecordsRepository;
	
	@Autowired
	private LibraryBookCountRepository libraryBookCountRepository;
	
	@Autowired
	private LibraryThesisRecordsRepository libraryThesisRecordsRepository;
	
	@Autowired
	AcademicsClient academicsClient;
	
	@Transactional
	@Override
	public String addBook(AddBookForm addBookForm) throws ConflictException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		LibraryBookRecords libraryBookRecord = new LibraryBookRecords();
		libraryBookRecord.setAuthorName(addBookForm.getAuthorName());
		libraryBookRecord.setIsbn(addBookForm.getIsbn());
		libraryBookRecord.setEdition(addBookForm.getEdition());
		libraryBookRecord.setEntryDate(simpleDateFormat.format(new Date()));
		libraryBookRecord.setNoOfPages(addBookForm.getNoOfPages());
		libraryBookRecord.setPrice(addBookForm.getPrice());
		libraryBookRecord.setPublisherAndPlace(addBookForm.getPublisherAndPlace());
		libraryBookRecord.setPurchaseDate(addBookForm.getPurchaseDate());
		libraryBookRecord.setRemarks(addBookForm.getRemarks());
		libraryBookRecord.setSubjectCategory(addBookForm.getSubjectCategory());
		libraryBookRecord.setTitle(addBookForm.getTitle());
		libraryBookRecord.setYearOfPublication(addBookForm.getYearOfPublication());
		String bookId = generateBookId(addBookForm.getSubjectCategory());
		libraryBookRecord.setBookId(bookId);
		LibraryBookRecords test = libraryBookRecordsRepository.save(libraryBookRecord);
		if (test.equals(null)) 
			throw new ConflictException("No records updated. This is due to conflict in information on client side.");
		return bookId;
	}
	
	@Override
	public List<LibraryBookRecordsResponse> getAllBooks(){
		List<LibraryBookRecords> libraryBookRecords; 
		List<LibraryBookRecordsResponse> libraryBookRecordsResponses = new ArrayList<LibraryBookRecordsResponse>();
		LibraryBookRecordsResponse temp;
		libraryBookRecords = libraryBookRecordsRepository.findAll();
		for(LibraryBookRecords libraryBookRecord : libraryBookRecords) {
			temp=new LibraryBookRecordsResponse();
			temp.setAuthorName(libraryBookRecord.getAuthorName());
			temp.setEdition(libraryBookRecord.getEdition());
			temp.setStatus(libraryBookRecord.getStatus());
			temp.setTitle(libraryBookRecord.getTitle());
			temp.setSubjectCategory(libraryBookRecord.getSubjectCategory());
			libraryBookRecordsResponses.add(temp);
		}
		return libraryBookRecordsResponses;
	}
	
	@Override
	public List<LibraryBookRecordsResponse> getBookByTitle(String title) throws EventDoesNotExistException{
		List<LibraryBookRecords> libraryBookRecords; 
		List<LibraryBookRecordsResponse> libraryBookRecordsResponses = new ArrayList<LibraryBookRecordsResponse>();
		LibraryBookRecordsResponse temp;
		libraryBookRecords = libraryBookRecordsRepository.findByTitleContainingIgnoreCase(title);
		if(libraryBookRecords.isEmpty())
			throw new EventDoesNotExistException("Book with Title ["+title+"] doesn't exist.");
		for(LibraryBookRecords libraryBookRecord : libraryBookRecords) {
			temp=new LibraryBookRecordsResponse();
			temp.setAuthorName(libraryBookRecord.getAuthorName());
			temp.setEdition(libraryBookRecord.getEdition());
			temp.setStatus(libraryBookRecord.getStatus());
			temp.setTitle(libraryBookRecord.getTitle());
			temp.setSubjectCategory(libraryBookRecord.getSubjectCategory());
			libraryBookRecordsResponses.add(temp);
		}
		return libraryBookRecordsResponses;	
	}

	@Override
	public List<LibraryBookRecordsResponse> getBookByAuthorName(String authorName) throws EventDoesNotExistException{
		List<LibraryBookRecords> libraryBookRecords; 
		List<LibraryBookRecordsResponse> libraryBookRecordsResponses = new ArrayList<LibraryBookRecordsResponse>();
		LibraryBookRecordsResponse temp;
		libraryBookRecords = libraryBookRecordsRepository.findByAuthorNameContainingIgnoreCase(authorName);
		if(libraryBookRecords.isEmpty())
			throw new EventDoesNotExistException("Book with author name ["+authorName+"] doesn't exist.");
		for(LibraryBookRecords libraryBookRecord : libraryBookRecords) {
			temp=new LibraryBookRecordsResponse();
			temp.setAuthorName(libraryBookRecord.getAuthorName());
			temp.setEdition(libraryBookRecord.getEdition());
			temp.setStatus(libraryBookRecord.getStatus());
			temp.setTitle(libraryBookRecord.getTitle());
			temp.setSubjectCategory(libraryBookRecord.getSubjectCategory());
			libraryBookRecordsResponses.add(temp);
		}
		return libraryBookRecordsResponses;	
	}
	
	//Helper function to generate book-id

	private String generateBookId(String subjectCategory) {
		LibraryBookCount libraryBookCount = new LibraryBookCount(subjectCategory);	
		if(libraryBookCountRepository.findBySubjectCategory(subjectCategory).isEmpty()){
			libraryBookCount.setCount(1l);
			libraryBookCount.setSubjectCategory(subjectCategory);
			libraryBookCountRepository.save(libraryBookCount);	

		}
		else {
			libraryBookCountRepository.updateCount(subjectCategory);
			libraryBookCount = libraryBookCountRepository.findBySubjectCategory(subjectCategory).get(0);
			libraryBookCount.setCount(libraryBookCount.getCount()+1);
				
		}
	
	return libraryBookCount.getSubjectCategory()+"-"+libraryBookCount.getCount();
	}

	@Override
	public void updateBook(AddBookForm addBookForm,String bookId) throws ConflictException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		LibraryBookRecords libraryBookRecord = new LibraryBookRecords();
		libraryBookRecord.setAuthorName(addBookForm.getAuthorName());
		libraryBookRecord.setIsbn(addBookForm.getIsbn());
		libraryBookRecord.setEdition(addBookForm.getEdition());
		libraryBookRecord.setEntryDate(simpleDateFormat.format(new Date()));
		libraryBookRecord.setNoOfPages(addBookForm.getNoOfPages());
		libraryBookRecord.setPrice(addBookForm.getPrice());
		libraryBookRecord.setPublisherAndPlace(addBookForm.getPublisherAndPlace());
		libraryBookRecord.setPurchaseDate(addBookForm.getPurchaseDate());
		libraryBookRecord.setRemarks(addBookForm.getRemarks());
		libraryBookRecord.setSubjectCategory(addBookForm.getSubjectCategory());
		libraryBookRecord.setTitle(addBookForm.getTitle());
		libraryBookRecord.setYearOfPublication(addBookForm.getYearOfPublication());
		libraryBookRecord.setBookId(bookId);
		LibraryBookRecords test = libraryBookRecordsRepository.save(libraryBookRecord);
		if (test.equals(null)) 
			throw new ConflictException("No records updated. This due to conflict in information on client side.");
	}

	@Transactional
	@Override
	public void deleteBook(String bookId) throws EventDoesNotExistException {
		if( libraryBookRecordsRepository.deleteByBookId(bookId) <= 0)
				throw new EventDoesNotExistException("Unable to delete book "+bookId+".");
	}

	@Override
	public Long addThesis(AddThesisForm addThesisForm) throws ConflictException {
		
		LibraryThesisRecords test = libraryThesisRecordsRepository.save(new LibraryThesisRecords(addThesisForm.getYear(),
									addThesisForm.getSubmittedBy(), 
									addThesisForm.getGuidedBy(), 
									addThesisForm.getCdStatus(), 
									academicsClient.getCourseIdByName(addThesisForm.getCourse()), 
									new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()), 
									addThesisForm.getTitle(),  
									addThesisForm.getRemarks()));
		
		if(test.equals(null))
			throw new ConflictException("No records updated. This is due to conflict in information on client side.");
					
		return test.getThesisId();
	}
	
	
}

