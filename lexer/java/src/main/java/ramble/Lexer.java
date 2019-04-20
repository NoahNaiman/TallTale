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
 *	Takes in one or several text files
 *. These are then parsed appropriately
 *	before being tagged by speaker
 *	and output into a new text file.<br>
 *	Takes heavy advantage of the
 *	Stanford CoreNLP Natural Language
 *	Software, and we would like to
 *	thank the Stanford team for their
 *	excellent work.
 *	@see stanfordnlp.github.io/CoreNLP
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
	private Hashtable<String, String> ensemble;
	private StanfordCoreNLP nlp;
	private String textName;
	private String filePathIn;
	private String filePathOut;
	private String language;

	/****************************
     *		Initializer(s)		*
     ****************************/

	public Lexer(){
		this.filePathIn = "../../corpus/pre/";
		this.filePathOut = "../../corpus/post/";
		book = new ArrayList<>();
		ensemble = new Hashtable<>();
	}

	public Lexer(String inPath, String outPath){
		this.filePathIn = inPath;
		this.filePathOut = outPath;
		book = new ArrayList<>();
		ensemble = new Hashtable<>();
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
	 *	<speaker character="SPEAKER_NAME">"Quote spoken by character"</speaker><br>
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
	public boolean parse(String fileName) throws IOException{
		setBook(fileName);
		for(CoreDocument chapter: book){
			nlp.annotate(chapter);
			tagText(chapter);
		}
		return true;
	}

	/**
	 *	Sets the name of the text to be be processed
	 * @param textName
	 *	A String representing the name of a text to
	 *	be parsed.
	 */
	private void setBook(String fileName){
		System.out.println("Setting book!");
		this.textName = fileName.substring(0, fileName.lastIndexOf('.'));
		filePathOut = filePathOut + "/marked/" + textName + ".txt";

		try{
			File textFile = new File(filePathIn + fileName);
			BufferedReader reader = new BufferedReader(new FileReader(textFile));
			StringBuilder plaintext = new StringBuilder("");

			String line;
			while((line = reader.readLine()) != null){
				if(line.contains("<===========0xC4A87E2===========>")){
					book.add(new CoreDocument(plaintext.toString()));
					plaintext.setLength(0);
				}
				else{
					plaintext.append(line+'\n');
				}
			}

			String plaintextStr = plaintext.toString();

			book.add(new CoreDocument(plaintextStr));
			setNLP(plaintextStr);
		}
		catch(IOException e){
			System.out.println("ERROR: " + e);
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
						System.out.println("ERROR: language " + language + " is not currently handled.\nExiting program to prevent loss and waste of resources.");
						System.exit(1);
				}
			}

			nlp = new StanfordCoreNLP(nlpProperties);
		}
		catch(IOException e){
			System.out.println("ERROR: " + e);
		}
	}

	/**
	 *	Tags all text in novel in one of two ways: in the form of:
	 *	1. <speaker character="Narrator">General narration text block</speaker>
	 *	2. <speaker character="[SPEAKER_NAME]">"Quote spoken by character"</speaker>
	 *	All tagged text is written to filePathOut
	 *	@param chapter
	 *	 A fully annotated CoreDocument including tokenization, quotations,
	 *	 and coreferences.
	 */
	private void tagText(CoreDocument chapter){
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePathOut, true));
			String openTag = "<speaker=\"";
			String closeTag = "</speaker>";

			boolean inQuote = false;
			int quoteNumber = 0;

			List<CoreLabel> words = chapter.tokens();
			List<CoreQuote> quotes = chapter.quotes();

			String token = words.get(0).word();

			/* One time check to see if text begins with quote or narration */
			if(!token.equals("``")){
				writer.write(openTag + "Narrator\">");
			}

			for(int i = 0; i < words.size(); i++){

				token = words.get(i).word();

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
					writer.write(token " ");
				}
			}

			if(!words.get(words.size()-1).word().equals("''")){
				writer.write(closeTag);
			}

			writer.close();
		}
		catch(IOException e){
			System.out.println("ERROR: " + e);
		}
	}

}
