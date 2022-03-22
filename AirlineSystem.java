import java.util.*;
import java.io.*;

final public class AirlineSystem implements AirlineInterface {
  private String [] cityNames = null;
  private static final int INFINITY = Integer.MAX_VALUE;
  private Digraph G = null;
  Route route; 

  /**
  * reads the city names and the routes from a file
  * @param fileName the String file name
  * @return true if routes loaded successfully and false otherwise
  */
  public boolean loadRoutes(String fileName){ //WORKING!
  	try{
    Scanner fileScan = new Scanner(new FileInputStream(fileName));
    int v = Integer.parseInt(fileScan.nextLine());
    G = new Digraph(v);
     
    cityNames = new String[v];
    for(int i=0; i<v; i++){
      cityNames[i] = fileScan.nextLine();
    }

	
    while(fileScan.hasNext()){
      int from = fileScan.nextInt();
      int to = fileScan.nextInt();
      int weight = fileScan.nextInt();
      int cost = (int) (fileScan.nextDouble());
      G.addEdge(new WeightedUndirectedEdge(from-1, to-1, weight, cost)); // change this to Set<Integer>
      G.addEdge(new WeightedUndirectedEdge(to-1, from-1,  weight, cost)); // change this to Set<Integer>
      if(fileScan.hasNext()) fileScan.nextLine();
    }
    fileScan.close();
    return true;
    }
    catch(IOException exception){
    return false;
    }
  }

  /**
  * writes the city names and the routes into a file
  * @param fileName the String file name
  * @return true if routes saved successfully and false otherwise
  */
  public boolean saveRoutes(String fileName){ //NEEDS WORK
   try{
  	FileWriter fileWriter = new FileWriter(fileName);
    fileWriter.write(cityNames.length + "\n"); // write length to the file
    // start with cityNames sending them to the file
    
    //iterate through graph and creates temp and ponly put forward edge into it 
	for(int i = 0; i < cityNames.length; i++){
		fileWriter.write(cityNames[i]+ "\n"); //change this
	}
	
  	Digraph temp = new Digraph(cityNames.length);
  	for(int x = 0; x < cityNames.length-1; x++){
	Iterable<WeightedUndirectedEdge> it = G.adj(x);  // iterable for weightedundirectededge
    for (WeightedUndirectedEdge e : it) {
      temp.addEdge(new WeightedUndirectedEdge(e.v, e.w, e.weight, e.price)); // change this to Set<Integer>
     // temp.addEdge(new WeightedUndirectedEdge(e.w, e.v,  e.weight, e.price)); // change this to Set<Integer>
    	//temp.deleteEdge(e);
  	}
  	}

  	for(int x = 0; x < cityNames.length-1; x++){
	Iterable<WeightedUndirectedEdge> it = temp.adj(x);  // iterable for weightedundirectededge
    for (WeightedUndirectedEdge e : it) {
    	WeightedUndirectedEdge lo = temp.findEdge(e.w,e.v);
    	if(lo == null)continue;
    	temp.deleteEdge(lo);
    	//temp.deleteEdge(e);
  	}
  	}


	for(int i = 0; i < cityNames.length; i++){ //iterate over all vertices
	Iterable<WeightedUndirectedEdge> it = temp.adj(i);  // iterable for weightedundirectededge
    for (WeightedUndirectedEdge e : it) { //as you iterate through write out the stuff to the file
        fileWriter.write(e.v + 1 + " ");
        fileWriter.write(e.w+ 1+" ");
        fileWriter.write(e.weight+ " ");
        fileWriter.write((int)(e.price) + ".00");
        fileWriter.write("\n");
    }
    }
    fileWriter.close();
    return true;
   }
   catch(IOException e){
    return false;
   }
  }


  /**
  * returns the set of city names in the Airline system
  * @return a (possibly empty) Set<String> of city names
  */
  public Set<String> retrieveCityNames(){ //WORKING!
  	Set<String> sets = new HashSet<String>();
	for(int i = 0; i < cityNames.length; i++){
		if(cityNames[i] == null) continue;
		sets.add(cityNames[i]);
	}
    return sets;
  }

  /**
  * returns the set of direct routes out of a given city
  * @param city the String city name
  * @return a (possibly empty) Set<Route> of Route objects representing the
  * direct routes out of city
  * @throws CityNotFoundException if the city is not found in the Airline
  * system
  */
  public Set<Route> retrieveDirectRoutesFrom(String city)
    throws CityNotFoundException{ //WORKING!
    //try{
    int start = 0; //start index
    Route route;
    Set<Route> set = new HashSet<Route>();
    for(int i = 0; i < cityNames.length; i++){ // getting the start index
    	if(cityNames[i] == null) continue;
    	if(cityNames[i].equals(city)) start = i;
    } 
	int x = 0;
	Iterable<WeightedUndirectedEdge> it = G.adj(start);  // iterable for weightedundirectededge
    for (WeightedUndirectedEdge e : it) {
        route = new Route(city, cityNames[e.to()], e.weight(), e.price);
         set.add(route);
    }
    return set;
    //}
    //catch(Exception e){
    //return new HashSet<Route>();
    //}
  }

  /**
  * finds cheapest path(s) between two cities
  * @param source the String source city name
  * @param destination the String destination city name
  * @return a (possibly empty) Set<ArrayList<Route>> of cheapest
  * paths. Each path is an ArrayList<Route> of Route objects that includes a
  * Route out of the source and a Route into the destination.
  * @throws CityNotFoundException if any of the two cities are not found in the
  * Airline system
  */
  public Set<ArrayList<Route>> cheapestItinerary(String source,
    String destination) throws CityNotFoundException{ //WORKING!
      //Use the index values for source and destination cities
       int sourceNum = -1;
       int destNum = -1;
      //Create set and arraylist variables needed 
       Set<ArrayList<Route>> set = new HashSet<ArrayList<Route>>();
       ArrayList<Route> shortList = new ArrayList<Route>();
      
      //get the index values for source and destination cities!
       for(int i = 0; i < cityNames.length; i++){
      	 if(cityNames[i].equals(source)){
      		sourceNum = i;
      	 }
      	 if(cityNames[i].equals(destination)){
      		destNum = i;
      	 }
       }
       
       if(sourceNum == -1 || destNum == -1) return set; // if the city or destination does not exist return an empty set

       G.dijkstras(sourceNum, destNum); // run dijkstras on source and destination according to distance rather than price
        
       Stack<Integer> path = new Stack<>();
       for (int x = destNum; x != sourceNum; x = G.edgeTo[x]){
            path.push(x);
       }
       
       int prevVertex = sourceNum;
       
       while(!path.empty()){
       		int v = path.pop();
            shortList.add(new Route(cityNames[prevVertex], cityNames[v], G.distTo[v] - G.distTo[prevVertex], G.priceTo[v] - G.priceTo[prevVertex]));
            prevVertex = v;
          }
    //shortest path / distance 
	set.add(shortList);
    return set;
    //return new HashSet<ArrayList<Route>>();
  }


  /**
  * finds cheapest path(s) between two cities going through a third city
  * @param source the String source city name
  * @param transit the String transit city name
  * @param destination the String destination city name
  * @return a (possibly empty) Set<ArrayList<Route>> of cheapest
  * paths. Each path is an ArrayList<Route> of city names that includes
  * a Route out of source, into and out of transit, and into destination.
  * @throws CityNotFoundException if any of the three cities are not found in
  * the Airline system
  */
  public Set<ArrayList<Route>> cheapestItinerary(String source,
    String transit, String destination) throws CityNotFoundException{ //WORKING!
       Set<ArrayList<Route>> ret = new HashSet<ArrayList<Route>>();
       Set<ArrayList<Route>> set1 = cheapestItinerary(source, transit);
       Set<ArrayList<Route>> set2 = cheapestItinerary(transit, destination);
	   ArrayList<Route> shortList = new ArrayList<Route>();
	   
	   shortList=set1.iterator().next();
	   shortList.addAll(set2.iterator().next());
	   ret.add(shortList);

      return ret;
    }

  /**
   * finds one Minimum Spanning Tree (MST) for each connected component of
   * the graph
   * @return a (possibly empty) Set<Set<Route>> of MSTs. Each MST is a Set<Route>
   * of Route objects representing the MST edges.
   */
  public Set<Set<Route>> getMSTs(){ //NEEDS WORK
    return new HashSet<Set<Route>>();
  }

  /**
   * finds all itineraries starting out of a source city and within a given
   * price
   * @param city the String city name
   * @param budget the double budget amount in dollars
   * @return a (possibly empty) Set<ArrayList<Route>> of paths with a total cost
   * less than or equal to the budget. Each path is an ArrayList<Route> of Route
   * objects starting with a Route object out of the source city.
   */
  public Set<ArrayList<Route>> tripsWithin(String city, double budget)
    throws CityNotFoundException { //NEEDS WORK
       /*Set<ArrayList<Route>> set = new HashSet<ArrayList<Route>>();
       ArrayList<Route> shortList = new ArrayList<Route>();

       for(int i = 0; i < cityNames; i++){
    		set = tripsWithinHelper(i,  0, budget, shortList, set);
       }
       
      return set; // return set*/
       return new HashSet<ArrayList<Route>>();
  }
  
  //recursive function -- implement 2 things current decision (vertex) ++ current route + total price so far
  //iterate over all choices and check if choice is valid or not and if choice is valid 
  public static Set<ArrayList<Route>> tripsWithinHelper(int vertex, double currPrice, double maxPrice, ArrayList<Route> r, Set<ArrayList<Route>> set){ //current soln and curr decision
	Iterable<WeightedUndirectedEdge> it = G.adj(vertex);  //check if each negihber is a valid choice -- currPrice + priceNe <= maxprice then add the choice to arraylist R, then add to the set soln then recurse on that neighbor and pass in the new currPrice added, then undo deleted neighbor, mark the neighbor to true then the undo step set ,mrark back to false 
	for(int i = 0; i < cityNames.length; i++){
		
	}

  }

  /**
   * finds all itineraries within a given price regardless of the
   * starting city
   * @param  budget the double budget amount in dollars
   * @return a (possibly empty) Set<ArrayList<Route>> of paths with a total cost
   * less than or equal to the budget. Each path is an ArrayList<Route> of Route
   * objects.
   */
  public Set<ArrayList<Route>> tripsWithin(double budget){ //NEEDS WORK
    /*Set<ArrayList<Route>> set = new HashSet<ArrayList<Route>>();
    ArrayList<Route> shortList = new ArrayList<Route>();
    
  	for(int i = 0; i < cityNames.length; i++){
		Route t = new route(cityName[])
  	}*/
  	
    return new HashSet<ArrayList<Route>>();
  }

  /**
   * delete a given non-stop route from the Airline's schedule. Both directions
   * of the route have to be deleted.
   * @param  source the String source city name
   * @param  destination the String destination city name
   * @return true if the route is deleted successfully and false if no route
   * existed between the two cities
   * @throws CityNotFoundException if any of the two cities are not found in the
   * Airline system
   */
  public boolean deleteRoute(String source, String destination)
    throws CityNotFoundException{ //WORKING!
       int sourceNum = -1;
       int destNum = -1;
      //Create set and arraylist variables needed 
       Set<ArrayList<Route>> set = new HashSet<ArrayList<Route>>();
       ArrayList<Route> shortList = new ArrayList<Route>();
      
      //get the index values for source and destination cities!
       for(int i = 0; i < cityNames.length; i++){
      	 if(cityNames[i].equals(source)){
      		sourceNum = i;
      	 }
      	 if(cityNames[i].equals(destination)){
      		destNum = i;
      	 }
       }
       if(destNum == -1 || sourceNum == -1) return false;
		
	   WeightedUndirectedEdge sourceEdge = G.findEdge(sourceNum,destNum);
	   WeightedUndirectedEdge destEdge = G.findEdge(destNum, sourceNum);
	   G.deleteEdge(sourceEdge);
	   G.deleteEdge(destEdge);
	   return true;
	   
  }

  /**
   * delete a given city and all non-stop routes out of and into the city from
   * the Airline schedule.
   * @param  city  the String city name
   * @throws CityNotFoundException if the city is not found in the Airline system
   */
  public void deleteCity(String city) throws CityNotFoundException{ //WORKING!
    int cityIndex = -1;
  	for(int i = 0; i < cityNames.length; i++){ // FINDS THE INDEX OF THE CITY 
  		if(city.equals(cityNames[i])) cityIndex = i;
  	} 
	
	
	Iterable<WeightedUndirectedEdge> it = G.adj(cityIndex);  // iterable for weightedundirectededge
// 	Iterator<WeightedUndirectedEdge> ft = it.iterator;
   	//deletes edges
   	ArrayList<WeightedUndirectedEdge> array = new ArrayList<WeightedUndirectedEdge>();
    for (WeightedUndirectedEdge e : it) {
 	   		WeightedUndirectedEdge sourceEdge = G.findEdge(cityIndex,e.to());
	   		WeightedUndirectedEdge destEdge = G.findEdge(e.to(), cityIndex);
	   		array.add(e);
    		G.deleteEdge(destEdge); 
  	}
  	for (WeightedUndirectedEdge e : array) {
  		G.deleteEdge(e);
  	}
  	
	//PUT A NULL WHERE CITY USED TO BE THEN HANDLE NULL IN CITY NAMES 
	cityNames[cityIndex] = null;
  }
  




  public class Digraph {
    private final int v;
    private int e;
    private LinkedList<WeightedUndirectedEdge>[] adj;
    private boolean[] marked;  // marked[v] = is there an s-v path
    private int[] edgeTo;      // edgeTo[v] = previous edge on shortest s-v path
    private int[] distTo;      // distTo[v] = number of edges shortest s-v path
    private double[] priceTo; 	// cost of the flight


    /**
    * Create an empty digraph with v vertices.
    */
    public Digraph(int v) {
      if (v < 0) throw new RuntimeException("Number of vertices must be nonnegative");
      this.v = v;
      this.e = 0;
      @SuppressWarnings("unchecked")
      LinkedList<WeightedUndirectedEdge>[] temp =
      (LinkedList<WeightedUndirectedEdge>[]) new LinkedList[v];
      adj = temp;
      for (int i = 0; i < v; i++)
        adj[i] = new LinkedList<WeightedUndirectedEdge>();
    }

    /**
    * Add the edge e to this digraph.
    */
    public void addEdge(WeightedUndirectedEdge edge) {
      int from = edge.from();
      adj[from].add(edge);
      e++;
    }
    
    public void deleteEdge(WeightedUndirectedEdge edge){
      int from = edge.from();
      adj[from].remove(edge);
      e--;
    }
    
    public WeightedUndirectedEdge findEdge(int source, int destination){
    	for(WeightedUndirectedEdge e  :  adj(source)){
    		if(e.to() == destination){
    			return e;
    		}
    	}
    	return null;
    }
    

    /**
    * Return the edges leaving vertex v as an Iterable.
    * To iterate over the edges leaving vertex v, use foreach notation:
    * <tt>for (WeightedUndirectedEdge e : graph.adj(v))</tt>.
    */
    public Iterable<WeightedUndirectedEdge> adj(int v) {
      return adj[v];
    }

    public void bfs(int source) {
      marked = new boolean[this.v];
      distTo = new int[this.e];
      edgeTo = new int[this.v];
      priceTo = new double[this.v];

      Queue<Integer> q = new LinkedList<Integer>();
      for (int i = 0; i < v; i++){
      	distTo[i] = INFINITY;
        priceTo[i] = INFINITY;
        marked[i] = false;
      }
      distTo[source] = 0;
      marked[source] = true;
      q.add(source);

      while (!q.isEmpty()) {
        int v = q.remove();
        for (WeightedUndirectedEdge w : adj(v)) {
          if (!marked[w.to()]) {
            edgeTo[w.to()] = v;
            distTo[w.to()] = distTo[v] + 1;
            priceTo[w.to()] = priceTo[v] + w.price;
            marked[w.to()] = true;
            q.add(w.to());
          }
        }
      }
    }
    public void dijkstras(int source, int destination) {
      marked = new boolean[this.v];
      distTo = new int[this.v];
      edgeTo = new int[this.v];
      priceTo = new double[this.v];
      


      for (int i = 0; i < v; i++){
        distTo[i] = INFINITY;
        priceTo[i] = INFINITY;
        marked[i] = false;
      }
      distTo[source] = 0;
      priceTo[source] = 0;
      marked[source] = true;
      int nMarked = 1;

      int current = source;
      while (nMarked < this.v) {
        for (WeightedUndirectedEdge w : adj(current)) {
          if (priceTo[current]+w.price() < priceTo[w.to()]) {
	      	edgeTo[w.to()] = current;
	      	distTo[w.to()] = distTo[current] + w.weight();
	      	priceTo[w.to()] = priceTo[current] + w.price();
          }
        }
        //Find the vertex with minimim path distance
        //This can be done more effiently using a priority queue!
        double min = INFINITY;
        current = -1;

        for(int i=0; i<priceTo.length; i++){
          if(marked[i])
            continue;
          if(priceTo[i] < min){
            min = priceTo[i];
            current = i;
          }
        }
        if(current == -1){
        	break;
        }
        else{
        	marked[current] = true;
        	nMarked++;
        }
		/// update number of marked vertices --- 
	//TODO: Update marked[] and nMarked. Check for disconnected graph.
      }
    }
  }

  /**
  *  The <tt>WeightedUndirectedEdge</tt> class represents a weighted edge in an directed graph.
  */

  public class WeightedUndirectedEdge {
    private final int v;
    private final int w;
    private int weight;
    private double price;
    /**
    * Create a directed edge from v to w with given weight.
    */
    public WeightedUndirectedEdge(int v, int w, int weight, double price) {
      this.v = v;
      this.w = w;
      this.weight = weight;
      this.price = price;
    }

    public int from(){
      return v;
    }

    public int to(){
      return w;
    }

    public int weight(){
      return weight;
    }
    
    public double price(){
    	return price; 
    }
    
  }
} //EOF AIRLINESYSTEM 

