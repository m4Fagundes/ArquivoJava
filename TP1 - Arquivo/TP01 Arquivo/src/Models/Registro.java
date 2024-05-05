package models;

import java.util.List;

/**
 * Interface Registro define as operações necessárias para objetos que podem ser
 * armazenados e recuperados em forma de registros numa estrutura de arquivo.
 * Ela estende Cloneable e é comparável com objetos do tipo Object.
 */
public interface Registro extends Comparable<Object>, Cloneable {

    public int getID();
    public void setID(int id);

    /**
    * Serializa o objeto para um array de bytes para armazenamento.
    * 
    * @return O array de bytes que representa o registro serializado.
    */
    public byte[] toByteArray() throws Exception;

    /**
    * Deserializa um array de bytes para obter um objeto Registro.
    * Este método repopula os atributos do objeto usando dados de um array de bytes.
    * 
    * @param registro O array de bytes contendo os dados do registro.
    */
    public void fromByteArray(byte[] registro) throws Exception;

    /**
    * Compara este registro com outro objeto. A implementação desta
    * função deve lançar ClassCastException se o objeto especificado não
    * puder ser comparado com este registro.
    * 
    * @return Um valor inteiro negativo, zero ou positivo quando este objeto
    *         é menor que, igual a, ou maior que o objeto especificado, respectivamente.
    */
    public int compareTo();
    public String getNome();
    public String[] getPalavras();

}
