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
import br.pucminas.web.model.Curriculo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import rx.Observable;
import rx.observables.BlockingObservable;

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

	@Inject @ConsulServices ServiceDiscovery services;

	private static final long serialVersionUID = 1L;

	public static final int HTTP_CREATED = 201;
	public static final int HTTP_NOCONTENT = 204;

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
	@Inject CursoBean cursoBean;

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

		Curriculo response = services
				.getCurriculoService()
				.path(String.valueOf(id))
				.request(MediaType.APPLICATION_JSON)
				.get(Curriculo.class);

		return response;
	}

	/*
	 * Support updating and deleting Curriculo entities
	 */

	public String update() {
		
		if (this.id == null)
			return insert();
		
		this.conversation.end();
		
		try {
			Response response = services
					.getCurriculoService()
					.path(String.valueOf(id))
					.request(MediaType.APPLICATION_JSON)
					.put(Entity.entity(this.curriculo, MediaType.APPLICATION_JSON));

			if(response.getStatus() == HTTP_NOCONTENT){
				if (this.id == null)
					return "search?faces-redirect=true";
				else
					return "view?faces-redirect=true&id=" + this.curriculo.getId();
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
					.getCurriculoService()
					.request(MediaType.APPLICATION_JSON)
					.post(Entity.entity(this.curriculo, MediaType.APPLICATION_JSON));
			if(response.getStatus() == HTTP_CREATED){
				if (this.id == null)
					return "search?faces-redirect=true";
				else
					return "view?faces-redirect=true&id=" + this.curriculo.getId();
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
					.getCurriculoService()
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

		HttpResourceGroup httpResourceGroup = Ribbon.createHttpResourceGroup(
				"curso-service",
				ClientOptions.create()
				.withMaxAutoRetriesNextServer(3)
				.withLoadBalancerEnabled(true)
				);

		@SuppressWarnings({ "unchecked", "rawtypes" })
		HttpRequestTemplate<ByteBuf> template = httpResourceGroup
		.newTemplateBuilder("listAll", ByteBuf.class)
		.withMethod("GET")
		.withUriTemplate("/rest/curriculos")
		.withFallbackProvider(new FallbackHandler() {
			@Override
			public Observable<ByteBuf> getFallback(HystrixInvokableInfo hystrixInvokableInfo, Map map) {
				System.out.println("<< Serving fallback result list >>");
				return Observable.just(curriculoCachedResults);
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
			curriculoCachedResults = responseBuffer;
			this.pageItems = gson.fromJson(payload, new TypeToken<List<Curriculo>>(){}.getType());

		} else {
			String payload = curriculoCachedResults.toString(Charset.forName("UTF-8"));
			this.pageItems = gson.fromJson(payload, new TypeToken<List<Curriculo>>(){}.getType());
		}
		
		//TODO - cursos
		//for(Curriculo c : pageItems){
		//	c.setCurso(cursoBean.findById());
		//}
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

		paginate();
		return this.pageItems;
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
	
	private ByteBuf curriculoCachedResults = Unpooled.buffer();
}
