package de.l3s.eumssi.dao;

import java.util.ArrayList;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import de.l3s.eumssi.model.Articles_tan;

public class SolrDBManager_tan {
	HttpSolrServer solr;
	public SolrDBManager_tan() {
		solr = new HttpSolrServer("http://eumssi.cloudapp.net/Solr_EUMSSI/content_items/");
	}
	public ArrayList<Articles_tan> getArticles(String parameter_headline) throws Exception{
		SolrQuery query = new SolrQuery();
		query.setQuery("meta.source.headline:\"computer\" AND (source:*article OR source:article*)");
		
		QueryResponse response = solr.query(query);
		SolrDocumentList results = response.getResults();
		
		ArrayList<Articles_tan> articles_tan = new ArrayList<Articles_tan>();
		Articles_tan article;
		for (SolrDocument solrDocument : results) {
			article = new Articles_tan();
			article.setHeadline(solrDocument.getFieldValue("meta.source.headline").toString());
			article.setMeta_extracted_text_ner_all(solrDocument.getFieldValue("meta.extracted.text.ner.all").toString());
			articles_tan.add(article);
		}
		return articles_tan;
	}
}
