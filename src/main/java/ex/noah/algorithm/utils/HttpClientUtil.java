/**
 * HttpClientUtil.java Copyright 2012 lemon sea, Inc. All Rights Reserved.
 */
package ex.noah.algorithm.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;


class HttpClientUtil {

    private static HttpClient httpClient = null;

    static {
        httpClient = new DefaultHttpClient();
        httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
    }

    /**
     * 通过Get提交数据
     *
     * @param targetUrl
     * @return
     */
    private static HttpEntity fetchContentByGet(String targetUrl) {
        if (StringUtils.isBlank(targetUrl) || !targetUrl.startsWith("http:")) {
            return null;
        }
        HttpGet httpGet = new HttpGet(targetUrl);
        HttpContext context = new BasicHttpContext();
        try {
            HttpResponse remoteResponse = httpClient.execute(httpGet, context);
            if (remoteResponse != null && remoteResponse.getStatusLine().getStatusCode() == 200) {
                return remoteResponse.getEntity();
            }
            assert remoteResponse != null;

        } catch (Exception e) {
            httpGet.abort();
            httpClient = new DefaultHttpClient();
        }
        return null;
    }

    /**
     * 通过Post提交
     *
     * @param targetUrl
     * @param entity    ByteArrayEntity or StringEntity
     * @return
     */
    public static HttpEntity fetchContentByPost(String targetUrl, HttpEntity entity) {
        if (StringUtils.isBlank(targetUrl) || !targetUrl.startsWith("http:")) {
            return null;
        }
        HttpPost httpPost = new HttpPost(targetUrl);
        HttpContext context = new BasicHttpContext();
        try {
            if (entity != null) {
                httpPost.setEntity(entity);
            }
            HttpResponse remoteResponse = httpClient.execute(httpPost, context);
            if (remoteResponse != null && remoteResponse.getStatusLine().getStatusCode() == 200) {
                return remoteResponse.getEntity();
            }
        } catch (Exception e) {
            httpPost.abort();
            httpClient = new DefaultHttpClient();
        }
        return null;
    }

    public static String fetchStringByGet(String url) {
        return fetchStringByGet(url, "utf-8");
    }

    public static String fetchStringByGet(String url, String encoding) {
        HttpEntity entity = fetchContentByGet(url);
        if (entity != null) {
            InputStream input;
            try {
                input = entity.getContent();
                return IOUtils.toString(input, encoding);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static byte[] fetchByteArrayByGet(String url) {
        HttpEntity entity = fetchContentByGet(url);
        if (entity != null) {
            InputStream input;
            try {
                input = entity.getContent();
                return IOUtils.toByteArray(input);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String fetchStringByPost(String url, String data) {
        return fetchStringByPost(url, data, "utf-8");
    }

    public static String fetchStringByPost(String url, String data, String encoding) {
        try {
            HttpEntity dataEntity = data == null ? null : new StringEntity(data);
            HttpEntity entity = fetchContentByPost(url, dataEntity);
            if (entity != null) {
                InputStream input;
                try {
                    input = entity.getContent();
                    return IOUtils.toString(input, encoding);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String fetchStringByPost(String url, byte[] data, String encoding) {
        try {
            HttpEntity dataEntity = data == null ? null : new ByteArrayEntity(data);
            HttpEntity entity = fetchContentByPost(url, dataEntity);
            if (entity != null) {
                InputStream input;
                try {
                    input = entity.getContent();
                    return IOUtils.toString(input, encoding);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] fetchByteArrayByPost(String url, String data) {
        try {
            HttpEntity dataEntity = data == null ? null : new StringEntity(data);
            HttpEntity entity = fetchContentByPost(url, dataEntity);
            if (entity != null) {
                InputStream input;
                try {
                    input = entity.getContent();
                    return IOUtils.toByteArray(input);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] fetchByteArrayByPost(String url) {
        return fetchByteArrayByPost(url, (String) null);
    }

    public static byte[] fetchByteArrayByPost(String url, byte[] data) {
        try {
            HttpEntity dataEntity = data == null ? null : new ByteArrayEntity(data);
            HttpEntity entity = fetchContentByPost(url, dataEntity);
            if (entity != null) {
                InputStream input;
                try {
                    input = entity.getContent();
                    return IOUtils.toByteArray(input);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws InterruptedException {
        String url;
        String str;
        int index = 0;
        while (true) {
            index++;
            url = "http://192.168.1.40:18001/?objId=" + index % 10;
            str = HttpClientUtil.fetchStringByGet(url);
            if (str == null) {
                continue;
            }
            System.out.println(url + "	" + str.trim());
            Thread.sleep(300);
        }
    }
}
