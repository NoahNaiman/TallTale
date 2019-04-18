import os
import stanfordnlp
from collections import Counter
from langdetect import detect
from subprocess import call

class Lexer:
	
	def __init__(self, pathIn = "../../corpus/pre/", pathOut = "../../corpus/post/", oneShot = True):
		self.textName = ""
		self.pathIn = pathIn 
		self.pathOut = pathOut
		self.bookEnsemble = {}
		self.language = ""
		self.oneShot = oneShot

	def detect_language(self, text):
		"""
		Determines the language of a given text.

		Parameters:
		-----------
		textPath: String
			-A chunk of text to determine the language of

		Return:
		-------
		String
			-An ISO 639-1 language code
		"""
		
		lines = ""
		counter = 0
		
		for line in text:
			line = text.readLine(counter).strip()
			if(line != ""):
				lines += line
			if(counter == 500):
				break
			counter += 1

		text.seek(0)
		language = detect(lines)
		return(language)
