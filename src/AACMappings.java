import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.StringCache;
import util.AssociativeArray;
import util.KeyNotFoundException;
import util.NullKeyException;
import java.util.ArrayList;
/**
 * Creates a set of mappings of an AAC that has two levels,
 * one for categories and then within each category, it has
 * images that have associated text to be spoken. This class
 * provides the methods for interacting with the categories
 * and updating the set of images that would be shown and handling
 * an interactions.
 * 
 * @author Catie Baker & Paden Houck
 *
 */
public class AACMappings implements AACPage {
	AssociativeArray<String, AACCategory> categories = new AssociativeArray<String, AACCategory>();
	String selectedCategory = "";
	String FName;
	/**
	 * Creates a set of mappings for the AAC based on the provided
	 * file. The file is read in to create categories and fill each
	 * of the categories with initial items. The file is formatted as
	 * the text location of the category followed by the text name of the
	 * category and then one line per item in the category that starts with
	 * > and then has the file name and text of that image
	 * 
	 * for instance:
	 * img/food/plate.png food
	 * >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon
	 * img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing
	 * and food has french fries and watermelon and clothing has a
	 * collared shirt
	 * 
	 * @param filename the name of the file that stores the mapping information
	 */
	public AACMappings(String filename) {

		this.FName = filename;
		AACCategory lastCategory = null;
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] command = line.split(" ", 2);
				if (command.length >= 2) {
					if (command[0].startsWith(">")) {
						lastCategory.addItem(command[0].substring(1,command[0].length()), command[1]);
					} else {
						addItem(command[0], command[1]);
						try {
							lastCategory = categories.get(command[0]);
						} catch (KeyNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} // try
					} // if else
				} // if
			} // while
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Given the image location selected, it determines the action to be
	 * taken. This can be updating the information that should be displayed
	 * or returning text to be spoken. If the image provided is a category,
	 * it updates the AAC's current category to be the category associated
	 * with that image and returns the empty string. If the AAC is currently
	 * in a category and the image provided is in that category, it returns
	 * the text to be spoken.
	 * 
	 * @param imageLoc the location where the image is stored
	 * @return if there is text to be spoken, it returns that information, otherwise
	 *         it returns the empty string
	 * @throws Exception 
	 * @throws NoSuchElementException if the image provided is not in the current
	 *                                category
	 */
	public String select(String imageLoc) throws IOException {
		if (!selectedCategory.equals("")) {
			try {
				return categories.get(selectedCategory).contents.get(imageLoc);
			} catch (KeyNotFoundException e) {
				throw new IOException();
			}
		}
		if (categories.hasKey(imageLoc)) {
			selectedCategory = imageLoc;
		} else {
			throw new IOException();
		}
		return "";
	}

	/**
	 * Provides an array of all the images in the current category
	 * 
	 * @return the array of images in the current category; if there are no images,
	 *         it should return an empty array
	 */
	public String[] getImageLocs() {
		if (!selectedCategory.equals("")) {
			try {
				String[] imageLocs = categories.get(selectedCategory).getImageLocs();
				return imageLocs;
			} catch (KeyNotFoundException e) {
				return new String[]{};
			}
		}  else {

			ArrayList<String> tempImageLocs = new ArrayList<String>();
			for (int i = 0; i < categories.getPairs().length; i++) {
				if (categories.getPairs()[i]!= null) {
					tempImageLocs.add(categories.getPairs()[i].getKey());
				}
			}
			String[] temp = new String[tempImageLocs.toArray().length];
			for (int i = 0; i < tempImageLocs.toArray().length; i++) {
				temp[i] = tempImageLocs.get(i);
			}

			return temp;
		}
	}

	/**
	 * Resets the current category of the AAC back to the default
	 * category
	 */
	public void reset() {
		selectedCategory =  "";
	}

	/**
	 * Writes the ACC mappings stored to a file. The file is formatted as
	 * the text location of the category followed by the text name of the
	 * category and then one line per item in the category that starts with
	 * > and then has the file name and text of that image
	 * 
	 * for instance:
	 * img/food/plate.png food
	 * >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon
	 * img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing
	 * and food has french fries and watermelon and clothing has a
	 * collared shirt
	 * 
	 * @param filename the name of the file to write the
	 *                 AAC mapping to
	 */
	public void writeToFile(String filename) {

	}

	/**
	 * Adds the mapping to the current category (or the default category if
	 * that is the current category)
	 * 
	 * @param imageLoc the location of the image
	 * @param text     the text associated with the image
	 */
	public void addItem(String imageLoc, String text) {
		if (!selectedCategory.equals("")) {
			try {
				categories.get(selectedCategory).addItem(imageLoc, text);
			} catch (KeyNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
		try {
			categories.set(imageLoc, new AACCategory(text));
		} catch (NullKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	}

	/**
	 * Gets the name of the current category
	 * 
	 * @return returns the current category or the empty string if
	 *         on the default category
	 */
	public String getCategory() {
		try {
			return categories.get(selectedCategory).getCategory();
		} catch (KeyNotFoundException e) {
			return "";
		}
	}

	/**
	 * Determines if the provided image is in the set of images that
	 * can be displayed and false otherwise
	 * 
	 * @param imageLoc the location of the category
	 * @return true if it is in the set of images that
	 *         can be displayed, false otherwise
	 */
	public boolean hasImage(String imageLoc) {
		return false;
	}
}
