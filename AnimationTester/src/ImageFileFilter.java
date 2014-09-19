/*Should filter out files so that only these image file types are accepted:
 * .tiff
 * .tif
 * .gif
 * .jpeg
 * .jpg
 * .png
*/

import java.io.File;
import javax.swing.filechooser.*;

public class ImageFileFilter extends FileFilter
{

	public boolean accept(File f)
	{
		if (f.isDirectory())
		{
			return true;
		}
		
		String extension = Utils.getExtension(f);
		if (extension != null)
		{
			if (extension.equals(Utils.gif)
					|| extension.equals(Utils.jpeg)
					|| extension.equals(Utils.jpg)
					|| extension.equals(Utils.png)
					)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		
		//Default case
		return false;
	}

	public String getDescription()
	{
		return "Image files";
	}

	

}

class Utils
{
	public final static String gif = "gif";
	public final static String jpeg = "jpeg";
	public final static String jpg = "jpg";
	public final static String png = "png";
	
	public static String getExtension(File f)
	{
		String export = null;
		String fileName = f.getName();
		int i = fileName.lastIndexOf('.');
		if (i > 0 && i < fileName.length() - 1)
		{
			export = fileName.substring(i+1).toLowerCase();
		}
		
		
		return export;
	}
}
