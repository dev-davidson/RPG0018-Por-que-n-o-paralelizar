import java.io.*;
import java.net.*;
import java.util.List;
import model.Produto;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;

public class CadastroThread extends Thread {
    private ProdutoJpaController ctrl;
    private UsuarioJpaController ctrlUsu;
    private Socket s1;

    public CadastroThread(ProdutoJpaController ctrl, UsuarioJpaController ctrlUsu, Socket s1) {
        this.ctrl = ctrl;
        this.ctrlUsu = ctrlUsu;
        this.s1 = s1;
    }

    public void run() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(s1.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(s1.getInputStream());

           
            String login = (String) in.readObject();
            String senha = (String) in.readObject();

            
            if (ctrlUsu.findUsuario(login, senha) == null) {
                s1.close(); 
                return;
            }

           
            while (true) {
                
                String comando = (String) in.readObject();

                
                if (comando.equals("L")) {
                    List<Produto> produtos = ctrl.findProdutoEntities();
                    out.writeObject(produtos);
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CadastroServerPU");
        ProdutoJpaController ctrl = new ProdutoJpaController(emf);
        UsuarioJpaController ctrlUsu = new UsuarioJpaController(emf);

        try (ServerSocket ss = new ServerSocket(4321)) {
            while (true) {
                Socket s = ss.accept();
                CadastroThread ct = new CadastroThread(ctrl, ctrlUsu, s);
                ct.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
