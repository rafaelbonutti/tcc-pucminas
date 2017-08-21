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
	private Long disciplina;
	
	@Transient
	private Disciplina disciplinaEntity;

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

	public Long getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(Long disciplina) {
		this.disciplina = disciplina;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (disciplina != null)
			result += "disciplina: " + disciplina;
		return result;
	}

	public Disciplina getDisciplinaEntity() {
		return disciplinaEntity;
	}

	public void setDisciplinaEntity(Disciplina disciplinaEntity) {
		this.disciplinaEntity = disciplinaEntity;
	}

}