package edu.carleton.comp4601.data.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import Jama.Matrix;
import edu.carleton.comp4601.users.UserProfile;

public class Kmeans {

	private int no_users;
	private UserProfile[] users;
	private int no_features;
	private boolean changed;
	private int no_clusters;
	private static final String CLUSTER = "cluster";
	HashMap<String, ArrayList<UserProfile>>  clusters;
	ArrayList<double[]> centroids;
	boolean firstIteration;
	/*
	 * Constructor that reads the data in from a file.
	 * You must specify the number of clusters.
	 */
	public Kmeans(int noClusters, File file) throws FileNotFoundException {
		changed = true;
		Scanner s = new Scanner(file);
		no_users = s.nextInt();
		no_features = s.nextInt();
		users = new UserProfile[no_users];
		this.no_clusters = noClusters;
		clusters = new HashMap<String, ArrayList<UserProfile>>();
		for (int i = 0; i < no_users; i++) {
			String name = s.next();
			//users[i] = new UserProfile(name, no_features, noClusters);
			
		}
		s.close();
		
		for (int i = 0; i < users.length; i++) {
			System.out.println(users[i].toString());
		}
	}
	public Kmeans(int noClusters, ArrayList<UserProfile> userslist) {
		changed = true;
		no_users = userslist.size();
		users = new UserProfile[no_users];
		this.no_clusters = noClusters;
		clusters = new HashMap<String, ArrayList<UserProfile>>();
		for (int i = 0; i < no_users; i++) {
			users[i] = userslist.get(i);
			
		}
	}

	/*
	 * This is where your implementation goes
	 */
	public HashMap<String, ArrayList<UserProfile>> algorithm() {
		for (int i = 0; i < no_users; i++) {
			//System.out.println(users[i].toString());
		}
		ArrayList<UserProfile>  usersCopy = new ArrayList<UserProfile>(Arrays.asList(users));
		centroids = new ArrayList<double[]>();
		
		for (int i = 0; i < no_clusters; i++){
			Random rand = new Random();
			int  n = rand.nextInt(usersCopy.size());
			centroids.add(usersCopy.get(n).getFeatures());
			usersCopy.remove(n);
			clusters.put(CLUSTER + i,  new ArrayList<UserProfile>());
		}
		int iterations = 0;
		//Initial clusters 
		
		while (changed) {
			
			for (UserProfile user : users) {
				double minDistance = 30000;
				int cIndex = 0;
				for (int i = 0; i < centroids.size(); i++) {
					if (distance(user.getFeatures(), centroids.get(i)) < minDistance) {
						minDistance = distance(user.getFeatures(), centroids.get(i));
						cIndex = i;
					}
				}
				for (int i = 0; i < clusters.get(CLUSTER + cIndex).size(); i++) {
					if (clusters.get(CLUSTER + cIndex).get(i).getUsername() == user.getUsername()) {
						clusters.get(CLUSTER + cIndex).remove(i);
					}
				}
				user.setCluster(cIndex);
				clusters.get(CLUSTER+cIndex).add(user);
			}
			ArrayList<double[]> oldCentroids = centroids;
			centroids = new ArrayList<double[]>();
			for (int i = 0; i < no_clusters; i++) {
				findNewCentroid(i);
			}

			boolean converged = true;
			for (int i = 0; i < centroids.size(); i++) {
				double diff = distance(centroids.get(i), oldCentroids.get(i));
					if (!(diff < 0.00001) || !(diff > -0.0001)) {
						converged = false;
				}
			}
			iterations++;
			changed=!converged;
		}
		clusters = new HashMap<String, ArrayList<UserProfile>>();
		for (int i = 0; i < no_clusters; i++){
			clusters.put(CLUSTER + i,  new ArrayList<UserProfile>());
		}
		for (UserProfile user : users) {
			clusters.get(CLUSTER + user.getCluster()).add(user);
		}
		System.out.println("Converged after: " + iterations + " iterations");
		
		return clusters;
	}
	private ArrayList<UserProfile> getUsersInCluster(int cluster) {
		ArrayList<UserProfile> usersInCluster = new ArrayList<UserProfile>();
		
		for (UserProfile user : users) {
			if (user.getCluster() == cluster) {
				usersInCluster.add(user);
			}
		}
		return usersInCluster;
	}
	private double[] findNewCentroid(int cluster) {
		ArrayList<UserProfile> usersInCluster = getUsersInCluster(cluster);
		double[] features = null;
		int featuresize =  usersInCluster.get(0).getFeatures().length;
		Matrix allFeatures = new Matrix(usersInCluster.size(), featuresize);
		for (int i = 0; i < usersInCluster.size(); i++) {
			features = usersInCluster.get(i).getFeatures();
			for (int j = 0; j < features.length; j++) {
				allFeatures.getArray()[i][j] = features[j];
			}
		}
		//allFeatures.print(allFeatures.getRowDimension(), allFeatures.getRowDimension());
		double[] sums = new double[featuresize];
		for (int i = 0; i < allFeatures.getColumnDimension(); i++) {
			double accum = 0;
			for (int j = 0; j < allFeatures.getRowDimension(); j++) {
				accum += allFeatures.getArray()[j][i];
			}
			sums[i] = accum;
		}
		//Divide each of the sums by the length
		double[] averages = new double[featuresize];
		for (int i = 0; i < sums.length;i++) {
			averages[i] = sums[i]/allFeatures.getRowDimension();
		}
		centroids.add(averages);
		return  averages;
	}
	/* 
	 * Computes distance between two users
	 * Could implement this on User too.
	 */
	private double distance(double[] a, double[] b) {
		double rtn = 0.0;
		for (int i = 0; i < a.length; i++) {
			rtn += (a[i] - b[i])
					* (a[i] - b[i]);
		}
		return Math.sqrt(rtn);
	}
	private double distance(UserProfile a, UserProfile b) {
		double rtn = 0.0;
		// Assumes a and b have same number of features
		for (int i = 0; i < a.getFeatures().length; i++) {
			rtn += (a.getFeatures()[i] - b.getFeatures()[i])
					* (a.getFeatures()[i] - b.getFeatures()[i]);
		}
		return Math.sqrt(rtn);
	}
	private static double distance2(UserProfile a, UserProfile b) {
		double rtn = 0.0;
		// Assumes a and b have same number of features
		for (int i = 0; i < a.getFeatures().length; i++) {
			rtn += (a.getFeatures()[i] - b.getFeatures()[i])
					* (a.getFeatures()[i] - b.getFeatures()[i]);
		}
		return Math.sqrt(rtn);
	}
	// Private class for representing user
	
	//Output total distance within all clusters 
	//for the first one it seems to be around 5/6
	//for the second time it is around 4/5
	public static void main(String[] args) {
		try {
			String fileName = "KNN-1.txt";
			Kmeans knn = new Kmeans(4, new File(fileName));
			HashMap<String, ArrayList<UserProfile>> clusters = knn.algorithm();

			for (String key : clusters.keySet()){
				System.out.println(key + ": ");
				System.out.println("Size: " + clusters.get(key).size());
				for (UserProfile user : clusters.get(key)) {
					System.out.println(user.getUsername());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
