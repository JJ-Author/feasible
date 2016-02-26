package org.aksw.simba.benchmark.log.operations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.aksw.simba.benchmark.Config;
import org.aksw.simba.largerdfbench.util.Selectivity;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
/**
 * Read Virtuoso Queries log
 * @author Saleem
 *
 */
public class VirtuosoLogReader {
	public static HashMap<String, HashMap<String, Set<Long>>> normalizedQueries  = new HashMap<String, HashMap<String, Set<Long>>> ();
	public static void main(String[] args) throws IOException, MalformedQueryException, RepositoryException, QueryEvaluationException {
		//String query = "select+distinct+%3Fn+++where+%7B+%3Fn++%3Chttp%3A%2F%2Fwww.w3.org%2F2004%2F02%2Fskos%2Fcore%23narrower%3E+%3Chttp%3A%2F%2Fdbpedia.org%2Fresource%2FCategory%3AMen%2527s_social_titles%3E+.%7D";
		//query = (java.net.URLDecoder.decode(query, "UTF-8"));
		//		String query = "SELECT count(*) as ?Count  WHERE { ?s ?p ?o}";
		//		Query jenaQuery = QueryFactory.create(query, Syntax.syntaxARQ);
		//		jenaQuery.getProjectVars().clear();
		//		Set<String> vars = new HashSet<String>() ;
		//		vars.add("total");
		//		jenaQuery.addProjectVars(vars);
		//		System.out.println(jenaQuery.toString());
		//String queryLogsDir = "D:/Query Logs/RKBExplorer/";
		String queryLogDir = Config.queryLogDir;
		String endpoint = Config.endpoint;
		String graph = Config.graph; //can be null
		Selectivity.maxRunTime= Config.max_run_time; //query timeout in second. zero or negative means no timout limit
		HashMap<String, Set<String>> queries = getSesameLogQueries(queryLogDir);
		CleanQueryWriter.writeCleanQueriesWithStats(queries,endpoint,graph, "Queries.txt");


	}
	
	/**
	 * add some prefixes and remove some virtuoso specific syntactic sugar
	 * 
	 * @param queryStr the query ot clean
	 * @return cleaned query
	 */
	public static String rewriteQuery(String queryStr) 
	{
		String prefixes = "Prefix bif: <bif:>\nPrefix dbprop: <http://dbpedia.org/property/>\nPrefix dcterms: <http://purl.org/dc/terms/>\nPrefix a: <http://www.w3.org/2005/Atom>\nPrefix address: <http://schemas.talis.com/2005/address/schema#>\nPrefix admin: <http://webns.net/mvcb/>\nPrefix atom: <http://atomowl.org/ontologies/atomrdf#>\nPrefix aws: <http://soap.amazon.com/>\nPrefix b3s: <http://b3s.openlinksw.com/>\nPrefix batch: <http://schemas.google.com/gdata/batch>\nPrefix bibo: <http://purl.org/ontology/bibo/>\nPrefix bugzilla: <http://www.openlinksw.com/schemas/bugzilla#>\nPrefix c: <http://www.w3.org/2002/12/cal/icaltzd#>\nPrefix campsite: <http://www.openlinksw.com/campsites/schema#>\nPrefix cb: <http://www.crunchbase.com/>\nPrefix cc: <http://web.resource.org/cc/>\nPrefix content: <http://purl.org/rss/1.0/modules/content/>\nPrefix cv: <http://purl.org/captsolo/resume-rdf/0.2/cv#>\nPrefix cvbase: <http://purl.org/captsolo/resume-rdf/0.2/base#>\nPrefix dawgt: <http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#>\nPrefix dbc: <http://dbpedia.org/resource/Category:>\nPrefix dbo: <http://dbpedia.org/ontology/>\nPrefix dbp: <http://dbpedia.org/property/>\nPrefix dbr: <http://dbpedia.org/resource/>\nPrefix dc: <http://purl.org/dc/elements/1.1/>\nPrefix dct: <http://purl.org/dc/terms/>\nPrefix digg: <http://digg.com/docs/diggrss/>\nPrefix dul: <http://www.ontologydesignpatterns.org/ont/dul/DUL.owl>\nPrefix ebay: <urn:ebay:apis:eBLBaseComponents>\nPrefix enc: <http://purl.oclc.org/net/rss_2.0/enc#>\nPrefix exif: <http://www.w3.org/2003/12/exif/ns/>\nPrefix fb: <http://api.facebook.com/1.0/>\nPrefix ff: <http://api.friendfeed.com/2008/03>\nPrefix fn: <http://www.w3.org/2005/xpath-functions/#>\nPrefix foaf: <http://xmlns.com/foaf/0.1/>\nPrefix freebase: <http://rdf.freebase.com/ns/>\nPrefix g: <http://base.google.com/ns/1.0>\nPrefix gb: <http://www.openlinksw.com/schemas/google-base#>\nPrefix gd: <http://schemas.google.com/g/2005>\nPrefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\nPrefix geonames: <http://www.geonames.org/ontology#>\nPrefix georss: <http://www.georss.org/georss/>\nPrefix gml: <http://www.opengis.net/gml>\nPrefix go: <http://purl.org/obo/owl/GO#>\nPrefix hlisting: <http://www.openlinksw.com/schemas/hlisting/>\nPrefix hoovers: <http://wwww.hoovers.com/>\nPrefix ical: <http://www.w3.org/2002/12/cal/ical#>\nPrefix ir: <http://web-semantics.org/ns/image-regions>\nPrefix itunes: <http://www.itunes.com/DTDs/Podcast-1.0.dtd>\nPrefix ldp: <http://www.w3.org/ns/ldp#>\nPrefix lgv: <http://linkedgeodata.org/vocabulary#>\nPrefix link: <http://www.xbrl.org/2003/linkbase>\nPrefix lod: <http://lod.openlinksw.com/>\nPrefix math: <http://www.w3.org/2000/10/swap/math#>\nPrefix media: <http://search.yahoo.com/mrss/>\nPrefix mesh: <http://purl.org/commons/record/mesh/>\nPrefix meta: <urn:oasis:names:tc:opendocument:xmlns:meta:1.0>\nPrefix mf: <http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#>\nPrefix mmd: <http://musicbrainz.org/ns/mmd-1.0#>\nPrefix mo: <http://purl.org/ontology/mo/>\nPrefix mql: <http://www.freebase.com/>\nPrefix nci: <http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#>\nPrefix nfo: <http://www.semanticdesktop.org/ontologies/nfo/#>\nPrefix ng: <http://www.openlinksw.com/schemas/ning#>\nPrefix nyt: <http://www.nytimes.com/>\nPrefix oai: <http://www.openarchives.org/OAI/2.0/>\nPrefix oai_dc: <http://www.openarchives.org/OAI/2.0/oai_dc/>\nPrefix obo: <http://www.geneontology.org/formats/oboInOwl#>\nPrefix office: <urn:oasis:names:tc:opendocument:xmlns:office:1.0>\nPrefix ogc: <http://www.opengis.net/>\nPrefix ogcgml: <http://www.opengis.net/ont/gml#>\nPrefix ogcgs: <http://www.opengis.net/ont/geosparql#>\nPrefix ogcgsf: <http://www.opengis.net/def/function/geosparql/>\nPrefix ogcgsr: <http://www.opengis.net/def/rule/geosparql/>\nPrefix ogcsf: <http://www.opengis.net/ont/sf#>\nPrefix oo: <urn:oasis:names:tc:opendocument:xmlns:meta:1.0:>\nPrefix openSearch: <http://a9.com/-/spec/opensearchrss/1.0/>\nPrefix opencyc: <http://sw.opencyc.org/2008/06/10/concept/>\nPrefix opl: <http://www.openlinksw.com/schema/attribution#>\nPrefix opl-gs: <http://www.openlinksw.com/schemas/getsatisfaction/>\nPrefix opl-meetup: <http://www.openlinksw.com/schemas/meetup/>\nPrefix opl-xbrl: <http://www.openlinksw.com/schemas/xbrl/>\nPrefix oplweb: <http://www.openlinksw.com/schemas/oplweb#>\nPrefix ore: <http://www.openarchives.org/ore/terms/>\nPrefix owl: <http://www.w3.org/2002/07/owl#>\nPrefix product: <http://www.buy.com/rss/module/productV2/>\nPrefix protseq: <http://purl.org/science/protein/bysequence/>\nPrefix r: <http://backend.userland.com/rss2>\nPrefix radio: <http://www.radiopop.co.uk/>\nPrefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\nPrefix rdfa: <http://www.w3.org/ns/rdfa#>\nPrefix rdfdf: <http://www.openlinksw.com/virtrdf-data-formats#>\nPrefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\nPrefix rss: <http://purl.org/rss/1.0/>\nPrefix sc: <http://purl.org/science/owl/sciencecommons/>\nPrefix scovo: <http://purl.org/NET/scovo#>\nPrefix sd: <http://www.w3.org/ns/sparql-service-description#>\nPrefix sf: <urn:sobject.enterprise.soap.sforce.com>\nPrefix sioc: <http://rdfs.org/sioc/ns#>\nPrefix sioct: <http://rdfs.org/sioc/types#>\nPrefix skiresort: <http://www.openlinksw.com/ski_resorts/schema#>\nPrefix skos: <http://www.w3.org/2004/02/skos/core#>\nPrefix slash: <http://purl.org/rss/1.0/modules/slash/>\nPrefix sql: <sql:>\nPrefix stock: <http://xbrlontology.com/ontology/finance/stock_market#>\nPrefix twfy: <http://www.openlinksw.com/schemas/twfy#>\nPrefix umbel: <http://umbel.org/umbel#>\nPrefix umbel-ac: <http://umbel.org/umbel/ac/>\nPrefix umbel-rc: <http://umbel.org/umbel/rc/>\nPrefix umbel-sc: <http://umbel.org/umbel/sc/>\nPrefix uniprot: <http://purl.uniprot.org/>\nPrefix units: <http://dbpedia.org/units/>\nPrefix usc: <http://www.rdfabout.com/rdf/schema/uscensus/details/100pct/>\nPrefix v: <http://www.openlinksw.com/xsltext/>\nPrefix vcard: <http://www.w3.org/2001/vcard-rdf/3.0#>\nPrefix vcard2006: <http://www.w3.org/2006/vcard/ns#>\nPrefix vi: <http://www.openlinksw.com/virtuoso/xslt/>\nPrefix virt: <http://www.openlinksw.com/virtuoso/xslt>\nPrefix virtcxml: <http://www.openlinksw.com/schemas/virtcxml#>\nPrefix virtpivot: <http://www.openlinksw.com/schemas/virtpivot#>\nPrefix virtrdf: <http://www.openlinksw.com/schemas/virtrdf#>\nPrefix void: <http://rdfs.org/ns/void#>\nPrefix wb: <http://www.worldbank.org/>\nPrefix wdrs: <http://www.w3.org/2007/05/powder-s#>\nPrefix wf: <http://www.w3.org/2005/01/wf/flow#>\nPrefix wfw: <http://wellformedweb.org/CommentAPI/>\nPrefix wikicompany: <http://dbpedia.openlinksw.com/wikicompany/>\nPrefix wikidata: <http://www.wikidata.org/entity/>\nPrefix xf: <http://www.w3.org/2004/07/xpath-functions>\nPrefix xfn: <http://gmpg.org/xfn/11#>\nPrefix xhtml: <http://www.w3.org/1999/xhtml>\nPrefix xhv: <http://www.w3.org/1999/xhtml/vocab#>\nPrefix xi: <http://www.xbrl.org/2003/instance>\nPrefix xml: <http://www.w3.org/XML/1998/namespace>\nPrefix xn: <http://www.ning.com/atom/1.0>\nPrefix xsd: <http://www.w3.org/2001/XMLSchema#>\nPrefix xsl10: <http://www.w3.org/XSL/Transform/1.0>\nPrefix xsl1999: <http://www.w3.org/1999/XSL/Transform>\nPrefix xslwd: <http://www.w3.org/TR/WD-xsl>\nPrefix y: <urn:yahoo:maps>\nPrefix yago: <http://dbpedia.org/class/yago/>\nPrefix yago-res: <http://mpii.de/yago/resource/>\nPrefix yt: <http://gdata.youtube.com/schemas/2007>\nPrefix zem: <http://s.zemanta.com/ns#>\n";
		queryStr = queryStr.replaceAll("define sql:describe-mode '[^\\']*'", ""); // remove virtuoso query sugar
		return prefixes+queryStr;
	}
	
	/**
	 * Get the set of all distinct queries from the log.
	 * @param queryLogDir Query Log Directory
	 * @return queries A Map of queries. Where keys of the Map are:select, ask, describe, construct
	 * @throws IOException
	 */
	public static HashMap<String, Set<String>> getSesameLogQueries(String queryLogDir) throws IOException  {
		HashMap<String, Set<String>> queries = new HashMap<String, Set<String>>();
		long totalLogQueries = 0 ;
		long parseErrorCount =0;
		Set<String> selectQueries = new HashSet<String> ();
		Set<String> constructQueries = new HashSet<String> ();
		Set<String> askQueries = new HashSet<String> ();
		Set<String> describeQueries = new HashSet<String> ();
		File dir = new File(queryLogDir);
		File[] listOfQueryLogs = dir.listFiles();
		System.out.println("Query Log Parsing in progress...");
		for (File queryLogFile : listOfQueryLogs)
		{
			System.out.println(queryLogFile.getName()+ ": in progress...");
			BufferedReader br = new BufferedReader(new FileReader(queryLogFile.getAbsolutePath()));
			String line;
			while ((line = br.readLine()) != null)
			{	
				//System.out.println(line);
				if(line.contains("query="))
				{
					totalLogQueries++;
					String queryStr = getQuery(line); 
					//System.out.println(queryStr);
					try{
						
						String querySub = rewriteQuery(queryStr);
						
						Query query = QueryFactory.create(querySub);
						query = removeNamedGraphs(query);
						if(query.isDescribeType())
						{
							if (!describeQueries.contains(query.toString()))
								describeQueries.add(query.toString());
						}
						else if (query.isSelectType())
						{
							if (!selectQueries.contains(query.toString()))
								selectQueries.add(query.toString());
						}
						else if (query.isAskType()){
							if (!askQueries.contains(query.toString()))
								askQueries.add(query.toString());
						}
						else if (query.isConstructType()){
							if (!constructQueries.contains(query.toString()))
								constructQueries.add(query.toString());
						}
					}
					catch (Exception ex){parseErrorCount++;System.out.println("Query parse Error in query line "+totalLogQueries);}
				}
			}
			br.close();

		}
		queries.put("select", selectQueries);
		queries.put("construct", constructQueries);
		queries.put("ask", askQueries);
		queries.put("describe", describeQueries);
		System.out.println("Query log parsing completed\nTotal Number of queries (including duplicates): " + totalLogQueries );
		System.out.println("Number of queries with parse errors: " + parseErrorCount);
		System.out.println("Total distinct log queries: "+ (selectQueries.size()+constructQueries.size()+askQueries.size()+describeQueries.size()));
		System.out.println(" SELECT: "+selectQueries.size());
		System.out.println(" CNOSTRUCT: "+constructQueries.size());
		System.out.println(" ASK: "+askQueries.size() );
		System.out.println(" DESCRIBE: "+describeQueries.size() );
		return queries;
	}
	/**
	 * Remove Named Graphs from query
	 * @param query Jena parsed query
	 * @return Jena parsed query with no named graphs
	 */
	public static Query removeNamedGraphs(Query query) {
		query.getGraphURIs().clear();
		return query;
	}
	/**
	 * Parse the log line and get the required SAPARQL query 
	 * @param line Log Line
	 * @return query SPARQL quey
	 */
	private static String getQuery(String line) {
		String parts[] = line.split("query=");
		String query = parts[1];
		
		parts  = query.split("&");
				query = parts[0];
		parts  = query.split(" ");
				query = parts[0];
		/*
		//		String [] parts  = query.split("&results=");
		//		query = parts[0];
		parts  = query.split("&format=");
		query = parts[0];
		parts  = query.split("&timeout=");
		query = parts[0];
		//		parts  = query.split("&maxrows=");
		//		query = parts[0];
		//		parts  = query.split("&_=");
		//		query = parts[0];
		//		parts  = query.split("&Accept=");
		//		query = parts[0];
		parts  = query.split("&graph=");
		query = parts[0];
		//		parts  = query.split("&output=");
		//		query = parts[0];
		//		parts  = query.split("&callback=");
		//		query = parts[0];
		parts  = query.split("&stylesheet");
		query = parts[0];
		parts  = query.split("&default-graph-uri=");
		query = parts[0];
		//	System.out.println(query);
		// query = queryPrts[0].substring(7,queryPrts[0].length());*/
		try{
			query  = java.net.URLDecoder.decode(query, "UTF-8");
		}
		catch (Exception e) {//System.err.println(query+ " "+ e.getMessage());
		}
System.out.println(query);
		return query;
	}

}
