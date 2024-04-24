package services;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import models.*;

/**
* Classe genérica Arquivo para manipulação de registros de arquivo.
* 
* @param <T> Tipo do registro que estende a interface Registro.
*          Esta classe assume que T pode ser serializado e deserializado
*          através de um array de bytes.
*/
public class Arquivo<T extends Registro> {

  protected RandomAccessFile arquivo; // Objeto para leitura e escrita no arquivo.
  protected Constructor<T> construtor; // Construtor do tipo T, usado para criar instâncias de T.
  final protected int TAM_CABECALHO = 4; // Tamanho fixo do cabeçalho do arquivo.


  /**
  * Constrói um arquivo que manipula registros do tipo T.
  * O arquivo é criado ou aberto como leitura e escrita ('rw').
  * Um cabeçalho de arquivo é inicializado se o arquivo estiver vazio.
  *
  * @param c Construtor da classe T.
  */
  public Arquivo(Constructor<T> c) throws Exception {

    this.construtor = c;
    arquivo = new RandomAccessFile("pessoas.db", "rw");
    if (arquivo.length() < TAM_CABECALHO) {
      arquivo.seek(0);
      arquivo.writeInt(0);
    }
  }


  /**
  * Cria um novo registro no arquivo.
  * Incrementa o último ID usado, salva o novo ID no objeto e no arquivo,
  * e escreve o objeto serializado no arquivo, reutilizando espaço de registros deletados se possível.
  *
  * @param obj Instância de T para ser armazenada no arquivo.
  */
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
  

  /**
  * Apaga um registro pelo ID marcando seu espaço como deletado (lapide '*').
  *
  * @param id ID do registro a ser deletado.
  */
  public void delete(int id){
   
    try{
      // Primeiro temos que salvar o endereço da lapide e caminhar até o 
      arquivo.seek(TAM_CABECALHO);
      while(arquivo.getFilePointer() < arquivo.length()){

        long lapide = arquivo.getFilePointer();
        arquivo.readByte();
        short tamanhoRegistro = arquivo.readShort();
  
        int idAtual = arquivo.readInt();
        //System.out.println(idAtual);

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


  /**
  * Atualiza um registro no arquivo substituindo o antigo por um novo
  * se o espaço for suficiente, ou escrevendo ao final do arquivo se necessário.     * 
  * @param obj Registro a ser atualizado.
  */
  public void Update(T obj){
    
    int id = obj.getID();
    try{
      arquivo.seek(TAM_CABECALHO);
      while(arquivo.getFilePointer() < arquivo.length()){

        byte[] registroObj = obj.toByteArray();
        short objTam = (short) registroObj.length;


        long lapide = arquivo.getFilePointer();
        byte lapideValor = arquivo.readByte();
        short tamanhoRegistro = arquivo.readShort();
        
        int idAtual = arquivo.readInt();
        

        if(idAtual == id && lapideValor == ' '){
          arquivo.seek(lapide);

          if(tamanhoRegistro >= objTam){

              arquivo.writeByte(' ');
              arquivo.writeShort(objTam);
              arquivo.write(registroObj);

          } else {
            arquivo.seek(lapide);
            arquivo.writeByte('*');
            arquivo.seek(arquivo.length());
            arquivo.writeByte(' ');
            arquivo.writeShort(objTam);
            arquivo.write(registroObj);
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


  /**
  * Lê e deserializa um registro pelo ID.
  * Retorna null se o registro não for encontrado ou se estiver marcado como deletado.
  * 
  * @param id ID do registro a ser lido.
  * @return Uma instância de T se encontrado, null caso contrário.
  */
  public T read(int id) throws Exception{
    
    arquivo.seek(TAM_CABECALHO);
    T obj = construtor.newInstance();

    while (arquivo.getFilePointer() < arquivo.length()) {

      long lapide = arquivo.getFilePointer();
      byte lapideValor = arquivo.readByte();
      short tamanhoRegistro = arquivo.readShort();
      byte[] registro; 

      int index = arquivo.readInt();
      arquivo.seek(lapide);
      arquivo.readByte();
      arquivo.readShort();

      if(index == id && lapideValor != '*'){
        System.out.println(tamanhoRegistro);
        registro = new byte[tamanhoRegistro];
        arquivo.read(registro,0,tamanhoRegistro);
        obj.fromByteArray(registro);
        return obj;
      } else{
        arquivo.skipBytes(tamanhoRegistro);
      }
    }
    return null;
  }


  /**
  * Fecha o arquivo de registros.
  * Deve ser chamado para garantir que todas as alterações sejam salvas.
  */
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
