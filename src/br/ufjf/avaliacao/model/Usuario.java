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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

/**
 * DTO da Tabela {@code Usuario} contÃƒÆ’Ã‚Â©m os atributos e relacionamentos da
 * mesma.
 * 
 */
@Entity
@Table(name = "Usuario")
public class Usuario implements Serializable {
	public static final int COORDENADOR = 0, PROFESSOR = 1, ALUNO = 2,
			ADMIN = 3;

	private static final long serialVersionUID = 1L;


	@Id
	@Column(name = "idUsuario", unique = true, nullable = false)
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Integer idUsuario;

	@Column(name = "nome", length = 500, nullable = false)
	private String nome;

	@ManyToOne()
	@JoinColumn(name = "idCurso", nullable = true)
	private Curso curso;

	@Column(name = "tipoUsuario", nullable = false)
	private Integer tipoUsuario;

	@Column(name = "cpf", length = 11, nullable = true)
	private String cpf;
	
	@Column(name = "email", length = 200, nullable = true)
	private String email;
	
	@OneToMany(mappedBy = "avaliando")
	private List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "usuario")
	private List<Matricula> matriculas = new ArrayList<Matricula>();
	
	@OneToOne()
	@JoinColumn(name = "idMatriculaAtiva", nullable = true)
	private Matricula matriculaAtiva;
	
	@ManyToMany(fetch = FetchType.EAGER, mappedBy = "usuarios")
	private List<Turma> turmas = new ArrayList<Turma>();

	@Transient
	private String nomeTipoUsuario;

	@Transient
	private boolean editingStatus;

	public Usuario() {

	}

	public Usuario(String nome, Curso curso,
			Integer tipoUsuario) {
		this.nome = nome;
		this.curso = curso;
		this.tipoUsuario = tipoUsuario;
	}

	public Usuario(String nome) {// para professores

		this.nome = nome;
		this.curso = null;
		this.tipoUsuario = PROFESSOR;

	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}
	
	public String getCPF() {
		return cpf;
	}

	public void setCPF(String cpf) {
		this.cpf = cpf;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
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

	public List<Matricula> getMatriculas() {
		return matriculas;
	}

	public void setMatriculas(List<Matricula> matriculas) {
		this.matriculas = matriculas;
	}
	
	public String getNomeTipoUsuario() {
		if (tipoUsuario == COORDENADOR)
			return "Coordenador";
		else if (tipoUsuario == PROFESSOR)
			return "Professor";
		else if (tipoUsuario == ALUNO)
			return "Aluno";
		else if (tipoUsuario == ADMIN)
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


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Matricula getMatriculaAtiva() {
		return matriculaAtiva;
	}

	public void setMatriculaAtiva(Matricula matriculaAtiva) {
		this.matriculaAtiva = matriculaAtiva;
	}

}