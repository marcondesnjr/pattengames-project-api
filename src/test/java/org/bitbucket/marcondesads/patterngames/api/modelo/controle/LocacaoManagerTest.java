package org.bitbucket.marcondesads.patterngames.api.modelo.controle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import javax.security.auth.login.AccountException;
import org.bitbucket.marcondesads.patterngames.api.modelo.AlocacaoException;
import org.bitbucket.marcondesads.patterngames.api.modelo.Cliente;
import org.bitbucket.marcondesads.patterngames.api.modelo.EstadoJogo;
import org.bitbucket.marcondesads.patterngames.api.modelo.EstadoJogoEnum;
import org.bitbucket.marcondesads.patterngames.api.modelo.Jogo;
import org.bitbucket.marcondesads.patterngames.api.modelo.LocStrategyEnum;
import org.bitbucket.marcondesads.patterngames.api.modelo.Locacao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 
 * @author José Marcondes do Nascimento Junior
 */
public class LocacaoManagerTest {

    private Cliente cli;
    private Jogo jg;
    private LocStrategyEnum tipo;
    
    public LocacaoManagerTest() {
       
    }
    
    @Before
    public void setUp() throws SQLException {
        LocacaoManager.setLocacoes(new ArrayList<Locacao>());
        LocacaoManager.setClientes(new ArrayList<Cliente>());
        LocacaoManager.setJogos(new ArrayList<Jogo>());
        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/pattern-games","postgres","123");
        conn.createStatement().executeUpdate("DELETE FROM LOCACAO; "
                + "DELETE FROM OBSERVADO ;"
                + "DELETE FROM JOGO ;"
                + "DELETE FROM CLIENTE ;");
        cli = new Cliente("login", "senha", "pattensgames","117.376.474-78", "junior3917@gmail.com");
        jg = new Jogo("Devil May Cry");
        if(LocalDate.now().getDayOfWeek() == DayOfWeek.FRIDAY)
            tipo = LocStrategyEnum.LOCACAO_ESPECIAL;
        else
            tipo = LocStrategyEnum.LOCACAO_NORMAL;
    }
    
    @After
    public void tearDown() throws Exception{
        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/pattern-games","postgres","123");
        conn.createStatement().executeUpdate("DELETE FROM LOCACAO; "
                + "DELETE FROM OBSERVADO ;"
                + "DELETE FROM JOGO ;"
                + "DELETE FROM CLIENTE ;");
        LocacaoManager.setLocacoes(new ArrayList<Locacao>());
        LocacaoManager.setClientes(new ArrayList<Cliente>());
        LocacaoManager.setJogos(new ArrayList<Jogo>());
    }

    /**
     * Test of cadastrarCliente method, of class LocacaoManager.
     */
    @Test
    public void testCadastrarCliente() throws Exception {
        System.out.println("cadastrarCliente");
        Cliente cli = this.cli;
        LocacaoManager instance = new LocacaoManager();
        instance.cadastrarCliente(cli);
        assertTrue(instance.getClientes().contains(cli));
        try{
            instance.cadastrarCliente(new Cliente("", "senha", "pattensgames","117.376.474-78", "pattengames3@gmail.com"));
        }catch(ClienteInvalido e){
            try{
                instance.cadastrarCliente(new Cliente("login", "", "pattensgames","117.376.474-78", "pattengames3@gmail.com"));
            }catch(ClienteInvalido e2){
                try{
                    instance.cadastrarCliente(new Cliente("login", "senha", "","117.376.474-78", "pattengames3@gmail.com"));
                }catch(ClienteInvalido e3){
                    try{
                        instance.cadastrarCliente(new Cliente("login", "senha", "pattensgames","", "pattengames3@gmail.com"));
                    }catch(ClienteInvalido e4){
                        try{
                            instance.cadastrarCliente(new Cliente("login", "senha", "pattensgames","123.123.123-12", "pattengames3@gmail.com"));
                        }catch(ClienteInvalido e5){
                            try{
                                instance.cadastrarCliente(new Cliente("login", "senha", "pattensgames","117.376.474-78", ""));
                            }catch(ClienteInvalido ex6){
                                return;
                            }
                        }
                    }
                }
            }
        }
        fail();
    }

    /**
     * Test of excluirCliente method, of class LocacaoManager.
     */
    @Test
    public void testExcluirCliente() throws Exception {
        System.out.println("excluirCliente");
        LocacaoManager instance = new LocacaoManager();
        instance.cadastrarCliente(cli);
        instance.excluirCliente(cli.getLogin());
        assertFalse(instance.getClientes().contains(cli));
    }

    /**
     * Test of cadastrarJogo method, of class LocacaoManager.
     */
    @Test
    public void testCadastrarJogo() throws Exception{
        System.out.println("cadastrarJogo");
        Jogo jogo = this.jg;
        LocacaoManager instance = new LocacaoManager();
        instance.cadastrarJogo(jogo);
        assertTrue(instance.getJogos().contains(jg));
    }

    /**
     * Test of excluirJogo method, of class LocacaoManager.
     */
    @Test
    public void testExcluirJogo() throws Exception{
        System.out.println("excluirJogo");
        LocacaoManager instance = new LocacaoManager();
        instance.cadastrarJogo(jg);
        instance.excluirJogo(jg.getId());
        assertFalse(instance.getJogos().contains(jg));
    }

    /**
     * Test of realizarLocacao method, of class LocacaoManager.
     */
    @Test
    public void testRealizarLocacao() throws Exception {
        System.out.println("realizarLocacao");
        Cliente cli = this.cli;
        Jogo jogo = this.jg;
        LocacaoManager instance = new LocacaoManager();
        
        try{
            LocacaoManager.realizarLocacao("login","senha", jogo);
        }catch(AlocacaoException | AccountException e ){
            LocacaoManager.cadastrarCliente(cli);
            LocacaoManager.cadastrarJogo(jogo);
            Locacao loc = LocacaoManager.realizarLocacao("login", "senha", jogo);
            assertTrue(LocacaoManager.getLocacoes().contains(loc));
            assertEquals(jogo, loc.getJogo());
            assertEquals(cli,loc.getCliente());
            assertEquals(tipo, loc.getTipo());
            assertEquals(EstadoJogoEnum.ALOCADO, jogo.getEstado());
            return;
        }
        fail();
    }

    /**
     * Test of realizarDesalocacao method, of class LocacaoManager.
     */
    @Test
    public void testRealizarDesalocacao() throws Exception {
        System.out.println("realizarDesalocacao");
        int id;
        LocacaoManager instance = new LocacaoManager();
        LocacaoManager.cadastrarCliente(cli);
        LocacaoManager.cadastrarJogo(jg);
        Locacao loc = LocacaoManager.realizarLocacao(cli.getLogin(), cli.getSenha(), jg);
        LocacaoManager.realizarDesalocacao(loc.getId());
        assertFalse(LocacaoManager.getLocacoes().contains(loc));
    }
    
//    @Test
//    public void testDesalocacaoComObservador() throws Exception{
//        System.out.println("realizarDesalocacaoComObservador");
//        LocacaoManager instance = new LocacaoManager();
//        instance.cadastrarCliente(cli);
//        instance.cadastrarJogo(jg);
//        Locacao loc = instance.realizarLocacao(cli.getLogin(), cli.getSenha(), jg);
//        instance.adicionarObservador(cli, jg);
//        instance.realizarDesalocacao(loc.getId());
//    }
}
