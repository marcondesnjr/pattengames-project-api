package org.bitbucket.marcondesads.patterngames.api.modelo;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Predicate;
import org.bitbucket.marcondesads.patterngames.api.modelo.dao.IdManager;

/**
 * Classe que representa um jogo cadastrado no sistema, este jogo pode ser observado e cada
 * vez que este jogo for  desalocado todos os seus observadores serão informados.
 * @author José Marcondes do Nascimento Junior
 */
public class Jogo implements Observable{
    private final int id;
    private String nome;
    private EstadoJogo estado;
    private Collection<Observer> observers;

    /**
     * Cria um novo jogo com o nome especificado, um jogo recentimente criado é tratado com o estado DISPONIVEL.
     * @param nome 
     */
    public Jogo(String nome){
        this(nome,EstadoJogoEnum.DISPONIVEL);
    }
    
    public Jogo(String nome, EstadoJogo est){
        this(IdManager.getJogoId(),nome,est);
    }
    
    public Jogo(int id, String nome, EstadoJogo est){
        this.id = id;
        this.nome = nome;
        this.observers = new HashSet<>();
        this.estado = est;
    }
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public EstadoJogo getEstado() {
        return estado;
    }

    public Collection<Observer> getObservers() {
        return Collections.unmodifiableCollection(observers);
    }

    public void setObservers(Collection<Observer> observers) {
        this.observers = observers;
    }
    
    

    public void setEstado(EstadoJogo estado) {
        this.estado = estado;
    }

    @Override
    public void addObserver(Observer obs) {
        this.observers.add(obs);
    }

    @Override
    public void remObserver(final Observer obs) {
        this.observers.removeIf(new Predicate<Observer>() {

            @Override
            public boolean test(Observer t) {
                return ((Cliente) obs).getLogin().equals(((Cliente)t).getLogin());
            }
        });
    }

    @Override
    public void notifyObs() {
        for(Observer obs: this.observers){
            obs.doAction(this);
        }
    }
    
    public void alocar() throws AlocacaoException{
        estado.alocar(this);
    }
    
    public void desalocar() throws AlocacaoException{
        estado.desalocar(this);
        for(Observer obs: observers){
            obs.doAction(this);
        }
    }

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Jogo other = (Jogo) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
    
    
}
