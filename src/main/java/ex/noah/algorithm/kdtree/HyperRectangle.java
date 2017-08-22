package ex.noah.algorithm.kdtree;

/**
 * 数据范围的超矩形结构
 * 
 * @author NOAH
 *
 */
public class HyperRectangle {
	//统计数据集中所有数据向量每个维度上最大值组成的一个数据向量
	private Data max;
	//统计数据集中所有数据向量每个维度上最小值组成的一个数据向量
	private Data min;

	public HyperRectangle(Data max, Data min) {
		this.max = max;
		this.min = min;
	}

	public Data getMin() {
		return min;
	}

	public void setMin(Data min) {
		this.min = min;
	}

	public Data getMax() {
		return max;
	}

	public void setMax(Data max) {
		this.max = max;
	}

	@Override
	public String toString() {
		return "HyperRectangle [min=" + min + ", max=" + max + "]";
	}

}
