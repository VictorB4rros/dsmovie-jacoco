package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTests {
	
	@InjectMocks
	private MovieService service;

	@Mock
	private MovieRepository repository;

	private MovieEntity movie;
	private MovieDTO movieDTO;
	private PageImpl<MovieEntity> page;
	private String movieTitle;
	private Long existingMovieId, nonExistingMovieId, dependentMovieId;

	@BeforeEach
	void setUp() throws Exception {
		movie = MovieFactory.createMovieEntity();
		movieDTO = MovieFactory.createMovieDTO();
		page = new PageImpl<>(List.of(movie));
		movieTitle = movie.getTitle();
		existingMovieId = 1L;
		nonExistingMovieId = 2L;
		dependentMovieId = 3L;
	}

	@Test
	public void findAllShouldReturnPagedMovieDTO() {
		Mockito.when(repository.searchByTitle(any(), (Pageable) any())).thenReturn(page);
		Pageable pageable = PageRequest.of(0, 12);

		Page<MovieDTO> result = service.findAll("", pageable);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(1, result.getSize());
		Assertions.assertEquals(movieTitle, result.getContent().getFirst().getTitle());
	}

	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {
		Mockito.when(repository.findById(existingMovieId)).thenReturn(Optional.of(movie));

		MovieDTO result = service.findById(existingMovieId);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingMovieId, result.getId());
		Assertions.assertEquals(movieTitle, result.getTitle());
	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Mockito.when(repository.findById(nonExistingMovieId)).thenReturn(Optional.empty());

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingMovieId);
		});
	}

	@Test
	public void insertShouldReturnMovieDTO() {
		Mockito.when(repository.save(any())).thenReturn(movie);

		MovieDTO result = service.insert(movieDTO);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(movie.getId(), result.getId());
	}

	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
		Mockito.when(repository.save(any())).thenReturn(movie);
		Mockito.when(repository.getReferenceById(existingMovieId)).thenReturn(movie);

		MovieDTO result = service.update(existingMovieId, movieDTO);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(existingMovieId, result.getId());
		Assertions.assertEquals(movieDTO.getTitle(), result.getTitle());
	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Mockito.when(repository.getReferenceById(nonExistingMovieId)).thenThrow(EntityNotFoundException.class);

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingMovieId, movieDTO);
		});
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Mockito.when(repository.existsById(existingMovieId)).thenReturn(true);
		Mockito.doNothing().when(repository).deleteById(existingMovieId);

		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingMovieId);
		});
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Mockito.when(repository.existsById(nonExistingMovieId)).thenReturn(false);

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingMovieId);
		});
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		Mockito.when(repository.existsById(dependentMovieId)).thenReturn(true);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentMovieId);

		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentMovieId);
		});
	}
}
