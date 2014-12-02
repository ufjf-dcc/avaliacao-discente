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
	 * DTO da Tabela {@code Curso} contém os atributos e relacionamentos da mesma.
	 * 
	 */
	@Entity
	@Table(name = "Matricula")
	public class Matricula implements Serializable {

		private static final long serialVersionUID = 1L;

		
		@Id
		@Column(name = "idMatricula", unique = true, nullable = false)
		@GeneratedValue(generator = "increment")
		@GenericGenerator(name = "increment", strategy = "increment")
		private int idCurso;

		
		@Column(name = "matricula", length = 45, nullable = false)
		private String matricula;

		@ManyToOne(fetch = FetchType.EAGER)
		@JoinColumn(name = "idUsuario", nullable = false)
		private Usuario usuario;
		
		public String getMatricula() {
			return matricula;
		}

		public void setMatricula(String matricula) {
			this.matricula = matricula;
		}

		public Usuario getUsuario() {
			return usuario;
		}

		public void setUsuario(Usuario usuario) {
			this.usuario = usuario;
		}
	}
	

