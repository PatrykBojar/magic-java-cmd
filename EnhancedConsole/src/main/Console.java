package main;

//// START OF IMPORTS ////

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import sax.SAXManagement;
import strings.Strings;

//// END OF IMPORTS ////

/**
 * Simulation of a command console to perform actions with customised
 * instructions. Contains all the code for a proper console functioning. <br>
 * Cannot be instantiated.
 * 
 * @author Patryk Bojar
 * @version 1.0.5, 03 Dec 2021
 */
public final class Console {
	/**
	 * Main private Constructor.
	 */
	private Console() {
		// empty.
	}

	private static Scanner sc = new Scanner(System.in);
	private static String pathOri = "";
	private static String pathDes = "";
	/**
	 * Globally used variable, checks if XML tags have been activated or not. True
	 * by default.
	 */
	private static boolean showXMLTags = true;
	/**
	 * Locally used variable, checks if some methods need to write the file. True by
	 * default.
	 */
	private static boolean writeCopiedFile = true;

	/**
	 * Moves the file from source/origin to destination path. If needed deletes the
	 * file from source path and saves the new file.
	 * 
	 * @param source the origin path of the file.
	 * @param dest   the destination or new path of the file.
	 */
	private static void moveFile(File source, File dest) {
		copyFile(source, dest, true);
		// Deletes the file only if the source and destination path exists, source
		// path is different from destination path and writes file is true.
		if ((source.exists() && dest.exists()) && (!source.equals(dest)) && writeCopiedFile) {
			deleteFile(source, false);
		}
	}

	/**
	 * Gets the name of the file and copies it at the specified location. It checks
	 * all the possibilities when copying a file. Tries to simulate Windows CMD
	 * copy/move command.
	 * 
	 * @param source               the source path file.
	 * @param dest                 the destination path file.
	 * @param isCalledFromMoveFile true if the method is called from moveFile
	 *                             method; false otherwise.
	 */
	@SuppressWarnings("resource")
	private static void copyFile(File source, File dest, boolean isCalledFromMoveFile) {
		try {
			if (isPathCorrect(source, dest)) {
				if (source.exists()) {
					// Creates a new path which consists of destination path and the source file
					// name.
					File copiedFile = new File(dest + File.separator + source.getName());

					// If both paths are the same, shows a message, sets write file to false and
					// ends the execution of the method.
					if (source.equals(copiedFile) || source.equals(dest)) {
						writeCopiedFile = false;
						if (isCalledFromMoveFile) {
							System.out.println(Strings.FILE_MOVED + "\n");
							return;
						}
						System.out.println(Strings.FILE_COPIED_SAME_PLC + "\n");
						return;
					}

					if (isNewFileNameCorrect(dest)) {
						// If destination path exists, start the main chain of tests.
						// If not, creates the file with the name of destination path with no file
						// specified.
						if (dest.exists()) {

							String choice = ""; // user's string choice value.

							// If the name of the file is not specified, then it will be the
							// name of source path.
							// If this file exists, then asks for overwrite it.
							if (copiedFile.exists()) {

								do {
									System.out.print("¿Sobreescribir " + source + "? [s/n]: ");
									choice = sc.nextLine().toLowerCase();
								} while ((!choice.equals("s")) && (!choice.equals("n")));
								writeCopiedFile = choice.equals("s"); // if choice is "s", then set it to true, false
																		// otherwise.

								// If the destination path it's a directory, then it exists so it will copy the
								// file with the source name.
								// If it not exists, then the destination path has his own and unique name for
								// the file. It will copy it with than name.
								if (dest.isDirectory()) {
									// Checks if write is enabled and calls for write method.
									// If false, shows a information message.
									if (writeCopiedFile) {
										writeFile(new FileInputStream(source), new FileOutputStream(copiedFile),
												isCalledFromMoveFile);
									} else {
										if (isCalledFromMoveFile) {
											System.out.println(Strings.FILE_NOT_MOVED + "\n");
											return;
										}
										System.out.println(Strings.FILE_NOT_COPIED + "\n");
									}

								} else {
									writeFile(new FileInputStream(source), new FileOutputStream(dest),
											isCalledFromMoveFile);
									return;
								}

							} else {
								// If the destination path it's a file and exists, then it overwrites it.
								if (dest.isFile()) {

									do {
										System.out.print("¿Sobreescribir " + source + "? [s/n]: ");
										choice = sc.nextLine().toLowerCase();
									} while ((!choice.equals("s")) && (!choice.equals("n")));

									writeCopiedFile = choice.equals("s");

									if (writeCopiedFile) {
										writeFile(new FileInputStream(source), new FileOutputStream(dest),
												isCalledFromMoveFile);
										return;
									} else {

										if (isCalledFromMoveFile) {
											System.out.println(Strings.FILE_NOT_MOVED + "\n");
											return;
										}
										System.out.println(Strings.FILE_NOT_COPIED + "\n");
										return;
									}
								}
								// Destination file name is not specified, so it'll be the name of the source
								// path.
								writeFile(new FileInputStream(source), new FileOutputStream(copiedFile),
										isCalledFromMoveFile);
							}
						} else {
							// Creates the file with source name.
							// This call occurs only once if the file doesn't already exists.
							writeCopiedFile = true;
							writeFile(new FileInputStream(source), new FileOutputStream(dest), isCalledFromMoveFile);
						}
					} // isFileNameCorrect ends here.

				} else { // if file doesn't exists.
					throw new FileNotFoundException();
				}
			} // isPathCorrect ends here.
		} catch (FileNotFoundException e) {
			System.out.println(Strings.FILE_NOT_FOUND + "\n");
		}
	}

	/**
	 * Checks if the name contains any invalid Windows OS characters or it uses
	 * reserved names by the system.
	 * 
	 * @param file the file to be checked.
	 * @return true if the file name has a correct syntax; false otherwise.
	 */
	private static boolean isNewFileNameCorrect(File file) {
		String[] invalidChars = { "\\", ":", "*", "<", ">", "\"" };
		String fileName = file.getName();

		for (String ch : invalidChars) {
			if (fileName.contains(ch)) {
				System.out.println(Strings.INVALID_FILE_NAME + "\n");
				return false;
			}
		}
		if (removeFileExtension(fileName, true).equalsIgnoreCase("aux")) {
			System.out.println(Strings.SYS_RESERVED_NAME_THROW + "\n");
			return false;
		}
		return true;
	}

	/**
	 * Checks if all path syntax requirements are met.
	 * 
	 * @param pathOne the source and first path to be checked.
	 * @param pathTwo the destination and second path.
	 * @return true if the path has a correct syntax; false otherwise.
	 */
	private static boolean isPathCorrect(File pathOne, File pathTwo) {
		// Last string from both paths.
		String lastOriString = pathOri.substring(pathOri.length() - 1, pathOri.length());
		String lastDesString = pathDes.substring(pathDes.length() - 1, pathDes.length());

		// If the destination path ends with a separator and it's not a directory, throw
		// an error.
		if (lastDesString.equals(File.separator) && !pathTwo.isDirectory()) {
			System.out.println(Strings.PATH_NOT_FOUND + "\n");
			return false;
		}
		// If the origin path ends with a separator, throw an error. Can't copy all
		// content from a directory or file can't end with a separator.
		if (lastOriString.equals(File.separator)) {
			System.out.println(pathOne + File.separator + "*");
			System.out.println(Strings.NAME_DIR_NOT_FOUND + "\n");
			return false;
		}
		// If origin path or destination path hasn't a separator, throw an error.
		// Requires at least one separator in each path.
		if (!pathOri.contains(File.separator) || !pathDes.contains(File.separator)) {
			System.out.println(Strings.PATH_NOT_SEPARATED);
			System.out.println(Strings.PATH_GENERAL_THROW + "\n");
			return false;
		}

		// If origin path is a directory, then can't execute copy or move.
		if (pathOne.isDirectory()) {
			System.out.println(Strings.CANT_CPY_MVE_DIR + "\n");
			return false;
		}

		// If a path gets here, it has the desired format to proceed with copy or move
		// file.

		return true;
	}

	/**
	 * Writes the file and shows a confirmation message when finished.
	 * 
	 * @param fis                  the bytes from a file in a file system. File of
	 *                             which the content is going to be copied.
	 * @param fos                  the output stream for writing data. File where
	 *                             the new content is going to be written on.
	 * @param isCalledFromMoveFile if true, shows confirmation messages for move
	 *                             command; if false, shows messages for copy
	 *                             command.
	 */
	private static void writeFile(FileInputStream fis, FileOutputStream fos, boolean isCalledFromMoveFile) {
		try {
			int b; // bytes
			while ((b = fis.read()) != -1) {
				fos.write(b);
			}
			// Closing streams
			fos.flush();
			fis.close();
			fos.close();

			// Controlling different messages, depending on if it's called
			// or not from Mover.
			if (isCalledFromMoveFile) {
				System.out.println(Strings.FILE_MOVED + "\n");
				return;
			}
			System.out.println(Strings.FILE_COPIED + "\n");

		} catch (IOException e) {
			System.out.println(Strings.SOMETHING_WENT_WRONG);
		}
	}

	/**
	 * Removes a file from disk and then displays an informational message on the
	 * console.
	 * 
	 * @param file                the file to be deleted.
	 * @param showConfirmationMsg true if the operation returns a confirmation
	 *                            message; false otherwise.
	 */
	private static void deleteFile(File file, boolean showConfirmationMsg) {
		if (file.exists() && file.isFile()) {
			file.delete();
			if (showConfirmationMsg) {
				System.out.println(Strings.FILE_DELETED + "\n");
			}
		} else {
			if (showConfirmationMsg) {
				System.out.println(Strings.FILE_NOT_FOUND + "\n");
			}
		}
	}

	/**
	 * List the content of a directory with additional information such as type,
	 * size and last modification.
	 * 
	 * @param path the path where the directories and files are listed.
	 */
	private static void listDirContent(File path) {
		File[] files = path.listFiles();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH:mm");
		int totalFileNum = 0;
		int totalDirNum = 0;
		long totalFileB = 0;

		if (path.isFile()) {
			System.out.println(Strings.CANT_SHOW_DIR_CONTENTS + "\n");
			return;
		}

		if (path.exists()) {
			System.out.println(" El volumen de la unidad es: " + (path.getTotalSpace() / 1073741824) + " GB");
			System.out.println(" Espacio usable: " + (path.getUsableSpace() / 1073741824) + " GB");

			System.out.println("");
			System.out.println(" Directorio de " + path);
			System.out.println("");

			for (int i = 0; i < files.length; i++) {
				System.out.print(sdf.format(files[i].lastModified()));

				if (files[i].isFile()) {
					System.out.printf("%21s", customFormat("###,###.###", files[i].length()));
					System.out.print("\t" + files[i].getName());
					System.out.println();

					totalFileNum++;
					totalFileB += files[i].length();
				} else {
					System.out.println("    <DIR>\t\t" + files[i].getName());
					totalDirNum++;
				}
			}

			System.out.printf("%17d", totalFileNum);
			System.out.print(" arch.");
			System.out.printf("%15s", customFormat("###,###.###", totalFileB));
			System.out.printf("%5s%n", "-B-");
			System.out.printf("%17d", +totalDirNum);
			System.out.println(" dirs.");
			System.out.println("");
		} else {
			System.out.println(Strings.PATH_NOT_FOUND + "\n");
		}
	}

	/**
	 * Gets the directories and files from the system folder path, saves all the
	 * data into a StringBuilder Object and then shows them on the screen. The more
	 * 
	 * @param folder the folder path from which all content will be listed.
	 */
	private static void showDirTree(File folder) {

		if (folder.isDirectory()) {
			int space = 0;
			StringBuilder strBld = new StringBuilder();

			System.out.println(Strings.FOLDER_PATH_LIST);
			System.out.println(folder.getAbsolutePath().toUpperCase());

			getDirTree(folder, space, strBld);

			System.out.println(strBld.toString());
		} else {
			System.out.println(Strings.CANT_SHOW_DIR_CONTENTS + "\n");
		}

	}

	/**
	 * 
	 * @param folder the path of the folder from which the content will be listed.
	 * @param space  the space or indentation that increases with child folders.
	 * @param strBld the string builder that saves all the data tree.
	 */
	private static void getDirTree(File folder, int space, StringBuilder strBld) {
		File[] files = folder.listFiles();

		strBld.append(getSpaceString(space));
		// strBld.append("├─ 📁 "); // indicates the folder.
		strBld.append("   ");
		strBld.append(folder.getName());
		strBld.append("\n");

		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					getDirTree(file, space + 1, strBld);
				} else {
					getFileOnly(file, space + 1, strBld);
				}
			}
		}
	}

	/**
	 * 
	 * @param file   the path of the file from which the content will be listed.
	 * @param space  the space or indentation that increases with child folders and
	 *               files.
	 * @param strBld the string builder that saves all the data tree for files.
	 */
	private static void getFileOnly(File file, int space, StringBuilder strBld) {
		// String fileExt = getFileExtension(file.getName());

		strBld.append(getSpaceString(space));
		strBld.append("   ");

		// Simple checks for setting the right symbol for every file, depending on its
		// extension.

		/*
		 * if (fileExt.equalsIgnoreCase(".zip") || fileExt.equalsIgnoreCase(".rar")) {
		 * strBld.append("├─ 📚 "); // indicates a compressed file. } else if
		 * (fileExt.equalsIgnoreCase(".gif") || fileExt.equalsIgnoreCase(".png") ||
		 * fileExt.equalsIgnoreCase(".jpg")) { strBld.append("├─ 📷 "); // indicates a
		 * graphic file, like images. } else if (fileExt.equalsIgnoreCase(".pdf")) {
		 * strBld.append("├─ 📑 "); } else { strBld.append("├─ 📄 "); // other generic
		 * files, like .txt. }
		 */

		strBld.append(file.getName());
		strBld.append("\n");
	}

	/**
	 * 
	 * @param space the left and vertical branch that increases with child folders
	 *              or files.
	 * @return The vertical branches of strings / indentations for data tree.
	 */
	private static String getSpaceString(int space) {
		StringBuilder strBld = new StringBuilder();
		for (int i = 0; i < space; i++) {
			// strBld.append("│ ");
			strBld.append("   ");
		}
		return strBld.toString();
	}

	/**
	 * Reads and prints on the screen the content of a TXT file.
	 * 
	 * @param file         the file to read.
	 * @param printContent if true, prints the content on the screen; if false,
	 *                     doesn't show the content.
	 * @return The content of the TXT file.
	 */
	private static List<String> showTXTContent(File file, boolean printContent) {
		ArrayList<String> contentInArray = null;
		try (BufferedReader fileToRead = new BufferedReader(new FileReader(file))) {
			String line = "";
			contentInArray = new ArrayList<>();

			if (getFileExtension(file.getName()).equalsIgnoreCase(".txt")) {
				if (printContent) {
					System.out.println("");
				}
				while ((line = fileToRead.readLine()) != null) {
					if (printContent) {
						System.out.println(line);
					}
					contentInArray.add(line);
				}
				if (printContent) {
					System.out.println();
				}
				fileToRead.close();
			} else {
				System.out.println(Strings.TXT_HANDLE_ONLY + "\n");
			}
		} catch (EOFException e) {
			// end of file
		} catch (Exception e) {
			System.out.println(Strings.FILE_NOT_ACCESIBLE + "\n");
		}
		return contentInArray;
	}

	/**
	 * Checks if the XML file exists; if true it reads the file, taking into account
	 * the input of tags modifiers.
	 * 
	 * @param fileXML     the XML file.
	 * @param showXMLTags the tag modifiers. If true, shows the XML tags; if false,
	 *                    doesn't show the XML tags.
	 */
	private static void showXMLContent(File fileXML) {
		if (fileXML.exists()) {
			SAXManagement manSAX = new SAXManagement(fileXML);

			manSAX.openXML();

			System.out.println(manSAX.roamSAXandShowContent());
		} else if (!getFileExtension(pathOri).equalsIgnoreCase(".xml")) {
			System.out.println(Strings.XML_HANDLE_ONLY + "\n");
		} else {
			System.out.println(Strings.FILE_NOT_FOUND + "\n");
		}
	}

	/**
	 * Checks if the content of both files it's the same, if so, shows a message; if
	 * it doesn't match, shows each different line and compares it to the other
	 * file.
	 * 
	 * @param fileOne the first file to compare.
	 * @param fileTwo the second file to compare with the first file.
	 */
	private static void compareTXT(File fileOne, File fileTwo) {

		// If one of the files doesn't have a txt extension, throws an error message and
		// returns.
		if (!getFileExtension(fileOne.getName()).equalsIgnoreCase(".txt")
				|| !getFileExtension(fileTwo.getName()).equalsIgnoreCase(".txt")) {
			System.out.println(Strings.TXT_HANDLE_ONLY + "\n");
			return;
		}

		// Compare only and only if two files exist.
		if (fileOne.exists() && fileTwo.exists()) {
			// Original file content Array.
			ArrayList<String> fileOneList = new ArrayList<>();
			fileOneList.addAll(showTXTContent(fileOne, false));
			// Second file content Array to compare with the Original file.
			ArrayList<String> fileTwoList = new ArrayList<>();
			fileTwoList.addAll(showTXTContent(fileTwo, false));

			// Checks if both files are not equal.
			if (!(fileTwoList.equals(fileOneList))) {

				// If one of the files is empty (no lines, content or characters), then throw an
				// error message and return.
				if (fileOneList.isEmpty() || fileTwoList.isEmpty()) {
					System.out.println(Strings.ONE_FILE_HAS_NO_LINES + "\n");
					return;
				}

				System.out.println("");

				int aux = 0;
				while (aux < fileTwoList.size() || aux < fileOneList.size()) {
					// System.out.println(fileOne.getName() + ": " + fileOneList.get(aux));
					// Prints the n file line if the content doesn't match.
					if (!fileOneList.get(aux).equals(fileTwoList.get(aux))) {
						System.out.println(fileOne.getName() + ": " + fileOneList.get(aux));
						System.out.println(fileTwo.getName() + ": " + fileTwoList.get(aux));
						System.out.println("");
					}

					// If one of the files has less lines, then adds empty string to compare.
					if (fileOneList.size() > fileTwoList.size()) {
						fileTwoList.add(" ");
					} else if ((fileTwoList.size() > fileOneList.size())) {
						fileOneList.add(" ");
					}
					aux++;
				}
			} else {
				System.out.println(Strings.FILES_ARE_EQUAL + "\n"); // files are equal.
			}
		} else {
			System.out.println(Strings.ONE_OF_FILES_NOT_FOUND + "\n"); // 1 or more files doesn't exist.
		}
	}

	/**
	 * Asks for the command written in the console and then splits it into orders
	 * and useful data for further execution.
	 */
	public static void askForCommand() {
		try {
			String disk = System.getProperty("user.dir").substring(0, 3); // actual project's drive.
			String commnd = "";
			// Asks for the command if the line is empty.
			do {
				System.out.print(System.getProperty("user.dir") + ">");
				commnd = sc.nextLine();
			} while (commnd.equals(""));

			String[] order = commnd.split("\\s");

			// When the command is a valid order, then checks the parameters and if they are
			// correct, execute the command.
			switch (order[0]) {
			// MOVE COMMAND.
			case "mueve": {
				if (order.length == 3) {
					pathOri = order[1]; // defines source path of the file to move.
					pathDes = order[2]; // defines destination path of the file to move.
					moveFile(new File(pathOri), new File(pathDes));
				} else if (order.length == 2) { // show a message when the command has only 1 path.
					System.out.println(Strings.MISSING_DEST_PATH + "\n");
					askForCommand();
				} else {
					System.out.println(Strings.WRONG_SYNTAX + "\n");
					askForCommand();
				}
				break;
			}
			// COPY COMMAND.
			case "copia": {
				if (order.length == 3) {
					pathOri = order[1]; // defines source path of the file to copy.
					pathDes = order[2]; // defines destination path of the file to copy.
					copyFile(new File(pathOri), new File(pathDes), false);
				} else if (order.length == 2) { // show a message when the command has only 1 path.
					System.out.println(Strings.MISSING_DEST_PATH + "\n");
					askForCommand();
				} else {
					System.out.println(Strings.WRONG_SYNTAX + "\n");
					askForCommand();
				}
				break;
			}
			// DELETE COMMAND.
			case "elimina": {
				if (order.length == 2) {
					pathOri = order[1]; // defines the path of file to delete.
					deleteFile(new File(pathOri), true);
				} else {
					System.out.println(Strings.WRONG_SYNTAX + "\n");
					askForCommand();
				}
				break;
			}
			// LIST DIR COMMAND.
			case "lista": {
				if (order.length == 1) {
					listDirContent(new File(disk));
				} else if (order.length == 2) {
					pathOri = order[1];
					listDirContent(new File(pathOri));
				} else {
					System.out.println(Strings.WRONG_SYNTAX + "\n");
					askForCommand();
				}
				break;
			}
			// LIST TREE COMMAND.
			case "listaArbol": {
				if (order.length == 1) {
					showDirTree(new File(disk));
				} else if (order.length == 2) {
					pathOri = order[1];
					showDirTree(new File(pathOri));
				} else {
					System.out.println(Strings.WRONG_SYNTAX + "\n");
					askForCommand();
				}
				break;
			}
			// COMPARE TXTs COMMAND.
			case "comparaTXT": {
				if (order.length == 3) {
					pathOri = order[1]; // defines source path for further uses.
					pathDes = order[2]; // defines destination path for further uses.
					compareTXT(new File(pathOri), new File(pathDes));
				} else if (order.length == 2) { // show a message when the command has only 1 path.
					System.out.println(Strings.MISSING_DEST_PATH + "\n");
					askForCommand();
				} else {
					System.out.println(Strings.WRONG_SYNTAX + "\n");
					askForCommand();
				}
				break;
			}
			// SHOW TXT COMMAND.
			case "muestraTXT": {
				if (order.length == 2) {
					pathOri = order[1]; // defines the path of TXT file to show.
					showTXTContent(new File(pathOri), true);
				} else {
					System.out.println(Strings.WRONG_SYNTAX + "\n");
					askForCommand();
				}
				break;
			}
			// SHOW XML COMMAND.
			case "muestraXML": {
				if (order.length == 3) {
					pathOri = order[1]; // defines the path of XML file to show.
					if (order[2].equals("/conEtiquetas")) {
						setShowXMLTags(true); // will show XML tags.
					} else if (order[2].equals("/sinEtiquetas")) {
						setShowXMLTags(false); // won't show XML tags.
					} else { // show error message when tags aren't correct.
						System.out.println("\"" + order[2] + "\" " + Strings.WRONG_SYNTAX_MODIFIER + "\n");
						System.out.println(Strings.SHOW_XML_WITH_TAGS);
						System.out.println(Strings.SHOW_XML_WITHOUT_TAGS + "\n");
						askForCommand();
					}
					showXMLContent(new File(pathOri));
				} else {
					System.out.println(Strings.WRONG_SYNTAX + "\n");
					askForCommand();
				}
				break;
			}
			// HELP COMMAND.
			case "ayuda": {
				if (order.length == 1) {
					ConsolePretty.showHelpMessage();
				} else {
					System.out.println(Strings.WRONG_SYNTAX + "\n");
					askForCommand();
				}
				break;
			}
			// EXIT COMMAND.
			case "salir": {
				if (order.length == 1) {
					System.exit(0);
				} else {
					System.out.println(Strings.WRONG_SYNTAX + "\n");
					askForCommand();
				}
				break;
			}
			// IF WRONG COMMAND IS WRITTEN.
			default:
				System.out.println("\"" + order[0] + "\" " + Strings.UNKNOWN_COMMAND);
				System.out.println(Strings.SHOW_HELP + "\n");
				askForCommand();
				break;
			}

			askForCommand(); // after execution, asks again for new command.

		} catch (Exception e) {
			askForCommand();
		}
	}

	/**
	 * Formats a type long number to the desired format.
	 * 
	 * @param pattern the pattern to which the number will be formatted.
	 * @param value   the value to be formatted.
	 * @return The formatted number in String type.
	 */
	public static String customFormat(String pattern, long value) {
		DecimalFormat myFormatter = new DecimalFormat(pattern);
		return myFormatter.format(value);
	}

	/**
	 * Obtains the current date and time and displays it in the following format:
	 * DD.MM.YYYY - HH:MM:SS.MS.
	 */
	public static void getCurrentTime() {
		Calendar cal = Calendar.getInstance();

		// Gets the time
		System.out.print(cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND)
				+ "." + cal.get(Calendar.MILLISECOND));

		System.out.print(" - ");

		// Gets the date
		System.out.print(cal.get(Calendar.DATE) + "." + (cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.YEAR));

		System.out.println("");
	}

	/**
	 * Obtains the file extension through a file name with it's extension. <br>
	 * Example: gets file.txt; returns .txt
	 * 
	 * @param fileWithExtension the name of the file with extension.
	 * @return The extension of the file, dot included.
	 */
	public static String getFileExtension(String fileWithExtension) {
		String extension = "";

		int dotPos = fileWithExtension.lastIndexOf('.');
		if (dotPos > 0) {
			extension = fileWithExtension.substring(dotPos);
		}
		return extension;
	}

	/**
	 * Checks if the file has an extension or a .file leading dot extension, then
	 * replaces the extension and removes it from the string. <br>
	 * <br>
	 * Source: https://www.baeldung.com/java-filename-without-extension
	 * 
	 * @param fileName            the name of the file, with extension.
	 * @param removeAllExtensions if true removes all the extensions from the file;
	 *                            if false removes only the last extension from a
	 *                            file name.
	 * @return The file without extension.
	 * 
	 */
	public static String removeFileExtension(String fileName, boolean removeAllExtensions) {
		if (fileName == null || fileName.isEmpty()) {
			return fileName;
		}
		String extPattern = "(?<!^)[.]" + (removeAllExtensions ? ".*" : "[^.]*$");
		return fileName.replaceAll(extPattern, "");
	}

	//////////////////////////
	// GETTERS AND SETTERS //
	//////////////////////////

	/**
	 * @return The showXMLTags.
	 */
	public static boolean isShowXMLTags() {
		return showXMLTags;
	}

	/**
	 * @param showXMLTags the showXMLTags to set.
	 */
	public static void setShowXMLTags(boolean showXMLTags) {
		Console.showXMLTags = showXMLTags;
	}

}