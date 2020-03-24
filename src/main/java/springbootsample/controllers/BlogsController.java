package springbootsample.controllers;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import springbootsample.bean.BlogPost;
import springbootsample.bean.Response;

@Controller
@ResponseBody
public class BlogsController extends AbstractController {

	private static ConcurrentHashMap<String, BlogPost> BLOG_DB = new ConcurrentHashMap<>();
	
	
	@RequestMapping(path="/blogs",produces = "application/json",method = RequestMethod.POST)
	@Async("theExec")
	public CompletableFuture<Response<BlogPost>> createPost(HttpServletRequest req,@RequestBody BlogPost post){
		
			
			Response<BlogPost> response = new Response<>();
			response.setNextOffset(-1);
			
			try {
				sleep();
				response.setResult(post);
				if( validUser(req) ) {
					if( validatePost(post,response) ) {
						if( post.getId() == null ) {
							post.setId(UUID.randomUUID().toString());
						}
						if( post.getDate() == null ) {
							post.setDate(new Date());
						}
						BLOG_DB.put(post.getId(), post);
						response.setResult(post);
						response.setTotal(1);
					}
				}else {
					response.getMeta().setCode(400);
					response.getMeta().setErrorType("validation error");
					response.getMeta().setErrorMessage("Unauthorized user");
				}
			} catch (Throwable t) {
				response.getMeta().setCode(500);
				response.getMeta().setErrorType("server error");
				response.getMeta().setErrorMessage(t.toString());
			}
			
			return CompletableFuture.completedFuture(response);
		
		
	}

	
	private void sleep() {
		try {
			Thread.sleep(1000);
		} catch (Throwable t) {

		}
	}

	@RequestMapping(path="/blogs/{id}",produces = "application/json",method = RequestMethod.GET)
	@Async("theExec")
	public CompletableFuture<Response<BlogPost>> getPost(HttpServletRequest req,
														@PathVariable(name="id") String id){
			Response<BlogPost> response = new Response<>();
			try {
				sleep();
				
				response.setNextOffset(-1);
				if( validUser(req) ) {
					BlogPost bp = BLOG_DB.get(id);
					if( bp != null ) {
						response.setResult(bp);
						response.setTotal(1);
					}else {
						response.getMeta().setCode(404);
						response.getMeta().setErrorType("validation error");
						response.getMeta().setErrorMessage("Not found");
					}
				}
			} catch (Throwable t) {
				response.getMeta().setCode(500);
				response.getMeta().setErrorType("server error");
				response.getMeta().setErrorMessage(t.toString());
			}
			
			return CompletableFuture.completedFuture(response);
		};
		
	
	
	private boolean validatePost(BlogPost post,Response<BlogPost> response) {
		if( post == null ) {
			response.getMeta().setCode(500);
			response.getMeta().setErrorType("server error");
			response.getMeta().setErrorMessage("Null object");
			return false;
		}
		if( post.getId() != null && BLOG_DB.contains(post.getId())) {
			response.getMeta().setCode(409);
			response.getMeta().setErrorType("validation error");
			response.getMeta().setErrorMessage("ID already in use");
			return false;
		}
		if( post.getAuthor() == null ) {
			response.getMeta().setCode(400);
			response.getMeta().setErrorType("validation error");
			response.getMeta().setErrorMessage("Author is required already in use");
			return false;
		}
		if( post.getContent() == null || post.getContent().trim().length() == 0 ) {
			response.getMeta().setCode(400);
			response.getMeta().setErrorType("validation error");
			response.getMeta().setErrorMessage("Content is required");
			return false;
		}
		if( post.getTitle() == null || post.getTitle().trim().length() == 0 ) {
			response.getMeta().setCode(400);
			response.getMeta().setErrorType("validation error");
			response.getMeta().setErrorMessage("Title is required");
			return false;
		}
		return true;
	}
}
