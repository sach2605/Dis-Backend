package sgsits.cse.dis.administration.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sgsits.cse.dis.administration.model.FacultyRequest;

public interface FacultyRequestRepo extends JpaRepository<FacultyRequest, String>{
	Optional<FacultyRequest> findById(String id);
}
