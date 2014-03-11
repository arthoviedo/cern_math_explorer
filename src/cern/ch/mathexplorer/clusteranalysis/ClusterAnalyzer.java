package cern.ch.mathexplorer.clusteranalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.mahout.clustering.Cluster;
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
	static int k=1024;
	public static void main(String args[]) throws Exception {
		
		
		
		computeInterClusterDistance();
		computerIntraClusterDistance();
				
	}
	
	/**
	 * Computer the average distance of the points with the centroid of the cluster it was assigned.
	 * 
	 */
	static void computerIntraClusterDistance() throws Exception{
		String filePath = "/home/cern/Desktop/Thesis/sw/mahout-distribution-0.9_2/results/clustering_kmeans/k"+k+
				"/clusteredPoints/cluster-points.txt";
		String number = "0\\.[0-9]*?";
		Pattern numberPattern = Pattern.compile(number);
		Pattern distancePattern = Pattern.compile("distance: "+number+" ");
		BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
		String line = "";
		double sum = 0.0;
		int count = 0;
		while ((line=br.readLine())!=null) {
			Matcher m = distancePattern.matcher(line);
			
			if(m.find()){
				String matchingPart = m.group();
				Scanner s = new Scanner(matchingPart);
				s.next();
				sum += s.nextDouble();
				count ++;
			}
		}
		System.out.println("Average intracluster distance: " + (sum/count));
	}
	
	static void computeInterClusterDistance() throws Exception{
		
		String inputFile = "/home/cern/Desktop/Thesis/sw/mahout-distribution-0.9_2/results/clustering_kmeans/k"+k
				+ "/clusters-11-final/";
		for( int i = 0; i<k; i++) {
			String ending = "part-0";
			if(i<10){
				ending += "000"+i;
			} else if (i < 100) {
				ending += "00"+i;
			} else if (i < 1000) {
				ending += "0" +i ;
			} else {
				ending += i;
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
