package models;
import java.io.*;

// Descricao dos metodos genericos na interface @Registro
public class Livro implements Registro {

    private int ID;
    public String nome;
    public String altor;

    @Override
    public int compareTo(Object o) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'compareTo'");
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void setID(int id) {
        this.ID = id;
    }

    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream ba_out = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(ba_out);
        dos.writeInt(this.ID);
        dos.writeUTF(nome);
        dos.writeUTF(altor);

        return ba_out.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] registro) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(registro);
        DataInputStream dataInputStream = new DataInputStream(baInputStream);

        try {
            this.ID = dataInputStream.readInt();                
            this.nome = dataInputStream.readUTF();              
            this.altor = dataInputStream.readUTF();         
        } finally {
            dataInputStream.close();                        
        }
    }

    @Override
    public int compareTo() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'compareTo'");
    }

}
