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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * DTO da Tabela {@code Curso} contém os atributos e relacionamentos da mesma.
 * 
 */
@Entity
@Table(name = "Curso")
public class Curso implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Campo com ID do curso. Relaciona com a coluna {@code idCurso} do banco e
	 * é gerado por autoincrement do MySQL através das anotações
	 * {@code @GeneratedValue(generator = "increment")} e
	 * {@code @GenericGenerator(name = "increment", strategy = "increment")}
	 * 
	 */
	@Id
	@Column(name = "idCurso", unique = true, nullable = false)
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int idCurso;

	/**
	 * Campo com o nome do curso. Relaciona com a coluna {@code nomeCurso} do
	 * banco através da anotação
	 * {@code @Column(name = "nomeCurso", length = 45, nullable = false)}.
	 */
	@Column(name = "nomeCurso", length = 500, nullable = false)
	private String nomeCurso;

	@Column(name = "turno", length = 500, nullable = false)
	private String turno;
	
	@Column(name = "modalidade", length = 500, nullable = false)
	private String modalidade;
	
	/**
	 * Relacionamento 1 para N entre curso e questionário. Mapeada em
	 * {@link Questionario} pela variável {@code curso} e retorno do tipo
	 * {@code EAGER} que indica que não será carregado automáticamente este
	 * dado quando retornarmos a {@link Curso} .
	 * 
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "curso")
	private List<Questionario> questionarios = new ArrayList<Questionario>();
	
	

	/**
	 * Relacionamento N para 1 entre usuário e curso. Mapeando {@link Curso} na
	 * variável {@code curso} e retorno do tipo {@code LAZY} que indica que
	 * não será carregado automáticamente este dado quando retornarmos o
	 * {@link Usuario}.
	 * 
	 */
	@OneToOne()
	@JoinColumn(name = "idCoordenador", nullable = true)
	private Usuario coordenador;
	
	
	@OneToOne()
	@JoinColumn(name = "idViceCoordenador", nullable = true)
	private Usuario viceCoordenador;

	public Curso() {
	
	}

	public Curso(String nomeCurso) {
		this.nomeCurso = nomeCurso;
	}

	public Usuario getCoordenador() {
		return coordenador;
	}

	public void setCoordenador(Usuario coordenador) {
		this.coordenador = coordenador;
	}

	public int getIdCurso() {
		return idCurso;
	}

	public void setIdCurso(int idCurso) {
		this.idCurso = idCurso;
	}

	public String getNomeCurso() {
		return nomeCurso;
	}

	public void setNomeCurso(String nomeCurso) {
		this.nomeCurso = nomeCurso;
	}

	public String getTurno() {
		return turno;
	}

	public void setTurno(String turno) {
		this.turno = turno;
	}

	public String getModalidade() {
		return modalidade;
	}

	public void setModalidade(String modalidade) {
		this.modalidade = modalidade;
	}
	
	public List<Questionario> getQuestionarios() {
		return questionarios;
	}

	public void setQuestionarios(List<Questionario> questionarios) {
		this.questionarios = questionarios;
	}

	public Usuario getViceCoordenador() {
		return viceCoordenador;
	}

	public void setViceCoordenador(Usuario viceCoordenador) {
		this.viceCoordenador = viceCoordenador;
	}

}
