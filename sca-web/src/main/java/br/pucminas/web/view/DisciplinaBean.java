package br.pucminas.web.view;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

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
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.ribbon.ClientOptions;
import com.netflix.ribbon.Ribbon;
import com.netflix.ribbon.http.HttpRequestTemplate;
import com.netflix.ribbon.http.HttpResourceGroup;
import com.netflix.ribbon.hystrix.FallbackHandler;

import br.pucminas.web.consul.ConsulServices;
import br.pucminas.web.consul.ServiceDiscovery;
import br.pucminas.web.model.Disciplina;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import rx.Observable;
import rx.observables.BlockingObservable;

/**
 * Backing bean for Disciplina entities.
 */

@Named
@Stateful
@ConversationScoped
public class DisciplinaBean implements Serializable {

	@Inject @ConsulServices ServiceDiscovery services;

	private static final long serialVersionUID = 1L;

	public static final int HTTP_CREATED = 201;
	public static final int HTTP_NOCONTENT = 204;

	/*
	 * Support creating and retrieving Disciplina entities
	 */

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Disciplina disciplina;

	public Disciplina getDisciplina() {
		return this.disciplina;
	}

	public void setDisciplina(Disciplina disciplina) {
		this.disciplina = disciplina;
	}

	@Inject
	private Conversation conversation;

	@PersistenceContext(unitName = "disciplina-service-persistence-unit", type = PersistenceContextType.EXTENDED)
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
			this.disciplina = this.example;
		} else {
			this.disciplina = findById(getId());
		}
	}

	public Disciplina findById(Long id) {

		Disciplina response = services
				.getDisciplinaService()
				.path(String.valueOf(id))
				.request(MediaType.APPLICATION_JSON)
				.get(Disciplina.class);

		return response;
	}

	/*
	 * Support updating and deleting Disciplina entities
	 */

	public String update() {

		if (this.id == null)
			return insert();
		
		this.conversation.end();
		
		try {
			Response response = services
					.getDisciplinaService()
					.path(String.valueOf(id))
					.request(MediaType.APPLICATION_JSON)
					.put(Entity.entity(this.disciplina, MediaType.APPLICATION_JSON));

			if(response.getStatus() == HTTP_NOCONTENT){
				if (this.id == null)
					return "search?faces-redirect=true";
				else
					return "view?faces-redirect=true&id=" + this.disciplina.getId();
			}
			else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Ocorreu algum erro ao incluir. "));
				return null;
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	public String insert() {

		this.conversation.end();

		try {
			Response response = services
					.getDisciplinaService()
					.request(MediaType.APPLICATION_JSON)
					.post(Entity.entity(this.disciplina, MediaType.APPLICATION_JSON));
			if(response.getStatus() == HTTP_CREATED){
				if (this.id == null)
					return "search?faces-redirect=true";
				else
					return "view?faces-redirect=true&id=" + this.disciplina.getId();
			}
			else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Ocorreu algum erro ao incluir "));
				return null;
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
			Response response = services
					.getDisciplinaService()
					.path(String.valueOf(id))
					.request(MediaType.APPLICATION_JSON)
					.delete();

			if(response.getStatus() == HTTP_NOCONTENT){
				return "search?faces-redirect=true";
			}
			else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Ocorreu algum erro ao deletar " + String.valueOf(id)));
				return null;
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/*
	 * Support searching Disciplina entities with pagination
	 */

	private int page;
	private long count;
	private List<Disciplina> pageItems;

	private Disciplina example = new Disciplina();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Disciplina getExample() {
		return this.example;
	}

	public void setExample(Disciplina example) {
		this.example = example;
	}

	public String search() {
		this.page = 0;
		return null;
	}

	public void paginate() {

		HttpResourceGroup httpResourceGroup = Ribbon.createHttpResourceGroup(
				"disciplina-service",
				ClientOptions.create()
				.withMaxAutoRetriesNextServer(3)
				.withLoadBalancerEnabled(true)
				);

		@SuppressWarnings({ "unchecked", "rawtypes" })
		HttpRequestTemplate<ByteBuf> template = httpResourceGroup
		.newTemplateBuilder("listAll", ByteBuf.class)
		.withMethod("GET")
		.withUriTemplate("/rest/disciplinas")
		.withFallbackProvider(new FallbackHandler() {
			@Override
			public Observable<ByteBuf> getFallback(HystrixInvokableInfo hystrixInvokableInfo, Map map) {
				System.out.println("<< Serving fallback result list >>");
				return Observable.just(disciplinaCachedResults);
			}
		})
		.build();

		BlockingObservable<ByteBuf> obs = template.requestBuilder()
				.withHeader("Content-Type", "application/json; charset=utf-8")
				.withRequestProperty("start",this.page * getPageSize())
				.withRequestProperty("max", getPageSize())
				.build()
				.observe().toBlocking();

		ByteBuf responseBuffer = obs.last().copy().retain();
		Gson gson = new Gson();
		if(responseBuffer.capacity()>0) {
			String payload = responseBuffer.toString(Charset.forName("UTF-8"));
			disciplinaCachedResults = responseBuffer;
			this.pageItems = gson.fromJson(payload, new TypeToken<List<Disciplina>>(){}.getType());

		} else {
			String payload = disciplinaCachedResults.toString(Charset.forName("UTF-8"));
			this.pageItems = gson.fromJson(payload, new TypeToken<List<Disciplina>>(){}.getType());
		}
	}

	public List<Disciplina> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}
	
	private ByteBuf disciplinaCachedResults = Unpooled.buffer();

	/*
	 * Support listing and POSTing back Disciplina entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Disciplina> getAll() {

		paginate();
		return this.pageItems;
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final DisciplinaBean ejbProxy = this.sessionContext
				.getBusinessObject(DisciplinaBean.class);

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

				return String.valueOf(((Disciplina) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Disciplina add = new Disciplina();

	public Disciplina getAdd() {
		return this.add;
	}

	public Disciplina getAdded() {
		Disciplina added = this.add;
		this.add = new Disciplina();
		return added;
	}
}
