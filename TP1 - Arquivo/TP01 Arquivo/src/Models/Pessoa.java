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
    public void fromByteArray(byte[] registro) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(registro);
        DataInputStream dataInputStream = new DataInputStream(baInputStream);

        try {
            this.ID = dataInputStream.readInt();                // Lê o ID
            this.nome = dataInputStream.readUTF();              // Lê o nome
            this.sobrenome = dataInputStream.readUTF();         // Lê o sobrenome
        } finally {
            dataInputStream.close();                            // Fecha o stream para liberar recursos
        }
    }

    @Override
    public int compareTo() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'compareTo'");
    }

}
