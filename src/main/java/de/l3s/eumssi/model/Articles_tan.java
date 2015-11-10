package de.l3s.eumssi.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Articles_tan {

	@XmlElement
	String headline;
	@XmlElement
	String meta_extracted_text_ner_all;
	
	@XmlElement
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	@XmlElement
	public void setMeta_extracted_text_ner_all(
			String meta_extracted_text_ner_all) {
		this.meta_extracted_text_ner_all = meta_extracted_text_ner_all;
	}
}
