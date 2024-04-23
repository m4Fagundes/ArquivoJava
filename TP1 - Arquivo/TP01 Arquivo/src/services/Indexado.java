package services;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import models.*;

public class Indexado <T extends Registro>{

    protected RandomAccessFile arquivo; // Objeto para leitura e escrita no arquivo.
    protected RandomAccessFile indice; // Objeto para leitura e escrita no arquivo.
    protected Constructor<T> construtor; // Construtor do tipo T, usado para criar instâncias de T.
    final protected int TAM_CABECALHO = 4; // Tamanho fixo do cabeçalho do arquivo.

    public int create(T obj) throws Exception{

        if(arquivo.length() == 0){
            arquivo.writeInt(0);
        }

        arquivo.seek(0);
        int id = arquivo.readInt();
        id +=1;
        arquivo.seek(0);
        arquivo.writeInt(id);
        arquivo.seek(arquivo.length());
        obj.setID(id);
        byte[] registro = obj.toByteArray();
        long posicao = arquivo.getFilePointer();
        arquivo.write(registro);

        return id;
        
    }
}
