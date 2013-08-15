package br.ufjf.model;

import java.beans.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * DTO da Tabela {@code Usuario} contém os atributos e relacionamentos da
 * mesma.
 * 
 */
@Entity
@Table(name = "Usuario")
public class Usuario implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Campo com ID do usuário. Relaciona com a coluna
	 * {@code idUsuario} do banco e é gerado por autoincrement do MySQL
	 * através das anotações {@code @GeneratedValue(generator = "increment")} e
	 * {@code @GenericGenerator(name = "increment", strategy = "increment")}
	 * 
	 */
	@Id
	@Column(name = "idUsuario", unique = true, nullable = false)
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int idUsuario;
	
	/**
	 * Campo com a senha do usuario. Relaciona com a coluna
	 * {@code senha} do banco através da anotação
	 * {@code @Column(name = "senha", length = 45, nullable = false)}
	 * .
	 */
	@Column(name = "senha", length = 45, nullable = false)
	private String senha;
	
	/**
	 * Campo com o nome do usuário. Relaciona com a coluna
	 * {@code nome} do banco através da anotação
	 * {@code @Column(name = "nome", length = 45, nullable = false)}.
	 */
	@Column(name = "nome", length = 45, nullable = false)
	private String nome;

	/**
	 * Campo com o email do usuário. Relaciona com a coluna
	 * {@code email} do banco através da anotação
	 * {@code @Column(name = "email", length = 45, nullable = false)}.
	 */
	@Column(name = "email", length = 45, nullable = false)
	private String email;
	
	/**
	 * Relacionamento N para 1 entre usuário e curso. Mapeando
	 * {@link Curso} na variável {@code curso} e retorno do tipo
	 * {@code LAZY} que indica que não será carregado automáticamente este dado
	 * quando retornarmos o {@link Usuario}.
	 * 
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idCurso", nullable = false)
	private Curso curso;

	/**
	 * Campo com o tipo de usuário (aluno ou coordenador). Relaciona com a
	 * coluna {@code coordenador} do banco através da anotação
	 * {@code @Column(name = "coordenador", nullable = false)}.
	 */
	@Column(name = "coordenador", nullable = false)
	private boolean coordenador;
	
	/**
	 * Relacionamento 1 para N entre usuário e avaliação. Mapeada em
	 * {@link Avaliacao} pela variável {@code avaliando} e retorno do tipo
	 * {@code LAZY} que indica que não será carregado automáticamente este dado
	 * quando retornarmos o {@link Usuario} .
	 * 
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "avaliando")
	private List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
	
	@Transient

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public boolean isCoordenador() {
		return coordenador;
	}

	public void setTipoUsuario(boolean coordenador) {
		this.coordenador = coordenador;
	}

	public List<Avaliacao> getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(List<Avaliacao> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}



	
	
	
}