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
 * @author Christoph Knabe 2007-05-23
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
	
	public <E> void save(final E object){
		logger.info("save: " + object);
		final Client client = (Client)object;
		this.pool.add(client);
	}

	public <E> void delete(final E object){
		logger.info("delete: " + object);
		this.pool.remove(object);
	}

	public <E> E load(final Class<E> resultClass, final long id) throws Exc {
		logger.info("load: " + id);
		for(final Object elem: this.pool){
			final Client client = (Client)elem;
			if(client.getId()==id){
				return (E)client;
			}
		}
		throw new Exc("Object of class {0} with id {1} not found", resultClass.getName(), new Long(id));
	}
	
	public <E> List<E> search(final Class<E> queryResultClass) throws Exc {
		logger.info("search: " + queryResultClass.getName());
		final List<E> result = new ArrayList<E>(this.pool.size());
		for(final Object elem: this.pool){
			result.add((E)elem);
		}
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
