package services;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.HashMap;

import models.*;

public class Indexado <T extends Registro>{

    protected RandomAccessFile arquivo; // Objeto para leitura e escrita no arquivo.
    protected RandomAccessFile indice; // Objeto para leitura e escrita no arquivo.
    protected Constructor<T> construtor; // Construtor do tipo T, usado para criar instâncias de T.
    final protected int TAM_CABECALHO = 4; // Tamanho fixo do cabeçalho do arquivo.
    HashMap<Integer, Long> index = new HashMap<>();  //Tabela hash de indexacao para pesquisa

    


    public void Create(T obj) throws Exception{
        try{
          arquivo.seek(0);
          int ultimoID = arquivo.readInt();
          ultimoID++;
          arquivo.seek(0);
          arquivo.writeInt(ultimoID);
          obj.setID(ultimoID);

          
          //System.out.println("Este e o id: "+ultimoID);
    
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
                //System.out.println("Registro reaproveitado: ");
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

    public T read(int id)throws Exception{
        //TODO fazer a classe read
        T obj = construtor.newInstance();
        return obj;
    }
}
