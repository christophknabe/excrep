package db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import lg.Client;
import static multex.MultexUtil.create;
import multex.Exc;
import multex.Failure;

/** Offers a simple persistence mechanism for demonstration purposes only.
 * The Set of Client object is stored on commit by serialization in a file.
 *   
 * @author Christoph Knabe
 * @since 2007-05-23
 */
public class Persistence {
	

	private static final Logger logger = Logger.getLogger(Persistence.class.getName());
	private static final String FILENAME = Persistence.class.getSimpleName() + ".ser";
	
	public static final long INEXISTENT_ID = 0;
	
	private static long lastId = INEXISTENT_ID;
	
	private final Set<Client> pool;
	

	/** Initializes this Persistence from file FILENAME, or helpwise empty. */ 
	public Persistence() {
		logger.info("");
		final File storage = new File(FILENAME);
		if(!storage.exists()){
			this.pool = Collections.checkedSet(new HashSet<Client>(), Client.class);
			return;
		}
		Set<Client> result;
		try {
			final FileInputStream f = new FileInputStream(FILENAME);
			final ObjectInputStream o = new ObjectInputStream(f);
			lastId = o.readLong();
			result = (Set<Client>)o.readObject();
			o.close();
		} catch (Exception e) {
			throw create(LoadFailure.class, e, FILENAME);
		}
		this.pool = result;
	}
	
	public long getNextId(){
		return ++lastId;
	}
	
	public <E extends Client> void save(final E client){
		logger.info("save: " + client);
		this.pool.add(client);
	}

	public <E extends Client> void delete(final E client){
		logger.info("delete: " + client);
		this.pool.remove(client);
	}

	public <E extends Client> E load(final Class<E> resultClass, final long id) throws Exc {
		logger.info("load: " + id);
		for(final Client client: this.pool){
			if(client.getId()==id){
				return (E)client;
			}
		}
		throw new Exc("Object of class {0} with id {1} not found", resultClass.getName(), new Long(id));
	}
	
	public <E extends Client> List<E> search(final Class<E> queryResultClass) throws Exc {
		logger.info("search: " + queryResultClass.getName());
		final List<E> result = new ArrayList<E>(this.pool.size());
		for(final Client elem: this.pool){
			result.add((E)elem);
		}
        result.sort((Client a, Client b) -> (int)(a.getId()-b.getId()));
		return result;
	}

	public void commit(){
		logger.info("commit");
        String filePath = FILENAME;
		try {
            filePath = new File(FILENAME).getCanonicalPath();
			final FileOutputStream f = new FileOutputStream(filePath);
			final ObjectOutputStream o = new ObjectOutputStream(f);
			o.writeLong(lastId);
			o.writeObject(this.pool);
			o.close();
		} catch (Exception e) {
			throw new Failure("Failure committing persistence into file {0}", e, filePath);
		}
	}
	
	/**Failure loading persistence from file {0}*/
	public static class LoadFailure extends multex.Failure {}
	

}
