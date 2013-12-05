package br.ufjf.avaliacao.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

/**
 * DTO da Tabela {@code Usuario} contém os atributos e relacionamentos da
 * mesma.
 * 
 */
@Entity
@Table(name = "Usuario")
public class Usuario implements Serializable{
	public static final int COORDENADOR = 0, PROFESSOR = 1, ALUNO = 2,
			ADMIN = 3;
	
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
	private Integer idUsuario;
	
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
	@ManyToOne()
	@JoinColumn(name = "idCurso", nullable = true)
	private Curso curso;

	/**
	 * Campo com o tipo de usuário (Coordenador = 0, Profesor = 1 , Aluno = 2). 
	 * Relaciona com a coluna {@code tipoUsuario} do banco através da anotação
	 * {@code @Column(name = "tipoUsuario", nullable = false)}.
	 */
	@Column(name = "tipoUsuario", nullable = false)
	private Integer tipoUsuario;
	
	/**
	 * Relacionamento 1 para N entre usuário e avaliação. Mapeada em
	 * {@link Avaliacao} pela variável {@code avaliando} e retorno do tipo
	 * {@code LAZY} que indica que não será carregado automáticamente este dado
	 * quando retornarmos o {@link Usuario} .
	 * 
	 */
	@OneToMany(mappedBy = "avaliando")
	private List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
	
	/**
	 * Relacionamento N para N entre usuário e turma. Mapeada em
	 * {@link Turma} pela variável {@code usuários} e retorno do tipo
	 * {@code LAZY} que indica que não será carregado automáticamente este dado
	 * quando retornarmos as {@link Usuario} .
	 * 
	 */
	@ManyToMany(fetch = FetchType.EAGER, mappedBy = "usuarios")
	private List<Turma> turmas = new ArrayList<Turma>();
	
	@Transient
	private String nomeTipoUsuario;
	
	@Transient
	private boolean editingStatus;
	
	
	public Usuario() {
		
	}

	public Usuario(String nome, String email, String senha, Curso curso,
			Integer tipoUsuario) {
		this.nome = nome;
		this.email = email;
		this.senha = senha;
		this.curso = curso;
		this.tipoUsuario = tipoUsuario;
	}
	
	public Usuario(String nome, String email, String senha){//para professores
		
		this.nome = nome;
		this.email = email;
		this.senha = senha;
		this.curso = null;
		this.tipoUsuario = PROFESSOR;
		
	}

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

	public Integer getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(Integer tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

	public List<Avaliacao> getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(List<Avaliacao> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	public String getNomeTipoUsuario() {
		if(tipoUsuario==COORDENADOR)
			return "Coordenador";
		else if (tipoUsuario==PROFESSOR)
			return "Professor";
		else if (tipoUsuario==ALUNO)
			return "Aluno";
		else if (tipoUsuario==ADMIN)
			return "Administrador";
		return null;
	}

	public void setNomeTipoUsuario(String nomeTipoUsuario) {
		this.nomeTipoUsuario = nomeTipoUsuario;
	}

	public boolean isEditingStatus() {
		return editingStatus;
	}

	public void setEditingStatus(boolean editingStatus) {
		this.editingStatus = editingStatus;
	}

	public List<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(List<Turma> turmas) {
		this.turmas = turmas;
	}
	
	public void addTurmas(Turma turma) {
		this.turmas.add(turma);
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}
	
	
	
	
}