import os
import stanfordnlp
from stanfordnlp.server import CoreNLPClient
from collections import Counter
from langdetect import detect
from subprocess import call

class Lexer:
	
	def __init__(self, pathIn = "../../corpus/pre/", pathOut = "../../corpus/post/"):
		self.textName = ""
		self.pathIn = pathIn 
		self.pathOut = pathOut
		self.bookEnsemble = {}

	def read(self):
		with os.scandir(self.pathIn) as texts:
			for text in texts:
				print(text.name)
				if(text.is_file()):
					corpus = open(text, "r")
					
					language = self.detect_language(corpus)
					try:
						nlp = stanfordnlp.Pipeline(lang=language)
					except:
						stanfordnlp.download(language)
						nlp = stanfordnlp.Pipeline(lang=language, use_gpu=True)
					with CoreNLPClient(annotators=['tokenize','ssplit','pos','lemma','ner', 'parse', 'depparse','coref'], timeout=30000, memory='16G') as client:
						parsedDoc = client.annotate(corpus.read())
						print(parsedDoc.sentences[0].print_dependencies())


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
			lines += line
			counter += 1
			if(counter == 500):
				break

		text.seek(0)
		language = detect(lines)
		return(language)

testLex = Lexer()
testLex.read()
