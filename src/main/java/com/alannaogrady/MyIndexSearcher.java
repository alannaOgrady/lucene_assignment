package com.alannaogrady;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MyIndexSearcher {


    private static MyIndexSearcher instance = null;

    private MyIndexSearcher() {
        // Exists only to defeat instantiation.
    }
    public static MyIndexSearcher getInstance() {
        if(instance == null) {
            instance = new MyIndexSearcher();
        }
        return instance;
    }

    public void search(Directory index) throws IOException, ParseException {
        queryFileParser();
        StandardAnalyzer analyzer = new StandardAnalyzer();

        // 2. query
        //String querystr = args.length > 0 ? args[0] : "lucene";
        //hard coding query for now
        String querystr = "what similarity laws must be obeyed when constructing aeroelastic models of heated high speed aircraft .";


        //boolean query!!!!!!!!
        //term query!!!!!!
        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the query.
        Query q = new QueryParser("content", analyzer).parse(querystr);

        // 3. search
        int hitsPerPage = 10;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(new BM25Similarity());
        TopDocs docs = searcher.search(q, hitsPerPage);
        //TopDocs docs = searcher.search(booleanQuery.build(), hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;   //returns an array of retrieved documents

        // 4. display results
        System.out.println("Found " + hits.length + " hits.");
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            Similarity bm25 = searcher.getSimilarity(true);

            System.out.println((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title") + " " + bm25.toString());
        }

        // reader can only be closed when there
        // is no need to access the documents any more.
        reader.close();
    }

    private void queryFileParser() throws IOException {
        File file = new File("../luceneAssignment/src/main/java/com/alannaogrady/cran.qry");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str = "";
        String tag = "";
        String prevTag = "";
        String queryId = "";
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
                //check the previuos tag and add the appeded string as its value
                //checkPrevTag(prevTag, stringBuilder);
                if (prevTag.equals(".I")) {
                    if (!firstRun) {
                        //do stuff with data gathered from previous document
                        System.out.println("Query ID " + queryId);
                        System.out.println("Query " + queryWords);
                        //reinitialise
                        queryId = queryWords = "";
                    }
                    firstRun = false;
                    queryId += " " + appendedString;
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
        //checkPrevTag(prevTag, stringBuilder);
        if (prevTag.equals(".I")) {
            queryId += " " + appendedString;
            stringBuilder.setLength(0);
        }
        else {
            queryWords += " " + appendedString;
            stringBuilder.setLength(0);
        }
        //do stuff with data gathered from previous document
        System.out.println("Query ID " + queryId);
        System.out.println("Query " + queryWords);
        //reinitialise
        queryId = queryWords = "";
    }
}
