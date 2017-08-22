package ex.noah.algorithm.kdtree;

import java.util.Arrays;

/**
 * 节点数据封装类
 * 
 * @author NOAH
 *
 */
public class Data {

	


	public Data(double...args) {
		this.vector = args;
	}

	//节点数据向量
	private double[] vector = new double[Config.DEFAULT_DIMENSION];

	//附在位置数据节点上的实体对象，如汽车，POI信息,通常只存储对象的ID值
	private Object obj;

	public double[] getVector() {
		return vector;
	}

	public void setVector(double[] vector) {
		this.vector = vector;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	@Override
	public String toString() {
		return "Data [ obj=" + obj + ",vector=" + Arrays.toString(vector) +"]";
	}

}
