import java.io.*;
import java.net.*;
import controller.MovimentoJpaController;
import controller.PessoaJpaController;
import model.Movimento;

public class CadastroThreadV2 extends Thread {
    private MovimentoJpaController ctrlMov;
    private PessoaJpaController ctrlPessoa;
    private Socket s1;

    public CadastroThreadV2(MovimentoJpaController ctrlMov, PessoaJpaController ctrlPessoa, Socket s1) {
        this.ctrlMov = ctrlMov;
        this.ctrlPessoa = ctrlPessoa;
        this.s1 = s1;
    }

    public void run() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(s1.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(s1.getInputStream());

           
            String comando = (String) in.readObject();

           
            if (comando.equals("E") || comando.equals("S")) {
                String tipo = comando;
                String idPessoa = (String) in.readObject();
                String idProduto = (String) in.readObject();
                int quantidade = (int) in.readObject();
                double valorUnitario = (double) in.readObject();

                
                Movimento movimento = new Movimento();
                movimento.setTipo(tipo);
                movimento.setPessoa(ctrlPessoa.findPessoa(idPessoa)); 
                movimento.setProduto(ctrlProduto.findProduto(idProduto)); 
                movimento.setQuantidade(quantidade);
                movimento.setValorUnitario(valorUnitario);

                
                ctrlMov.create(movimento);

               
                if (tipo.equals("E")) {
                    
                } else {
                  
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}

// Main.java
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import controller.MovimentoJpaController;
import controller.PessoaJpaController;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CadastroServerPU");
        MovimentoJpaController ctrlMov = new MovimentoJpaController(emf);
        PessoaJpaController ctrlPessoa = new PessoaJpaController(emf);

        try (ServerSocket ss = new ServerSocket(4321)) {
            while (true) {
                Socket s = ss.accept();
                CadastroThreadV2 ct = new CadastroThreadV2(ctrlMov, ctrlPessoa, s);
                ct.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
