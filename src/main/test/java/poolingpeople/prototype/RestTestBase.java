/**
 * Copyright (c) 2002-2013 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package poolingpeople.prototype;


import com.poolingpeople.utils.neo4jApi.Neo4jRestApiAdapter;
import com.poolingpeople.utils.neo4jApi.Neo4jRestApiAdapterImpl;
import org.junit.*;
import org.neo4j.graphdb.*;
import org.neo4j.helpers.collection.IteratorUtil;
//import org.neo4j.rest.graphdb.util.Config;
import org.neo4j.tooling.GlobalGraphOperations;

import java.util.Iterator;

public class RestTestBase {

    private GraphDatabaseService restGraphDb;
    private static final String HOSTNAME = "127.0.0.1";
    private static final int PORT = 7474;
    private static LocalTestServer neoServer;
    public static final String SERVER_ROOT = "http://" + HOSTNAME + ":" + PORT;
    protected static final String SERVER_ROOT_URI = SERVER_ROOT + "/db/data/cypher";
    private static final String SERVER_CLEANDB_URI = SERVER_ROOT + "/cleandb/secret-key";
    private static final String CONFIG = RestTestBase.class.getResource("/neo4j-server.properties").getFile();
    private long referenceNodeId;
    private Node referenceNode;
    private static Neo4jRestApiAdapter neo4jRestApiAdapter = new Neo4jRestApiAdapterImpl();


    static {
        initServer();
    }

    protected static void initServer() {
        if (neoServer!=null) {
            neoServer.stop();
        }
        neoServer = new LocalTestServer(HOSTNAME,PORT).withPropertiesFile("neo4j-server.properties");
    }

    @BeforeClass
    public static void startDb() throws Exception {
        neoServer.start();
        tryConnect();
    }

    private static void tryConnect() throws InterruptedException {
        int retryCount = 3;
        for (int i = 0; i < retryCount; i++) {
            try {
                neo4jRestApiAdapter.runParametrizedCypherQuery("match n return n", null);
                System.err.println("Successful HTTP connection to " + SERVER_ROOT_URI);
                return;
            } catch (Exception e) {
                System.err.println("Error retrieving ROOT URI " + e.getMessage());
                Thread.sleep(500);
            }
        }
    }

    @Before
    public void setUp() throws Exception {
//        System.setProperty(Config.CONFIG_BATCH_TRANSACTION,"false");
//        neoServer.cleanDb();
//        restGraphDb = new RestGraphDatabase(SERVER_ROOT_URI);
//
//        GraphDatabaseService db = getGraphDatabase();
//        try (Transaction tx = db.beginTx()) {
//            Node node = db.createNode();
//            this.referenceNodeId = node.getId();
//            tx.success();
//        }
//        this.referenceNode = restGraphDb.getNodeById(referenceNodeId);

    }

    @Test
    public void test(){

    }

    @After
    public void tearDown() throws Exception {
        restGraphDb.shutdown();
    }

    @AfterClass
    public static void shutdownDb() {
        neoServer.stop();

    }

//    protected Relationship relationship() {
//        Iterator<Relationship> it = node().getRelationships(Direction.OUTGOING).iterator();
//        if (it.hasNext()) return it.next();
//        return node().createRelationshipTo(restGraphDb.createNode(), Type.TEST);
//    }

    protected Node node() {
        return referenceNode;
    }
    protected long nodeId() {
        return referenceNodeId;
    }

    protected GraphDatabaseService getGraphDatabase() {
    	return neoServer.getGraphDatabase();
    }

	protected GraphDatabaseService getRestGraphDb() {
		return restGraphDb;
	}

    protected int countExistingNodes() {
        return IteratorUtil.count(GlobalGraphOperations.at(getGraphDatabase()).getAllNodes());
    }

    protected Node loadRealNode(Node node) {
        return getGraphDatabase().getNodeById(node.getId());
    }
    public String getUserAgent() {
        return neoServer.getUserAgent();
    }
}
