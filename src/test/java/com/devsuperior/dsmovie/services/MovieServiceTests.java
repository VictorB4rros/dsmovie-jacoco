package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.tests.MovieFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTests {
	
	@InjectMocks
	private MovieService service;

	@Mock
	private MovieRepository repository;

	private MovieEntity movie;
	private PageImpl<MovieEntity> page;
	private String movieTitle;

	@BeforeEach
	void setUp() throws Exception {
		movie = MovieFactory.createMovieEntity();
		page = new PageImpl<>(List.of(movie));
		movieTitle = movie.getTitle();

		Mockito.when(repository.searchByTitle(any(), (Pageable) any())).thenReturn(page);
	}

	@Test
	public void findAllShouldReturnPagedMovieDTO() {
		Pageable pageable = PageRequest.of(0, 12);
		Page<MovieDTO> result = service.findAll("", pageable);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(1, result.getSize());
		Assertions.assertEquals(movieTitle, result.getContent().getFirst().getTitle());
	}

	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {
	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
	}

	@Test
	public void insertShouldReturnMovieDTO() {
	}

	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
	}
}
