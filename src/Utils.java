import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;


public class Utils {
	public static byte[] readBytes(String fileName) {
		try {

			// shows what the path is (for testing/debugging)
			// System.out.println(System.getProperty("user.dir"));

			// longer form of file name w/ path (if needed)
			// FileInputStream fstream = new
			// FileInputStream(System.getProperty("user.dir") + "/" + fileName);

			// shorter form of file name assumes file in current directory
			// (which is the project directory
			// if no subdirectory option used)
			
			File f = new File(fileName);
			if (f.exists() && !f.isDirectory()) {
			FileInputStream fstream = new FileInputStream(fileName);

			// Create a stream and reader for the file
			DataInputStream in = new DataInputStream(fstream);
	
			return org.apache.commons.io.IOUtils.toByteArray(in);
			}
			return null;
					
			
		} catch (Exception e) { // Catch exception if any
			WebServer.triggerInternalError("Error Loading File: " + e.getMessage());
			return null;
		}

	}
	
	public static boolean writeTextFile(String fileName, String content) {
		try {
			File f = new File(fileName);
			if (f.exists() && !f.isDirectory() && f.canWrite()) {
				
			
				FileWriter fw = new FileWriter(fileName);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(content);
				bw.close();

			return true;
			}
		} catch (Exception e) {
			WebServer.triggerInternalError("Error Writing to File: " + fileName + " error: " + e.getMessage());
		}
		return false;
	}
	
	public static String readTextFile(String fileName) {
		try {
			File f = new File(fileName);
			if (f.exists() && !f.isDirectory()) {
			FileInputStream fstream = new FileInputStream(fileName);

			// Create a stream and reader for the file
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String strLine;
			String content = "";

			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				content += strLine + "\n";
			}

			// Close the input stream
			in.close();
			return content;
			}
			WebServer.logInfo("Couldn't Find File: " + fileName);
			return null;
		} catch (Exception e) { // Catch exception if any
			WebServer.triggerInternalError("Error Loading File: " + fileName + " error: " + e.getMessage());
			return null;
		}

	}
	
	public static long sum(int[] arr) {
		long sum = 0;
		for (int i : arr)
			sum += i;
		return sum;
	}

	public static double sum(double[] arr) {
		double sum = 0.0;
		for (double d : arr)
			sum += d;
		return sum;
	}
	
	// Returns -1 if not found
	public static int nthIndexOf(String str, char ch, int n) {
		int index = -1;
		for (int i = 0; i < n; i ++) {			
			index = str.substring(index + 1).indexOf(ch) + index + 1;
		}
		return index;
	}

	
	
	
}
