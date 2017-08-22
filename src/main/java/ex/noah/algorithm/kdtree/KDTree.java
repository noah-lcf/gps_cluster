package ex.noah.algorithm.kdtree;

import ex.noah.algorithm.utils.DistanceUtils;

import java.util.*;
import java.util.Map.Entry;

/**
 * KD树 
 * 
 * N个节点的K维k-d树搜索过程时间复杂度为：tworst=O（kN1-1/k）
 * 
 * @author NOAH
 *
 */
public class KDTree {

	private KDNode root;

	public KDTree(DataList dataList) {
		this.root = createTree(dataList);
	}

	public KDTree(KDNode root) {
		this.root = root;
	}

	/**
	 * 根据数据集合创建KD树
	 * @param dataList
	 * @return
	 */
	public KDNode createTree(DataList dataList) {
		if (dataList == null || dataList.getDataList().isEmpty()) {
			return null;
		}
		List<Data> allData = dataList.getDataList();
		//求出哪个维度下方差最大，以该维度划分
		double avg = 0, var = 0, var_max = -1;
		int split_dim = -1;
		for (int dimIndex = 0; dimIndex < Config.DEFAULT_DIMENSION; dimIndex++) {
			//求某一维度总和
			for (int dataIndex = 0; dataIndex < allData.size(); dataIndex++) {
				avg += allData.get(dataIndex).getVector()[dimIndex];
			}
			avg = avg / allData.size();
			//求方差
			for (int dataIndex = 0; dataIndex < allData.size(); dataIndex++) {
				var = Math.pow(allData.get(dataIndex).getVector()[dimIndex] - avg, 2);
			}
			var = var / allData.size();
			if (var > var_max) {
				var_max = var;
				split_dim = dimIndex;
			}
		}
		HyperRectangle hr = dataList.getHyperRectangle();
		dataList.sortByDim(split_dim);
		int mid = allData.size() / 2;
		Data midData = allData.get(mid);
		List<Data> allLeftData = allData.subList(0, mid);
		List<Data> allRightData = allData.subList(mid + 1, allData.size());
		KDNode node = new KDNode(midData, split_dim, hr);
		KDNode nodeLeft = createTree(new DataList(allLeftData));
		if (nodeLeft != null) {
			nodeLeft.setParent(node);
		}
		node.setLeft(nodeLeft);
		KDNode nodeRight = createTree(new DataList(allRightData));
		if (nodeRight != null) {
			nodeRight.setParent(node);
		}
		node.setRight(nodeRight);
		return node;
	}

	/**
	 * 查找距离最近的节点
	 * @param node
	 * @return  
	 */
	public Object[] findNearest(KDNode targetNode) {
		//堆栈用于保存搜索路径
		Stack<KDNode> pathStack = new Stack<KDNode>();
		KDNode curNode = root, nearest;
		double maxDistance;
		//二叉查找
		while (curNode != null) {
			pathStack.add(curNode);
			int splitDim = curNode.getSplitDimension();
			if (targetNode.getValueByDim(splitDim) < curNode.getValueByDim(splitDim)) {
				curNode = curNode.getLeft();
			} else {
				curNode = curNode.getRight();
			}
		}
		//从可能最接近的点搜索最近距离的点
		nearest = pathStack.pop();
		maxDistance = DistanceUtils.getDistance(targetNode, nearest);
		double disNearest = 0, disBack = 0;
		while (!pathStack.isEmpty()) {
			KDNode backNode = pathStack.pop();
			//叶结点直接计算距离
			if (backNode.isLeaf()) {
				disNearest = DistanceUtils.getDistance(nearest, targetNode);
				disBack = DistanceUtils.getDistance(backNode, targetNode);
				if (disNearest > disBack) {
					nearest = backNode;
					maxDistance = disBack;
				}
			} else {
				int splitDim = backNode.getSplitDimension();
				//target为圆心，maxDistance为半径划圆
				if (DistanceUtils.getDirectDistance(backNode , targetNode,splitDim) < maxDistance) {
					disNearest = DistanceUtils.getDistance(nearest, targetNode);
					disBack = DistanceUtils.getDistance(backNode, targetNode);
					if (disNearest > disBack) {
						nearest = backNode;
						maxDistance = disBack;
					}
					if (targetNode.getValueByDim(splitDim) <= backNode.getValueByDim(splitDim)) {
						curNode = backNode.getRight();
					} else {
						curNode = backNode.getLeft();
					}
					if (curNode != null) {
						pathStack.add(curNode);
					}
				}
			}
		}
		Object[] obj = { nearest, maxDistance };
		return obj;
	}

	public Object[] findNearest(Data data) {
		return findNearest(new KDNode(data));
	}

	public static Map<KDNode, Double> findByRange(KDNode root, Data data, double range) {
		Map<KDNode, Double> map = new LinkedHashMap<KDNode, Double>();
		findByRange(root, new KDNode(data), range, map);
		sortMapByValue(map);
		return map;
	}

	/**
	* 对Map的值排序
	*/
	private static void sortMapByValue(Map<KDNode, Double> ret) {
		List<Entry<KDNode, Double>> infoIds = new ArrayList<Entry<KDNode, Double>>(ret.entrySet());
		Collections.sort(infoIds, (o1, o2) -> (int) (o1.getValue() - o2.getValue()));
		ret.clear();
		for (int i = 0; i < infoIds.size(); i++) {
			ret.put(infoIds.get(i).getKey(), infoIds.get(i).getValue());
		}
		return;
	}

	/**
	 * 按范围查找附近的节点
	 * @param node
	 * @param Range
	 * @return
	 */
	public static int findByRange(KDNode root, KDNode targetNode, double range, Map<KDNode, Double> res) {
		int count = 0, ret = 0;
		if (root == null) {
			return count;
		}
		double dis = DistanceUtils.getDistance(targetNode, root), directDis;
		if (dis < range) {
			res.put(root, dis);
			count++;
		}
		directDis = targetNode.getValueByDim(root.getSplitDimension()) - root.getValueBySplitDim();
		//左（右）子树递归查找
		if (directDis <= 0) {
			ret = findByRange(root.getLeft(), targetNode, range, res);
		} else {
			ret = findByRange(root.getRight(), targetNode, range, res);
		}
		//另一侧可能也存在 前面查询为结果为零且存在子树下不需要再  在一侧为空的情况下仍然要遍历另外一侧的子树
		
		if (ret>0||DistanceUtils.getDirectDistance(root, targetNode, root.getSplitDimension()) < range) {
			count += ret;
			if (directDis <= 0) {
				ret = findByRange(root.getRight(), targetNode, range, res);
			} 
			if(directDis > 0){
				ret = findByRange(root.getLeft(), targetNode, range, res);
			}
		}
		count += ret;
		return count;
	}

	/**
	 * 添加节点,会改会树的结构(一次性生成时的结构和一个个添加的结构可能不同，但总性质不变）
	 * 
	 * tavg=O(log2N);
	 * 
	 * @param node
	 */
	public void addNode(KDNode targetNode) {
		if (root == null) {
			this.root = targetNode;
			return;
		}
		KDNode curNode = root;
		while (true) {
			if (curNode.getValueBySplitDim() > targetNode.getValueByDim(curNode.getSplitDimension())) {
				if (curNode.getLeft() == null) {
					curNode.setLeft(targetNode);
					targetNode.setParent(curNode);
					return;
				} else {
					curNode = curNode.getLeft();
				}
			} else {
				if (curNode.getRight() == null) {
					curNode.setRight(targetNode);
					targetNode.setParent(curNode);
					return;
				} else {
					curNode = curNode.getRight();
				}
			}
		}

	}

	/**
	 * 获取节点下的数据集合
	 * @param node
	 * @param list
	 */
	private static void getNodeDataList(KDNode node, DataList list) {
		if (node.getLeft() != null) {
			list.addData(node.getLeft().getData());
			getNodeDataList(node.getLeft(), list);
		}
		if (node.getRight() != null) {
			list.addData(node.getRight().getData());
			getNodeDataList(node.getRight(), list);
		}
	}

	/**
	 * 移除节点
	 * 
	 * @param node
	 */
	public void removeNode(KDNode node) {
		KDNode parent = node.getParent();
		//根结点
		if (parent == null) {
			if (root.isLeaf()) {
				this.root = null;
				return;
			} else {
				DataList dataList = new DataList();
				getNodeDataList(node, dataList);
				this.root = createTree(dataList);
				return;
			}
		}
		//子结点
		if (node.equals(parent.getLeft())) {
			if (node.isLeaf()) {
				node.getParent().setLeft(null);
			} else {
				DataList dataList = new DataList();
				getNodeDataList(node, dataList);
				node.getParent().setLeft(createTree(dataList));
			}
		} else {
			if (node.isLeaf()) {
				node.getParent().setRight(null);
			} else {
				DataList dataList = new DataList();
				getNodeDataList(node, dataList);
				node.getParent().setRight(createTree(dataList));
			}
		}

	}

	public KDNode getRoot() {
		return root;
	}

	public String toString() {
		return TreePrinter.getString(this);
	}

	/**
	 * 按ID查找结点
	 * 
	 * @param node
	 * @param objId
	 * @return
	 */
	public static KDNode getNode(KDNode node, String objId) {
		if (node == null || node.getData().getObj().equals(objId))
			return node;
		else {
			if (node.hasLeft()) {
				if (objId.equals(node.getLeft().getData().getObj())) {
					return node.getLeft();
				} else {
					KDNode nodeRes = getNode(node.getLeft(), objId);
					if (nodeRes != null) {
						return nodeRes;
					}
				}
			}
			if (node.hasRight()) {
				if (objId.equals(node.getRight().getData().getObj())) {
					return node.getRight();
				} else {
					KDNode nodeRes = getNode(node.getRight(), objId);
					if (nodeRes != null) {
						return nodeRes;
					}
				}
			}
			return null;
		}
	}

	protected static class TreePrinter {

		public static String getString(KDTree tree) {
			if (tree.getRoot() == null)
				return "Tree has no nodes.";
			return getString(tree.getRoot(), "", true);
		}

		private static String getString(KDNode node, String prefix, boolean isTail) {
			StringBuilder builder = new StringBuilder();
			if (node.getParent() != null) {
				String side = "left";
				if (node.getParent().getRight() != null && node.equals((node.getParent().getRight())))
					side = "right";
				builder.append(prefix + (isTail ? "└── " : "├── ") + "[" + side + "] " + node + "\n");
			} else {
				builder.append(prefix + (isTail ? "└── " : "├── ") + node + "\n");
			}
			List<KDNode> children = null;
			if (node.getLeft() != null || node.getRight() != null) {
				children = new ArrayList<KDNode>(2);
				if (node.getLeft() != null)
					children.add(node.getLeft());
				if (node.getRight() != null)
					children.add(node.getRight());
			}
			if (children != null) {
				for (int i = 0; i < children.size() - 1; i++) {
					builder.append(getString(children.get(i), prefix + (isTail ? "    " : "│   "), false));
				}
				if (children.size() >= 1) {
					builder.append(getString(children.get(children.size() - 1), prefix + (isTail ? "    " : "│   "),
							true));
				}
			}

			return builder.toString();
		}
	}


}
