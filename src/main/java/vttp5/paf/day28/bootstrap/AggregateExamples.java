package vttp5.paf.day28.bootstrap;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import vttp5.paf.day28.repositories.BGGRepository;
import vttp5.paf.day28.repositories.SeriesRepository;

@Component
public class AggregateExamples implements CommandLineRunner{

    @Autowired
    private BGGRepository bggRepo;

    @Autowired 
    private SeriesRepository seriesRepository;

    @Override
    public void run(String... args) throws Exception {

        // List<Document> results = bggRepo.findGamesByName("carcassonne"); // use bgg
        // List<Document> results = bggRepo.groupCommentsByUser();
        List<Document> results = seriesRepository.listSeriesByGenres();

        System.out.printf(">>>>> count: %d\n", results.size());
        
        for(Document d : results)
        {
            System.out.printf(">>>>> %s\n\n", d);
        }
    }
    
}
