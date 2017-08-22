package ex.noah.algorithm.kdtree;

import java.util.*;

public class DataList {

	public DataList(List<Data> dataList) {
		this.dataList = dataList;
	}
	
	
	
	public DataList() {
		this.dataList = new ArrayList<Data>();;
	}



	public void addData(Data data){
		dataList.add(data);
	}

    public void addAllData(List<Data> listToAdd){
        dataList.addAll(listToAdd);
    }

	private List<Data> dataList;

	/**
	 * 按维度排序数据
	 * 
	 * @param dim
	 */
	public void sortByDim(final int dim) {
		Collections.sort(dataList, new DataComparator(dim));
	}

	//节点空间范围
	public HyperRectangle getHyperRectangle(){
		double[] vectorMax = new double[Config.DEFAULT_DIMENSION];
		double[] vectorMin = new double[Config.DEFAULT_DIMENSION];
		for(int i=0;i<Config.DEFAULT_DIMENSION;i++){
			double max=Collections.max(dataList, new DataComparator(i)).getVector()[i];
			double min=Collections.min(dataList, new DataComparator(i)).getVector()[i];
			vectorMax[i]=max;
			vectorMin[i]=min;
		}
		return new HyperRectangle(new Data(vectorMax),new Data(vectorMin));
	}

	public List<Data> getDataList() {
		return dataList;
	}

	public void setDataList(List<Data> dataList) {
		this.dataList = dataList;
	}
	
	class DataComparator implements Comparator<Data> {
		
		private int dim;
		
		public DataComparator(int dim) {
			super();
			this.dim = dim;
		}

		public int compare(Data d1, Data d2) {
			if (d1.getVector()[dim] > d2.getVector()[dim]) {
				return 1;
			} else if (d1.getVector()[dim] < d2.getVector()[dim]) {
				return -1;
			} else {
				return 0;
			}
		}

	}

	public static void main(String[] args) {
		double a[] = { 3.1111, 1.3333 };
		double b[] = { 2.1111, 2.3333 };
		double c[] = { 1.1111, 3.3333 };
		Data d1 = new Data(a);
		Data d2 = new Data(b);
		Data d3 = new Data(c);
		Data d4 = new Data(c);
		Data[] dataAry = { d1, d2, d3, d4 };
		DataList dataSet = new DataList(Arrays.asList(dataAry));
		System.out.println(Arrays.toString(dataSet.getDataList().toArray()));
		dataSet.sortByDim(0);
		System.out.println(Arrays.toString(dataSet.getDataList().toArray()));
		dataSet.sortByDim(1);
		System.out.println(Arrays.toString(dataSet.getDataList().toArray()));
		System.out.println(dataSet.getHyperRectangle());
	}
}
