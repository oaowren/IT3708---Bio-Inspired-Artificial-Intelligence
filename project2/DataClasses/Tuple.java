package DataClasses;

import java.util.Objects;
import java.io.Serializable;

public class Tuple<X, Y> implements Serializable {

    private static final long serialVersionUID = -6488429050034814203L;
    public final X x;
    public final Y y; 
    public Tuple(X x, Y y) { 
      this.x = x; 
      this.y = y; 
    } 

    @Override
    public boolean equals(Object o){
        if (o == this){
            return true;
        }
        if (!(o instanceof Tuple<? ,?>)) { 
            return false; 
        } 
        
        Tuple<?, ?> c = (Tuple<?, ?>) o;
        return Objects.equals(c.x, this.x) && Objects.equals(c.y, this.y);
    } 

    @Override
    public int hashCode() {
        return (x == null ? 0 : x.hashCode()) ^ (y == null ? 0 : y.hashCode());
    }
  } 