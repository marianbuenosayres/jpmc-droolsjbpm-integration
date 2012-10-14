package org.drools.persistence.variablepersistence;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.ObjectMarshallingStrategyAcceptor;

public class StringIDPlaceholderResolverStrategy implements ObjectMarshallingStrategy {

    private Map<String, Object> ids;
    private Map<Object, String> objects;

    private ObjectMarshallingStrategyAcceptor acceptor;
    
    public StringIDPlaceholderResolverStrategy(ObjectMarshallingStrategyAcceptor acceptor) {
        this.acceptor = acceptor;
        this.ids = new HashMap<String, Object>();
        this.objects = new IdentityHashMap<Object, String>();
    }

    public Object read(ObjectInputStream os) throws IOException,
                                                       ClassNotFoundException {
        int id = os.readInt();
        return  ids.get( id );
    }

    public void write(ObjectOutputStream os,
                      Object object) throws IOException {
        String id = ( String ) objects.get( object );
        if ( id == null ) {
            id = String.valueOf(ids.size());
            ids.put( id, object );
            objects.put(  object, id );
        }
        os.writeUTF( id );
    }

    public boolean accept(Object object) {
        return this.acceptor.accept( object );
    }

    public byte[] marshal(Context context,
                          ObjectOutputStream os,
                          Object object) {
        String id = ( String ) objects.get( object );
        if ( id == null ) {
            id = String.valueOf(ids.size());
            ids.put( id, object );
            objects.put(  object, id );
        }
        return id.getBytes();
    }

    public Object unmarshal(Context context,
                            ObjectInputStream is,
                            byte[] object, 
                            ClassLoader classloader ) {
        return ids.get( new String( object ) );
    }
    
    public Context createContext() {
        // no need for context
        return null;
    }

}
