package de.hoell.jobcontrol.query;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;

public class CustomBodyStringRequest extends StringRequest {
    private final String requestBody;

    public CustomBodyStringRequest(String url, String requestBody, Response.Listener<String> listener,
                                   Response.ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
        this.requestBody = requestBody;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        byte[] body = null;
        if (!TextUtils.isEmpty(this.requestBody)) {
            try {
                body = requestBody.getBytes(getParamsEncoding());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Encoding not supported: " + getParamsEncoding(), e);
            }
        }

        return body;
    }
    @Override
    public String getBodyContentType() {
        return "application/xml; charset=" + getParamsEncoding();
    }

}
