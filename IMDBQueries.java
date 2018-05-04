import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("static-method")
public class IMDBQueries {

  /**
   * A helper class for pairs of objects of generic types 'K' and 'V'.
   *
   * @param <K>
   *          first value
   * @param <V>
   *          second value
   */
  class Tuple<K, V> {
    K first;
    V second;

    public Tuple(K f, V s) {
      this.first = f;
      this.second = s;
    }

    @Override
    public int hashCode() {
      return this.first.hashCode() + this.second.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      return this.first.equals(((Tuple<?, ?>) obj).first)
          && this.second.equals(((Tuple<?, ?>) obj).second);
    }
    
  }
 

  /**
   * All-rounder: Determine all movies in which the director stars as an actor
   * (cast). Return the top ten matches sorted by decreasing IMDB rating.
   *
   * @param movies
   *          the list of movies which is to be queried
   * @return top ten movies and the director, sorted by decreasing IMDB rating
   */
  public List<Tuple<Movie, String>> queryAllRounder(List<Movie> movies) {
    // TODO Basic Query: insert code here
    return new ArrayList<>();
  }

  /**
   * Under the Radar: Determine the top ten US-American movies until (including)
   * 2015 that have made the biggest loss despite an IMDB score above
   * (excluding) 8.0, based on at least 1,000 votes. Here, loss is defined as
   * budget minus gross.
   *
   * @param movies
   *          the list of movies which is to be queried
   * @return top ten highest rated US-American movie until 2015, sorted by
   *         monetary loss, which is also returned
   */
  public List<Tuple<Movie, Long>> queryUnderTheRadar(List<Movie> movies) {
	  List<Tuple<Movie,Long>> result = new ArrayList<Tuple<Movie,Long>>();
	 for (Movie movie: movies){
		try{
			if(movie.getRatingValue().charAt(0)>=56 && !movie.getRatingValue().equals("8.0")){ //char '0' equals int 48
				if(movie.getCountryList().contains("USA")){
					if(movie.getRatingCount().length()>=5){				
						long budget = Long.parseLong(movie.getBudget().replace(",", "").substring(1));
						long gross = Long.parseLong(movie.getGross().replace(",", "").substring(1));
						result.add(new Tuple<Movie, Long>(movie, budget-gross));
					}
				}	
			}
		}catch(StringIndexOutOfBoundsException e){}
	}
	result.sort(Comparator.comparing(tuple -> tuple.second));
	result = result.subList(0, 9);
	
    return result;
  }

  /**
   * The Pillars of Storytelling: Determine all movies that contain both
   * (sub-)strings "kill" and "love" in their lowercase description
   * (String.toLowerCase()). Sort the results by the number of appearances of
   * these strings and return the top ten matches.
   *
   * @param movies
   *          the list of movies which is to be queried
   * @return top ten movies, which have the words "kill" and "love" as part of
   *         their lowercase description, sorted by the number of appearances of
   *         these words, which is also returned.
   */
  public List<Tuple<Movie, Integer>> queryPillarsOfStorytelling(List<Movie> movies) {
	
	  Map<String, Integer> res = new HashMap<Movie, Integer>();
		 
	  for(Movie mov: movies){
		  
			if(!res.containsKey(name)){res.put(name, 1);}
		  	else{res.replace(name, res.get(name)+1);}
	    		
	  }
	  
	  List<Tuple<Movie, Integer>> result = new ArrayList<Tuple<String, Integer>>();
	  for(String name: res.keySet()){
		  if(res.get(name)>1){
			  result.add(new Tuple<String,Integer>(name, res.get(name)));
		  }
	  }
	  result.sort(Collections.reverseOrder(Comparator.comparing(tuple->tuple.second)));
	  result = result.subList(0, 9);  
	  
    
	  return result;
  }

  /**
   * The Red Planet: Determine all movies of the Sci-Fi genre that mention
   * "Mars" in their description (case-aware!). List all found movies in
   * ascending order of publication (year).
   *
   * @param movies
   *          the list of movies which is to be queried
   * @return list of Sci-Fi movies involving Mars in ascending order of
   *         publication.
   */
  public List<Movie> queryRedPlanet(List<Movie> movies) {
	
	List<Movie> result = new ArrayList<Movie>();
	for(Movie mov: movies){
    	if(mov.getGenreList().contains("Sci-Fi")){
    		if(mov.getDescription().contains("Mars")) {
    			result.add(mov);
    		}
    	}
    	result.sort(Comparator.comparing(Movie::getYear));	
    }
    
    return result;
  }

  /**
   * Colossal Failure: Determine all US-American movies with a duration beyond 2
   * hours, a budget beyond 1 million and an IMDB rating below 5.0. Sort results
   * by ascending IMDB rating.
   *
   * @param movies
   *          the list of movies which is to be queried
   * @return list of US-American movies with high duration, large budgets and a
   *         bad IMDB rating, sorted by ascending IMDB rating
   */
  public List<Movie> queryColossalFailure(List<Movie> movies) {
	  List<Movie> result = new ArrayList();

	for (Movie movie: movies){
		try{	
			if(movie.getRatingValue().charAt(0)<53){ //char '0' equals int 48
				if((movie.getDuration().charAt(0)>=50 || movie.getDuration().charAt(1)!='h')){
					if(movie.getCountryList().contains("USA")){
						String budg = movie.getBudget();		
						if(budg.indexOf(",") != budg.lastIndexOf(",") && budg.contains(",")){
							result.add(movie);
						}
					}	

				}
			}
		}catch(StringIndexOutOfBoundsException e){}
	}	
	result.sort(Comparator.comparing(Movie::getRatingValue));
	
    return result;
  }

  /**
   * Uncreative Writers: Determine the 10 most frequent character names of all
   * times ordered by frequency of occurrence. Filter any lowercase names
   * containing substrings "himself", "doctor", and "herself" from the result.
   *
   * @param movies
   *          the list of movies which is to be queried
   * @return the top 10 character names and their frequency of occurrence;
   *         sorted in decreasing order of frequency
   */
  public List<Tuple<String, Integer>> queryUncreativeWriters(List<Movie> movies) {

	  Map<String, Integer> res = new HashMap<String, Integer>();
		 
	  for(Movie mov: movies){
		  for(String name: mov.getCharacterList()){
	    		String name1 = name.toLowerCase();
	    		if(!name1.contains("himself") && !name1.contains("herself") && !name1.contains("doctor") && !name1.equals("") && !name1.contains("voice")){
	    			if(!res.containsKey(name)){res.put(name, 1);}
	  			  	else{res.replace(name, res.get(name)+1);}
	    		}
	    	}
	  }
	  
	  List<Tuple<String, Integer>> result = new ArrayList<Tuple<String, Integer>>();
	  for(String name: res.keySet()){
		  if(res.get(name)>1){
			  result.add(new Tuple<String,Integer>(name, res.get(name)));
		  }
	  }
	  result.sort(Collections.reverseOrder(Comparator.comparing(tuple->tuple.second)));
	  result = result.subList(0, 9);  
	  
    
    return result;
  }


/**
   * Workhorse: Provide a ranked list of the top ten most active actors (i.e.
   * starred in most movies) and the number of movies they played a role in.
   *
   * @param movies
   *          the list of movies which is to be queried
   * @return the top ten actors and the number of movies they had a role in,
   *         sorted by the latter.
   */
  public List<Tuple<String, Integer>> queryWorkHorse(List<Movie> movies) {
	  Map<String, Integer> res = new HashMap<String, Integer>();
	 
	  for(Movie mov: movies){
		  for(String name: mov.getCastList()){
			  if(!res.containsKey(name)){res.put(name, 1);}
			  else{res.replace(name, res.get(name)+1);}
		  }
	  }
	  
	  List<Tuple<String, Integer>> result = new ArrayList<Tuple<String, Integer>>();
	  for(String name: res.keySet()){
		  if(res.get(name)>1){
			  result.add(new Tuple<String,Integer>(name, res.get(name)));
		  }
	  }
	  result.sort(Collections.reverseOrder(Comparator.comparing(tuple->tuple.second)));
	  result = result.subList(0, 9);
	  
    return  result;
  }

  /**
   * Must See: List the best-rated movie of each year starting from 1990 until
   * (including) 2010 with more than 10,000 ratings. Order the movies by
   * ascending year.
   *
   * @param movies
   *          the list of movies which is to be queried
   * @return best movies by year, starting from 1990 until 2010.
   */
  public List<Movie> queryMustSee(List<Movie> movies) {
	 
    return new ArrayList<>();
  }

  /**
   * Rotten Tomatoes: List the worst-rated movie of each year starting from 1990
   * till (including) 2010 with an IMDB score larger than 0. Order the movies by
   * increasing year.
   *
   * @param movies
   *          the list of movies which is to be queried
   * @return worst movies by year, starting from 1990 till (including) 2010.
   */
  public List<Movie> queryRottenTomatoes(List<Movie> movies) {
    Map<String, Movie> res = new HashMap<String, Movie>();
    res.put("1990",null);res.put("1991",null);res.put("1992",null);res.put("1993",null);res.put("1994",null);res.put("1995",null);res.put("1996",null);res.put("1997",null);res.put("1998",null);res.put("1999",null)
    ;res.put("2000",null);res.put("2001",null);res.put("2002",null);res.put("2003",null);res.put("2004",null);res.put("2005",null);res.put("2006",null);res.put("2007",null);res.put("2008",null);res.put("2009",null)
    ;res.put("2010",null);
    
    for(Movie mov:movies){
    	
    	if(res.containsKey(mov.getYear())){
    			String year = mov.getYear();
	    		if(mov.getRatingValue()!="0"){	
	    			if(res.get(year)==null||mov.getRatingValue().compareTo(res.get(year).getRatingValue()) < 0){
	    				res.put(year, mov);
	    			}	
	    		}
    	}
    	
    }    
    List<Movie> result = new ArrayList<Movie>();
    for(String year: res.keySet()){
    	if(!(res.get(year)==null)){
    		result.add(res.get(year));
    	}
    }
    

    result.sort(Comparator.comparing(Movie::getYear));
    
    return result;
  }

  /**
   * Magic Couples: Determine those couples that feature together in the most
   * movies. E.g., Adam Sandler and Allen Covert feature together in multiple
   * movies. Report the top ten pairs of actors, their number of movies and sort
   * the result by the number of movies.
   *
   * @param movies
   *          the list of movies which is to be queried
   * @return report the top 10 pairs of actors and the number of movies they
   *         feature together. Sort by number of movies.
   */
  public List<Tuple<Tuple<String, String>, Integer>> queryMagicCouple(
      List<Movie> movies) {
    // TODO Impossibly Hard Query: insert code here
    return new ArrayList<>();
  }


  public static void main(String argv[]) throws IOException {
    String moviesPath = "./data/movies/";

    if (argv.length == 1) {
      moviesPath = argv[0];
    } else if (argv.length != 0) {
      System.out.println("Call with: IMDBQueries.jar <moviesPath>");
      System.exit(0);
    }

    List<Movie> movies = MovieReader.readMoviesFrom(new File(moviesPath));

    System.out.println("All-rounder");
    {
      IMDBQueries queries = new IMDBQueries();
      long time = System.currentTimeMillis();
      List<Tuple<Movie, String>> result = queries.queryAllRounder(movies);
      System.out.println("Time:" + (System.currentTimeMillis() - time));

      if (result != null && !result.isEmpty() && result.size() == 10) {
        for (Tuple<Movie, String> tuple : result) {
          System.out.println("\t" + tuple.first.getRatingValue() + "\t"
              + tuple.first.getTitle() + "\t" + tuple.second);
        }
      } else {
        System.out.println("Error? Or not implemented?");
      }
    }
    System.out.println("");

    System.out.println("Under the radar");
    {
      IMDBQueries queries = new IMDBQueries();
      long time = System.currentTimeMillis();
      List<Tuple<Movie, Long>> result = queries.queryUnderTheRadar(movies);
      System.out.println("Time:" + (System.currentTimeMillis() - time));

      if (result != null && !result.isEmpty() && result.size() <= 10) {
        for (Tuple<Movie, Long> tuple : result) {
          System.out.println("\t" + tuple.first.getTitle() + "\t"
              + tuple.first.getRatingCount() + "\t"
              + tuple.first.getRatingValue() + "\t" + tuple.second);
        }
      } else {
        System.out.println("Error? Or not implemented?");
      }
    }
    System.out.println("");

    System.out.println("The pillars of storytelling");
    {
      IMDBQueries queries = new IMDBQueries();
      long time = System.currentTimeMillis();
      List<Tuple<Movie, Integer>> result = queries
          .queryPillarsOfStorytelling(movies);
      System.out.println("Time:" + (System.currentTimeMillis() - time));

      if (result != null && !result.isEmpty() && result.size() <= 10) {
        for (Tuple<Movie, Integer> tuple : result) {
          System.out.println("\t" + tuple.first.getTitle() + "\t"
              + tuple.second);
        }
      } else {
        System.out.println("Error? Or not implemented?");
      }
    }
    System.out.println("");

    System.out.println("The red planet");
    {
      IMDBQueries queries = new IMDBQueries();
      long time = System.currentTimeMillis();
      List<Movie> result = queries.queryRedPlanet(movies);
      System.out.println("Time:" + (System.currentTimeMillis() - time));

      if (result != null && !result.isEmpty()) {
        for (Movie movie : result) {
          System.out.println("\t" + movie.getTitle() + " " + movie.getYear());
        }
      } else {
        System.out.println("Error? Or not implemented?");
      }
    }
    System.out.println("");

    System.out.println("ColossalFailure");
    {
      IMDBQueries queries = new IMDBQueries();
      long time = System.currentTimeMillis();
      List<Movie> result = queries.queryColossalFailure(movies);
      System.out.println("Time:" + (System.currentTimeMillis() - time));

      if (result != null && !result.isEmpty()) {
        for (Movie movie : result) {
          System.out.println("\t" + movie.getTitle() + "\t"
              + movie.getRatingValue());
        }
      } else {
        System.out.println("Error? Or not implemented?");
      }
    }
    System.out.println("");

    System.out.println("Uncreative writers");
    {
      IMDBQueries queries = new IMDBQueries();
      long time = System.currentTimeMillis();
      List<Tuple<String, Integer>> result = queries
          .queryUncreativeWriters(movies);
      System.out.println("Time:" + (System.currentTimeMillis() - time));

      if (result != null && !result.isEmpty() && result.size() <= 10) {
        for (Tuple<String, Integer> tuple : result) {
          System.out.println("\t" + tuple.first + "\t" + tuple.second);
        }
      } else {
        System.out.println("Error? Or not implemented?");
      }
    }
    System.out.println("");

    System.out.println("Workhorse");
    {
      IMDBQueries queries = new IMDBQueries();
      long time = System.currentTimeMillis();
      List<Tuple<String, Integer>> result = queries.queryWorkHorse(movies);
      System.out.println("Time:" + (System.currentTimeMillis() - time));

      if (result != null && !result.isEmpty() && result.size() <= 10) {
        for (Tuple<String, Integer> actor : result) {
          System.out.println("\t" + actor.first + "\t" + actor.second);
        }
      } else {
        System.out.println("Error? Or not implemented?");
      }
    }
    System.out.println("");

    System.out.println("Must see");
    {
      IMDBQueries queries = new IMDBQueries();
      long time = System.currentTimeMillis();
      List<Movie> result = queries.queryMustSee(movies);
      System.out.println("Time:" + (System.currentTimeMillis() - time));

      if (result != null && !result.isEmpty() && !result.isEmpty()) {
        for (Movie m : result) {
          System.out.println("\t" + m.getYear() + "\t" + m.getRatingValue()
              + "\t" + m.getTitle());
        }
      } else {
        System.out.println("Error? Or not implemented?");
      }
    }
    System.out.println("");

    System.out.println("Rotten tomatoes");
    {
      IMDBQueries queries = new IMDBQueries();
      long time = System.currentTimeMillis();
      List<Movie> result = queries.queryRottenTomatoes(movies);
      System.out.println("Time:" + (System.currentTimeMillis() - time));

      if (result != null && !result.isEmpty() && !result.isEmpty()) {
        for (Movie m : result) {
          System.out.println("\t" + m.getYear() + "\t" + m.getRatingValue()
              + "\t" + m.getTitle());
        }
      } else {
        System.out.println("Error? Or not implemented?");
      }
    }
    System.out.println("");

    System.out.println("Magic Couples");
    {
      IMDBQueries queries = new IMDBQueries();
      long time = System.currentTimeMillis();
      List<Tuple<Tuple<String, String>, Integer>> result = queries
          .queryMagicCouple(movies);
      System.out.println("Time:" + (System.currentTimeMillis() - time));

      if (result != null && !result.isEmpty()) {
        for (Tuple<Tuple<String, String>, Integer> tuple : result) {
          System.out.println("\t" + tuple.first.first + ":"
              + tuple.first.second + "\t" + tuple.second);
        }
      } else {
        System.out.println("Error? Or not implemented?");
      }
      System.out.println("");

    }
  }
}