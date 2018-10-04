package com.alannaogrady;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.InputStreamReader;
//import java.nio.charset.StandardCharsets;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
//import org.apache.lucene.store.RAMDirectory;

public class MyIndexWriter 
{
    private static MyIndexWriter instance = null;
    private String indexPath = "../luceneAssignment/indexes/";

    private MyIndexWriter() {
      // Exists only to defeat instantiation.
    }
    public static MyIndexWriter getInstance() {
      if(instance == null) {
         instance = new MyIndexWriter();
      }
      return instance;
    }


    public Directory index() throws IOException, ParseException {
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        readTheFile();
        StandardAnalyzer analyzer = new StandardAnalyzer();

        // 1. create the index - not saved to disc.. just temporary
        //Directory index = new RAMDirectory();
        Directory index = FSDirectory.open(Paths.get(indexPath));

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = new IndexWriter(index, config);
        addDoc(w, "Lucene in Action", "193398817");
        addDoc(w, "Lucene for Dummies", "55320055Z");
        addDoc(w, "Managing Gigabytes", "55063554A");
        addDoc(w, "The Art of Computer Science", "9900333X");
        w.close();

        return index;

    }

    private void readTheFile() throws FileNotFoundException, IOException {
        File file = new File("../luceneAssignment/src/main/java/com/alannaogrady/fruit.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str = "";
        String tag = "";
        String prevTag = "";
        String appendedString = "";
        StringBuilder stringBuilder = new StringBuilder();
        while ((str = br.readLine()) != null){
            tag = str.length() < 2 ? str : str.substring(0, 2);
            
            //does beginning of line start with a tag
            if (tag.equals(".I") || tag.equals(".T") || tag.equals(".A") || tag.equals(".B") || tag.equals(".W")) {
                //remove tage from the rest of the string
                if (str.length() > 2) {
                    str = str.substring(2);
                }
                //check what the previous tag was as the string we have been collecting belongs to this
                appendedString = stringBuilder.toString();
                if (prevTag.equals(".I")) {
                    //put subsequent info you have gathered into identity field
                    System.out.println("ID " + appendedString);
                    stringBuilder.setLength(0);
                }
                else if (prevTag.equals(".T")) {
                    //put subsequent info you have gathered into title field
                    System.out.println("Title " + appendedString);
                    stringBuilder.setLength(0);
                }
                else if (prevTag.equals(".A")) {
                    //put subsequent info you have gathered into author field
                    System.out.println("Author " + appendedString);
                    stringBuilder.setLength(0);
                }
                else if (prevTag.equals(".B")) {
                    //put subsequent info you have gathered into bibliography field
                    System.out.println("Biblio " + appendedString);
                    stringBuilder.setLength(0);
                }
                else if (prevTag.equals(".W")) {
                    //put into content field
                    System.out.println("Content " + appendedString);
                    stringBuilder.setLength(0);
                }
                else {
                    //must check the rest of the line that the tag is on
                    stringBuilder = stringBuilder.append(str);
                }

                //update prevTag
                prevTag = tag;
            }
            else {
                //not the tag, the value of the tag
                stringBuilder = stringBuilder.append(str);
            }
            
        }
        //must deal with last section
        checkPrevTag(prevTag, stringBuilder, stringBuilder.toString());
    }

    private void checkPrevTag(String prevTag, StringBuilder stringBuilder, String appendedString) {
        if (prevTag.equals(".I")) {
                    //put subsequent info you have gathered into identity field
                    System.out.println("ID " + appendedString);
                    stringBuilder.setLength(0);
                }
                else if (prevTag.equals(".T")) {
                    //put subsequent info you have gathered into title field
                    System.out.println("Title " + appendedString);
                    stringBuilder.setLength(0);
                }
                else if (prevTag.equals(".A")) {
                    //put subsequent info you have gathered into author field
                    System.out.println("Author " + appendedString);
                    stringBuilder.setLength(0);
                }
                else if (prevTag.equals(".B")) {
                    //put subsequent info you have gathered into bibliography field
                    System.out.println("Biblio " + appendedString);
                    stringBuilder.setLength(0);
                }
                else if (prevTag.equals(".W")) {
                    //put into content field
                    System.out.println("Content " + appendedString);
                    stringBuilder.setLength(0);
                }
    }

   

    private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));

        // use a string field for isbn because we don't want it tokenized
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        w.addDocument(doc);
    }
}