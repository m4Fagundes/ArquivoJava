import java.io.*;

public class Pessoa implements Registro {

    private int ID;
    public String nome;
    public String sobrenome;

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
        dos.writeUTF(sobrenome);

        return ba_out.toByteArray();
    }

    @Override
    public void fromByteArray(byte ba[]) throws Exception {
        ByteArrayInputStream ba_in = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(ba_in);
        this.ID = dis.readInt();
        this.nome = dis.readUTF();
        this.sobrenome = dis.readUTF();
    }

    @Override
    public int compareTo() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'compareTo'");
    }

}
