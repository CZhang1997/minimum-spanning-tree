package cxz173430;

/* Starter code for Project 5.  
 * Do not change names or signatures of methods that are declared as public.  
 * If you want to create additional classes, make them nested classes of MST,
 * instead of placing them in separate java files.
 * Do not modify Graph.java or move it from package rbk.
 */
import rbk.Graph;
import rbk.Graph.Vertex;
import rbk.Graph.Edge;
import rbk.Graph.GraphAlgorithm;
import rbk.Graph.Factory;
import rbk.Graph.Timer;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.LinkedList;
/**
 * @author 		Churong Zhang 
 * 				cxz173430
 * @date		December 12 2018
 * 				Dr. Raghavachari
 * 				This class is for Project 5
 * 				classes of MST
 */
public class MST extends GraphAlgorithm<MST.MSTVertex> {
    String algorithm;	// the algorithm was used
    public long wmst; 	// the minimum length
    public LinkedList<Edge> mst;	// the list of edge was used
    
    private MST(Graph g) {
	super(g, new MSTVertex(null));
	mst = new LinkedList<>();
    wmst = 0;
    }

    public static class MSTVertex implements Comparable<MSTVertex>, Factory {
    boolean seen;	// check if the vertex is seen
    MSTVertex parent;	// the parent of the vertex
    int rank;		// the rank of the vertex
    /**
     * create a MSTVertex
     * @param u the input vertex
     */
	public MSTVertex(Vertex u) {
		parent = this;
		rank = 0;
		seen = false;
	}
	/**
	 * create a MSTVertex 
	 * @param u the input vertex
	 * @return the MSTVertex
	 */
	public MSTVertex make(Vertex u) { return new MSTVertex(u); }
	/**
	 * @return the highest parent of the MSTVertex
	 */
	public MSTVertex find()
	{
		if(this != parent)	// if itself is not the parent
			parent = parent.find();	// recursive find the 
		return parent;		// find the parent with the highest rank
	}
	/**
	 * combine two spanning tree
	 * @param rv the other MST
	 */
	public void union (MSTVertex rv)
	{	// assign the parent of the other MSTVertex to the MSTVertex that has higher rank
		if(this.rank > rv.rank)
			rv.parent = this;
		else if (this.rank < rv.rank)
			this.parent = rv;
		else
		{	// if they have the same rank then 
			// let rv.parent = this
			// increase the rank of this
			this.rank++;
			rv.parent = this;
		}
	}
	public int compareTo(MSTVertex other) { return 0;}
    }
    /**
     * create a MST using the Kruskal algorithm
     * @param g the graph
     * @return the new MST
     */
    public static MST kruskal(Graph g) 
    {
		MST m = new MST(g);
	////////////////was define in the MST constructor /////////
	//m.mst = new LinkedList<>();	
	//m.wmst = 0;
	////////////////////////////////////////////////////////////
		m.algorithm = "Kruskal";
		Edge[] edgeArray = g.getEdgeArray();		
		Arrays.sort(edgeArray);			// sort the edges by weight
		for(Edge e : edgeArray)			// loop through each edge from the smallest weight
		{	// need to use m.get because this function is static, so we need to call it from object m
			MSTVertex ru = m.get(e.fromVertex()).find();	// find the parent of the two vertex 
			MSTVertex rv = m.get(e.toVertex()).find();		// find the parent of the two vertex 
			if(ru != rv)		// if they do not share a same parent
			{					// then this is a minimum edge can add to mst
				m.mst.add(e);
				m.wmst += e.getWeight();		// increase the weight
				ru.union(rv);					// combine the two spanning tree
			}
		}
	        return m;
    }
    /**
     * create a MST using the Prim algorithm using priority queue
     * @param g the graph
     * @param s the source vertex to start the algorithm 
     * @return the new MST
     */
    public static MST prim(Graph g, Vertex s) {
    // parent = this and parent = null has no difference on this algorithm
	MST m = new MST(g);
	m.algorithm = "Prim with PriorityQueue<Edge>";
	PriorityQueue<Edge> q = new PriorityQueue<>();
	m.get(s).seen = true;		// seen the source 
	for(Edge e: g.incident(s))
	{
		q.offer(e);		// add all edge that incident with the source vertex
	}
	while(!q.isEmpty())
	{
		Edge e = q.remove();	// get the edge with minimum weight
					// default 
		Vertex u = e.toVertex();		// u is the to vertex
		Vertex v = e.fromVertex();		// v is the from vertex
		if(m.get(e.fromVertex()).seen)	// if the from vertex is seen
		{
			u = e.fromVertex();			// that mean u is fromVertex
			v = e.toVertex();			// v is toVertex
		}
		if(!m.get(v).seen)			// if v is not seen then do the following
		{
			m.get(v).seen = true;	// we seen it
			m.get(v).parent = m.get(u);	// change the parent of it
			m.wmst += e.getWeight();	// add the weight
			m.mst.add(e);				// add the edge 
			for(Edge e2: g.incident(v))	// check the edge that incident with v
			{	// if one of the vertex is not seen, then add the edge 
				if(!m.get(e2.toVertex()).seen || !m.get(e2.fromVertex()).seen)
					q.add(e2);			// add it to the pq if the to vertex has not seen
			}
		}
	}
	return m;
    }

    // No changes need to be made below this
    
    public static MST mst(Graph g, Vertex s, String choice) {
	if(choice.equals("Kruskal")) {
	    return kruskal(g);
	} else {
	    return prim(g, s);
	}
    }

    public static void main(String[] args) throws java.io.FileNotFoundException {
	java.util.Scanner in;
	String choice = "Kruskal1";
        if (args.length == 0 || args[0].equals("-")) {
            in = new java.util.Scanner(System.in);
        } else {
            java.io.File inputFile = new java.io.File(args[0]);
            in = new java.util.Scanner(inputFile);
        }

	if (args.length > 1) { choice = args[1]; }
	//in = new java.util.Scanner(new java.io.File("mst-10k-30k-1085305.txt"));
	String graph = "5 6   1 2 3 1 4 5 4 5 9 1 5 6 2 5 7 2 3 5 ";
	in = new java.util.Scanner(graph);
	
	Graph g = Graph.readGraph(in);
        Vertex s = g.getVertex(1);

	Timer timer = new Timer();
	MST m = mst(g, s, choice);
	System.out.println("Algorithm: " + m.algorithm + "\nThe minimum weigth is " + m.wmst);
	for(Edge e : m.mst) {
		System.out.println("From " + e.fromVertex().getName() + " To " + e.toVertex().getName());
	}
	System.out.println(timer.end());
    }
}