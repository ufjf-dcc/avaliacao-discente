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
 * DTO da Tabela {@code Resposta} contém os atributos e relacionamentos da
 * mesma.
 * 
 */
@Entity
@Table(name = "Resposta")
public class Resposta implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Campo com ID da resposta. Relaciona com a coluna
	 * {@code idResposta} do banco e é gerado por autoincrement do MySQL
	 * através das anotações {@code @GeneratedValue(generator = "increment")} e
	 * {@code @GenericGenerator(name = "increment", strategy = "increment")}
	 * 
	 */
	@Id
	@Column(name = "idResposta", unique = true, nullable = false)
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int idResposta;
	
	/**
	 * Campo com a descrição da resposta. Relaciona com a coluna
	 * {@code resposta} do banco através da anotação
	 * {@code @Column(name = "resposta", length = 45, nullable = false)}
	 * .
	 */
	@Column(name = "resposta", length = 45, nullable = false)
	private String resposta;
	
	/**
	 * Relacionamento N para 1 entre resposta e pergunta. Mapeando
	 * {@link Pergunta} na variável {@code pergunta} e retorno do tipo
	 * {@code LAZY} que indica que não será carregado automáticamente este dado
	 * quando retornarmos o {@link Resposta}.
	 * 
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idPergunta", nullable = false)
	private Pergunta pergunta;
	
	/**
	 * Relacionamento N para 1 entre resposta e avaliação. Mapeando
	 * {@link Avaliacao} na variável {@code avaliacao} e retorno do tipo
	 * {@code LAZY} que indica que não será carregado automáticamente este dado
	 * quando retornarmos o {@link Resposta}.
	 * 
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idAvliacao", nullable = false)
	private Avaliacao avaliacao;

	public int getIdResposta() {
		return idResposta;
	}

	public void setIdResposta(int idResposta) {
		this.idResposta = idResposta;
	}

	public String getResposta() {
		return resposta;
	}

	public void setResposta(String resposta) {
		this.resposta = resposta;
	}

	public Pergunta getPergunta() {
		return pergunta;
	}

	public void setPergunta(Pergunta pergunta) {
		this.pergunta = pergunta;
	}

	public Avaliacao getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(Avaliacao avaliacao) {
		this.avaliacao = avaliacao;
	}

}
