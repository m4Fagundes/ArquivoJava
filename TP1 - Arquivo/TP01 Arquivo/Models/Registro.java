public interface Registro extends Comparable<Object>, Cloneable {
    
    public int getID();
    public void setID(int id);
    public byte[] toByteArray();
    public void fromByteArray();
    public int compareTo();
    
}
