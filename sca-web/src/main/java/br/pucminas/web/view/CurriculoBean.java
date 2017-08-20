package br.pucminas.web.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.pucminas.web.model.Curriculo;
import br.pucminas.web.model.Curso;

/**
 * Backing bean for Curriculo entities.
 * <p/>
 * This class provides CRUD functionality for all Curriculo entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class CurriculoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving Curriculo entities
	 */

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Curriculo curriculo;

	public Curriculo getCurriculo() {
		return this.curriculo;
	}

	public void setCurriculo(Curriculo curriculo) {
		this.curriculo = curriculo;
	}

	@Inject
	private Conversation conversation;

	@PersistenceContext(unitName = "curso-service-persistence-unit", type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	public String create() {

		this.conversation.begin();
		this.conversation.setTimeout(1800000L);
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.conversation.isTransient()) {
			this.conversation.begin();
			this.conversation.setTimeout(1800000L);
		}

		if (this.id == null) {
			this.curriculo = this.example;
		} else {
			this.curriculo = findById(getId());
		}
	}

	public Curriculo findById(Long id) {

		return this.entityManager.find(Curriculo.class, id);
	}

	/*
	 * Support updating and deleting Curriculo entities
	 */

	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				this.entityManager.persist(this.curriculo);
				return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.curriculo);
				return "view?faces-redirect=true&id=" + this.curriculo.getId();
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	public String delete() {
		this.conversation.end();

		try {
			Curriculo deletableEntity = findById(getId());
			Curso curso = deletableEntity.getCurso();
			curso.getCurriculo().remove(deletableEntity);
			deletableEntity.setCurso(null);
			this.entityManager.merge(curso);
			this.entityManager.remove(deletableEntity);
			this.entityManager.flush();
			return "search?faces-redirect=true";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/*
	 * Support searching Curriculo entities with pagination
	 */

	private int page;
	private long count;
	private List<Curriculo> pageItems;

	private Curriculo example = new Curriculo();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Curriculo getExample() {
		return this.example;
	}

	public void setExample(Curriculo example) {
		this.example = example;
	}

	public String search() {
		this.page = 0;
		return null;
	}

	public void paginate() {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<Curriculo> root = countCriteria.from(Curriculo.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Curriculo> criteria = builder
				.createQuery(Curriculo.class);
		root = criteria.from(Curriculo.class);
		TypedQuery<Curriculo> query = this.entityManager.createQuery(criteria
				.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Curriculo> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		Integer semestre = this.example.getSemestre();
		if (semestre != null && semestre.intValue() != 0) {
			predicatesList.add(builder.equal(root.get("semestre"), semestre));
		}
		Integer ano = this.example.getAno();
		if (ano != null && ano.intValue() != 0) {
			predicatesList.add(builder.equal(root.get("ano"), ano));
		}
		Curso curso = this.example.getCurso();
		if (curso != null) {
			predicatesList.add(builder.equal(root.get("curso"), curso));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Curriculo> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Curriculo entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Curriculo> getAll() {

		CriteriaQuery<Curriculo> criteria = this.entityManager
				.getCriteriaBuilder().createQuery(Curriculo.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(Curriculo.class)))
				.getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final CurriculoBean ejbProxy = this.sessionContext
				.getBusinessObject(CurriculoBean.class);

		return new Converter() {

			@Override
			public Object getAsObject(FacesContext context,
					UIComponent component, String value) {

				return ejbProxy.findById(Long.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context,
					UIComponent component, Object value) {

				if (value == null) {
					return "";
				}

				return String.valueOf(((Curriculo) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Curriculo add = new Curriculo();

	public Curriculo getAdd() {
		return this.add;
	}

	public Curriculo getAdded() {
		Curriculo added = this.add;
		this.add = new Curriculo();
		return added;
	}
}
