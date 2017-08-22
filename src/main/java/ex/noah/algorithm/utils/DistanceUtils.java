package ex.noah.algorithm.utils;

import ex.noah.algorithm.kdtree.KDNode;

public class DistanceUtils {

	/**
	 * 计算两点间欧氏距离 
	 *  
	 * @param p1
	 * @param p2
	 * @param deminision
	 * @return
	 */
	public static double getDistance(KDNode node1, KDNode node2, int dimension) {
		double temp = 0;
		double[] p1 = node1.getData().getVector();
		double[] p2 = node2.getData().getVector();
		for (int i = 0; i < dimension; i++) {
			temp += (p1[i] - p2[i]) * (p1[i] - p2[i]);
		}
		return Math.sqrt(temp);
	}

	/**
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static double getDistance(KDNode node1, KDNode node2) {
		return getEarthDistance(node1, node2);
		//		return getDistance(node1, node2, Config.DEFAULT_DIMENSION);
	}

	public static double getDirectDistance(KDNode node1, KDNode node2, int dim) {
		if (dim == 0) {
			return getDistance(node1.getValueByDim(dim), 0, node2.getValueByDim(dim), 0);
		} else if (dim == 1) {
			return getDistance(0, node1.getValueByDim(dim), 0, node2.getValueByDim(dim));
		} else {
			throw new IllegalArgumentException("unsupport dim:" + dim);
		}
		//		return getDistance(node1, node2, Config.DEFAULT_DIMENSION);
	}

	public static double getEarthDistance(KDNode node1, KDNode node2) {
		return getDistance(node1.getData().getVector()[0], node1.getData().getVector()[1],
				node2.getData().getVector()[0], node2.getData().getVector()[1]);
	}

	private static double EARTH_RADIUS = 6378.138;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 计算地球两点距离 
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return
	 */
	public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10;
		return s;
	}



	public static void main(String[] args) {
		System.out.println(getDistance(34.816254, 113.65225, 34.811399, 113.6549));
	}
}
