package com.alannaogrady;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;


import java.io.*;
import java.util.ArrayList;

public class MyIndexSearcher {


    private static MyIndexSearcher instance = null;
    private ArrayList<String> queries = new ArrayList<>();
    public int num = 0;

    private MyIndexSearcher() {
        // Exists only to defeat instantiation.
    }
    public static MyIndexSearcher getInstance() {
        if(instance == null) {
            instance = new MyIndexSearcher();
        }
        return instance;
    }

    public void search(IndexWriterConfig iwConfig, Directory index, BufferedWriter writer, Analyzer analyzer) throws IOException, ParseException, QueryNodeException {

        IndexReader reader = DirectoryReader.open(index);

        //query
        for (int j = 0; j < queries.size(); j++) {

            MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"content", "title"}, analyzer);
            String querystr = parser.escape(queries.get(j));
            Query q = parser.parse(querystr);


            IndexSearcher searcher = new IndexSearcher(reader);
            searcher.setSimilarity(iwConfig.getSimilarity());

            //to get all retrieved docs
            TotalHitCountCollector collector = new TotalHitCountCollector();
            searcher.search(q, collector);
            //use 1 if there is 0 hits
            TopDocs docs = searcher.search(q, Math.max(1, collector.getTotalHits()));
            ScoreDoc[] hits = docs.scoreDocs;   //returns an array of retrieved documents
            num++;


            // 4. display results
            System.out.println("Found " + hits.length + " hits.\t" + iwConfig.getSimilarity().toString());
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);

                float score = hits[i].score;


                System.out.println("Query ID: " + (i + 1) + ". "  + "\tDocument ID: " + d.get("id") + "\t Score: " + score);
                //write to a results file
                String results = (j+1) + " Q0" + d.get("id") + " " + (i + 1) + " " + score + " exp\n";
                writer.write(results);

            }
        }

        System.out.println("num " + num);
        // reader can only be closed when there
        // is no need to access the documents any more.
        reader.close();
    }


    public void queryFileParser() throws IOException {
        File file = new File("../lucene_assignment/src/main/java/com/alannaogrady/cran.qry");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str = "";
        String tag = "";
        String prevTag = "";
        String queryWords = "";
        Boolean firstRun = true;
        String appendedString = "";


        StringBuilder stringBuilder = new StringBuilder();
        while ((str = br.readLine()) != null){
            tag = str.length() < 2 ? str : str.substring(0, 2);

            appendedString = stringBuilder.toString();
            //does beginning of line start with a tag
            if (tag.equals(".I") || tag.equals(".W")) {

                //remove tag from the rest of the string
                str = str.substring(2);
                //check the previous tag and add the appended string as its value
                //checkPrevTag(prevTag, stringBuilder);
                if (prevTag.equals(".I")) {
                    if (!firstRun) {
                        //do stuff with data gathered from previous document
                        queries.add(queryWords);
                        //reinitialise
                        queryWords = "";
                    }
                    firstRun = false;
                    appendedString = appendedString.replaceAll(" ","");
                    stringBuilder.setLength(0);
                }
                else if (prevTag.equals(".W")) {
                    queryWords += " " + appendedString;
                    stringBuilder.setLength(0);
                }

                //must append the rest of the line that the tag is on
                stringBuilder = stringBuilder.append(str + " ");

                //update prevTag
                prevTag = tag;
            }
            else {
                //not the tag, therefore must append this line to the string (value of tag)
                stringBuilder = stringBuilder.append(str + " ");
            }

            //no more documents => must deal with data from previous (last) document


        }
        //must deal with last section
        //must check what the last tag was
        if (prevTag.equals(".I")) {
            appendedString = appendedString.replaceAll(" ","");
            stringBuilder.setLength(0);
        }
        else {
            queryWords += " " + appendedString;
            stringBuilder.setLength(0);
        }
        //do stuff with data gathered from previous document
        queries.add(queryWords);
        //reinitialise
        queryWords = "";
    }

}
