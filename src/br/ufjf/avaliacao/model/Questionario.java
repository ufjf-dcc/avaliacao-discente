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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import br.ufjf.avaliacao.persistent.impl.PerguntaDAO;
import br.ufjf.avaliacao.persistent.impl.PrazoQuestionarioDAO;

/**
 * DTO da Tabela {@code Questionario} contém os atributos e relacionamentos da
 * mesma.
 * 
 */
@Entity
@Table(name = "Questionario")
public class Questionario implements Serializable {
	public static final int COORD = 0, PROF = 1, AUTO = 2, INFRA = 3;

	private static final long serialVersionUID = 1L;

	/**
	 * Campo com ID do questionário. Relaciona com a coluna
	 * {@code idQuestionario} do banco e é gerado por autoincrement do MySQL
	 * através das anotações {@code @GeneratedValue(generator = "increment")}
	 * e {@code @GenericGenerator(name = "increment", strategy = "increment")}
	 * 
	 */
	@Id
	@Column(name = "idQuestionario", unique = true, nullable = false)
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int idQuestionario;

	@Column(name = "tituloQuestionario", length = 45, nullable = false)
	private String tituloQuestionario;

	@Column(name = "tipoQuestionario", length = 45, nullable = false)
	private Integer tipoQuestionario;

	/**
	 * Campo com a situação do questionário(ativo ou inativo). Relaciona com
	 * a coluna {@code ativo} do banco através da anotação
	 * {@code @Column(name = "ativo", nullable = false)}.
	 */
	@Column(name = "ativo", nullable = false)
	private boolean ativo;

	/**
	 * Relacionamento 1 para N entre questionário e pergunta. Mapeada em
	 * {@link Pergunta} pela variável {@code questionario} e retorno do tipo
	 * {@code EAGER} que indica que será carregado automáticamente este dado
	 * quando retornarmos o {@link Questionario} .
	 * 
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "questionario")
	private List<Pergunta> perguntas = new ArrayList<Pergunta>();

	/**
	 * Relacionamento N para 1 entre questionario e curso. Mapeando
	 * {@link Curso} na variável {@code curso} e retorno do tipo {@code LAZY}
	 * que indica que não será carregado automáticamente este dado quando
	 * retornarmos o {@link Questionario}.
	 * 
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idCurso", nullable = false)
	private Curso curso;

	/**
	 * Relacionamento 1 para N entre questionario e prazoQuestionario. Mapeada
	 * em {@link PrazoQuestionario} pela variável {@code questionario} e
	 * retorno do tipo {@code LAZY} que indica que não será carregado
	 * automáticamente este dado quando retornarmos a {@link Questionario} .
	 * 
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "questionario")
	private List<PrazoQuestionario> prazos = new ArrayList<PrazoQuestionario>();

	@Transient
	private String nomeTipoQuestionario;

	@Transient
	private boolean editingStatus;

	@Transient
	private String status;

	public int getIdQuestionario() {
		return idQuestionario;
	}

	public void setIdQuestionario(int idQuestionario) {
		this.idQuestionario = idQuestionario;
	}

	public String getTituloQuestionario() {
		return tituloQuestionario;
	}

	public void setTituloQuestionario(String tituloQuestionario) {
		this.tituloQuestionario = tituloQuestionario;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public List<Pergunta> getPerguntas() {
		return (new PerguntaDAO().getPerguntasQuestionario(this));
	}

	public void setPerguntas(List<Pergunta> perguntas) {
		this.perguntas = perguntas;
	}

	public Integer getTipoQuestionario() {
		return tipoQuestionario;
	}

	public void setTipoQuestionario(Integer tipoQuestionario) {
		this.tipoQuestionario = tipoQuestionario;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public String getNomeTipoQuestionario() {
		if (tipoQuestionario == 0)
			return "Avaliação de Coordenador";
		else if (tipoQuestionario == 1)
			return "Avaliação de Professor";
		else if (tipoQuestionario == 2)
			return "Auto-Avaliação";
		else
			return "Avaliação de Infraestrutura";
	}

	public void setNomeTipoQuestionario(String nomeTipoQuestionario) {
		this.nomeTipoQuestionario = nomeTipoQuestionario;
	}

	public boolean isEditingStatus() {
		return editingStatus;
	}

	public void setEditingStatus(boolean editingStatus) {
		this.editingStatus = editingStatus;
	}

	public String getStatus() {
		if (isAtivo()) {
			return "Ativo";
		}
		return "Inativo";
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<PrazoQuestionario> getPrazos() {
		List<PrazoQuestionario> prazos = new PrazoQuestionarioDAO()
				.getPrazos(this);
		return prazos;
	}

	public void setPrazos(List<PrazoQuestionario> prazos) {
		this.prazos = prazos;
	}

}
