package example;

import java.io.File;
import java.util.List;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class MahoutExmples {
	public static void main(String[] args) throws Exception {		
		// We generally want the files to be downloaded and reloaded from files. This is faster than pulling from a database since there is no network latency.
		File data = new File("board_game_data_ratings.csv");
		
		DataModel model = new FileDataModel(data);
		UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
		UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
		UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
		
		List<RecommendedItem> recommendations = recommender.recommend(1, 3);
		for (RecommendedItem recommendation : recommendations) {
		  System.out.println(recommendation);
		}
		
		File itemData = new File("board_game_data_ratings.csv");
		DataModel itemModel = new FileDataModel(itemData);
		ItemSimilarity itemSim = new EuclideanDistanceSimilarity(itemModel);
		
		GenericItemBasedRecommender itemRecommender = new GenericItemBasedRecommender(itemModel, itemSim);
		List<RecommendedItem> similarItems = itemRecommender.mostSimilarItems(1, 3);
		for (RecommendedItem recommendation : similarItems) {
		  System.out.println(recommendation);
		}
	}
}
