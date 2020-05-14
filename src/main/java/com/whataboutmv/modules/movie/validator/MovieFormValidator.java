package com.whataboutmv.modules.movie.validator;


import com.whataboutmv.modules.movie.MovieRepository;
import com.whataboutmv.modules.movie.form.MovieForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class MovieFormValidator implements Validator {

    private final MovieRepository movieRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return MovieForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        MovieForm movieForm = (MovieForm)o;
        if(movieRepository.existsByPath(movieForm.getPath())) {
            errors.rejectValue("path", "wrong.path", "해당 경로는 사용할 수 없습니다.");
        }
    }
}
