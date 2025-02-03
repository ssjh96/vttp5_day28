package vttp5.paf.day28.repositories;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.stereotype.Repository;

@Repository
public class SeriesRepository {
    
    @Autowired
    private MongoTemplate template;

    // db.series.aggregate([
    // { $unwind: '$genres'},
    // {
    //     $group: {
    //         _id: '$genres',
    //         count: {$sum: 1}
    //     }
    // },
    // { $sort: { _id: 1 } }    // Once group by genres, genres becomes _id, so must sort by _id
    // ])

    // if want to sort by most popular genre?
    // { $sort: { count: -1 } }  // Sort by count in descending order

    public List<Document> listSeriesByGenres() 
    {
        UnwindOperation unwindGenres = Aggregation.unwind("genres");
        
        GroupOperation groupAndCountGenres = Aggregation
                                    .group("genres")
                                    .count().as("count");
        
        SortOperation sortGenres = Aggregation.sort(Sort.Direction.ASC, "_id");

        Aggregation pipeline = Aggregation.newAggregation(unwindGenres, groupAndCountGenres, sortGenres);

        return template.aggregate(pipeline, "series", Document.class).getMappedResults();
    }

    // if want a list of titles for each genre?
    // db.series.aggregate([
    //     { $unwind: "$genres" },
    //     {
    //         $group: {
    //             _id: "$genres",
    //             count: { $sum: 1 },
    //             titles: { $push: "$title" }
    //         }
    //     },
    //     { $sort: { count: -1 } }
    // ])

    // e.g. O/P 
//     [
//     { "_id": "Drama", "count": 3, "titles": ["Breaking Bad", "Game of Thrones", "Stranger Things"] },
//     { "_id": "Fantasy", "count": 2, "titles": ["Game of Thrones", "Stranger Things"] },
//     { "_id": "Crime", "count": 1, "titles": ["Breaking Bad"] },
//     { "_id": "Horror", "count": 1, "titles": ["Stranger Things"] }
// ]


// if we want to sum views?
// What Happens If We Use $sum: "$someField"?
// If instead of 1, we sum a field, it will accumulate that field’s value.

// Example: Summing a views Field Instead

// db.series.aggregate([
//     { $unwind: "$genres" },
//     {
//         $group: {
//             _id: "$genres",
//             total_views: { $sum: "$views" }  // Instead of counting, we sum views
//         }
//     }
// ])

}
