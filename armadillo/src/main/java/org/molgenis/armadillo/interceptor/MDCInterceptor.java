package org.molgenis.armadillo.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

public class MDCInterceptor implements WebRequestInterceptor {

  private static Logger LOGGER = LoggerFactory.getLogger(MDCInterceptor.class);

  @Override
  public void preHandle(WebRequest webRequest) throws Exception {
    MDC.put("sessionID", webRequest.getSessionId());
    // FIXME: remove
    LOGGER.trace(
        "preHandle: "
            + webRequest.getUserPrincipal()
            + " :: "
            + webRequest.getSessionId()
            + " :: "
            + webRequest.getRemoteUser());
  }

  @Override
  public void postHandle(WebRequest webRequest, ModelMap modelMap) throws Exception {
    // FIXME: remove
    LOGGER.trace("postHandle", webRequest);
  }

  @Override
  public void afterCompletion(WebRequest webRequest, Exception e) throws Exception {
    LOGGER.trace("afterCompletion", e);
    MDC.clear();
  }
}
