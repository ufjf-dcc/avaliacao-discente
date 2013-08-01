package br.ufjf.model;

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

import org.hibernate.annotations.GenericGenerator;

/**
 * DTO da Tabela {@code Questionario} contém os atributos e relacionamentos da
 * mesma.
 * 
 */
@Entity
@Table(name = "Questionario")
public class Questionario implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Campo com ID do questionário. Relaciona com a coluna
	 * {@code idQuestionario} do banco e é gerado por autoincrement do MySQL
	 * através das anotações {@code @GeneratedValue(generator = "increment")} e
	 * {@code @GenericGenerator(name = "increment", strategy = "increment")}
	 * 
	 */
	@Id
	@Column(name = "idQuestionario", unique = true, nullable = false)
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int idQuestionario;
	
	/**
	 * Campo com a descrição do questionário. Relaciona com a coluna
	 * {@code nome} do banco através da anotação
	 * {@code @Column(name = "descricao", length = 45, nullable = false)}.
	 */
	@Column(name = "descricao", length = 45, nullable = false)
	private String descricao;
	
	/**
	 * Campo com a situação do questionário(ativo ou inativo). Relaciona com a
	 * coluna {@code ativo} do banco através da anotação
	 * {@code @Column(name = "ativo", nullable = false)}.
	 */
	@Column(name = "ativo", nullable = false)
	private boolean ativo;
	
	/**
	 * Relacionamento 1 para N entre questionário e pergunta. Mapeada em
	 * {@link Pergunta} pela variável {@code questionario} e retorno do tipo
	 * {@code LAZY} que indica que não será carregado automáticamente este dado
	 * quando retornarmos o {@link Questionario} .
	 * 
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "questionario")
	private List<Pergunta> perguntas = new ArrayList<Pergunta>();

	/**
	 * Relacionamento 1 para N entre questionário e avaliação. Mapeada em
	 * {@link Avaliação} pela variável {@code questionario} e retorno do tipo
	 * {@code LAZY} que indica que não será carregado automáticamente este dado
	 * quando retornarmos o {@link Questionario} .
	 * 
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "questionario")
	private List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
	
	/**
	 * Relacionamento N para N entre questionário e curso. Mapeada em
	 * {@link Curso} pela variável {@code questionarios} e retorno do tipo
	 * {@code LAZY} que indica que não será carregado automáticamente este dado
	 * quando retornarmos as {@link Questionario} .
	 * 
	 */
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "questionarios")
	private List<Curso> cursos = new ArrayList<Curso>();
	
	
	/**
	 * Relacionamento N para 1 entre questionário e tipo de questionário. Mapeando
	 * {@link Tipo_Questionario} na variável {@code tipoQuestionario} e retorno do tipo
	 * {@code LAZY} que indica que não será carregado automáticamente este dado
	 * quando retornarmos o {@link Questionario}.
	 * 
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idTipo_Questionario", nullable = false)
	private TipoQuestionario tipoQuestionario;

	public int getIdQuestionario() {
		return idQuestionario;
	}

	public void setIdQuestionario(int idQuestionario) {
		this.idQuestionario = idQuestionario;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public List<Pergunta> getPerguntas() {
		return perguntas;
	}

	public void setPerguntas(List<Pergunta> perguntas) {
		this.perguntas = perguntas;
	}

	public List<Avaliacao> getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(List<Avaliacao> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	public List<Curso> getCursos() {
		return cursos;
	}

	public void setCursos(List<Curso> cursos) {
		this.cursos = cursos;
	}

	public TipoQuestionario getTipoQuestionario() {
		return tipoQuestionario;
	}

	public void setTipoQuestionario(TipoQuestionario tipoQuestionario) {
		this.tipoQuestionario = tipoQuestionario;
	}
	
	
		
}
