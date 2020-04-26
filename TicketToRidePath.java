import java.util.ArrayList;
import java.util.HashSet;
class TicketToRidePath {
    public static void main (String[] args) {
        System.out.println("Input graph:");
        System.out.println(EXAMPLE1);
        System.out.println("Longest path subgraph:");
        System.out.println(longestPath(EXAMPLE1));
    }
    public static final Graph EXAMPLE1 = new Graph()
        .connect("France1"    , "Geneve"     , 1)
        .connect("Geneve"     , "Yverdon"    , 6)
        .connect("Yverdon"    , "Neuchatel"  , 2)
        .connect("Neuchatel"  , "Bern"       , 2)
        .connect("Bern"       , "Fribourg"   , 1)
        .connect("Neuchatel"  , "Solothurn"  , 4)
        .connect("Delemont"   , "Solothurn"  , 1)
        .connect("Delemont"   , "Basel"      , 2)
        .connect("Baden"      , "Basel"      , 3)
        .connect("Baden"      , "Olten"      , 2)
        .connect("Luzern"     , "Olten"      , 3)
        .connect("Luzern"     , "Schurgz"    , 1)
        .connect("Zug"        , "Schurgz"    , 1)
        .connect("Pfalfiken"  , "Schurgz"    , 1)
        .connect("Pfalfiken"  , "Sarnans"    , 3)
        .connect("Chur"       , "Sarnans"    , 1)
        .connect("Pfalfiken"  , "St. Gallen" , 3)
        .connect("Winterthur" , "St. Gallen" , 3);
    public static Graph longestPath(Graph fullGraph) {
        if (fullGraph.isEmpty())
            return fullGraph;
        ArrayList<Graph> traversals = new ArrayList<>();
        for (String city : fullGraph.cities())
            traversals.addAll(calculateFrom(city, fullGraph));
        Graph bestTraversal = traversals.remove(0);
        for (Graph traversal : traversals)
            if (bestTraversal.distanceSum() < traversal.distanceSum())
                bestTraversal = traversal;
        return bestTraversal;
    }
    public static ArrayList<Graph> calculateFrom(String city, Graph graph) {
        ArrayList<Graph> routes = new ArrayList<>(graph.size());
        for (Path path : graph.connectionsWith(city)) {
            for (Graph route : calculateFrom(path.follow(city),
                    graph.without(path))) {
                route.add(path);
                routes.add(route);
            }
        }
        if (routes.isEmpty())
            routes.add(new Graph());
        return routes;
    }
}
class Graph extends HashSet<Path> {
    public Graph() {}
    public Graph(int loadFactor) { super(loadFactor); }
    public Graph without(Path path) {
        Graph without = new Graph(this.size() - 1);
        for (Path other : this)
            if (!path.equals(other))
                without.add(other);
        return without;
    }
    @Override public String toString() {
        String out = "Graph - distance sum " + distanceSum() + ":\n";
        for (Path path : this)
            out += "    " + path + '\n';
        return out;
    }
    public int distanceSum() {
        int sum = 0;
        for (Path path : this)
            sum += path.distance;
        return sum;
    }
    public ArrayList<Path> connectionsWith(String city) {
        ArrayList<Path> connections = new ArrayList<>();
        for (Path path : this)
            if (path.contains(city))
                connections.add(path);
        return connections;
    }
    public HashSet<String> cities() {
        HashSet<String> cities = new HashSet<>();
        for (Path path : this) {
            cities.add(path.cityA);
            cities.add(path.cityB);
        }
        return cities;
    }
    public Graph connect(Path path) { add(path); return this; }
    public Graph connect(String cityA, String cityB, int distance) {
        return connect(new Path(cityA, cityB, distance));
    }
}
class Path {
    public final String cityA, cityB;
    public final int distance;
    public Path(String cityA, String cityB, int distance) {
        if (cityA.equals(cityB))
            throw new IllegalStateException(
                    "Path cannot start and end at the same city.");
        if (distance < 1)
            throw new IllegalStateException(
                    "Path length must be greater than zero.");
        this.cityA = cityA;
        this.cityB = cityB;
        this.distance = distance;
    }
    @Override public boolean equals(Object otherObj) {
        @SuppressWarnings("unchecked")
        Path other = (Path) otherObj;
        if ((cityA.equals(other.cityA) && cityB.equals(other.cityB))
         || (cityA.equals(other.cityB) && cityB.equals(other.cityA))) {
            if (distance != other.distance)
                throw new IllegalStateException(
                        "Paths between the same cities must have equal " +
                        "distances.");
            return true;
        }
        else
            return false;
    }
    @Override public String toString() {
        return cityA + " --- " + distance + " ---> " + cityB;
    }
    public boolean contains(String city) {
        return cityA.equals(city) || cityB.equals(city);
    }
    public String follow(String city) {
        if (!contains(city))
            throw new IllegalArgumentException("Can't follow a path if the " +
                    "given city is at neither end.");
        return cityA.equals(city) ? cityB : cityA;
    }
}
