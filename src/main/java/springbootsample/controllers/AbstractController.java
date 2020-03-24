package springbootsample.controllers;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractController {

	protected boolean validUser(HttpServletRequest req) {
		return true;
	}
}
