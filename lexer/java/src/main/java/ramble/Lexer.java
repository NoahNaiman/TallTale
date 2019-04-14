package ramble;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ie.util.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.io.IOUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Path;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Scanner;

import org.apache.tika.exception.TikaException;
import org.apache.tika.language.LanguageIdentifier;

import org.xml.sax.SAXException;

/**
 * <h1>Lexer Class</h1>
 *	Takes in one or several files
 *	representing books. These files
 *	are then parsed appropriately
 *	before being tagged by speaker
 *	and output into a new text file.<br>
 *	Takes heavy advantage of the
 *	Stanford CoreNLP Natural Language
 *	Software, and we would like to
 *	thank the Stanford team for their
 *	excellent work.
 *	@see stanfordnlp.github.io/CoreNLP/index.html
 *
 *	@author Noah Naiman
 *	@author Jorge Nario
 *	@author Josh Bassin
 *	@version 0.0.1
 *	@since 2018-07-07
 */

public class Lexer{

	/****************************
     *			Fields			*
     ****************************/

	private ArrayList<CoreDocument> book;
	private Hashtable<String, String> bookEnsemble;
	private StanfordCoreNLP nlp;
	private String bookName;
	private String filePathIn;
	private String filePathOut;
	private String language;

	/****************************
     *		Initializer(s)		*
     ****************************/

	public Lexer(){
		this.filePathIn = "../corpus/preprocessed/";
		this.filePathOut = "../corpus/postprocessed/";
		book = new ArrayList<>();
		bookEnsemble = new Hashtable<>();
	}

	/****************************
     *			Methods			*
     ****************************/

	/**
	 *	Runs all necessary parsing functions in
	 *	order as:<br>
	 *	1. Set what book is being parsed.<br>
	 *	2. Set all NLP parsing options including
	 *	what language book will be parsed in.<br>
	 *	3. Fully parse book using StanfordNLP
	 *	and all options previously set.
	 *		--NOTE: This may take a while, ~30-60 minutes.<br>
	 *	4. Outputs all quotes to a postprocessed file 
	 *	in the format:
	 *	<\ramble character="SPEAKER_NAME">"Quote spoken by character"<\/ramble><br>
	 *	5. Fills in all other text surrounding quotes.
	 * @return
	 *	Returns true upon completion
	 * @throws IOException
	 *	Throws exception if an error occurs while
	 *	reading or writing pre or post-parsed text.
	 *	Possible points of error are:<br>
	 *		-Reading in text of book
	 *		-Reading in NLP language properties
	 *		-Outputting tagged text
	 */
	public boolean parseBook() throws IOException{
		String bookName;
		Scanner reader = new Scanner(System.in);
		System.out.println("Use default path of '../corpus/preprocessed/'? [y/n]");
		String useDefaultPath = reader.nextLine().toLowerCase();
		if(useDefaultPath.equals("y") || useDefaultPath.equals("yes")){
			System.out.println("Please enter file name [i.e. JungleBook.txt]: ");
			bookName = reader.nextLine();
		}
		else{
			System.out.println("Using non-default path. Please enter new file path: ");
			File file = new File(reader.nextLine());
			Path path = file.toPath();
			int pathElements = path.getNameCount();
			bookName = path.getName(pathElements).toString;
			System.out.println("Book name is: " + bookName);

		}
		setBook(bookName, fileType);
		for(CoreDocument chapter: book){
			nlp.annotate(chapter);
			tagText(chapter);
		}
		return true;
	}

	/**
	 *	Sets the name of the book to be be processed
	 *	followed by a call to set all NLP options.
	 * @param bookName
	 *	A String representing the name of the book to
	 *	be parsed.
	 * @param fileType
	 *	A String representing the type of file to
	 *	be parsed.
	 */
	private void setBook(String bookName, String fileType){
		this.bookName = bookName;
		filePathIn = filePathIn + bookName + fileType;
		filePathOut = filePathOut + bookName + fileType;

		try{
			File textFile = new File(filePathIn);
			BufferedReader reader = new BufferedReader(new FileReader(textFile));
			String plaintext = new String();

			String line;
			while((line = reader.readLine()) != null){
				if(line.contains("0xRAMBLE")){
					book.add(new CoreDocument(plaintext));
					plaintext = "";
				}
				else{
					plaintext += (line+'\n');
				}
			}
			book.add(new CoreDocument(plaintext));

			setNLP(plaintext);
		}
		catch(IOException e){
			System.out.println("ERROR IO exception caught: " + e);
		}
	}

	/**
	 *	Sets all NLP processing options including
	 *	language.
	 * @param plaintext
	 *	A String representing the plaintext of
	 * 	the book to be processed
	 */
	private void setNLP(String plaintext){
		Properties nlpProperties = new Properties();
		nlpProperties.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, coref, quote");
		nlpProperties.setProperty("coref.algorithm", "neural");

		language = new LanguageIdentifier(plaintext).getLanguage();
		try{
			if(!language.equals("en")){
				switch(language){
					case "ar":
						nlpProperties.load(IOUtils.readerFromString("StanfordCoreNLP-arabic.properties"));
						break;
					case "zh":
						nlpProperties.load(IOUtils.readerFromString("StanfordCoreNLP-chinese.properties"));
						break;
					case "fr":
						nlpProperties.load(IOUtils.readerFromString("StanfordCoreNLP-french.properties"));
						break;
					case "de":
						nlpProperties.load(IOUtils.readerFromString("StanfordCoreNLP-german.properties"));
						break;
					case "es":
						nlpProperties.load(IOUtils.readerFromString("StanfordCoreNLP-spanish.properties"));
						break;	
					default:
						System.out.println("ERROR language " + language + " is not currently handled.\nExiting program to prevent loss and/or waste of resources.");
						System.exit(1);
				}
			}

			nlp = new StanfordCoreNLP(nlpProperties);
		}
		catch(IOException e){
			System.out.println("ERROR IO exception caught: " + e);
		}
	}

	/**
	 *	Tags all text in novel in one of two ways: in the form of:
	 *	1. <\ramble character="Narrator">General narration text block<\/ramble>
	 *	2. <\ramble character="SPEAKER_NAME">"Quote spoken by character"<\/ramble>
	 *	All tagged text is written to filePathOut
	 *	@param chapter
	 *	 A fully annotated CoreDocument including tokenization, quotations,
	 *	 and coreferences.
	 */
	private void tagText(CoreDocument chapter){
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePathOut, true));
			String openTag = "<ramble character=\"";
			String closeTag = "</ramble>";

			boolean inQuote = false;
			int quoteNumber = 0;

			List<CoreLabel> words = chapter.tokens();
			List<CoreQuote> quotes = chapter.quotes();
			for(int i = 0; i < words.size(); i++){
				String token = words.get(i).word();

				if(i == 0 && !token.equals("``")){
					writer.write(openTag + "Narrator\">");
				}
				if(inQuote && token.equals("''")){
					writer.write(openTag + "Narrator\">");
					inQuote = false;
				}
				else if(token.equals("``") && quoteNumber != quotes.size()){
					CoreQuote quote = quotes.get(quoteNumber);
					writer.write(closeTag + " " + openTag + quote.speaker().orElse("UNKOWN") + "\">" + quote + closeTag);
					quoteNumber++;
					inQuote = true;
				}
				else if(!inQuote){
					writer.write(token + " ");
				}
			}

			if(!words.get(words.size()-1).word().equals("''")){
				writer.write(closeTag);
			}

			writer.close();
		}
		catch(IOException e){
			System.out.println("ERROR IO exception caught: " + e);
		}
	}

}
