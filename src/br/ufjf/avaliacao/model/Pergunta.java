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

import br.ufjf.avaliacao.persistent.impl.RespostaEspecificaDAO;

/**
 * DTO da Tabela {@code Pergunta} contém os atributos e relacionamentos da
 * mesma.
 * 
 */
@Entity
@Table(name = "Pergunta")
public class Pergunta implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Campo com ID da pergunta. Relaciona com a coluna {@code idPergunta} do
	 * banco e é gerado por autoincrement do MySQL através das anotações
	 * {@code @GeneratedValue(generator = "increment")} e
	 * {@code @GenericGenerator(name = "increment", strategy = "increment")}
	 * 
	 */
	@Id
	@Column(name = "idPergunta", unique = true, nullable = false)
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int idPergunta;

	/**
	 * Campo com o título da pergunta. Relaciona com a coluna {@code pergunta}
	 * do banco através da anotação
	 * {@code @Column(name = "pergunta", length = 45, nullable = false)}.
	 */
	@Column(name = "tituloPergunta", length = 8000, nullable = false)
	private String tituloPergunta;

	@Column(name = "tipoPergunta", nullable = false)
	private Integer tipoPergunta;

	/**
	 * Relacionamento N para 1 entre pergunta e questionário. Mapeando
	 * {@link Questionario} na variável {@code questionario} e retorno do tipo
	 * {@code LAZY} que indica que não será carregado automáticamente este
	 * dado quando retornarmos a {@link Pergunta}.
	 * 
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idQuestionario", nullable = false)
	private Questionario questionario;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pergunta")
	private List<Resposta> respostas = new ArrayList<Resposta>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pergunta")
	private List<RespostaEspecifica> respostasEspecificas = new ArrayList<RespostaEspecifica>();

	@Transient
	private String nomeTipoPergunta;

	public List<Resposta> getRespostas() {
		return respostas;
	}

	public void setRespostas(List<Resposta> respostas) {
		this.respostas = respostas;
	}

	public List<RespostaEspecifica> getRespostasEspecificasBanco() {
		return (new RespostaEspecificaDAO()
				.getRespostasEspecificasPerguntas(this));
	}

	public List<RespostaEspecifica> getRespostasEspecificas() {
		return this.respostasEspecificas;
	}

	public void setRespostasEspecificas(
			List<RespostaEspecifica> respostasEspecificas) {
		this.respostasEspecificas = respostasEspecificas;
	}

	public int getIdPergunta() {
		return idPergunta;
	}

	public void setIdPergunta(int idPergunta) {
		this.idPergunta = idPergunta;
	}

	public String getTituloPergunta() {
		return tituloPergunta;
	}

	public void setTituloPergunta(String tituloPergunta) {
		this.tituloPergunta = tituloPergunta;
	}

	public Integer getTipoPergunta() {
		return tipoPergunta;
	}

	public void setTipoPergunta(Integer tipoPergunta) {
		this.tipoPergunta = tipoPergunta;
	}

	public Questionario getQuestionario() {
		return questionario;
	}

	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}

	public String getNomeTipoPergunta() {
		if (tipoPergunta == 0)
			return "Texto";
		else if (tipoPergunta == 1)
			return "Caixa de Seleção";
		else if (tipoPergunta == 2)
			return "Múltipla Escolha";
		else
			return "Escala Numérica";
	}

	public void setNomeTipoPergunta(String nomeTipoPergunta) {
		this.nomeTipoPergunta = nomeTipoPergunta;
	}

}