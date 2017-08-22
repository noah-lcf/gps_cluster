package ex.noah.algorithm.main;

import ex.noah.algorithm.kdtree.*;
import ex.noah.algorithm.utils.Point;
import ex.noah.algorithm.utils.DistanceUtils;
import ex.noah.algorithm.utils.GeoUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by noah on 2016/5/19.
 */
public class KDMain {


    /**
     * 从文件中加载数据
     *
     * @param fName 文件名
     * @return
     * @throws IOException
     */
    static DataList loadData(String fName) throws IOException {
        InputStream is = KDMain.class.getClassLoader().getResourceAsStream(fName);
        List<String> lines = IOUtils.readLines(is);
        DataList dataList = new DataList();
        List<Data> dataObjs = lines.subList(1, lines.size() - 1).stream().map(line -> new DataInfo(line)).map(dataInfo -> {
            Data d = new Data(dataInfo.getLatitude(), dataInfo.getLongitude());
            d.setObj(dataInfo);
            return d;
        }).collect(Collectors.toList());
        dataList.addAllData(dataObjs);
        IOUtils.closeQuietly(is);
        return dataList;
    }


    /**
     * 分组存放
     *
     * @param groupList
     * @param d
     * @param pointSet
     */
    synchronized static void putToSet(List<Set<DataInfo>> groupList, DataInfo d, Set<DataInfo> pointSet) {
        boolean exists = false;
        for (Set groupSet : groupList) {
            if (groupSet.contains(d)) {
                groupSet.addAll(pointSet);
                exists = true;
                break;
            }
        }
        if (!exists) {
            groupList.add(pointSet);
        }
    }

    /**
     * 得到中心点和周围的点信息
     *
     * @param groupList
     * @return
     */
    static Map<Point, List<DataInfo>> getClusterInfo_avg(List<Set<DataInfo>> groupList) {
        Map<Point, List<DataInfo>> clusterMap = new HashMap<>();
        groupList.forEach(l -> {
            //直接取均值作为质心,免去聚类
            int len = l.size();
            float avgLat = l.stream().map(d -> d.getLatitude()).reduce((x, y) -> x + y).get() / len;
            float avgLng = l.stream().map(d -> d.getLongitude()).reduce((x, y) -> x + y).get() / len;
            Point centroidPoint = new Point(avgLat, avgLng);
            List<DataInfo> dataList = new ArrayList();
            for (DataInfo dataInfo : l) {
                Double dis = DistanceUtils.getDistance(avgLat, avgLng, dataInfo.getLatitude(), dataInfo.getLongitude());
                if (dis > cluster_dis) {
                    System.err.println("dis bigger than 100,delete it");
                    continue;
                }
                dataInfo.setDis(dis);
                dataList.add(dataInfo);
//                System.out.println(dataInfo + ",to centroid dis:" + dis);
            }
            if (dataList.size() > 0) {
                clusterMap.put(centroidPoint, dataList);
            }
        });
        return clusterMap;
    }


    /**
     * kd树中查询满足条件的点
     * @param dataList
     * @param root
     * @param mindis
     * @return
     */
    static List<Set<DataInfo>> seachTree(List<Data> dataList, KDNode root, int mindis) {
        List<Set<DataInfo>> groupList = new LinkedList<>();
        dataList.stream().parallel().forEach(d -> {
            Map<KDNode, Double> pointMap = KDTree.findByRange(root, d, mindis);
            if (pointMap.size() >= 2) {
                Set<DataInfo> dataInfoSet = pointMap.keySet().stream().map(node -> (DataInfo) node.getData().getObj()).collect(Collectors.toSet());
                putToSet(groupList, (DataInfo) d.getObj(), dataInfoSet);
            }
        });
        return groupList;
    }

    static Integer cluster_dis = Integer.valueOf(System.getProperty("cluster_dis", "100"));

    public static void main(String[] args) throws Exception {
        System.out.println("cluster_dis:" + cluster_dis);
        Long start = System.currentTimeMillis();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        DataList dataList = loadData("gps_data.csv");
        stopWatch.stop();
        System.out.println("load data end,len:" + dataList.getDataList().size() + ",cost:" + stopWatch);
        stopWatch.reset();
        stopWatch.start();
        //build kdtree数据结构
        KDTree tree = new KDTree(dataList);
        stopWatch.stop();
        System.out.println("build tree end,cost:" + stopWatch);
//        System.out.println(tree.toString());
        stopWatch.reset();
        stopWatch.start();
        //所有点并行从kdtree中查询范围100内的点,分组存放
        List<Set<DataInfo>> groupList = seachTree(dataList.getDataList(), tree.getRoot(), cluster_dis);
        stopWatch.stop();
        System.out.println("find by range all end,cost:" + stopWatch);
        stopWatch.reset();
        stopWatch.start();

        Map<Point, List<DataInfo>> clusterMap = getClusterInfo_avg(groupList);
        //按点数排序
        List<Map.Entry<Point, List<DataInfo>>> list = new ArrayList<>(clusterMap.entrySet());
        Collections.sort(list, (o1, o2) -> Integer.valueOf(o2.getValue().size()).compareTo(o1.getValue().size()));

        for (Map.Entry<Point, List<DataInfo>> entry : list) {
            Point k = entry.getKey();
            List<DataInfo> v = entry.getValue();
            System.out.println("----centroid:(" + k.getX() + "," + k.getY() + " ),count:" + v.size());
            if (v.size() >= 3) {
                //fixme 慢!
                String address = GeoUtils.getAddress(k);
                System.out.println("address:" + ((address == null) ? "地址解析失败" : address));
            }
            v.forEach(x -> System.out.println(x + ",to centroid dis:" + x.getDis()));
        }


//        printClusterInfo_kmean(groupList);
        System.out.println("find centroids  end,cost:" + stopWatch);
        Long end = System.currentTimeMillis();
        System.out.println("total cost:" + (end - start));

    }


    protected static class DataInfo {
        private float longitude;
        private float latitude;
        private String objId;
        private String time;
        private Double dis;

        public DataInfo(String line) {
            String[] props = line.split(",");
            this.longitude = Float.valueOf(props[2]);
            this.latitude = Float.valueOf(props[1]);
            this.objId = props[0];
            this.time = props[3];
        }

        public DataInfo(String objId, float latitude, float longitude, String time) {
            this.longitude = longitude;
            this.latitude = latitude;
            this.objId = objId;
            this.time = time;
        }


        public Double getDis() {
            return dis;
        }

        public void setDis(Double dis) {
            this.dis = dis;
        }

        public float getLongitude() {
            return longitude;
        }

        public void setLongitude(float longitude) {
            this.longitude = longitude;
        }

        public float getLatitude() {
            return latitude;
        }


        public void setLatitude(float latitude) {
            this.latitude = latitude;
        }

        public String getObjId() {
            return objId;
        }

        public void setObjId(String objId) {
            this.objId = objId;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DataInfo dataInfo = (DataInfo) o;

            if (Float.compare(dataInfo.longitude, longitude) != 0) return false;
            if (Float.compare(dataInfo.latitude, latitude) != 0) return false;
            if (objId != null ? !objId.equals(dataInfo.objId) : dataInfo.objId != null) return false;
            return time != null ? time.equals(dataInfo.time) : dataInfo.time == null;

        }

        @Override
        public int hashCode() {
            int result = (longitude != +0.0f ? Float.floatToIntBits(longitude) : 0);
            result = 31 * result + (latitude != +0.0f ? Float.floatToIntBits(latitude) : 0);
            result = 31 * result + (objId != null ? objId.hashCode() : 0);
            result = 31 * result + (time != null ? time.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "DataInfo{" +
                    "objId='" + objId + '\'' +
                    ", latitude=" + latitude +
                    ",longitude=" + longitude +
                    ", time='" + time + '\'' +
                    '}';
        }
    }


}
