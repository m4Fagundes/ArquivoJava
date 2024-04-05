import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Arquivo<T extends Registro> {

  protected RandomAccessFile arquivo;
  protected Constructor<T> construtor;
  final protected int TAM_CABECALHO = 4;

  public Arquivo(Constructor<T> c) throws Exception {

    this.construtor = c;
    arquivo = new RandomAccessFile("pessoas.db", "rw");
    if (arquivo.length() < TAM_CABECALHO) {
      arquivo.seek(0);
      arquivo.writeInt(0);
    }
  }

  public int Create(T obj) throws Exception {
    // Pegar e setar o ID no objeto
    arquivo.seek(0);
    int ultimoID = arquivo.readInt();
    ultimoID++;
    arquivo.seek(0);
    arquivo.writeInt(ultimoID);
    
    obj.setID(ultimoID);

    // Caminhar até o último registro e inseri-lo
    arquivo.seek(arquivo.length());
    byte[] registro = obj.toByteArray(); //TODO implementar o toByteArray
    short tamRegistro = (short) registro.length;
    arquivo.writeByte(' ');
    arquivo.writeShort(tamRegistro);
    arquivo.write(registro);
      
      return ultimoID;
  }

  public void delete(int id){
   
    try{
      // Primeiro temos que salvar o endereço da lapide e caminhar até o 
      arquivo.seek(TAM_CABECALHO);
      while(arquivo.getFilePointer() < arquivo.length()){

        long lapide = arquivo.getFilePointer();
        arquivo.readByte();
        short tamanhoRegistro = arquivo.readShort();
  
        int idAtual = arquivo.readInt();
        System.out.println(idAtual);

        if(idAtual == id){
          arquivo.seek(lapide);
          arquivo.writeByte('*');
        }
        else{
          arquivo.skipBytes(tamanhoRegistro - 4);
        
        }
      
      }

    } catch (Exception e){
      System.out.println("Ocorreu uma excessao: "+ e);
      
    }
  }

  public void Update(T obj){
    
    int id = obj.getID();
    System.out.println("O ID e: " + obj.getID());
    try{
      arquivo.seek(TAM_CABECALHO);
      while(arquivo.getFilePointer() < arquivo.length()){

        byte[] ba = obj.toByteArray();
        short objTam = (short) ba.length;


        long lapide = arquivo.getFilePointer();
        byte lapideValor = arquivo.readByte();
        short tamanhoRegistro = arquivo.readShort();
        
        int idAtual = arquivo.readInt();
        

        if(idAtual == id && lapide != '*'){
          arquivo.seek(lapide);

          if(tamanhoRegistro >= objTam){

              arquivo.writeShort(objTam);
              arquivo.write(ba);

          } else {
            arquivo.seek(lapide);
            arquivo.writeByte('*');
            arquivo.seek(arquivo.length());
            arquivo.writeByte(' ');
            arquivo.writeShort(objTam);
            arquivo.write(ba);
          }
        }
        else{
          arquivo.skipBytes(tamanhoRegistro - 4);
        
        }
      
      }

    } catch (Exception e){
      System.out.println("Ocorreu uma excessao: "+ e);
      
    }
    
  }

  public void close() {
    try {
      if (arquivo != null) {
        arquivo.close();
      }
    } catch (IOException e) {
      System.out.println("Erro ao fechar o arquivo: " + e.getMessage());
    }
  }

  

}
