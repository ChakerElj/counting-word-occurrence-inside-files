import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static final Pattern WORD_BREAK = Pattern.compile("\\W+");
    private static final Comparator<Map.Entry<String,Long>> valueOrder = Map.Entry.comparingByValue();
    private static final Comparator<Map.Entry<String,Long>> reversedValue = valueOrder.reversed();
    public static void main(String[] args) {
        List<String> filenames = Arrays.asList(
                "Emma.txt",
                "PrideAndPrejudice.txt",
                "AnimalHeroes.txt",
                "SenseAndSensibility.txt"
        );
        printWordOccurrenceMultipleFile(filenames);

    }
    public static Optional<Stream<String>>  lines(Path p){
        try {
            return Optional.of(Files.lines(p)) ;
        }catch (IOException ioe){
            System.err.println("File read failed : " + ioe.getMessage());
            return Optional.empty();
        }
    }
    public static <E,F> Function<E,Optional<F>> wrap (ExceptionFunction<E,F> op){
        return e-> {
            try {
                return Optional.of(op.apply(e));
            }
            catch (Throwable t){
                return Optional.empty();
            }
        };
    }
    public static void printWordOccurrenceMultipleFile(List<String> filesPath){

            filesPath.stream()
                    .map(Paths::get)
                    .map(wrap(Files::lines))
                    .peek((o)-> {if (o.isEmpty()) System.err.println("Bad File!");}).
                    filter(Optional::isPresent).
                    flatMap(Optional::get)
                    .flatMap(WORD_BREAK::splitAsStream)
                    .filter(w -> !w.isEmpty()).map(String::toLowerCase).
                    collect(Collectors.groupingBy(Function.identity(),Collectors.counting()))
                    .entrySet().stream().sorted(reversedValue).forEach(
                            (l) -> System.out.printf("%20s : %5d\n", l.getKey(), l.getValue()));


    }
    public static void printWordOccurrenceOneFile(String filePath){
        try {
            Stream<String> linesOfTheText = Files.lines(Path.of(filePath));
            /* Convert a stream of lines to another stream of words of every line using flatMap() then
             we do a groupBy() operation to group by the word next we introduce a new downstream
             to count the occurrence of any word inside the new stream, and finally we sort the output using reversed order of
             number of occurrence and print the result*/
            linesOfTheText.flatMap(WORD_BREAK::splitAsStream)
                    .filter(w -> !w.isEmpty()).map(String::toLowerCase).
                    collect(Collectors.groupingBy(Function.identity(),Collectors.counting()))
                    .entrySet().stream().sorted(reversedValue).limit(200).forEach(
                            (l) -> System.out.printf("%20s : %5d\n", l.getKey(), l.getValue()));


        }catch (IOException exception){
            System.out.println(exception.getMessage());
        }

    }
}
