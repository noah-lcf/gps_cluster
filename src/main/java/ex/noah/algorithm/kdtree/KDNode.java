package ex.noah.algorithm.kdtree;

/**
 * 树结点结构
 * @author NOAH
 *
 */
public class KDNode {
	//节点数据
	private Data data;
	//该节点的最大区分度方向维
	private int splitDimension;
	//该节点的左右子树和父节点
	private KDNode left, right, parent;
	//数据范围
	private HyperRectangle hr;

	public KDNode(Data data, int splitDimension, HyperRectangle hr) {
		super();
		this.data = data;
		this.splitDimension = splitDimension;
		this.hr = hr;
	}
	
	
	public KDNode(Data data) {
		super();
		this.data = data;
	}



	public double getValueBySplitDim(){
		return getValueByDim(splitDimension);
	}
	
	public double getValueByDim(int dim){
		return data.getVector()[dim];
	}

	public boolean isLeaf() {
		return !hasLeft() && !hasRight();
	}

	public boolean hasLeft() {
		return left != null;
	}

	public boolean hasRight() {
		return right != null;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public KDNode getLeft() {
		return left;
	}

	public void setLeft(KDNode left) {
		this.left = left;
	}

	public KDNode getRight() {
		return right;
	}

	public void setRight(KDNode right) {
		this.right = right;
	}

	public KDNode getParent() {
		return parent;
	}

	public void setParent(KDNode parent) {
		this.parent = parent;
	}

	public HyperRectangle getHr() {
		return hr;
	}

	public void setHr(HyperRectangle hr) {
		this.hr = hr;
	}

	public int getSplitDimension() {
		return splitDimension;
	}

	public void setSplitDimension(int splitDimension) {
		this.splitDimension = splitDimension;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KDNode other = (KDNode) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.getObj().equals(other.data.getObj()))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "KDNode [data=" + data + ", split_dimension=" + splitDimension + "]";
	}

}
