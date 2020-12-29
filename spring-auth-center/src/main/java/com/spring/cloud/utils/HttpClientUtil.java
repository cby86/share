package com.spring.cloud.utils;

import com.alibaba.fastjson.JSONObject;
import com.spring.cloud.entity.User;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/29.
 */
public class HttpClientUtil {

    private static final CloseableHttpClient httpClient;
    public static final String CHARSET = "UTF-8";

    static {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(100000).setSocketTimeout(80000).build();
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }


    /**
     * HTTP Get 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param params  请求的参数
     * @param charset 编码格式
     * @return 页面内容
     */
    public static String doGet(String url, List<BasicNameValuePair> params, String charset,String referUrl) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                for (BasicNameValuePair chb : params) {
                    if (StringUtils.hasText(chb.getName())) {
                        pairs.add(new BasicNameValuePair(chb.getName(), StringUtils.isEmpty(chb.getValue()) ? " " : chb.getValue()));
                    }
                }
                if (!pairs.isEmpty()) {
                    url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
                }
            }
            HttpGet httpGet = new HttpGet(url);
            addHeader(referUrl,httpGet);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpGet.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * HTTP Post 获取内容
     *
     * @param url     请求的url地址 ?之前的地址
     * @param params  请求的参数
     * @param charset 编码格式
     * @return 页面内容
     */
    public static String doPost(String url, List<BasicNameValuePair> params, String charset,String referUrl) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        try {
            List<NameValuePair> pairs = new ArrayList<>();
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<NameValuePair>();
                for (BasicNameValuePair chb : params) {
                    if (StringUtils.hasText(chb.getName()) && StringUtils.hasText(chb.getValue())) {
                        pairs.add(new BasicNameValuePair(chb.getName(), chb.getValue()));
                    }
                }
            }

            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
            httpPost.addHeader("Accept-Encoding", "gzip, deflate");
            httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
            httpPost.addHeader("Connection", "keep-alive");
//            httpPost.addHeader("Content-Length", "57");
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            httpPost.addHeader("Host", "182.150.21.216:8006");
            httpPost.addHeader("Origin", "http://182.150.21.216:8006");
            if (!StringUtils.isEmpty(referUrl)) {
                httpPost.addHeader("Referer", referUrl);
            }else {
                httpPost.addHeader("Referer", "http://182.150.21.216:8006/CYPublic/Weixin/Page/WeixinCredentialSearch.html?openid=olZW70oSIohPpdWUcCuBzeuvNfcc&timespan=30408&code=EE846445F264858B21E728A515537FDB");
            }
            httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
            httpPost.addHeader("X-Requested-With", "XMLHttpRequest");
            if (!pairs.isEmpty()) {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));
            }
            CloseableHttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void addHeader(String referUrl, HttpRequest httpGet) {
        httpGet.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        httpGet.addHeader("Accept-Encoding", "gzip, deflate");
        httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httpGet.addHeader("Connection", "keep-alive");
//            httpPost.addHeader("Content-Length", "57");
//            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpGet.addHeader("Host", "182.150.21.216:8006");
        httpGet.addHeader("Origin", "http://182.150.21.216:8006");
        if (!StringUtils.isEmpty(referUrl)) {
            httpGet.addHeader("Referer", referUrl);
        }else {
            http://cyweixin.dlysfw.com:9081/?openid=olZW70oSIohPpdWUcCuBzeuvNfcc&timespan=17605&code=2E0D34437BE9D9118E8C9F703366CB2B
            httpGet.addHeader("Referer", "http://182.150.21.216:8006/?openid=olZ3123Epffdfsdfsdfcfdsfsc&timespan=17605&code=2E0D34437BE9D9118E8C9F703366CB2B");
        }
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
    }

    final static String queryDriverUrl = "http://182.150.21.216:8006//Yunzheng/api/YzData/MyCertCard";

    public static String queryDriver(String cardNumber,String referUrl) {
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("token", "WKCY200421"));
        params.add(new BasicNameValuePair("idnumber", cardNumber));
        return HttpClientUtil.doGet(queryDriverUrl, params, HttpClientUtil.CHARSET,referUrl);
    }

    final static String queryDriverDetailsUrl = "http://182.150.21.216:8006/CYPublic/SysHandler.ashx";

    public static String queryDriverDetails(String cardNumber,String referUrl) {
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("cmd", "CredentialApplyQuery"));
        params.add(new BasicNameValuePair("inparams[sfzh]", cardNumber));
        return HttpClientUtil.doPost(queryDriverDetailsUrl, params, HttpClientUtil.CHARSET,referUrl);
    }

    final static String queryCarUrl = "http://182.150.21.216:8006/CYPublic/SysHandler.ashx";

    public static String queryCar(String cardNumber,String referUrl) {
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("cmd", "WycAduitStatusQuery"));
        params.add(new BasicNameValuePair("inparams[clpzh]", cardNumber));
        return HttpClientUtil.doPost(queryCarUrl, params, HttpClientUtil.CHARSET,referUrl);
    }

    public static void main(String[] args) {
        String s = HttpClientUtil.queryDriverDetails("5139011985120345171",null);
        System.out.println(s);
//        JSONObject jsonObject = JSONObject.parseObject("{\"returncode\":0,\"errmsg\":null,\"resultlist\":{\"result\":\"成功\",\"reccount\":1},\"datalist\":{\"TB0\":[{\"ssdq\":\"51010000\",\"fzjg\":\"成都市              \",\"xm\":\"陈帮元\",\"sfzh\":\"513901198512034517\",\"cyzgzh\":\"513901198512034517\",\"cylb\":\"网约车驾驶员\",\"cylbfullName\":\"网络预约出租汽车驾驶员\",\"clrq\":\"2018-12-10\",\"beginTime\":\"2018-12-10\",\"endTime\":\"2024-12-10\"}]}}");
//        System.out.println(jsonObject);
    }
}
