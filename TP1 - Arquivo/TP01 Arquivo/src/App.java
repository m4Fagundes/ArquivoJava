public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");

        Arquivo<Pessoa> fileTeste = new Arquivo<>(Pessoa.class.getConstructor());
        
        Pessoa pessoa = new Pessoa();

        pessoa.nome = "Matheus";
        pessoa.sobrenome = "Fagundes";

        Pessoa pessoa2 = new Pessoa();

        pessoa2.nome = "Luca";
        pessoa2.sobrenome = "Lourenco";

        Pessoa pessoa3 = new Pessoa();

        pessoa3.nome = "Livia";
        pessoa3.sobrenome = "Camara";

        int id1 = fileTeste.Create(pessoa);
        int id2 = fileTeste.Create(pessoa2);
        int id3 = fileTeste.Create(pessoa3);
        
        fileTeste.delete(2);

        pessoa3.nome = "Teste";
        pessoa3.sobrenome = "Cabess";


        fileTeste.Update(pessoa3);

        fileTeste.close(); 
        
    }
}
