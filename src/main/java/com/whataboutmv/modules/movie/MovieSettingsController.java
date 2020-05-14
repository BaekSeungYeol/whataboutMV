package com.whataboutmv.modules.movie;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whataboutmv.modules.account.CurrentUser;
import com.whataboutmv.modules.account.Account;
import com.whataboutmv.modules.tag.Tag;
import com.whataboutmv.modules.zone.Zone;
import com.whataboutmv.modules.movie.form.MovieDescriptionForm;
import com.whataboutmv.modules.account.form.TagForm;
import com.whataboutmv.modules.account.form.ZoneForm;
import com.whataboutmv.modules.tag.TagRepository;
import com.whataboutmv.modules.tag.TagService;
import com.whataboutmv.modules.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/movie/{path}/settings")
@RequiredArgsConstructor
public class MovieSettingsController {

    private final MovieRepository movieRepository;
    private final MovieService movieService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final TagService tagService;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;


    @GetMapping("/description")
    public String viewMovieSetting(@CurrentUser Account account, @PathVariable String path, Model model) {
        Movie movie = movieService.getMovieToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(movie);
        model.addAttribute(modelMapper.map(movie, MovieDescriptionForm.class));
        return "movie/settings/description";
    }

    @PostMapping("/description")
    public String updateStudyInfo(@CurrentUser Account account, @PathVariable String path, @Valid MovieDescriptionForm
                                  movieDescriptionForm, Errors errors, Model model, RedirectAttributes attributes) {
        Movie movie = movieService.getMovieToUpdate(account,path);

        if(errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(movie);
            return "movie/settings/description";
        }

        movieService.updateMovieDescription(movie, movieDescriptionForm);
        attributes.addFlashAttribute("message", "모임 소개를 수정했습니다.");
        return "redirect:/movie/" + movie.getEncodedPath() + "/settings/description";
    }

    @GetMapping("/banner")
    public String movieImageForm(@CurrentUser Account account, @PathVariable String path, Model model) {
        Movie movie = movieService.getMovieToUpdate(account,path);
        model.addAttribute(account);
        model.addAttribute(movie);
        return "movie/settings/banner";
    }

    @PostMapping("/banner")
    public String movieImageSubmit(@CurrentUser Account account, @PathVariable String path, String image, RedirectAttributes attributes) {
        Movie movie = movieService.getMovieToUpdate(account, path);
        movieService.updateMovieImage(movie,image);
        attributes.addFlashAttribute("message", "모임 이미지를 수정했습니다.");
        return "redirect:/movie/" + movie.getEncodedPath() + "/settings/banner";
    }

    @PostMapping("/banner/enable")
    public String enableMovieBanner(@CurrentUser Account account, @PathVariable String path) {
        Movie movie = movieService.getMovieToUpdate(account,path);
        movieService.enableMovieBanner(movie);
        return "redirect:/movie/" + movie.getEncodedPath() + "/settings/banner";
    }

    @PostMapping("/banner/disable")
    public String disableMovieBanner(@CurrentUser Account account, @PathVariable String path) {
        Movie movie = movieService.getMovieToUpdate(account,path);
        movieService.disableMovieBanner(movie);
        return "redirect:/movie/" + movie.getEncodedPath() + "/settings/banner";
    }

    @GetMapping("/tags")
    public String movieTagsForm(@CurrentUser Account account, @PathVariable String path, Model model)
    throws JsonProcessingException {
        Movie movie = movieService.getMovieToUpdate(account,path);
        model.addAttribute(account);
        model.addAttribute(movie);

        model.addAttribute("tags", movie.getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));
        List<String> allTagTitles = tagRepository.findAll().stream()
                .map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTagTitles));
        return "movie/settings/tags";
    }

    @PostMapping("/tags/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentUser Account account,@PathVariable String path,
                                 @RequestBody TagForm tagForm) {

        Movie movie = movieService.getMovieToUpdateTag(account, path);
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        movieService.addTag(movie,tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentUser Account account, @PathVariable String path,
                                    @RequestBody TagForm tagForm) {
        Movie movie = movieService.getMovieToUpdateTag(account, path);
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        if(tag == null) {
            return ResponseEntity.badRequest().build();
        }

        movieService.removeTag(movie,tag);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/zones")
    public String movieZoneForm(@CurrentUser Account account, @PathVariable String path, Model model)
            throws JsonProcessingException {
        Movie movie = movieService.getMovieToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(movie);

        model.addAttribute("zones", movie.getZones().stream().map(Zone::toString).collect(Collectors.toList()));
        List<String> allZones = zoneRepository.findAll().stream()
                .map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));
        return "movie/settings/zones";
    }

    @PostMapping("/zones/add")
    @ResponseBody
    public ResponseEntity addZone(@CurrentUser Account account, @PathVariable String path,
                                  @RequestBody ZoneForm zoneForm) {
        Movie movie = movieService.getMovieToUpdateZone(account,path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if(zone == null) {
            return ResponseEntity.badRequest().build();
        }

        movieService.addZone(movie,zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/zones/remove")
    @ResponseBody
    public ResponseEntity removeZone(@CurrentUser Account account, @PathVariable String path,
                                  @RequestBody ZoneForm zoneForm) {
        Movie movie = movieService.getMovieToUpdateZone(account,path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if(zone == null) {
            return ResponseEntity.badRequest().build();
        }

        movieService.removeZone(movie,zone);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/movie")
    public String movieSettingForm(@CurrentUser Account account, @PathVariable String path, Model model) {
        Movie movie = movieService.getMovieToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(movie);
        return "movie/settings/movie";
    }
    @PostMapping("/movie/publish")
    public String publishStudy(@CurrentUser Account account, @PathVariable String path,
                               RedirectAttributes attributes) {
        Movie movie = movieService.getMovieToUpdateStatus(account, path);
        movieService.publish(movie);
        attributes.addFlashAttribute("message", "모임을 공개했습니다.");
        return "redirect:/movie/" + movie.getEncodedPath() + "/settings/movie";
    }
    @PostMapping("/movie/close")
    public String closeStudy(@CurrentUser Account account, @PathVariable String path,
                             RedirectAttributes attributes) {
        Movie movie = movieService.getMovieToUpdateStatus(account,path);
        movieService.close(movie);
        attributes.addFlashAttribute("message", "모임을 종료합니다.");
        return "redirect:/movie/" + movie.getEncodedPath() + "/settings/movie";
    }
    @PostMapping("/recruit/start")
    public String startRecruit(@CurrentUser Account account, @PathVariable String path, Model model,
                               RedirectAttributes attributes) {
        Movie movie = movieService.getMovieToUpdateStatus(account,path);
        if(!movie.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "1시간 안에 인원 모집 설정을 여러번 변경할 수는 없습니다.");
            return "redirect:/movie/" + movie.getEncodedPath() + "/settings/movie";
        }

        movieService.startRecruit(movie);
        attributes.addFlashAttribute("message", "인원 모집을 시작합니다.");
        return "redirect:/movie/" + movie.getEncodedPath() + "/settings/movie";
    }

    @PostMapping("/recruit/stop")
    public String stopRecruit(@CurrentUser Account account, @PathVariable String path, Model model,
                              RedirectAttributes attributes) {
        Movie movie = movieService.getMovieToUpdateStatus(account,path);
        if(!movie.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "1시간 안에 인원 모집 설정을 여러번 변경할 수는 없습니다.");
            return "redirect:/movie/" + movie.getEncodedPath() + "/settings/movie";
        }

        movieService.stopRecruit(movie);
        attributes.addFlashAttribute("message", "인원 모집을 종료합니다.");
        return "redirect:/movie/" + movie.getEncodedPath() + "/settings/movie";
    }

    @PostMapping("/movie/path")
    public String updateMoviePath(@CurrentUser Account account, @PathVariable String path,
                                  String newPath, Model model, RedirectAttributes attributes) {

        Movie movie = movieService.getMovieToUpdateStatus(account,path);
        if(!movieService.isValidPath(newPath)) {
            model.addAttribute(account);
            model.addAttribute(movie);
            model.addAttribute("moviePathError", "해당 모임 경로는 사용할 수 없습니다. 다른 값을 입력하세요.");
            return "movie/settings/movie";
        }

        movieService.updateMoviePath(movie,newPath);
        attributes.addFlashAttribute("message", "모임 경로를 수정했습니다.");
        return "redirect:/movie/" + movie.getEncodedPath() + "/settings/movie";
    }

    @PostMapping("/movie/title")
    public String updateMovieTitle(@CurrentUser Account account, @PathVariable String path, String newTitle,
                                   Model model, RedirectAttributes attributes) {
        Movie movie = movieService.getMovieToUpdateStatus(account, path);
        if(!movieService.isValidTitle(newTitle)) {
            model.addAttribute(account);
            model.addAttribute(movie);
            model.addAttribute("moviePathError", "해당 모임 이름을 사용할 수 없습니다. 다른 값을 입력하세요.");
            return "movie/settings/movie";
        }

        movieService.updateMovieTitle(movie, newTitle);
        attributes.addFlashAttribute("message", "모임 이름을 수정했습니다.");
        return "redirect:/movie/" + movie.getEncodedPath() + "/settings/movie";

    }

    @PostMapping("/movie/remove")
    public String removeMovie(@CurrentUser Account account, @PathVariable String path, Model model) {
        Movie movie = movieService.getMovieToUpdateStatus(account,path);
        movieService.remove(movie);
        return "redirect:/";
    }
}
