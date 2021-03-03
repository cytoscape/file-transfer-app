package org.cytoscape.file_transfer.internal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CloudURL {
		
	public enum URLType {
		UNKNOWN,
		GOOGLE_DRIVE,
		MICROSOFT_ONEDRIVE,
		GITHUB,
		DROPBOX;
	}

	private URL workingURL;
	private Cloud workingCloud;
	
	Map<String, Cloud> cloudMap = Map.of (
			GoogleDriveCloud.DOMAIN, new GoogleDriveCloud(),
			MicrosoftOnedriveCloud.DOMAIN, new MicrosoftOnedriveCloud(),
			GithubCloud.DOMAIN, new GithubCloud(),
			DropboxCloud.DOMAIN, new DropboxCloud()
			);
	
	CloudURL(String rawURL) throws MalformedURLException {
		workingURL = new URL(rawURL);
		workingCloud = cloudMap.get(workingURL.getHost());
	}

	
	private URL createNewURL(String newHost, String newPath, String newQuery) throws MalformedURLException, URISyntaxException, UnsupportedEncodingException {
		if (newHost == null)
			newHost = workingURL.getHost();
		if (newPath == null) 
			newPath = workingURL.getPath();
		if (newQuery == null) {
			newQuery = workingURL.getQuery();
			if (newQuery != null)
				newQuery = URLDecoder.decode(newQuery, StandardCharsets.UTF_8);
		}
		String protocol = workingURL.getProtocol();
		String userInfo = workingURL.getUserInfo();
		int port = workingURL.getPort();
		String ref = workingURL.getRef();
		
		return new URI(protocol, userInfo, newHost, port, newPath, newQuery, ref).toURL();
	}

	protected abstract class Cloud {
		
		protected abstract URLType getURLType();
		
		protected abstract URL directURL() throws MalformedURLException, URISyntaxException, UnsupportedEncodingException;
		
		protected InputStream getInputStream(URL directURL) throws IOException, URISyntaxException {
			return directURL.openConnection().getInputStream();
		}
	}
	
	protected class GoogleDriveCloud extends Cloud {
		
		static final String DOMAIN =  "drive.google.com";

		@Override
		public URLType getURLType() {
			return URLType.GOOGLE_DRIVE;
		}

		@Override
		protected URL directURL() throws MalformedURLException, URISyntaxException, UnsupportedEncodingException {
			/*
			The transformation is straightforward if the file size is less than 
			100K. Otherwise, Google inserts a very troublesome verification page 
			before allowing a download. 
		 
			For a file less than 100K, this is the transformation:
		 
			For example: https://drive.google.com/file/d/1Fk1fEs9hryxqSZ4iOrum7zw-AYagr7IS/view?usp=sharing
			Converts to: https://drive.google.com/uc?export=download&id=1Fk1fEs9hryxqSZ4iOrum7zw-AYagr7IS
		 
			For files greater than 100K, the solution is in this Python code: 
			https://stackoverflow.com/questions/25010369/wget-curl-large-file-from-google-drive 
			... on the response that was upvoted 200 times … the third response.
		 
			It shows that given the Google file ID for a large file, getting 
			the download verification page also gets a “confirmation key”, which 
			can then be built into a URL that results in the whole download. 
			It’s a two step process, but it avoids having the user deal with the 
			verification page. I think the code works for both large and small 
			files.
		 
			Converts to: https://drive.google.com/uc?export=download&id=1Fk1fEs9hryxqSZ4iOrum7zw-AYagr7IS?confirm=xxy2rtt
			
			However, the confirmation protocol used by Google behind the scenes involves multiple
			URLs, each called in sequence via HTTP 302 redirection, and one or more URL relying on
			cookies passed from a previous URL. To enable this, there must be a cookie manager 
			installed before the protocol gets started. It will keep track of cookies downloaded and
			uploaded by the URLs. Note that URLConnection automatically follows 302 redirects 
			by default.
			
			For a non-Java explanation, see the curl example in the solution upvoted 54 times. This
			curl actually does work, and the L and b parameters are necessary (L for follow
			redirects, and b for provide cookies.)				
			*/
			Matcher m = Pattern.compile("^/file/d/([^/]*)/view$").matcher(workingURL.getPath());
			if (!m.matches())
				throw new MalformedURLException();
			return createNewURL(null, "/uc", "export=download&id=" + m.group(1));
		}
		
		@Override
		protected InputStream getInputStream(URL directURL) throws IOException, URISyntaxException {
			CookieHandler priorCoockieHandler = CookieHandler.getDefault();
			try {
				CookieManager cm = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
				CookieHandler.setDefault(cm);

				InputStream is = directURL.openConnection().getInputStream();
				
				Map<String, List<String>> cookieMap = cm.get(directURL.toURI(), new HashMap<String, List<String>>());
				String confirmToken = null;
				List<String> cookieList = cookieMap.get("Cookie");
				for (String cookie : cookieList) {
					if (cookie.startsWith("download_warning")) {
						confirmToken = cookie.substring(cookie.indexOf('=') + 1);
					}
				}
				if (confirmToken != null) {
					directURL = new URL(directURL.toString() + "&confirm=" + confirmToken);
					is = directURL.openConnection().getInputStream();
				}
				
				return is;
			} finally {
				CookieHandler.setDefault(priorCoockieHandler);
			}
		}
	}
	
	protected class MicrosoftOnedriveCloud extends Cloud {
		
		static final String DOMAIN =  "onedrive.live.com";

		@Override
		public URLType getURLType() {
			return URLType.MICROSOFT_ONEDRIVE;
		}

		@Override
		protected URL directURL() throws MalformedURLException, URISyntaxException, UnsupportedEncodingException {
			/*
			The transformation is described in: https://bydik.com/onedrive-direct-link/.
			It is straightforward if the user gets the URL from 
			the OneDrive web site by right clicking on the file and choosing the 
			Embed menu option. OneDrive will generate an HTML snippet. 
			The downloadable URL is in the iframe src parameter. 
			Extract it and replace “embed” with download.
	 
	 		For example: https://onedrive.live.com/embed?cid=C357475E90DD89C4&resid=C357475E90DD89C4%21155&authkey=AGYxzU5on7R3yMY&em=2
	    	Converts to: https://onedrive.live.com/download?cid=C357475E90DD89C4&resid=C357475E90DD89C4%21155&authkey=AGYxzU5on7R3yMY&em=2
		    */
		    return createNewURL(null, "/download", null);
		}
	}
	
	protected class GithubCloud extends Cloud {
		
		static final String DOMAIN = "github.com";

		@Override
		public URLType getURLType() {
			return URLType.GITHUB;
		}

		@Override
		protected URL directURL() throws MalformedURLException, URISyntaxException, UnsupportedEncodingException {
			/*
		    The transformation is apparent by inspection.
		    
			For example: https://github.com/bdemchak/cytoscape-jupyter/blob/main/sanity-test/data/galFiltered.sif
			Converts to: https://raw.githubusercontent.com/bdemchak/cytoscape-jupyter/main/sanity-test/data/galFiltered.sif
			*/
			String newPath = workingURL.getPath().replaceFirst("/blob/", "/");
			return createNewURL("raw.githubusercontent.com", newPath, null);
		}
	}
	
	protected class DropboxCloud extends Cloud {
		
		static final String DOMAIN = "www.dropbox.com";

		@Override
		public URLType getURLType() {
			return URLType.DROPBOX;
		}

		@Override
		protected URL directURL() throws MalformedURLException, URISyntaxException, UnsupportedEncodingException {
			/*
			The transformation is straight forward, as described in https://www.buildsometech.com/create-get-dropbox-direct-download-link/ … scroll mostly down to “But you can also do this by simply following these simple Dropbox URL hacks.”
				 
			For example: https://www.dropbox.com/s/g4ygalz2my88c71/Get%20Started.pdf?dl=0
			Converts to: https://dl.dropboxusercontent.com/s/g4ygalz2my88c71/Get%20Started.pdf?dl=0
			Alternate: https://dl.dropbox.com/s/g4ygalz2my88c71/Get%20Started.pdf?dl=1
			*/
			return createNewURL("dl.dropboxusercontent.com", null, null);
		}
	}


	public URLType getURLType() {
		return (workingCloud == null) ? URLType.UNKNOWN : workingCloud.getURLType();
	}

	public URL directURL() throws MalformedURLException, URISyntaxException, UnsupportedEncodingException {
		return (workingCloud == null) ? workingURL : workingCloud.directURL();
	}
	
	public long readFile(String outputFile) throws Exception {
		final int BUFFER_BYTES = 1024 * 1024;
		
		URL directURL = directURL();
		InputStream urlIS = null;
		OutputStream os = null;
		InputStream is = null;
		
		try {
			try {
				urlIS = (workingCloud == null) ? directURL.openConnection().getInputStream() : workingCloud.getInputStream(directURL);
			} catch (Exception ex) {
				throw new Exception("Error opening URL " + directURL + ": " + ex, ex);
			}
	
			if (outputFile == null) {
				throw new Exception("Output file name cannot be null");
			}
			try {
				os = new BufferedOutputStream(Files.newOutputStream(Paths.get(outputFile)), BUFFER_BYTES);
			} catch (Exception ex) {
				throw new Exception("Error opening output file " + outputFile + ": " + ex, ex);
			}
			
			try {
				is = new BufferedInputStream(urlIS, BUFFER_BYTES);
			} catch (Exception ex) {
				throw new Exception("Error openiong buffered URL for " + directURL + ": " + ex, ex);
			}
			
			try {
				return is.transferTo(os);
			} catch (Exception ex) {
				throw new Exception("Error copying " + directURL + " to " + outputFile + ": " + ex, ex);
			}
		} finally {
			if (urlIS != null) urlIS.close();
			if (is != null) is.close();
			if (os != null) os.close();
		}
	}
	
	private static void tryURL(String url, URLType expectedType, String expectedURL, long expectedLength) {
		try {
			System.out.println("Working on " + url);
			CloudURL cloudURL = new CloudURL(url);
			URLType urlType = cloudURL.getURLType();
			String directURL = cloudURL.directURL().toString();
			if (urlType != expectedType) throw new Exception("Expected type not found");
			if (!directURL.equals(expectedURL)) throw new Exception("Expected URL not found");
			
			long length = cloudURL.readFile("output.dat");
			if (length != expectedLength) throw new Exception("Expected length not read");
			System.out.println("Finished");
		}
		catch (Throwable e) {
			System.out.println(e);
		}
	}
	
	public static void main(String[] args) {		
			// Some random file
		CloudURL.tryURL("http://tpsoft.com/museum_images/IBM%20PC.JPG", URLType.UNKNOWN, "http://tpsoft.com/museum_images/IBM%20PC.JPG", 438233);
		
			// Dropbox - GDS112_full.soft
		CloudURL.tryURL("https://www.dropbox.com/s/r15azh0xb53smu1/GDS112_full.soft?dl=0", URLType.DROPBOX, "https://dl.dropboxusercontent.com/s/r15azh0xb53smu1/GDS112_full.soft?dl=0", 5536880);
		
			// Dropbox - BIOGRID-ORGANISM-Saccharomyces_cerevisiea-3.2.105.mitab
		CloudURL.tryURL("https://www.dropbox.com/s/8wc8o897tsxewt1/BIOGRID-ORGANISM-Saccharomyces_cerevisiae-3.2.105.mitab?dl=0", URLType.DROPBOX, "https://dl.dropboxusercontent.com/s/8wc8o897tsxewt1/BIOGRID-ORGANISM-Saccharomyces_cerevisiae-3.2.105.mitab?dl=0", 166981992);

			// Github - GDS112_full.soft
		CloudURL.tryURL("https://github.com/cytoscape/file-transfer-app/blob/master/test_data/GDS112_full.soft", URLType.GITHUB, "https://raw.githubusercontent.com/cytoscape/file-transfer-app/master/test_data/GDS112_full.soft", 5536880);
		
			// Onedrive - GDS112_full.soft
		CloudURL.tryURL("https://onedrive.live.com/embed?cid=C357475E90DD89C4&resid=C357475E90DD89C4%217207&authkey=ACEU5LrVtI_jWTU", URLType.MICROSOFT_ONEDRIVE, "https://onedrive.live.com/download?cid=C357475E90DD89C4&resid=C357475E90DD89C4!7207&authkey=ACEU5LrVtI_jWTU", 5536880);

			// Onedrive - BIOGRID-ORGANISM-Saccharomyces_cerevisiea-3.2.105.mitab
		CloudURL.tryURL("https://onedrive.live.com/embed?cid=C357475E90DD89C4&resid=C357475E90DD89C4%217208&authkey=ALtnboERjtOdRbQ", URLType.MICROSOFT_ONEDRIVE, "https://onedrive.live.com/download?cid=C357475E90DD89C4&resid=C357475E90DD89C4!7208&authkey=ALtnboERjtOdRbQ", 166981992);
		
			// GoogleDrive - GDS112_full.soft
		CloudURL.tryURL("https://drive.google.com/file/d/12sJaKQQbesF10xsrbgiNtUcqCQYY1YI3/view?usp=sharing", URLType.GOOGLE_DRIVE, "https://drive.google.com/uc?export=download&id=12sJaKQQbesF10xsrbgiNtUcqCQYY1YI3", 5536880);
		
			// GoogleDrive - BIOGRID-ORGANISM-Saccharomyces_cerevisiea-3.2.105.mitab
		CloudURL.tryURL("https://drive.google.com/file/d/1ws0DAJJjKupB2-mb5cL-A_T7yEI01tBd/view?usp=sharing", URLType.GOOGLE_DRIVE, "https://drive.google.com/uc?export=download&id=1ws0DAJJjKupB2-mb5cL-A_T7yEI01tBd", 166981992);

		System.out.println("Done");
	}
}
