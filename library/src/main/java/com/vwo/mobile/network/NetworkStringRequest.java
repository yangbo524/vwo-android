package com.vwo.mobile.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vwo.mobile.utils.NetworkUtils;
import com.vwo.mobile.utils.VWOLog;
import com.vwo.mobile.utils.VWOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by aman on 09/09/17.
 */

public class NetworkStringRequest extends NetworkRequest<String> {
    @Nullable
    private Map<String, String> headers;
    @Nullable
    private Map<String, String> params;
    private boolean gzipEnabled;
    private String body;

    public NetworkStringRequest(@NonNull String url, @NonNull String method) throws MalformedURLException {
        super(url, method);
    }

    public NetworkStringRequest(@NonNull String url, @NonNull String method,
                                @Nullable Map<String, String> headers,
                                @Nullable Map<String, String> params) throws MalformedURLException {
        super(url, method);
        this.headers = headers;
        this.params = params;
    }

    @Override
    @Nullable
    public String parseResponse(NetworkResponse response) {
        try {
            if (response.getBody() != null) {
                byte[] body;
                if (isGzipped(response.getHeaders())) {
                    try {
                        body = decompressResponse(response.getBody());
                    } catch (IOException exception) {
                        VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "Unable to decompress body",
                                exception, false, true);
                        return null;
                    }
                } else {
                    body = response.getBody();
                }
                return new String(body, NetworkUtils.Headers.parseCharset(response.getHeaders()));
            } else {
                VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "Response body is empty", true, false);
            }
        } catch (UnsupportedEncodingException exception) {
            VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, exception, true, false);
        }
        return null;
    }

    public void setGzipEnabled(boolean enabled) {
        this.gzipEnabled = enabled;
    }

    public boolean isGzipEnabled() {
        return this.gzipEnabled;
    }

    @Override
    @NonNull
    protected Map<String, String> getHeaders() {
        if (gzipEnabled) {
            if (headers == null) {
                headers = new HashMap<>();
            }

            headers.put(NetworkUtils.Headers.HEADER_CONTENT_ENCODING, NetworkUtils.Headers.ENCODING_GZIP);
            headers.put(NetworkUtils.Headers.HEADER_ACCEPT_ENCODING, NetworkUtils.Headers.ENCODING_GZIP);
        }
        return this.headers != null ? this.headers : super.getHeaders();
    }

    @Override
    @Nullable
    public Map<String, String> getParams() {
        return this.params;
    }

    private boolean isGzipped(Map<String, String> headers) {
        return headers != null && !headers.isEmpty() && headers.containsKey(NetworkUtils.Headers.HEADER_CONTENT_ENCODING) &&
                headers.get(NetworkUtils.Headers.HEADER_CONTENT_ENCODING).equalsIgnoreCase(NetworkUtils.Headers.ENCODING_GZIP);
    }

    /**
     * @param compressed is compressed body to be decompressed
     * @return the decompressed body back to calling function.
     * @throws IOException is the exception that can be thrown during decompression
     */
    private byte[] decompressResponse(byte[] compressed) throws IOException {
        ByteArrayOutputStream baos = null;
        try {
            int size;
            ByteArrayInputStream memstream = new ByteArrayInputStream(compressed);
            GZIPInputStream gzip = new GZIPInputStream(memstream);
            final int buffSize = 8192;
            byte[] tempBuffer = new byte[buffSize];
            baos = new ByteArrayOutputStream();
            while ((size = gzip.read(tempBuffer, 0, buffSize)) != -1) {
                baos.write(tempBuffer, 0, size);
            }
            return baos.toByteArray();
        } finally {
            if (baos != null) {
                baos.close();
            }
        }
    }

    @Override
    public String getBodyContentType() {
        return super.getBodyContentType();
    }

    @Override
    public byte[] getBody() {
        if (body != null) {
            if (gzipEnabled) {
                VWOLog.i(VWOLog.UPLOAD_LOGS, body, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                GZIPOutputStream gzos = null;
                try {
                    gzos = new GZIPOutputStream(baos);
                    gzos.write(body.getBytes(DEFAULT_CONTENT_ENCODING));
                    gzos.flush();
                } catch (IOException exception) {
                    VWOLog.e(VWOLog.UPLOAD_LOGS, exception, false, true);
                    return super.getBody();
                } finally {
                    if (gzos != null) try {
                        gzos.close();
                    } catch (IOException exception) {
                        VWOLog.e(VWOLog.UPLOAD_LOGS, exception, false, true);
                    }
                }
                return baos.toByteArray();
            } else {
                try {
                    return body.getBytes(DEFAULT_CONTENT_ENCODING);
                } catch (UnsupportedEncodingException exception) {
                    VWOLog.e(VWOLog.UPLOAD_LOGS, exception, false, true);
                }
            }
        }

        return super.getBody();
    }

    @Override
    public ErrorResponse getErrorResponse(ErrorResponse errorResponse) {
        try {
            if (errorResponse.getNetworkResponse().getBody() != null) {
                byte[] body;
                if (isGzipped(errorResponse.getNetworkResponse().getHeaders())) {
                    try {
                        body = decompressResponse(errorResponse.getNetworkResponse().getBody());
                    } catch (IOException exception) {
                        VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "Unable to decompress body",
                                exception, false, true);
                        return null;
                    }
                } else {
                    body = errorResponse.getNetworkResponse().getBody();
                }
                String err = new String(body, NetworkUtils.Headers.parseCharset(errorResponse.getNetworkResponse().getHeaders()));
                VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, err, false, true);
            } else {
                VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, "Response body is empty", true, false);
            }
        } catch (UnsupportedEncodingException exception) {
            VWOLog.e(VWOLog.DOWNLOAD_DATA_LOGS, exception, true, false);
        }
        return super.getErrorResponse(errorResponse);
    }
}
