package com.scut.vc.location;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class GetLocation {

	Activity mActivity;
	
	public GetLocation(Activity mActivity){
		this.mActivity = mActivity;
	}
	
	public Location getLocation(){
		LocationManager locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (location == null) {
			location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		return location;
	}

	public String getCity() {
		Location location = getLocation();

		// http://maps.google.com/maps/geo?q=40.714224,-73.961452&output=json&oe=utf8&sensor=true_or_false&key=your_api_key
		String localityName = "";
		HttpURLConnection connection = null;
		URL serverAddress = null;

		try {
			// build the URL using the latitude & longitude you want to lookup
			// NOTE: I chose XML return format here but you can choose something
			// else
			serverAddress = new URL("http://maps.google.com/maps/geo?q="
					+ Double.toString(location.getLatitude()) + ","
					+ Double.toString(location.getLongitude())
					+ "&output=xml&oe=utf8&sensor=true&key=" + "VC");
			// set up out communications stuff
			connection = null;

			// Set up the initial connection
			connection = (HttpURLConnection) serverAddress.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setReadTimeout(100000);

			connection.connect();

			try {
				InputStreamReader isr = new InputStreamReader(
						connection.getInputStream());
				InputSource source = new InputSource(isr);
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				XMLReader xr = parser.getXMLReader();
				GoogleReverseGeocodeXmlHandler handler = new GoogleReverseGeocodeXmlHandler();

				xr.setContentHandler(handler);
				xr.parse(source);

				localityName = handler.getLocalityName();
				System.out.println("GetCity.reverseGeocode() ==   " + localityName);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("GetCity.reverseGeocode()" + ex);
		}
		System.out.println("city = " + localityName);
		return localityName;
	}

}