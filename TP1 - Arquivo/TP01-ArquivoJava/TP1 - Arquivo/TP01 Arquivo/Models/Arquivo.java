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

  public void Create(T obj) throws Exception{
    
    try{
      arquivo.seek(0);
      int ultimoID = arquivo.readInt();
      ultimoID++;
      arquivo.seek(0);
      arquivo.writeInt(ultimoID);
      obj.setID(ultimoID);
      System.out.println("Este e o id: "+ultimoID);

      boolean spaceFound = false;
      arquivo.seek(TAM_CABECALHO);

      byte[] regitroOBJ = obj.toByteArray();
      short tamanhoRegistroNovo = (short) regitroOBJ.length;
      

      while (arquivo.getFilePointer() < arquivo.length()) {

        long lapidePosition = arquivo.getFilePointer();
        byte lapideValor = arquivo.readByte();
        short tamanhoRegistroAtual = arquivo.readShort();
        

        if(lapideValor == '*'){
          if(tamanhoRegistroAtual >= tamanhoRegistroNovo){

            arquivo.seek(lapidePosition);
            arquivo.writeByte(' ');
            arquivo.writeShort(tamanhoRegistroNovo);
            arquivo.write(regitroOBJ);
            spaceFound = true;
            System.out.println("Registro reaproveitado: ");
            break;
          } 
        } else{
          arquivo.skipBytes(tamanhoRegistroAtual);
        }
      }

      if(spaceFound == false){
        arquivo.seek(arquivo.length());
        arquivo.writeByte(' ');
        arquivo.writeShort(tamanhoRegistroNovo);
        arquivo.write(regitroOBJ);
      }

    } catch (Exception e){
        System.out.println("Ocorreu um excecao: " + e);
    }
  }

  /*public int Createe(T obj) throws Exception {
    // Pegar e setar o ID no objeto
    arquivo.seek(0);
    int ultimoID = arquivo.readInt();
    ultimoID++;
    arquivo.seek(0);
    arquivo.writeInt(ultimoID);
    
    obj.setID(ultimoID);

    // Caminhar até o último registro e inseri-lo
    arquivo.seek(arquivo.length());
    byte[] registro = obj.toByteArray();
    short tamRegistro = (short) registro.length;
    arquivo.writeByte(' ');
    arquivo.writeShort(tamRegistro);
    arquivo.write(registro);
      
      return ultimoID;
  } */

  public void delete(int id){
   
    try{
       
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
    try{
      arquivo.seek(TAM_CABECALHO);
      while(arquivo.getFilePointer() < arquivo.length()){

        byte[] ba = obj.toByteArray();
        short objTam = (short) ba.length;


        long lapide = arquivo.getFilePointer();
        byte lapideValor = arquivo.readByte();
        short tamanhoRegistro = arquivo.readShort();
        
        int idAtual = arquivo.readInt();
        

        if(idAtual == id && lapideValor == ' '){
          arquivo.seek(lapide);

          if(tamanhoRegistro >= objTam){

              arquivo.writeByte(' ');
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
          break;
        } else{
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
