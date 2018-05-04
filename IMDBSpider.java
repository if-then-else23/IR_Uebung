import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.SocketTimeoutException;
import java.text.Normalizer;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonWriter;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class IMDBSpider {

	public IMDBSpider() {
	}

	/**
	 * For each title in file movieListJSON:
	 *
	 * <pre>
	 * You should:
	 * - First, read a list of 500 movie titles from the JSON file in 'movieListJSON'.
	 *
	 * - Secondly, for each movie title, perform a web search on IMDB and retrieve
	 * movie’s URL: http://akas.imdb.com/find?q=<MOVIE>&s=tt&ttype=ft
	 *
	 * - Thirdly, for each movie, extract metadata (actors, budget, description)
	 * from movie’s URL and store to a JSON file in directory 'outputDir':
	 *    http://www.imdb.com/title/tt0499549/?ref_=fn_al_tt_1 for Avatar - store
	 * </pre>
	 *
	 * @param inputFileName
	 *            JSON file containing movie titles
	 * @param outputDir
	 *            output directory for JSON files with metadata of movies.
	 * @throws IOException
	 */
	public void fetchIMDBMovies(String movieListJSON, String outputDir) throws IOException {

		JsonArray movieList;

		try (InputStreamReader stream = new InputStreamReader(new FileInputStream(new File(movieListJSON)), "UTF-8")) {
			try (JsonReader reader = Json.createReader(stream)) {

				movieList = reader.readArray();
			}
		}

		for (int i = 0; i < movieList.size(); i++) {
			
			JsonString name = movieList.getJsonObject(i).getJsonString("movie_name");
			String movName = name.toString();
			JsonArray movie = Json.createArrayBuilder().build();
			int socket = 0;
			int http = 0;
			
			while(socket < 2 && http < 2){					//keep track of exceptions: if any of the exceptions occured twice, don't try again -> movie stays empty JsonArray
				try {
					movie = titleToJsonArray(movName);
					socket = 2;
					http = 2;
				} catch (SocketTimeoutException ste) {
						System.out.println("SocketTimeoutException");
						socket = socket +1;
						
				} catch (HttpStatusException hse) {			//in case of HttpStatusException, try again with normalized movie name (without accents)
					System.out.println("HttpStatusException");
					movName = Normalizer.normalize(movName, Normalizer.Form.NFD);
					movName = movName.replaceAll("\\p{M}", "");
					http = http +1;
				}
			}

		
			try (OutputStreamWriter stream = new OutputStreamWriter(new FileOutputStream(new File(outputDir + "\\" + i+".json")), "UTF-8")) {
				try (JsonWriter writer = Json.createWriter(stream)) {

					writer.writeArray(movie);

				}
			}

		}

	}
	

	private static JsonArray titleToJsonArray(String title) throws IOException, SocketTimeoutException, HttpStatusException {

		JsonArrayBuilder movieA = Json.createArrayBuilder();
		JsonObjectBuilder movieOB = Json.createObjectBuilder();
		String _url;
		
		Document searchResults = Jsoup.connect("http://akas.imdb.com/find?q=" + title + "&s=tt&ttype=ft").timeout(10*1000).get();
		
		try {
			Element resultCell = searchResults.select(".findList").select("td.result_text").first();
			_url = resultCell.select("a").first().attr("abs:href");
			movieOB.add("url", _url);
		
		} catch (NullPointerException e) {
//			System.out.println(title);
			JsonArray movie = movieA.build();
			return movie; 
		}

		Document movieSite = Jsoup.connect(_url).timeout(10*1000).get(); // open movie site via extracted direct link

		
		
		// TITLE, YEAR
		String _title = "";
		String _year = "";
		try {
			String fullTitle = movieSite.title();
			_title = fullTitle.substring(0, fullTitle.length() - 14);
			_year = fullTitle.substring(fullTitle.length() - 12, fullTitle.length() - 8);	
		} catch (NullPointerException e) {
//			System.out.println(title + "  title/year");
		}
		movieOB.add("title", _title);
		movieOB.add("year", _year);
		
		
		
		// GENRE_LIST
		JsonArrayBuilder _genres = Json.createArrayBuilder();
		try {
			Elements genres = movieSite.select("div[itemprop='genre']").select("a"); 
			for (Element el : genres) {
				_genres.add(el.text()); // extract link/genre names
			}
		} catch (NullPointerException e) {
//			System.out.println(title + "  genreList");
		}
		movieOB.add("genreList", _genres.build());

		
		
		// COUNTRY_LIST		
		JsonArrayBuilder _countries = Json.createArrayBuilder();
		try {
			Elements countries = movieSite.select("div.txt-block:contains(Country:)").select("a");
			for (Element el : countries) {
				_countries.add(el.text()); // extract link/country names
			}
		} catch (NullPointerException e) {
//			System.out.println(title + "  countryList");
		}
		movieOB.add("countryList", _countries.build());

		
		
		// DESCRIPTION
		String _description = "";
		try {
			_description = movieSite.select("div.inline[itemprop=description]").first().text();
		} catch (NullPointerException e) {
			//System.out.println(title + "  description");
		}
		movieOB.add("description", _description);

		
		
		// BUDGET
		String _budget = "";
		try {
			List<TextNode> bud = movieSite.select("div.txt-block:contains(Budget:)").first().textNodes(); 
			_budget = bud.get(1).toString().trim();
		} catch (NullPointerException e) {
			//System.out.println(title + "  budget");
		}
		movieOB.add("budget", _budget);

		
		
		// GROSS
		String _gross= "";
		try {
			List<TextNode> gro = movieSite.select("div.txt-block:contains(Gross:)").first().textNodes();
			_gross = gro.get(1).toString().trim();
		} catch (NullPointerException e) {
			//System.outprintln(title + "  gross");
		}
		movieOB.add("gross", _gross);

		
		
		// RATING_VALUE gives value*10
		String _ratingValue = "";
		try {
			_ratingValue = movieSite.select(".ratingValue").select("[itemprop=ratingValue]").first().text();
		} catch (NullPointerException e) {
			//System.out.println(title + "  ratingValue");
		}
		movieOB.add("ratingValue", _ratingValue);

		
		
		// RATING_VALUE
		String _ratingCount = "";
		try {
			_ratingCount = movieSite.select("[itemprop=ratingCount]").first().text();
		} catch (NullPointerException e) {
			//System.out.println(title + "  ratingCount");
		}
		movieOB.add("ratingCount", _ratingCount);

		
		
		// DURATION in minutes
		String _duration ="";
		try {
			_duration = movieSite.select("time[itemprop=duration]").first().text().trim();
		} catch (NullPointerException e) {
			//System.out.println(title + "  duration");
		}
		movieOB.add("duration", _duration);

		
		
		// CAST_LIST, CHARACTER_LIST, DIRECTOR_LIST

		Element cclist = movieSite.select("table.cast_list").first();
		Elements directors = movieSite.select("div.credit_summary_item").first().select("[itemprop=name]");		

		// CAST		
		JsonArrayBuilder _cast = Json.createArrayBuilder();
		try {
			Elements cast = cclist.select("[itemprop=name]");
			for (Element el : cast) {
				_cast.add(el.text()); // extract cast names
			}
		} catch (NullPointerException e) {
			//System.out.println(title + "  cast");

		}
		movieOB.add("cast", _cast.build());

		// CHARACTERS		
		JsonArrayBuilder _characters = Json.createArrayBuilder();
		try {
			Elements characters = cclist.select(".character");
			for (Element el : characters) {
				_characters.add(el.text()); // extract character names
			}
		} catch (NullPointerException e) {
			//System.out.println(title + "  characters");
		}
		movieOB.add("characters", _characters.build());

		
		
		// DIRECTORS
		JsonArrayBuilder _directors = Json.createArrayBuilder();
		try {
			for (Element el : directors) {
				_directors.add(el.text()); // extract cast names
			}
		} catch (NullPointerException e) {
			//System.out.println(title + "  directors");
		}
		movieOB.add("directors", _directors.build());


		JsonArray movie = movieA.add(movieOB).build();

		return movie;
	}

	
	
	/**
	 * Helper method to remove html and formating from text.
	 *
	 * @param text
	 *            The text to be cleaned
	 * @return clean text
	 */
	protected static String cleanText(String text) {
		return text.replaceAll("\\<.*?>", "").replace("&nbsp;", " ").replace("\n", " ").replaceAll("\\s+", " ").trim();
	}

	public static void main(String argv[]) throws IOException {

		long start = System.currentTimeMillis();
		String moviesPath = "./data/movies.json";
		String outputDir = "./data";

		if (argv.length == 2) {
			moviesPath = argv[0];
			outputDir = argv[1];
		} else if (argv.length != 0) {
			System.out.println("Call with: IMDBSpider.jar <moviesPath> <outputDir>");
			System.exit(0);
		}

		IMDBSpider sp = new IMDBSpider();
		sp.fetchIMDBMovies(moviesPath, outputDir);

		
		long end = System.currentTimeMillis();
		//System.out.println("total duration: "+ (end-start)/1000);

	}
}

