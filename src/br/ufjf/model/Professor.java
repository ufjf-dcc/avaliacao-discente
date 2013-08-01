package br.ufjf.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * DTO da Tabela {@code Professor} contém os atributos e relacionamentos da
 * mesma.
 * 
 */
@Entity
@Table(name = "Professor")
public class Professor implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Campo com ID do professor. Relaciona com a coluna
	 * {@code idProfessor} do banco e é gerado por autoincrement do MySQL
	 * através das anotações {@code @GeneratedValue(generator = "increment")} e
	 * {@code @GenericGenerator(name = "increment", strategy = "increment")}
	 * 
	 */
	@Id
	@Column(name = "idProfessor", unique = true, nullable = false)
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int idProfessor;
	
	/**
	 * Campo com o nome do professor. Relaciona com a coluna
	 * {@code nome} do banco através da anotação
	 * {@code @Column(name = "nome", length = 45, nullable = false)}
	 * .
	 */
	@Column(name = "nome", length = 45, nullable = false)
	private String nome;
    
	/**
	 * Campo com a senha do professor. Relaciona com a coluna
	 * {@code senha} do banco através da anotação
	 * {@code @Column(name = "senha", length = 45, nullable = false)}
	 * .
	 */
	@Column(name = "senha", length = 45, nullable = false)
	private String senha;

	/**
	 * Campo com o email do professor. Relaciona com a coluna
	 * {@code email} do banco através da anotação
	 * {@code @Column(name = "email", length = 45, nullable = false)}
	 * .
	 */
	@Column(name = "email", length = 45, nullable = false)
	private String email;	
	
	/**
	 * Relacionamento 1 para N entre professor e turma. Mapeada em
	 * {@link Turma} pela variável {@code professor} e retorno do tipo
	 * {@code LAZY} que indica que não será carregado automáticamente este dado
	 * quando retornarmos o {@link Professor} .
	 * 
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "professor")
	private List<Turma> turmas = new ArrayList<Turma>();

	public int getIdProfessor() {
		return idProfessor;
	}

	public void setIdProfessor(int idProfessor) {
		this.idProfessor = idProfessor;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(List<Turma> turmas) {
		this.turmas = turmas;
	}

		
	
}
