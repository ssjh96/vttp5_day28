package vttp5.paf.day28.repositories;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;

@Repository
public class BGGRepository {
    
    @Autowired 
    private MongoTemplate template;

// 1-5
//     db.games.aggregate([
//         {$match: {
//             name: {
//                $regex: 'carcassonne',
//                $options: 'i' }
//             }}
//    ])

//     db.games.aggregate([
//          { $match : { name : { $regex : name, $options: 'i' } } },
//          { $project : { name: 1, ranking: 1, image: 1, _id:-1 }},
//          { $sort: { ranking: -1 } },
//          { $limit: 3 }
//])

    public List<Document> findGamesByName(String name)
    {
        // 2) Create the aggregation stages
        Criteria criteria = Criteria.where("name")
            .regex(name, "i"); // match the name, ignore case
        
        // 1)
        MatchOperation matchName = Aggregation.match(criteria);

        // 6) Project Attributes
        ProjectionOperation projectFields = Aggregation
                            .project("name", "ranking", "image")
                            .andExclude("_id");

        // 7) Sort by ranking 
        SortOperation sortByRanking = Aggregation.sort(Sort.Direction.ASC, "ranking");

        // 8) Take top 3 results
        LimitOperation takeTop3 = Aggregation.limit(3);

        // 3) Create the pipeline
        Aggregation pipeline = Aggregation.newAggregation(
                            matchName, projectFields, sortByRanking, takeTop3);

        // 4) Run the aggregation
        AggregationResults<Document> results = template.aggregate(pipeline, "games", Document.class); // get them as documents

        // 5) Return the results as a list of Documents
        return results.getMappedResults(); // return List<Documents>
    }





    // db.comments.aggregate([
    //     {
    //         $group: {
    //             _id: '$user',            // Group by user
    //             comments: {              // Create an array of comments
    //                 $push: {             // Push each comment into the array
    //                     gid: '$gid',     
    //                     text: '$c_text'
    //                 }
    //             }
    //         }
    //     }
    // ])

    public List<Document> groupCommentsByUser() 
    {
        GroupOperation groupByUser = Aggregation.group("user")
            .push( // Creates an array of comments
                new BasicDBObject()
                .append("gid", "$gid")
                .append("text", "$c_text")
            ).as("comments");
        
        LimitOperation take3 = Aggregation.limit(3);

        Aggregation pipeline = Aggregation.newAggregation(groupByUser, take3);

        return template.aggregate(pipeline, "comments", Document.class).getMappedResults();
    }
}





