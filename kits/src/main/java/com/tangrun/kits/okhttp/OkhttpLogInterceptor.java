package com.tangrun.kits.okhttp;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import okhttp3.*;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;

import java.io.IOException;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author RainTang
 * @description:
 * @date :2020/12/24 11:10
 */
public class OkhttpLogInterceptor implements Interceptor {
    private static final String TAG = "OkHttpLog";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        int tag = request.hashCode();
        logRequest(tag, request);
        long startTime = SystemClock.elapsedRealtime();
        Response response =null;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            this.log(String.format("------------end " + tag + " 请求异常%s----------------------------", e.getMessage()));
            throw e;
        }
        if (response !=null){
            try {
                this.logResponse(tag, response);
                this.log(String.format("------------end " + tag + " 耗时%dms----------------------------", SystemClock.elapsedRealtime() - startTime));
            }catch (Exception e){
                this.log(String.format("------------end " + tag + " 打印异常%s----------------------------", e.getMessage()));
            }
        }
        return response;
    }


    public synchronized void log(String message) {
        int len = 2048;
        if (message.length() < len)
            Log.d(TAG, message);
        else {
            String[] strings = spiltForLength(message, len);
            for (String string : strings) {
                Log.d(TAG, string);
            }
        }
    }
    private String[] spiltForLength(String content, int length) {
        int len = content.length();
        int size = len / length + (len % length > 0 ? 1 : 0);
        String[] strings = new String[size];
        int begin =0;
        for (int i = 0; i < size; i++) {
            strings[i] = content.substring(begin, i == size -1? len:begin+length);
            begin += length;
        }
        return strings;
    }

    private String getTagWidthFormat(String s) {
        int len = s.length();
        if (len < 18) {
            StringBuilder stringBuilder = new StringBuilder(s);
            for (int i = 0, i1 = 16 - len; i < i1; i++) {
                stringBuilder.insert(0, " ");
            }
            return stringBuilder.toString();
        }
        return s;
    }

    private static final Pattern MULTIPART_VALUE_PATTERN = Pattern.compile("(?<=name=\").*?(?=\"($|;\\s))");
    private static final Pattern TEXT_CONTENT_TYPE_PATTERN = Pattern.compile("^(application/(json|xml)|text/*).*");

    private final void logRequest(int tag, Request request) {
        if (request != null) {
            this.log("--------request " + tag + "-------------------------------");
            this.log(getTagWidthFormat("url: ") + request.url().url());
            this.log(getTagWidthFormat("method: ") + request.method());
            logHeader(request.headers());
            logParameter(request.url());
            this.logBody(request.body());
        }
    }

    private void logResponse(int tag, Response response) {
        if (response == null) return;
        log("-------response " + tag + "-------------------------------");
        ResponseBody body = response.body();
        if (body == null) {
            log(getTagWidthFormat("response: ") + "response body is null");
            return;
        }
        logHeader(response.headers());
        String contentTypeStr = body.contentType() == null ? "" : body.contentType().toString();
        if (isTextContentType(contentTypeStr)) {
            String responseStr = readResponseBody(body);
            int len = responseStr.length();
            if (len == 0) {
                log(getTagWidthFormat("response: ") + "response body length is 0");
            } else {
                log(getTagWidthFormat("response: ") + responseStr);
            }
        } else {
            StringBuilder var4 = (new StringBuilder()).append(getTagWidthFormat("response: "));
            MediaType var10002 = body.contentType();
            var4 = var4.append(var10002 != null ? var10002.type() : null).append('/');
            var10002 = body.contentType();
            log(var4.append(var10002 != null ? var10002.subtype() : null).append(", ").append("size=").append(this.formatSize(body.contentLength())).toString());
        }
    }

    private final void logBody(RequestBody requestBody) {
        if (requestBody == null) return;
        if (requestBody instanceof FormBody) {
            for (int i = 0, i1 = ((FormBody) requestBody).size(); i < i1; i++) {
                log(getTagWidthFormat("form body: ") + ((FormBody) requestBody).name(i) + '=' + ((FormBody) requestBody).value(i));
            }
        }
        if (requestBody instanceof MultipartBody) {
            Iterator<MultipartBody.Part> var12 = ((MultipartBody) requestBody).parts().iterator();
            while (var12.hasNext()) {
                MultipartBody.Part part = var12.next();
                Headers header = part.headers();
                if (header == null) continue;
                for (int i = 0, i1 = header.size(); i < i1; i++) {
                    if (header.name(i).equals("Content-Disposition")) {
                        String key = matchMultipartBodyKey(header.value(i));
                        RequestBody body = part.body();
                        String contentTypeStr = body.contentType() == null ? "" : body.contentType().toString();
                        if (isTextContentType(contentTypeStr) || TextUtils.isEmpty(contentTypeStr)) {
                            log(getTagWidthFormat("multipart body: ") + key + '=' + readRequestBodyString(body));
                        } else {
                            try {
                                this.log(getTagWidthFormat("multipart body: ") + key + "={binary}, Content-Type=" + contentTypeStr + ", size=" + this.formatSize(body.contentLength()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } else {
            MediaType mediaType = requestBody.contentType();
            if (mediaType == null) return;
            if (TextUtils.equals(mediaType.subtype().toLowerCase(Locale.US), "json")) {
                log(getTagWidthFormat("json body: ") + readRequestBodyString(requestBody));
            }
        }
    }

    private void logHeader(Headers headers) {
        if (headers != null) {
            int i = 0;

            for (int var3 = headers.size(); i < var3; ++i) {
                this.log(getTagWidthFormat("header: ") + headers.name(i) + '=' + headers.value(i));
            }

        }
    }

    private final void logParameter(HttpUrl httpUrl) {
        if (httpUrl != null) {
            Iterator var3 = httpUrl.queryParameterNames().iterator();

            while (var3.hasNext()) {
                String name = (String) var3.next();
                Iterator var5 = httpUrl.queryParameterValues(name).iterator();

                while (var5.hasNext()) {
                    String value = (String) var5.next();
                    this.log(getTagWidthFormat("url parameter: ") + name + '=' + value);
                }
            }

        }
    }

    private final String readRequestBodyString(RequestBody requestBody) {
        Buffer buffer = new Buffer();
        try {
            requestBody.writeTo((BufferedSink) buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.readString(Charset.defaultCharset());
    }

    private final String readResponseBody(ResponseBody responseBody) {
        BufferedSource source = responseBody.source();
        try {
            source.request(Long.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Buffer buffer = source.buffer().clone();
        return buffer.readString(Charset.defaultCharset());
    }

    private final String matchMultipartBodyKey(String content) {
        String var2;
        try {
            Matcher matcher = MULTIPART_VALUE_PATTERN.matcher((CharSequence) content);
            matcher.find();
            var2 = matcher.group();
        } catch (Exception var4) {
            var4.printStackTrace();
            var2 = "";
        }

        return var2;
    }

    private final String formatSize(long var1) {
        StringBuilder sizeSB;
        label36:
        {
            sizeSB = new StringBuilder();
            if (0L <= var1) {
                if (1024L >= var1) {
                    sizeSB.append(this.formatDecimal((float) var1, 2)).append("Byte");
                    break label36;
                }
            }

            if (1024L <= var1) {
                if (1024000L >= var1) {
                    sizeSB.append(this.formatDecimal((float) var1 / 1024.0F, 2)).append("KB");
                    break label36;
                }
            }

            if (1024000L <= var1) {
                if (1024000000L >= var1) {
                    sizeSB.append(this.formatDecimal((float) var1 / 1024000.0F, 2)).append("MB");
                    break label36;
                }
            }

            if (1024000000L <= var1) {
                if (1024000000000L >= var1) {
                    sizeSB.append(this.formatDecimal((float) var1 / 1.024E9F, 2)).append("GB");
                }
            }
        }
        return sizeSB.toString();
    }

    private final String formatDecimal(float content, int scaleNumber) {
        StringBuilder ruleSB = new StringBuilder("#.");
        for (int var4 = 0, var5 = scaleNumber; var4 < var5; ++var4) {
            ruleSB.append("0");
        }
        DecimalFormat decimalFormat = new DecimalFormat(ruleSB.toString());
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        StringBuilder result = new StringBuilder(decimalFormat.format(content));
        if (result.charAt(0) == '.') {
            result.insert(0, "0");
        }
        return result.toString();
    }

    private final boolean isTextContentType(String contentType) {
        Matcher matcher = TEXT_CONTENT_TYPE_PATTERN.matcher((CharSequence) contentType);
        return matcher.matches();
    }

}
