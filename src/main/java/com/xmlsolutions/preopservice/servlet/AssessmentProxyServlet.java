package com.xmlsolutions.preopservice.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xmlsolutions.messaging.InjectServiceImpl;
import com.xmlsolutions.messaging.InvocationException;
import com.xmlsolutions.messaging.Output;

public class AssessmentProxyServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	//TODO - read from configuration
	private static final String URL = "http://nhshackday-integrator.herokuapp.com/assessment";
	//private static final String URL = "http://localhost:8080/cloudharness/listener/cr/assessment";
	private static URI PRE_OP_URI = null;
	
	static {
		try {
			PRE_OP_URI = new URI(URL);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		byte[] xmlPayload = readContent(req);
		InjectServiceImpl injectionService = new InjectServiceImpl();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "text/xml");
		try {
			Output result = injectionService.injectSingleMessageHTTP(PRE_OP_URI, xmlPayload, headers);
			if (result != null) {
				System.out.println("Got result: " + result.getResponseCode());
				System.out.println("Payload: " + new String(result.getOutput()));
			}
			System.out.println("Sent to " + PRE_OP_URI.toASCIIString());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationException e) {
			e.printStackTrace();
		}
		resp.setStatus(HttpServletResponse.SC_OK);	
	}
	
	//TODO - not that efficient to read one byte at a time
	public static byte[] readContent(HttpServletRequest req) throws IOException {
		InputStream in = req.getInputStream();		
		int bytesRead = 0;		
		int bytesToRead= req.getContentLength();
		byte[] payload = null;
		if (bytesToRead > 0) {
			payload = new byte[bytesToRead];
			while (bytesRead < bytesToRead) {
			  int result = in.read(payload, bytesRead, bytesToRead - bytesRead);
			  if (result == -1) {
				  break;
			  }
			  bytesRead += result;
			}
		}
		return payload;
	}
}
