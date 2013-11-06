package br.ufjf.avaliacao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * DTO da Tabela {@code Avaliacao} contém os atributos e relacionamentos da
 * mesma.
 * 
 */
@Entity
@Table(name = "Avaliacao")
public class Avaliacao implements Serializable{
 
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Campo com ID da avaliação. Relaciona com a coluna {@code idAvaliacao} do banco e
	 * é gerado por autoincrement do MySQL através das anotações
	 * {@code @GeneratedValue(generator = "increment")} e
	 * {@code @GenericGenerator(name = "increment", strategy = "increment")}
	 * 
	 */
	@Id
	@Column(name = "idAvaliacao", unique = true, nullable = false)
	@GeneratedValue (generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	private int idAvaliacao;
	
	
	/**
	 * Relacionamento N para 1 entre avaliação e usuário. Mapeando
	 * {@link Usuario} na variável {@code avaliando} e retorno do tipo
	 * {@code LAZY} que indica que não será carregado automáticamente este dado
	 * quando retornarmos a {@link Avaliacao}.
	 * 
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "avaliando", nullable = false)
	private Usuario avaliando;
	
	
	/**
	 * Relacionamento N para 1 entre avaliação e usuário. Mapeando
	 * {@link Usuario} na variável {@code avaliado} e retorno do tipo
	 * {@code LAZY} que indica que não será carregado automáticamente este dado
	 * quando retornarmos a {@link Avaliacao}.
	 * 
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "avaliado", nullable = true)
	private Usuario avaliado;
	
	/**
	 * Relacionamento N para 1 entre avaliação e turma. Mapeando
	 * {@link Turma} na variável {@code turma} e retorno do tipo
	 * {@code LAZY} que indica que não será carregado automáticamente este dado
	 * quando retornarmos a {@link Avaliacao}.
	 * 
	 */	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idTurma", nullable = true)
	private Turma turma;
	
	/**
	 * Relacionamento N para 1 entre avaliação e questionário. Mapeando
	 * {@link Questionario} na variável {@code questionario} e retorno do tipo
	 * {@code LAZY} que indica que não será carregado automáticamente este dado
	 * quando retornarmos a {@link Avaliacao}.
	 * 
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idQuestionario", nullable = false)
	private Questionario questionario;

	public int getIdAvaliacao() {
		return idAvaliacao;
	}

	public void setIdAvaliacao(int idAvaliacao) {
		this.idAvaliacao = idAvaliacao;
	}

	public Usuario getAvaliando() {
		return avaliando;
	}

	public void setAvaliando(Usuario avaliando) {
		this.avaliando = avaliando;
	}

	public Usuario getAvaliado() {
		return avaliado;
	}

	public void setAvaliado(Usuario avaliado) {
		this.avaliado = avaliado;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public Questionario getQuestionario() {
		return questionario;
	}

	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}

	
}
