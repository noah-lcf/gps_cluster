package ex.noah.algorithm.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by noah on 2016/6/20.
 */
public class GeoUtils {


    public static Point wgs84ToBd09API(Point location) {
        int from = 1;
        int to = 5;
        final String URL = "http://api.map.baidu.com/geoconv/v1/?coords=%s,%s&from=%s&to=%s&ak=FE994f0b0f8e441f9cedf4c8052b59db";
        String url = String.format(URL, location.getY(), location.getX(), from, to);
        String result = HttpClientUtil.fetchStringByGet(url);
        // {"status":0,"result":[{"x":114.23075682165,"y":29.579083538934},{"x":114.23075303195,"y":29.579088641852}]}
        Matcher matcher = Pattern.compile("\"x\"\\:([0-9\\.]+),\"y\"\\:([0-9\\.]+)").matcher(result);
        if (matcher.find()) {
            return new Point(Float.valueOf(matcher.group(2)), Float.valueOf(matcher.group(1)));
        }
        return null;
    }

    public static String getAddress(Point location) {
        Point baiduPoint = wgs84ToBd09API(location);
        if (baiduPoint == null) {
            return null;
        }
        String loc_url = String.format("http://api.map.baidu.com/geocoder/v2/?location=%f,%f&output=json&ak=FE994f0b0f8e441f9cedf4c8052b59db", baiduPoint.getX(), baiduPoint.getY());
        String result = HttpClientUtil.fetchStringByGet(loc_url);
        if (StringUtils.isEmpty(result)) {
            return null;
        }
        String addr = null;
        Matcher matcher = Pattern.compile("formatted_address\"\\:\"(.[^\",]*)\"").matcher(result);
        while (matcher.find()) {
            addr = matcher.group(1);
            break;
        }
        return addr;
    }
//
//     getLoc(lat: Double, lng: Double) = {
//        val (lat_bd, lng_bd) = gpsToBaidu(lat, lng)
//        val loc_url = "http://api.map.baidu.com/geocoder/v2/?location=%f,%f&output=json&ak=%s" %(lat_bd, lng_bd, baidu_ak)
//        val res = loc_url httpGet
//        val resMap = JsonUtils.json2Map(res)
//        resMap.get("result").get.asInstanceOf[Map[String, Any]].get("formatted_address").getOrElse("").toString
//    }
}
