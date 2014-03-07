package cern.ch.mathexplorer.clusteranalysis;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.cdbw.CDbwEvaluator;
import org.apache.mahout.clustering.evaluation.ClusterEvaluator;
import org.apache.mahout.clustering.iterator.ClusterWritable;
import org.apache.mahout.common.distance.CosineDistanceMeasure;
import org.apache.mahout.common.distance.DistanceMeasure;

/**
 * Clusters created with:
 * ./mahout kmeans 
 * -i ../results/vectors 
 * -o ../results/clustering_kmeans/k256 
 * -xm sequential 
 * -k 256 
 * --clusters ../results/clustering_kmeans/k256 
 * -x 50 
 * -cd 0.1 
 * -cl 
 * -dm org.apache.mahout.common.distance.CosineDistanceMeasure
 *
 */

public class ClusterAnalyzer {
	static List<Cluster> clusters = new ArrayList<Cluster>();

	public static void main(String args[]) throws Exception {
		int k = 2;
		
		String inputFile = "/home/cern/Desktop/Thesis/sw/mahout-distribution-0.9_2/results/clustering_kmeans/k"+k+""
				+ "/clusters-2-final/";
		for( int i = 0; i<k; i++) {
			String ending = "part-0";
			if(i<10){
				ending += "000"+i;
			} else if (i < 100) {
				ending += "00"+i;
			} else if (i < 1000) {
				ending += "0" +i ;
			}
			
			Configuration conf = new Configuration();
			Path path = new Path(inputFile+ending);
			System.out.println("Input Path: " + path);
			FileSystem fs = FileSystem.get(path.toUri(), conf);
			SequenceFile.Reader reader = new SequenceFile.Reader(fs, path, conf);
			Writable key = (Writable) reader.getKeyClass().newInstance();
			Writable value = (Writable) reader.getValueClass().newInstance();

			while (reader.next(key, value)) {
				Cluster cluster = ((ClusterWritable) value).getValue();
				clusters.add(cluster);
				value = (Writable) reader.getValueClass().newInstance();
			}

		}
		
		process();
		CDbwEvaluator ev = new CDbwEvaluator(new Configuration(), new Path(inputFile)); 
		//ClusterEvaluator ev = new ClusterEvaluator(new Configuration(), new Path(inputFile));
		//System.out.println("Intracluster densitiy: " + ev.intraClusterDensity());
		//System.out.println("Intercluster densitiy: " + ev.interClusterDensity());
		
	}
	
	static void process() {
		DistanceMeasure measure = new CosineDistanceMeasure();
		double max = 0;
		double min = Double.MAX_VALUE;
		double sum = 0;
		int count = 0;

		for (int i = 0; i < clusters.size(); i++) {
			for (int j = i + 1; j < clusters.size(); j++) {
				double d = measure.distance(clusters.get(i).getCenter(),
						clusters.get(j).getCenter());
				min = Math.min(d, min);
				max = Math.max(d, max);
				sum += d;
				count++;
			}
		}
		System.out.println("Maximum Intercluster Distance: " + max);
		System.out.println("Minium Intercluster Distance: " + min);
		System.out.println("Average Intercluster Distance(Scaled): " + (sum / count - min) / (max - min));

	}

}
