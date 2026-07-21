package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;

	@Mock
	private ScoreRepository repository;

	@Mock
	private UserService userService;

	@Mock
	private MovieRepository movieRepository;

	private ScoreEntity score;
	private ScoreDTO scoreDTO;
	private UserEntity user;
	private MovieEntity movie, alteredScoreMovie;

	@BeforeEach
	void setUp() throws Exception {
		score = ScoreFactory.createScoreEntity();
		scoreDTO = ScoreFactory.createScoreDTO();
		user = UserFactory.createUserEntity();
		movie = MovieFactory.createMovieEntity();
		alteredScoreMovie = MovieFactory.createMovieEntity();
		alteredScoreMovie.setScore(4.5);
	}

	@Test
	public void saveScoreShouldReturnMovieDTO() {
		Mockito.when(userService.authenticated()).thenReturn(user);
		Mockito.when(movieRepository.findById(any())).thenReturn(Optional.of(movie));
		Mockito.when(repository.saveAndFlush(any())).thenReturn(score);
		Mockito.when(movieRepository.save(any())).thenReturn(alteredScoreMovie);

		MovieDTO result = service.saveScore(scoreDTO);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(scoreDTO.getMovieId(), result.getId());
		Assertions.assertEquals(scoreDTO.getScore(), result.getScore());
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		Mockito.when(userService.authenticated()).thenReturn(user);
		Mockito.when(movieRepository.findById(any())).thenReturn(Optional.empty());

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.saveScore(scoreDTO);
		});
	}
}
