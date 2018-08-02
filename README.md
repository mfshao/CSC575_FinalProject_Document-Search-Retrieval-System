# CSC575_FinalProject_Document-Search-Retrieval-System
A Java-based search/retrieval system

0. Prerequisites\
	This application has been tested on a computer running Microsoft Windows 10 and JavaSE 8 (version 1.8.0_161).
	
1. Install\
	Please make sure a correct version (8 or above) of Java has been installed.\
	There is no actual "installation" required.
	
2. Execute\
	To execute the application, go to the "\binary files" folder and double click "CSC575_FinalProject.jar".
  
3. Project description\
This is a Java-based search/retrieval system. The backend system was implemented using JavaSE 8. The frontend graphical user interface (GUI) was implemented using JavaFX and is compatible with high DPI displays. The system allows user to read in a collection of documents, in which each document has a unique document number (docno) and a series of sentences (tags) as content, and then indexing each document based on algorithms chosen by the user. Then let user to perform queries on the indexed documents and retrieve highly correlated results by using a user-specified similarity calculation algorithm. User can also export calculated indices into XML file and save on local file system, as well as import them back for further uses. Detailed discussion about functionalities will be provided in the following section.\

4. Test dataset\
The dataset included in the deliverables is the Web Answer Passages (WebAP) Dataset, acquired from https://ciir.cs.umass.edu/downloads/WebAP/. It contains a XML structured files with 150 queries and a file contains 6,399 documents. For demonstration purposes, in this project, only the XML file which has 150 queries in it was used as test dataset, and each query here was treated as a document.\
The dataset is called “gov2.xml” and was placed under the \data folder. Each document element in the XML file has the following structure as illustrated in Figure 1: the outmost <query> tag defines a query document. Each query document has two parts defined by a <title> tag and a <desc> tag. In each of these two parts there are another two areas: <docno> contains a unique identifier of the document query, and <tag> field contains the actual context. In this implementation, contents in the two <tag> fields are combined together to represent the content of the document.
