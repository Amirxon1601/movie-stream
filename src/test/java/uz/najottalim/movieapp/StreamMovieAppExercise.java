package uz.najottalim.movieapp;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import lombok.extern.slf4j.Slf4j;
import uz.najottalim.movieapp.models.Director;
import uz.najottalim.movieapp.models.Genre;
import uz.najottalim.movieapp.models.Movie;
import uz.najottalim.movieapp.repos.DirectorRepo;
import uz.najottalim.movieapp.repos.GenreRepo;
import uz.najottalim.movieapp.repos.MovieRepo;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class StreamMovieAppExercise {

    private DirectorRepo directorRepo;
    private GenreRepo genreRepo;
    private MovieRepo movieRepo;

    @BeforeEach
    void setUp() {
        directorRepo = new DirectorRepo();
        genreRepo = new GenreRepo();
        movieRepo = new MovieRepo();
    }

    @Test
    @DisplayName("Hammasini ko'rish")
    public void printAll() {
        // System.out.println o'rniga shuni ishlatingla
        log.info("Movies:");
        movieRepo.findAll()
                .forEach(p -> log.info(p.toString() + '\n'));
        log.info("Directors:");
        directorRepo.findAll()
                .forEach(p -> log.info(p.toString() + '\n'));
        log.info("Genres:");
        genreRepo.findAll()
                .forEach(p -> log.info(p.toString() + '\n'));
    }

    @Test
    @DisplayName("Janri 'Drama' yoki 'Komediya' bo'lgan kinolarni toping")
    public void exercise1() {
        List<Movie> movies = movieRepo.findAll()
                .stream()
                .filter(movie ->
                        movie.getGenres()
                                .stream()
                                .anyMatch(genre ->
                                        genre.getName().equalsIgnoreCase("Drama")
                                                || genre.getName().equalsIgnoreCase("Komediya")))
                .collect(Collectors.toList());
        movies.forEach(movie -> {
            System.out.println(movie.getTitle() +
                    " -> janrlari: " +
                    movie.getGenres()
                            .stream()
                            .map(Genre::getName)
                            .collect(Collectors.toList()));
        });
    }

    @Test
    @DisplayName("Har bitta rejissorning olgan kinolar sonini chiqaring")
    public void exercise2() {
        var mapToCount = directorRepo.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        director -> (long) director.getMovies().size(),
                        Long::sum
                ));
//        mapToCount.forEach((key, value) -> {
//            System.out.println(key.getName() + " : " + value);
//        });
        var map = directorRepo.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.flatMapping(director -> director.getMovies().stream(),
                                Collectors.counting())
                ));
        map.forEach((key, value) -> {
            System.out.println(key + " : " + value);
        });
        assertListEquals(map.values(), mapToCount.values());
    }

    <T> void assertListEquals(Collection<T> aa, Collection<T> bb) {
        final String exceptionMessage = "Collections is not equals";
        if (aa.size() != bb.size())
            throw new RuntimeException(exceptionMessage);

        for (T element : aa) {
            if (!bb.contains(element)) {
                throw new RuntimeException(exceptionMessage);
            }
        }
    }

    @Test
    @DisplayName("Eng oldin olingan kinoni chiqaring")
    public void exercise8() {
        Optional<Movie> min = movieRepo.findAll()
                .stream()
                .min(Comparator.comparing(Movie::getYear));
        System.out.println(min);


    }

    @Test
    @DisplayName("2004 chi yilda kinolaga sarflangan umumiy summani chiqaring")
    public void exercise9() {
        double sum = movieRepo.findAll()
                .stream()
                .filter(movie -> movie.getYear() == 2004).mapToDouble(Movie::getSpending).sum();
        System.out.println(sum);
    }

    @Test
    @DisplayName("har bir yilda olingan kinolarni o'rtacha reytingini chiqaring")
    public void exercise10() {
        /*
        2001 -> 4.11
        1999 -> 2.2
         */

        var yearToRating = movieRepo.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        Movie::getYear,
                        Collectors.averagingDouble(Movie::getRating)
                ));
        yearToRating.forEach((key, value) -> {
            System.out.println(key + ":" + String.format("%.02f", value));
        });
    }

    @Test
    @DisplayName("Janri 'Drama' bo'lgan eng tarixda suratga olingan kinoni chiqaring")
    public void exercise11() {
        OptionalDouble drama = movieRepo.findAll()
                .stream()
                .filter(movie -> movie.getGenres().stream().anyMatch(genre -> genre.getName().equalsIgnoreCase("Drama")))
                .mapToDouble(Movie::getYear).min();
        System.out.println(drama);
    }

    @Test
    @DisplayName("Qaysi janrdagi kinolar eng ko'p oscar olganligini chiqaring")
    public void exercise12() {

    }

    @Test
    @DisplayName("Har bir rejissorni olgan kinolarining janrini soni chiqaring" +
            "Misol uchun: " +
            "Aziz Aliev suratga olgan" +
            "Komediya    : 2\n" +
            "Drama       : 5\n" +
            "Romantika   : 2")
    public void exercise3() {

    }

    @Test
    @DisplayName("2004 chi yilda chiqqan kinolar orasida eng ko'p pul sarflanganini chiqaring")
    public void exercise4() {
        OptionalDouble max = movieRepo.findAll()
                .stream()
                .filter(movie -> movie.getYear() == 2004).mapToDouble(Movie::getSpending).max();
        System.out.println(max);
    }

    @Test
    @DisplayName("har bitta rejissor olgan kinolarining o'rtacha ratingini chiqaring" +
            "Misol uchun:" +
            "Sardor Muhammadaliev: 2.23" +
            "Akrom Aliev: 2.33")
    public void exercise5() {

    }

    @Test
    @DisplayName("Rejissolarni umumiy kinolari uchun olgan oskarlari soni bo'yicha saralab chiqaring")
    public void exercise6() {

    }

    @Test
    @DisplayName("2004 yilda olingan Komediya kinolariga ketgan umumiy summani chiqaring")
    public void exercise7() {
        Double sum = movieRepo.findAll()
                .stream()
                .filter(movie -> movie.getYear() == 2004)
                .filter(movie -> movie.getGenres().stream()
                        .anyMatch(genre -> genre.getName().equalsIgnoreCase("Komediya")))
               .mapToDouble(Movie::getSpending).sum();
        System.out.println(sum);
    }


    @Test
    @DisplayName("Kinolarni chiqgan yili bo'yicha guruhlab, ratingi bo'yicha saralab toping")
    public void exercise13() {

    }

    @Test
    @DisplayName("Har bitta rejiossor qaysi janrda o'rtacha reytingi eng ko'p " +
            "ekanligini chiqaring")
    public void exercise14() {
//        directorRepo.findAll()
//                .stream()
//                .collect(Collectors.groupingBy(
//                        Function.identity(),
//                        Collectors.mapping(director -> director.getMovies(),
//                                Collectors.toList())
//                ));
        Map<Director, Genre> directorOptionalMap = directorRepo.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        director -> {
                            var rejissorOlganKinolar = director.getMovies();

                            var genreToAverageRating = genreRepo.findAll()
                                    .stream()
                                    .collect(Collectors.toMap(Function.identity(),
                                            genre -> rejissorOlganKinolar.stream()
                                                    .filter(rejissorOlganKino -> rejissorOlganKino.getGenres()
                                                            .contains(genre))
                                                    .mapToDouble(Movie::getRating)
                                                    .average()
                                                    .orElse(-1)));
                            return genreToAverageRating.entrySet()
                                    .stream()
                                    .max(Comparator.comparingDouble(Map.Entry::getValue))
                                    .map(Map.Entry::getKey)
                                    .get();
                        }
                ));
        directorOptionalMap.forEach((rejissor, janr) -> {
            System.out.println(rejissor + " : " + janr);
        });
    }

    @Test
    @DisplayName("Eng kam kinolarga pul sarflagan rejissorni chiqaring")
    public void exercise15() {

    }

    @Test
    @DisplayName("Komediya kinolarini, 2000 chi yildan keyin olinganlarga ketgan narxni DoubleSummaryStatisticasini chiqaring")
    public void exercise16() {

    }

    @Test
    @DisplayName("Qaysi kinoni eng ko'p rejissorlar birgalikda olishgan va ratingi eng baland chiqqan")
    public void exercise17() {

    }

    @Test
    @DisplayName("Har bir janrdagi kino nomlari umumiy nechta so'zdan iborat")
    public void exercise18() {

    }

    @Test
    @DisplayName("Har bir asrda olingan kinolarni o'rtacha reytingini chiqaring")
    public void exercise19() {

    }

    @Test
    @DisplayName("Ismi A harfi bilan boshlanadigan rejissorlarni olgan kinolarini o'rtacha ratingi bo'yicha saralab chiqaring")
    public void exercise20() {

    }
}
