package example;

import java.io.File;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AbstractDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.SpearmanCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

public class MahoutExmples {
	
	private static int nearestNeighborhoodSize = 50;
	private static double thresholdUserSize = 0.1;
	
	private static double percentDataEval = 0.1;
	private static double percentDataTrain = 0.9;
	
	private static double percentageRec = 0.5;
	private static double includeRecThreshold = 0.8;
	
	private static String evalFile = "num_posts_eval.csv";
	
	private static String file = "comments_posts_max_norm_no_bots_no_default_2_3.csv";
	private static String booleanFile = "num_comments_max_norm_no_bots_boolean.csv";
	
	private static Double[] possThresh = new Double[]{0.1,  0.25, 0.5, 0.75, 0.9};
	private static Integer[] neighborhoodSize = new Integer[]{5, 10, 50, 100};
	private static String[] files = new String[]{"comments_posts_max_norm_no_bots_no_default_2_3.csv",
			"comments_posts_max_norm_no_bots_no_default_2_25.csv"};
	
	public static void main(String[] args) throws Exception {		
		// We generally want the files to be downloaded and reloaded from files. This is faster than pulling from a database since there is no network latency.
		
		for (int i = 0; i < files.length; i++) {
			file = files[i];
			System.out.println(file);
			//Recommender Evals
			RandomUtils.useTestSeed();
			File data = new File(file);
			DataModel model = new FileDataModel(data);
			
			RecommenderEvaluator evaluator = new RMSRecommenderEvaluator();
			AbstractDifferenceRecommenderEvaluator avgEvaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
			
			for (int j = 0; j < neighborhoodSize.length; j++) {
				nearestNeighborhoodSize = neighborhoodSize[i];
				//Recommenders that give actual preferences
				
				RecommenderBuilder pearNear = new RecommenderBuilder() {
					public Recommender buildRecommender(DataModel model) throws TasteException {
						UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
						UserNeighborhood neighborhood = new NearestNUserNeighborhood(nearestNeighborhoodSize, similarity, model);
						UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
						return recommender;
					}
				};
				
				RecommenderBuilder eucNear = new RecommenderBuilder() {
					public Recommender buildRecommender(DataModel model) throws TasteException {
						UserSimilarity similarity = new EuclideanDistanceSimilarity(model);
						UserNeighborhood neighborhood = new NearestNUserNeighborhood(nearestNeighborhoodSize, similarity, model);
						UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
						return recommender;
					}
				};
				
				RecommenderBuilder logNear = new RecommenderBuilder() {
					public Recommender buildRecommender(DataModel model) throws TasteException {
						UserSimilarity similarity = new LogLikelihoodSimilarity(model);
						UserNeighborhood neighborhood = new NearestNUserNeighborhood(nearestNeighborhoodSize, similarity, model);
						UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
						return recommender;
					}
				};
				
				RecommenderBuilder spearmanNear = new RecommenderBuilder() {
					public Recommender buildRecommender(DataModel model) throws TasteException {
						UserSimilarity similarity = new SpearmanCorrelationSimilarity(model);
						UserNeighborhood neighborhood = new NearestNUserNeighborhood(nearestNeighborhoodSize, similarity, model);
						UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
						return recommender;
					}
				};
				
				RecommenderBuilder tanimotoNear = new RecommenderBuilder() {
					public Recommender buildRecommender(DataModel model) throws TasteException {
						UserSimilarity similarity = new TanimotoCoefficientSimilarity(model);
						UserNeighborhood neighborhood = new NearestNUserNeighborhood(nearestNeighborhoodSize, similarity, model);
						UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
						return recommender;
					}
				};
				
				RecommenderBuilder uncenteredNear = new RecommenderBuilder() {
					public Recommender buildRecommender(DataModel model) throws TasteException {
						UserSimilarity similarity = new UncenteredCosineSimilarity(model);
						UserNeighborhood neighborhood = new NearestNUserNeighborhood(nearestNeighborhoodSize, similarity, model);
						UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
						return recommender;
					}
				};
				
				double userSimEvaluationScore = evaluator.evaluate(pearNear, null, model, percentDataTrain, percentDataEval);
				System.out.println("Evaluation Score (Pearson + Nearest N):" + userSimEvaluationScore);
				
				userSimEvaluationScore = evaluator.evaluate(eucNear, null, model, percentDataTrain, percentDataEval);
				System.out.println("Evaluation Score (Euclidean Distance + Nearest N):" + userSimEvaluationScore);
				
				userSimEvaluationScore = evaluator.evaluate(logNear, null, model, percentDataTrain, percentDataEval);
				System.out.println("Evaluation Score (Log Likelihood + Nearest N):" + userSimEvaluationScore);
				
				double avgDiff = avgEvaluator.evaluate(logNear, null, model, percentDataTrain, percentDataEval);
				System.out.println("Evaluation Score Average Diffence (Log Likelihood + Nearest N):" + avgDiff);
				
				userSimEvaluationScore = evaluator.evaluate(spearmanNear, null, model, percentDataTrain, percentDataEval);
				System.out.println("Evaluation Score (Spearman + Nearest N):" + userSimEvaluationScore);
				
				userSimEvaluationScore = evaluator.evaluate(tanimotoNear, null, model, percentDataTrain, percentDataEval);
				System.out.println("Evaluation Score (Tanimoto + Nearest N):" + userSimEvaluationScore);
				
				avgDiff = avgEvaluator.evaluate(tanimotoNear, null, model, percentDataTrain, percentDataEval);
				System.out.println("Evaluation Score Average Diffence (Tanimoto + Nearest N):" + avgDiff);
				
				GenericRecommenderIRStatsEvaluator irstatsEvaluator = new GenericRecommenderIRStatsEvaluator();
				IRStatistics stats = irstatsEvaluator.evaluate(tanimotoNear, null, model, null, 1,
						GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 0.0001);
				System.out.println("Tanimoto Near 1 (0.0001):");
				System.out.println(stats.getPrecision());
				System.out.println(stats.getRecall());
				
				irstatsEvaluator = new GenericRecommenderIRStatsEvaluator();
				stats = irstatsEvaluator.evaluate(tanimotoNear, null, model, null, 1,
						GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 0.001);
				System.out.println("Tanimoto Near 1 (0.001):");
				System.out.println(stats.getPrecision());
				System.out.println(stats.getRecall());
				
				irstatsEvaluator = new GenericRecommenderIRStatsEvaluator();
				stats = irstatsEvaluator.evaluate(tanimotoNear, null, model, null, 2,
						GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 0.001);
				System.out.println("Tanimoto Near 2 (0.001):");
				System.out.println(stats.getPrecision());
				System.out.println(stats.getRecall());
				
				irstatsEvaluator = new GenericRecommenderIRStatsEvaluator();
				stats = irstatsEvaluator.evaluate(tanimotoNear, null, model, null, 2,
						GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 0.01);
				System.out.println("Tanimoto Near 2 (0.01):");
				System.out.println(stats.getPrecision());
				System.out.println(stats.getRecall());
				
				irstatsEvaluator = new GenericRecommenderIRStatsEvaluator();
				stats = irstatsEvaluator.evaluate(pearNear, null, model, null, 1,
						GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 0.0001);
				System.out.println("Pearson Near 1 (0.0001):");
				System.out.println(stats.getPrecision());
				System.out.println(stats.getRecall());
				
				irstatsEvaluator = new GenericRecommenderIRStatsEvaluator();
				stats = irstatsEvaluator.evaluate(pearNear, null, model, null, 1,
						GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 0.001);
				System.out.println("Pearson Near 1 (0.001):");
				System.out.println(stats.getPrecision());
				System.out.println(stats.getRecall());
				
				irstatsEvaluator = new GenericRecommenderIRStatsEvaluator();
				stats = irstatsEvaluator.evaluate(pearNear, null, model, null, 2,
						GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 0.0001);
				System.out.println("Pearson Near 2 (0.0001):");
				System.out.println(stats.getPrecision());
				System.out.println(stats.getRecall());
				
				irstatsEvaluator = new GenericRecommenderIRStatsEvaluator();
				stats = irstatsEvaluator.evaluate(pearNear, null, model, null, 2,
						GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 0.01);
				System.out.println("Pearson Near 2 (0.01):");
				System.out.println(stats.getPrecision());
				System.out.println(stats.getRecall());
			}
				
				//Boolean Recommenders
				
				/*RecommenderBuilder booleanRec = new RecommenderBuilder() {
					public Recommender buildRecommender(DataModel model) throws TasteException {
						UserSimilarity similarity = new TanimotoCoefficientSimilarity(model);
						UserNeighborhood neighborhood = new NearestNUserNeighborhood(nearestNeighborhoodSize, similarity, model);
						UserBasedRecommender recommender = new GenericBooleanPrefUserBasedRecommender(model, neighborhood, similarity);
						return recommender;
					}
				};*/
			
			for (int j = 0; j < neighborhoodSize.length; j++) {
				RecommenderBuilder pearThreshold = new RecommenderBuilder() {
					public Recommender buildRecommender(DataModel model) throws TasteException {
						UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
						UserNeighborhood neighborhood = new ThresholdUserNeighborhood(thresholdUserSize, similarity, model);
						UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
						return recommender;
					}
				};
				
				RecommenderBuilder uncenteredThrehold = new RecommenderBuilder() {
					public Recommender buildRecommender(DataModel model) throws TasteException {
						UserSimilarity similarity = new UncenteredCosineSimilarity(model);
						UserNeighborhood neighborhood = new ThresholdUserNeighborhood(thresholdUserSize, similarity, model);
						UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
						return recommender;
					}
				};
				
				RecommenderBuilder tanimotoThreshold = new RecommenderBuilder() {
					public Recommender buildRecommender(DataModel model) throws TasteException {
						UserSimilarity similarity = new TanimotoCoefficientSimilarity(model);
						UserNeighborhood neighborhood = new ThresholdUserNeighborhood(thresholdUserSize, similarity, model);
						UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
						return recommender;
					}
				};
				
				RecommenderBuilder spearmanThreshold = new RecommenderBuilder() {
					public Recommender buildRecommender(DataModel model) throws TasteException {
						UserSimilarity similarity = new SpearmanCorrelationSimilarity(model);
						UserNeighborhood neighborhood = new ThresholdUserNeighborhood(thresholdUserSize, similarity, model);
						UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
						return recommender;
					}
				};
				
				RecommenderBuilder logThreshold = new RecommenderBuilder() {
					public Recommender buildRecommender(DataModel model) throws TasteException {
						UserSimilarity similarity = new LogLikelihoodSimilarity(model);
						UserNeighborhood neighborhood = new ThresholdUserNeighborhood(thresholdUserSize, similarity, model);
						UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
						return recommender;
					}
				};
				
				RecommenderBuilder eucThreshold = new RecommenderBuilder() {
					public Recommender buildRecommender(DataModel model) throws TasteException {
						UserSimilarity similarity = new EuclideanDistanceSimilarity(model);
						UserNeighborhood neighborhood = new ThresholdUserNeighborhood(thresholdUserSize, similarity, model);
						UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
						return recommender;
					}
				};
				
				double userSimEvaluationScore = evaluator.evaluate(pearThreshold, null, model, percentDataTrain, percentDataEval);
				System.out.println("Evaluation Score (Pearson + Threshold): " + userSimEvaluationScore);
				
				// Threshold Recommenders
				
				userSimEvaluationScore = evaluator.evaluate(eucThreshold, null, model, percentDataTrain, percentDataEval);
				System.out.println("Evaluation Score (Euclidean Distance + Threshold):" + userSimEvaluationScore);
				
				userSimEvaluationScore = evaluator.evaluate(logThreshold, null, model, percentDataTrain, percentDataEval);
				System.out.println("Evaluation Score (Log Likelihood + Threshold):" + userSimEvaluationScore);
				
				double avgDiff = avgEvaluator.evaluate(logThreshold, null, model, percentDataTrain, percentDataEval);
				System.out.println("Evaluation Score Average Diffence (Log Likelihood + Threshold):" + avgDiff);
				
				userSimEvaluationScore = evaluator.evaluate(spearmanThreshold, null, model, percentDataTrain, percentDataEval);
				System.out.println("Evaluation Score (Spearman + Threshold):" + userSimEvaluationScore);
				
				userSimEvaluationScore = evaluator.evaluate(tanimotoThreshold, null, model, percentDataTrain, percentDataEval);
				System.out.println("Evaluation Score (Tanimoto + Threshold):" + userSimEvaluationScore);
				
				avgDiff = avgEvaluator.evaluate(tanimotoThreshold, null, model, percentDataTrain, percentDataEval);
				System.out.println("Evaluation Score Average Diffence (Tanimoto + Threshold):" + avgDiff);
			
				//UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
				//UserNeighborhood neighborhood = new ThresholdUserNeighborhood(thresholdUserSize, similarity, model);
				//UserBasedRecommender testRecommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
				
				//List<RecommendedItem> items = testRecommender.recommend(478331, 10);
				//System.out.println(items);
				
				GenericRecommenderIRStatsEvaluator irstatsEvaluator = new GenericRecommenderIRStatsEvaluator();
				IRStatistics stats = irstatsEvaluator.evaluate(tanimotoThreshold, null, model, null, 1,
						GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 0.0001);
				System.out.println("Tanimoto Threshold 1 (0.0001):");
				System.out.println(stats.getPrecision());
				System.out.println(stats.getRecall());
				
				irstatsEvaluator = new GenericRecommenderIRStatsEvaluator();
				stats = irstatsEvaluator.evaluate(tanimotoThreshold, null, model, null, 1,
						GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 0.001);
				System.out.println("Tanimoto Threshold 1 (0.001):");
				System.out.println(stats.getPrecision());
				System.out.println(stats.getRecall());
				
				irstatsEvaluator = new GenericRecommenderIRStatsEvaluator();
				stats = irstatsEvaluator.evaluate(tanimotoThreshold, null, model, null, 2,
						GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 0.001);
				System.out.println("Tanimoto Threshold 2 (0.001):");
				System.out.println(stats.getPrecision());
				System.out.println(stats.getRecall());
				
				irstatsEvaluator = new GenericRecommenderIRStatsEvaluator();
				stats = irstatsEvaluator.evaluate(tanimotoThreshold, null, model, null, 2,
						GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 0.01);
				System.out.println("Tanimoto Threshold 2 (0.01):");
				System.out.println(stats.getPrecision());
				System.out.println(stats.getRecall());
				
				irstatsEvaluator = new GenericRecommenderIRStatsEvaluator();
				stats = irstatsEvaluator.evaluate(pearThreshold, null, model, null, 1,
						GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 0.0001);
				System.out.println("Pearson Threshold 1 (0.0001):");
				System.out.println(stats.getPrecision());
				System.out.println(stats.getRecall());
				
				irstatsEvaluator = new GenericRecommenderIRStatsEvaluator();
				stats = irstatsEvaluator.evaluate(pearThreshold, null, model, null, 1,
						GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 0.001);
				System.out.println("Pearson Threshold 1 (0.001):");
				System.out.println(stats.getPrecision());
				System.out.println(stats.getRecall());
				
				irstatsEvaluator = new GenericRecommenderIRStatsEvaluator();
				stats = irstatsEvaluator.evaluate(pearThreshold, null, model, null, 2,
						GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 0.0001);
				System.out.println("Pearson Threshold 2 (0.0001):");
				System.out.println(stats.getPrecision());
				System.out.println(stats.getRecall());
				
				irstatsEvaluator = new GenericRecommenderIRStatsEvaluator();
				stats = irstatsEvaluator.evaluate(pearThreshold, null, model, null, 2,
						GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 0.01);
				System.out.println("Pearson Threshold 2 (0.01):");
				System.out.println(stats.getPrecision());
				System.out.println(stats.getRecall());
				
				//Boolean Evals
				//File booleanData = new File(booleanFile);
				//DataModel booleanModel = new FileDataModel(booleanData);
				
				//userSimEvaluationScore = evaluator.evaluate(booleanRec, null, booleanModel, percentDataTrain, percentDataEval);
				//System.out.println("Evaluation Score (Pearson + Threshold + Boolean Pref): " + userSimEvaluationScore);
				
				/*List<RecommendedItem> recommendations = recommender.recommend(88, 10);
				List<RecommendedItem> rec2 = recommender.recommend(544, 10);
				List<RecommendedItem> rec3 = recommender.recommend(988, 10);
				List<RecommendedItem> rec4 = recommender.recommend(99, 10);
				
				System.out.println("User 300 Recommendations:");
				for (RecommendedItem recommendation : recommendations) {
				  System.out.println(recommendation);
				}
				
				System.out.println("User 200 Recommendations:");
				for (RecommendedItem recommendation : rec2) {
					  System.out.println(recommendation);
				}
				
				System.out.println("User 450 Recommendations:");
				for (RecommendedItem recommendation : rec3) {
					  System.out.println(recommendation);
				}
				
				System.out.println("User 600 Recommendations:");
				for (RecommendedItem recommendation : rec4) {
					  System.out.println(recommendation);
				}*/
				
				/*File itemData = new File("num_comments_tfidf.csv");
				DataModel itemModel = new FileDataModel(itemData);
				ItemSimilarity itemSim = new EuclideanDistanceSimilarity(itemModel);
				
				GenericItemBasedRecommender itemRecommender = new GenericItemBasedRecommender(itemModel, itemSim);
				List<RecommendedItem> similarItems = itemRecommender.mostSimilarItems(1, 3);
				for (RecommendedItem recommendation : similarItems) {
				  System.out.println(recommendation);
				}*/
			}
		}
	}
}
