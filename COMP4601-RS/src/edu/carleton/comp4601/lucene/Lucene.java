package edu.carleton.comp4601.lucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.misc.*;

public class Lucene {
	private static final String INDEX_DIR =  "/Users/julianclayton/Documents/workspace/COMP4601-A2/Lucene"; 
	private static final String FILES__DIR = FileSystems.getDefault().getPath("").toAbsolutePath().toString() + "/data/reviews";
	private static final String USER_PATH = FileSystems.getDefault().getPath("").toAbsolutePath().toString() + "/data/users";
	private static FSDirectory dir;
	private static IndexWriter	writer;
	
	public static final String FIELD_CONTENTS = "contents";
	public static final String FIELD_PATH = "path";
	
	private static final String DOC_ID = "docId";
	private static final String USER_ID = "userId";
	private static final String CONTENT = "content";
	
	private static ArrayList<String> users;
	
	private static Lucene instance;

	public Lucene (){
			instance = this;	
			users = new ArrayList<String>();
	}
	
	public static  Lucene getInstance() {
		if (instance == null)
			instance = new Lucene();
		return instance;
	}

	//return  all user Ids
	public ArrayList<String> getUsers(){
		File folder = new File(USER_PATH);
		File[] listOfFiles = folder.listFiles();
			
		ArrayList<String> idList = new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++){
			String id = listOfFiles[i].getName();
			id = id.substring(0, id.lastIndexOf('.'));
			idList.add(id);
		}
		return idList;
	}
	
	//return 2000 most popular terms
	public ArrayList<String> getTerms() throws Exception{
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(INDEX_DIR)));
		Comparator<org.apache.lucene.misc.TermStats> comparator = new DocFreqComparator();
		
		ArrayList<String> termsArr = new ArrayList<String>();
		
		org.apache.lucene.misc.TermStats[] commonTerms = HighFreqTerms.getHighFreqTerms(reader, 2000, CONTENT, comparator);
		for (org.apache.lucene.misc.TermStats commonTerm : commonTerms) {
			termsArr.add(commonTerm.termtext.utf8ToString());
		}
		return termsArr;
	}

	//index review contents and user ids, this has to be run before query or getTerms
	public void indexLucene(){
	try	{	

		CharArraySet stopSet = getStopSet();
        
		dir	=	FSDirectory.open(new File(INDEX_DIR));	
		Analyzer	analyzer	=	new	StandardAnalyzer(Version.LUCENE_45,stopSet);	
		IndexWriterConfig iwc	=	new	IndexWriterConfig(Version.LUCENE_45, analyzer);	
		iwc.setOpenMode(OpenMode.CREATE);	
		writer = new IndexWriter(dir, iwc);	
		
		File dir = new File(FILES__DIR);
		File[] files = dir.listFiles();
		for (File file : files) {
			indexADoc(file);
		}
		
	} catch	(Exception	e)	{	
		e.printStackTrace();	
		}	finally	{	
			try	{	
			 	if	(writer	!=	null)	{	
					writer.close();	
			 	}
			 	if	(dir    !=	null)	{
					dir.close();	
			 	}
			 } catch (IOException	e)	{	
					e.printStackTrace();	
			 }
		}	
	}
	
	private CharArraySet getStopSet(){
		String[] stopWords = new String[]{ "also", "without", "make", "isn't", "few", "can't", "much", "who", "do", "can", "br", "i", "p", 
				"userid", "meta", "html"};	
		
		
		CharArraySet stopSet = CharArraySet.copy(Version.LUCENE_45, StandardAnalyzer.STOP_WORDS_SET);
		for (String s : stopWords){
			stopSet.add(s);
		}
		
		return stopSet;
	}
	
	public String queryPage(String movieId) {
		String contents = null;
		try {
			String path =  "/Users/julianclayton/Documents/workspace/COMP4601-A2/COMP4601-RS/data/pages/" + movieId + ".html";
			contents = new String(Files.readAllBytes(Paths.get(path))); 		
		}catch (IOException e) {
			e.printStackTrace();
		}
		return contents;	
	}

	
	private void indexADoc(File file) throws IOException	{	
		try{
			Document lucDoc	=	new	Document();	
			String id = file.getName();
			System.out.println(id);
			String userId = id.substring(0, id.lastIndexOf('-'));
			String movieId = id.substring(id.lastIndexOf('-'), id.lastIndexOf('-'));
			String docId = id.substring(0, id.lastIndexOf('.'));
			Reader reader = new FileReader(file);
			users.add(userId);
			
			lucDoc.add(new	StringField(DOC_ID, docId, Field.Store.YES));
			lucDoc.add(new	TextField(USER_ID, userId, Field.Store.YES));
			lucDoc.add(new Field(CONTENT, reader));	

			System.out.println("Indexing file: " + file.getName());
		
			writer.addDocument(lucDoc);
			
		}catch(Exception e){
			System.out.println("-------Error:  "+e);
			e.printStackTrace();
		}
	}
	

	//query a user's reviews for a term
	public Float query(String user, String searchStr)	{	
		try	{	
			
			dir = FSDirectory.open(new File(INDEX_DIR));	
			@SuppressWarnings("deprecation")
			IndexReader reader = IndexReader.open(dir);
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_45);
			QueryParser uParser = new QueryParser(Version.LUCENE_45, USER_ID, analyzer);
			QueryParser cParser = new QueryParser(Version.LUCENE_45, CONTENT, analyzer);

			BooleanQuery booleanQuery = new BooleanQuery();
		
			Query userQuery = uParser.parse(user);
			Query termQuery = cParser.parse(searchStr);
			booleanQuery.add(userQuery, Occur.MUST);
			booleanQuery.add(termQuery, Occur.MUST);
			
			TopDocs results = searcher.search(booleanQuery, 100);
			
			ScoreDoc[] hits = results.scoreDocs;		
			Float total = 0.0f;
			
			int length = hits.length;
			
			for	(ScoreDoc hit :	hits)	{	
			 	Document indexDoc = searcher.doc(hit.doc);	
			 	String id = indexDoc.get(DOC_ID);
			 	if	(id	!=	null) {	
						total += hit.score;
					}		
		 	}
			if	(reader	!=	null)	{
				reader.close();	
		 	}
			if	(dir	!=	null)	{
				dir.close();	
		 	}
			if (length!=0){
			
				return total/length;
			} 
			return 0.0f;
			
		} catch (Exception e)	{	
			e.printStackTrace();
		}	
		return 0.0f;
	}

	public final class DocFreqComparator implements Comparator<org.apache.lucene.misc.TermStats> {
	    
	    public int compare(org.apache.lucene.misc.TermStats a, org.apache.lucene.misc.TermStats b) {
	      int res = Long.compare(a.totalTermFreq, b.totalTermFreq);
	      return res;
	    }
	}
	public static void main(String[] args) {
		Lucene luc = Lucene.getInstance();
	
		//luc.indexLucene();
	}
}
