package br.pucminas.curso.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;
import javax.persistence.Enumerated;
import br.pucminas.curso.model.Classificacao;

@Entity
@Table(name = "TBGRADECURRICULAR")
@XmlRootElement
public class GradeCurricular implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private Long id;

	@Version
	@Column(name = "VERSION")
	private int version;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "FKCURRICULO")
	private Curriculo curriculo;

	@Column(name = "DISCIPLINA")
	private Long disciplinaId;

	@Transient
	private Disciplina disciplina;

	@Column(name = "PERIODO")
	private Integer periodo;

	@Enumerated
	@Column(name = "CLASSIFICACAO")
	private Classificacao classificacao;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof GradeCurricular)) {
			return false;
		}
		GradeCurricular other = (GradeCurricular) obj;
		if (id != null) {
			if (!id.equals(other.id)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public Curriculo getCurriculo() {
		return this.curriculo;
	}

	public void setCurriculo(final Curriculo curriculo) {
		this.curriculo = curriculo;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Classificacao getClassificacao() {
		return classificacao;
	}

	public void setClassificacao(Classificacao classificacao) {
		this.classificacao = classificacao;
	}

	@Override
	public String toString() {
		return "GradeCurricular [" + (id != null ? "id=" + id + ", " : "") + "version=" + version + ", "
				+ (curriculo != null ? "curriculo=" + curriculo + ", " : "")
				+ (disciplinaId != null ? "disciplinaId=" + disciplinaId + ", " : "")
				+ (disciplina != null ? "disciplina=" + disciplina + ", " : "")
				+ (periodo != null ? "periodo=" + periodo + ", " : "")
				+ (classificacao != null ? "classificacao=" + classificacao : "") + "]";
	}

	public Long getDisciplinaId() {
		return disciplinaId;
	}

	public void setDisciplinaId(Long disciplinaId) {
		this.disciplinaId = disciplinaId;
	}

	public Disciplina getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(Disciplina disciplina) {
		this.disciplina = disciplina;
	}

}